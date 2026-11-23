package com.example.proyecto.app.service.impl;

import com.example.proyecto.app.dto.request.ActualizarOrdenCompraRequest;
import com.example.proyecto.app.dto.request.ItemReposicionRequest;
import com.example.proyecto.app.dto.response.EnvioOrdenResponse;
import com.example.proyecto.app.dto.response.OrdenCompraResponse;
import com.example.proyecto.app.entity.*;
import com.example.proyecto.app.entity.OrdenCompra.EstadoOrden;
import com.example.proyecto.app.exception.BusinessException;
import com.example.proyecto.app.exception.ResourceNotFoundException;
import com.example.proyecto.app.repository.OrdenCompraRepository;
import com.example.proyecto.app.repository.ParametroSistemaRepository;
import com.example.proyecto.app.repository.ProductoRepository;
import com.example.proyecto.app.repository.ProveedorRepository;
import com.example.proyecto.app.service.EmailService;
import com.example.proyecto.app.service.OrdenCompraService;
import com.example.proyecto.app.service.ReposicionService;
import com.example.proyecto.app.util.OrdenCompraPdfGenerator;
import com.example.proyecto.app.util.SecurityUtils;
import jakarta.mail.MessagingException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.MailException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.*;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class OrdenCompraServiceImpl implements OrdenCompraService {

    private final OrdenCompraRepository ordenRepository;
    private final ProveedorRepository proveedorRepository;
    private final ProductoRepository productoRepository;
    private final ParametroSistemaRepository parametroRepository;
    private final ReposicionService reposicionService;
    private final SecurityUtils securityUtils;
    private final OrdenCompraPdfGenerator pdfGenerator;
    private final EmailService emailService;

    // ------------------------------------------------------------------ crear

    @Override
    public List<OrdenCompraResponse> crearDesdeReposicion(List<ItemReposicionRequest> items) {
        List<ItemReposicionRequest> validos = items == null ? List.of() : items.stream()
                .filter(i -> i.getCantidad() != null && i.getCantidad() > 0).toList();
        if (validos.isEmpty()) throw bad("No hay productos con cantidad mayor a 0.");

        // Una orden por proveedor
        Map<Integer, List<ItemReposicionRequest>> porProveedor = new LinkedHashMap<>();
        for (ItemReposicionRequest i : validos) {
            if (i.getProductoId() == null || i.getProveedorId() == null) {
                throw bad("Hay productos sin proveedor asignado.");
            }
            porProveedor.computeIfAbsent(i.getProveedorId(), k -> new ArrayList<>()).add(i);
        }

        Usuario usuario = securityUtils.getUsuarioAutenticado();
        List<OrdenCompraResponse> creadas = new ArrayList<>();

        for (Map.Entry<Integer, List<ItemReposicionRequest>> entry : porProveedor.entrySet()) {
            Proveedor proveedor = proveedorRepository.findById(entry.getKey())
                    .orElseThrow(() -> bad("Proveedor no encontrado: " + entry.getKey()));

            OrdenCompra orden = new OrdenCompra();
            orden.setProveedor(proveedor);
            orden.setUsuario(usuario);
            orden.setEstado(EstadoOrden.borrador);

            for (ItemReposicionRequest item : entry.getValue()) {
                Producto producto = productoRepository.findById(item.getProductoId())
                        .orElseThrow(() -> bad("Producto no encontrado: " + item.getProductoId()));

                DetalleOrdenCompra d = new DetalleOrdenCompra();
                d.setOrdenCompra(orden);
                d.setProducto(producto);
                d.setCantidadSolicitada(item.getCantidad());
                // El precio se calcula en el servidor, no se confía en el que manda el navegador
                d.setPrecioCompraEstimado(reposicionService.precioEstimado(producto, proveedor));
                orden.getDetalles().add(d);
            }
            recalcularTotal(orden);
            ordenRepository.save(orden);
            log.info("Orden {} creada para proveedor {} (total {})", orden.getId(), proveedor.getRazonSocial(), orden.getTotal());
            creadas.add(toResponse(orden));
        }
        return creadas;
    }

    // ------------------------------------------------------------ consultar

    @Override
    @Transactional(readOnly = true)
    public List<OrdenCompraResponse> listar(String estado) {
        List<OrdenCompra> ordenes;
        if (estado == null || estado.isBlank()) {
            ordenes = ordenRepository.findAllByOrderByFechaCreacionDesc();
        } else {
            ordenes = ordenRepository.findByEstadoOrderByFechaCreacionDesc(parseEstado(estado));
        }
        return ordenes.stream().map(this::toResponse).toList();
    }

    @Override
    @Transactional(readOnly = true)
    public OrdenCompraResponse obtener(Integer id) {
        return toResponse(buscar(id));
    }

    // ------------------------------------------------------------- guardar

    @Override
    public OrdenCompraResponse actualizar(Integer id, ActualizarOrdenCompraRequest req) {
        OrdenCompra orden = buscar(id);
        exigirBorrador(orden);

        orden.setNotas(req.getNotas() == null || req.getNotas().isBlank() ? null : req.getNotas().trim());

        if (req.getLineas() != null) {
            Map<Integer, ActualizarOrdenCompraRequest.Linea> porId = new HashMap<>();
            req.getLineas().forEach(l -> porId.put(l.getDetalleId(), l));

            Iterator<DetalleOrdenCompra> it = orden.getDetalles().iterator();
            while (it.hasNext()) {
                DetalleOrdenCompra d = it.next();
                ActualizarOrdenCompraRequest.Linea l = porId.get(d.getId());
                if (l == null) continue;
                if (l.getCantidad() == null || l.getCantidad() <= 0) {
                    it.remove(); // orphanRemoval borra la línea
                    continue;
                }
                d.setCantidadSolicitada(l.getCantidad());
                if (l.getPrecio() != null) {
                    if (l.getPrecio().signum() < 0) throw bad("El precio no puede ser negativo.");
                    d.setPrecioCompraEstimado(l.getPrecio().setScale(2, RoundingMode.HALF_UP));
                }
            }
        }
        if (orden.getDetalles().isEmpty()) {
            throw bad("La orden debe tener al menos un producto. Si no la necesitas, anúlala.");
        }
        recalcularTotal(orden);
        return toResponse(ordenRepository.save(orden));
    }

    // ----------------------------------------------------------------- pdf

    @Override
    @Transactional(readOnly = true)
    public byte[] generarPdf(Integer id) {
        return pdfGenerator.generar(buscar(id));
    }

    // --------------------------------------------------------------- enviar

    @Override
    public EnvioOrdenResponse enviar(Integer id, String canal) {
        OrdenCompra orden = buscar(id);
        exigirBorrador(orden);

        String c = canal == null ? "" : canal.trim().toUpperCase();
        EnvioOrdenResponse respuesta = switch (c) {
            case "EMAIL" -> enviarPorEmail(orden);
            case "WHATSAPP" -> enviarPorWhatsapp(orden);
            default -> throw bad("Canal de envío no válido (use EMAIL o WHATSAPP).");
        };

        orden.setEstado(EstadoOrden.enviada);
        orden.setFechaEnvio(LocalDateTime.now());
        orden.setCanalEnvio(c);
        ordenRepository.save(orden);
        return respuesta;
    }

    private EnvioOrdenResponse enviarPorEmail(OrdenCompra orden) {
        String destino = orden.getProveedor().getEmail();
        if (destino == null || destino.isBlank()) {
            throw bad("El proveedor no tiene correo registrado.");
        }
        String numero = OrdenCompraPdfGenerator.numero(orden);
        byte[] pdf = pdfGenerator.generar(orden);
        try {
            // Usa el SMTP configurado en parametros_sistema (smtp.host, smtp.username, ...)
            emailService.enviarBoleta(
                    destino,
                    "Orden de compra " + numero + " - " + empresa(),
                    cuerpoTexto(orden),
                    pdf,
                    numero + ".pdf");
        } catch (IllegalStateException e) {
            // "Configuración SMTP incompleta..."
            throw bad(e.getMessage());
        } catch (MessagingException | MailException e) {
            log.error("Error enviando correo de la orden {}", orden.getId(), e);
            throw bad("No se pudo enviar el correo: " + e.getMessage());
        }
        return new EnvioOrdenResponse("EMAIL", null, "Orden enviada por correo a " + destino);
    }

    private EnvioOrdenResponse enviarPorWhatsapp(OrdenCompra orden) {
        String numero = normalizarCelular(orden.getProveedor().getTelefono());
        String texto = URLEncoder.encode(cuerpoTexto(orden), StandardCharsets.UTF_8).replace("+", "%20");
        String url = "https://wa.me/" + numero + "?text=" + texto;
        return new EnvioOrdenResponse("WHATSAPP", url, "Se abrió WhatsApp con la orden lista para enviar.");
    }

    /** Deja solo dígitos y agrega el prefijo 51 (Perú) a celulares de 9 dígitos. */
    private String normalizarCelular(String telefono) {
        String digitos = telefono == null ? "" : telefono.replaceAll("\\D", "");
        if (digitos.length() == 9 && digitos.startsWith("9")) return "51" + digitos;
        if (digitos.length() == 11 && digitos.startsWith("519")) return digitos;
        throw bad(
                "El teléfono del proveedor no parece un celular válido (9 dígitos que empiecen con 9).");
    }

    private String cuerpoTexto(OrdenCompra orden) {
        StringBuilder sb = new StringBuilder();
        String contacto = orden.getProveedor().getContacto();
        sb.append("Hola").append(contacto == null || contacto.isBlank() ? "" : " " + contacto)
          .append(", le saluda ").append(empresa()).append(".\n")
          .append("Solicitamos la siguiente orden de compra ").append(OrdenCompraPdfGenerator.numero(orden)).append(":\n\n");
        for (DetalleOrdenCompra d : orden.getDetalles()) {
            sb.append("- ").append(d.getCantidadSolicitada()).append(" x ").append(d.getProducto().getNombre())
              .append(" (S/ ").append(d.getPrecioCompraEstimado()).append(" c/u)\n");
        }
        sb.append("\nTotal estimado: S/ ").append(orden.getTotal()).append("\n");
        if (orden.getNotas() != null && !orden.getNotas().isBlank()) sb.append("Notas: ").append(orden.getNotas()).append("\n");
        sb.append("\nQuedamos atentos a su confirmación. Gracias.");
        return sb.toString();
    }

    private String empresa() {
        return parametroRepository.findValorByClave("RAZON_SOCIAL").orElse("ServiFarma");
    }

    // --------------------------------------------------------------- anular

    @Override
    public OrdenCompraResponse anular(Integer id) {
        OrdenCompra orden = buscar(id);
        if (orden.getEstado() == EstadoOrden.recibida || orden.getEstado() == EstadoOrden.anulada) {
            throw bad("Una orden " + orden.getEstado() + " no se puede anular.");
        }
        orden.setEstado(EstadoOrden.anulada);
        return toResponse(ordenRepository.save(orden));
    }

    // -------------------------------------------------------------- helpers

    private OrdenCompra buscar(Integer id) {
        return ordenRepository.findById(id)
                .orElseThrow(() -> noEncontrada("Orden de compra no encontrada: " + id));
    }

    private void exigirBorrador(OrdenCompra o) {
        if (o.getEstado() != EstadoOrden.borrador) {
            throw bad("Solo las órdenes en borrador se pueden modificar o enviar.");
        }
    }

    private EstadoOrden parseEstado(String estado) {
        try {
            return EstadoOrden.valueOf(estado.trim().toLowerCase());
        } catch (IllegalArgumentException e) {
            throw bad("Estado no válido: " + estado);
        }
    }

    private void recalcularTotal(OrdenCompra orden) {
        BigDecimal total = orden.getDetalles().stream()
                .map(d -> d.getPrecioCompraEstimado().multiply(BigDecimal.valueOf(d.getCantidadSolicitada())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        orden.setTotal(total.setScale(2, RoundingMode.HALF_UP));
    }

    /** Error de negocio: el GlobalExceptionHandler lo devuelve como 400 con { mensaje }. */
    private BusinessException bad(String mensaje) {
        return new BusinessException(mensaje);
    }

    /** Recurso inexistente: el GlobalExceptionHandler lo devuelve como 404 con { mensaje }. */
    private ResourceNotFoundException noEncontrada(String mensaje) {
        return new ResourceNotFoundException(mensaje);
    }

    private OrdenCompraResponse toResponse(OrdenCompra o) {
        OrdenCompraResponse r = new OrdenCompraResponse();
        r.setId(o.getId());
        r.setNumero(OrdenCompraPdfGenerator.numero(o));
        r.setProveedorId(o.getProveedor().getId());
        r.setProveedorNombre(o.getProveedor().getRazonSocial());
        r.setProveedorEmail(o.getProveedor().getEmail());
        r.setProveedorTelefono(o.getProveedor().getTelefono());
        r.setUsuarioId(o.getUsuario().getId());
        r.setFechaCreacion(o.getFechaCreacion());
        r.setEstado(o.getEstado().name());
        r.setTotal(o.getTotal());
        r.setFechaEnvio(o.getFechaEnvio());
        r.setCanalEnvio(o.getCanalEnvio());
        r.setNotas(o.getNotas());
        r.setDetalles(o.getDetalles().stream().map(d -> {
            OrdenCompraResponse.Detalle x = new OrdenCompraResponse.Detalle();
            x.setId(d.getId());
            x.setProductoId(d.getProducto().getId());
            x.setProductoNombre(d.getProducto().getNombre());
            x.setCantidad(d.getCantidadSolicitada());
            x.setPrecio(d.getPrecioCompraEstimado());
            x.setSubtotal(d.getPrecioCompraEstimado().multiply(BigDecimal.valueOf(d.getCantidadSolicitada())));
            return x;
        }).toList());
        return r;
    }
}

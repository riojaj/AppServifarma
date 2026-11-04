package com.example.proyecto.app.unit.service;

import com.example.proyecto.app.dto.request.ProductoRequest;
import com.example.proyecto.app.dto.response.ProductoResponse;
import com.example.proyecto.app.entity.Producto;
import com.example.proyecto.app.exception.DuplicadoException;
import com.example.proyecto.app.exception.ResourceNotFoundException;
import com.example.proyecto.app.mapper.ProductoMapper;
import com.example.proyecto.app.repository.CategoriaRepository;
import com.example.proyecto.app.repository.FabricanteRepository;
import com.example.proyecto.app.repository.LoteRepository;
import com.example.proyecto.app.repository.ProductoRepository;
import com.example.proyecto.app.service.BitacoraComunicacionService;
import com.example.proyecto.app.service.impl.ProductoImportService;
import com.example.proyecto.app.service.impl.ProductoServiceImpl;
import com.example.proyecto.app.util.SecurityUtils;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Pruebas UNITARIAS del módulo Producto (ProductoServiceImpl).
 * Se mockean todas las dependencias (repositorios, mapper, servicios externos).
 *
 * Casos cubiertos:
 *  - CP-U01: RF24 — Crear producto con datos válidos (caso exitoso)
 *  - CP-U02: RF24 — Rechazar producto con código de barras duplicado
 *  - CP-U03: RF20 — Buscar producto inexistente lanza ResourceNotFoundException
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("CP-U - Pruebas unitarias de ProductoServiceImpl")
class ProductoServiceImplTest {

    // ==========================================================
    // MOCKS: objetos falsos que simulan las dependencias reales
    // ==========================================================
    @Mock private ProductoRepository productoRepository;
    @Mock private FabricanteRepository fabricanteRepository;
    @Mock private CategoriaRepository categoriaRepository;
    @Mock private LoteRepository loteRepository;
    @Mock private ProductoMapper productoMapper;
    @Mock private BitacoraComunicacionService bitacoraService;
    @Mock private SecurityUtils securityUtils;
    @Mock private ProductoImportService productoImportService;

    @InjectMocks
    private ProductoServiceImpl productoService;

    // ==========================================================
    // HELPERS: métodos auxiliares para crear objetos de prueba
    // ==========================================================

    private Producto producto(Integer id, String nombre, String codigo) {
        return Producto.builder()
                .id(id)
                .nombre(nombre)
                .codigoBarras(codigo)
                .precioVentaActual(new BigDecimal("10.00"))
                .esGenerico(false)
                .build();
    }

    private ProductoResponse response(Integer id, String nombre) {
        return ProductoResponse.builder()
                .id(id)
                .nombre(nombre)
                .build();
    }

    private ProductoRequest request(String codigo) {
        return ProductoRequest.builder()
                .codigoBarras(codigo)
                .nombre("Paracetamol")
                .precioVentaActual(new BigDecimal("10.00"))
                .build();
    }

    // ==========================================================
    // CP-U01 — RF24: Crear producto con datos válidos
    // ==========================================================
    @Test
    @DisplayName("CP-U01: crearProducto() guarda y devuelve el producto correctamente")
    void cpU01_crearProducto_exitoso() {
        // GIVEN: los mocks devuelven datos coherentes
        ProductoRequest req = request("123456");
        Producto entidad = producto(null, "Paracetamol", "123456");
        Producto guardado = producto(1, "Paracetamol", "123456");
        ProductoResponse respuesta = response(1, "Paracetamol");

        when(productoRepository.existsByCodigoBarras("123456")).thenReturn(false);
        when(productoMapper.toEntity(req)).thenReturn(entidad);
        when(productoRepository.save(any(Producto.class))).thenReturn(guardado);
        when(productoMapper.toResponse(guardado)).thenReturn(respuesta);
        when(securityUtils.getUsuarioAutenticado()).thenThrow(new RuntimeException("sin auth"));

        // WHEN: ejecutamos el método bajo prueba
        ProductoResponse resultado = productoService.crearProducto(req);

        // THEN: verificamos la respuesta
        assertThat(resultado).isNotNull();
        assertThat(resultado.getId()).isEqualTo(1);
        assertThat(resultado.getNombre()).isEqualTo("Paracetamol");
    }

    // ==========================================================
    // CP-U02 — RF24: Rechazar código de barras duplicado
    // ==========================================================
    @Test
    @DisplayName("CP-U02: crearProducto() con código duplicado lanza DuplicadoException")
    void cpU02_crearProducto_duplicado() {
        // GIVEN: el código de barras ya existe en la BD
        ProductoRequest req = request("123456");
        when(productoRepository.existsByCodigoBarras("123456")).thenReturn(true);

        // WHEN + THEN: la llamada debe lanzar la excepción
        assertThatThrownBy(() -> productoService.crearProducto(req))
                .isInstanceOf(DuplicadoException.class)
                .hasMessageContaining("123456");

        // Y no debe intentar guardar
        verify(productoRepository, never()).save(any(Producto.class));
    }

    // ==========================================================
    // CP-U03 — RF20: Buscar producto inexistente
    // ==========================================================
    @Test
    @DisplayName("CP-U03: obtenerProductoPorId() con ID inexistente lanza ResourceNotFoundException")
    void cpU03_obtenerPorId_noExiste() {
        // GIVEN: el repositorio no encuentra el producto
        when(productoRepository.findById(99)).thenReturn(Optional.empty());

        // WHEN + THEN: se debe lanzar la excepción
        assertThatThrownBy(() -> productoService.obtenerProductoPorId(99))
                .isInstanceOf(ResourceNotFoundException.class);
    }
}
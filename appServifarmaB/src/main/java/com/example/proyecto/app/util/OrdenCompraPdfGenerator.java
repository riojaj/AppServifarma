package com.example.proyecto.app.util;

import com.example.proyecto.app.entity.DetalleOrdenCompra;
import com.example.proyecto.app.entity.OrdenCompra;
import com.example.proyecto.app.entity.Proveedor;
import com.example.proyecto.app.repository.ParametroSistemaRepository;
import com.lowagie.text.*;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.awt.Color;
import java.io.ByteArrayOutputStream;
import java.math.BigDecimal;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

@Component
@RequiredArgsConstructor
public class OrdenCompraPdfGenerator {

    private final ParametroSistemaRepository parametroRepository;

    public static String numero(OrdenCompra o) {
        return "OC-" + String.format("%06d", o.getId());
    }

    private static String money(BigDecimal v) {
        return String.format(Locale.US, "S/ %.2f", v == null ? BigDecimal.ZERO : v);
    }

    private static String nz(String s) {
        return s == null || s.isBlank() ? "-" : s;
    }

    public byte[] generar(OrdenCompra orden) {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        Document doc = new Document(PageSize.A4, 40, 40, 50, 40);
        try {
            PdfWriter.getInstance(doc, out);
            doc.open();

            Font h1 = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 16);
            Font h2 = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 11);
            Font normal = FontFactory.getFont(FontFactory.HELVETICA, 10);
            Font bold = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 10);

            String empresa = parametroRepository.findValorByClave("RAZON_SOCIAL").orElse("ServiFarma");
            String ruc = parametroRepository.findValorByClave("RUC_EMPRESA").orElse("-");

            doc.add(new Paragraph(empresa, h1));
            doc.add(new Paragraph("RUC: " + ruc, normal));
            doc.add(new Paragraph(" "));
            doc.add(new Paragraph("ORDEN DE COMPRA " + numero(orden), h2));
            doc.add(new Paragraph("Fecha: " + orden.getFechaCreacion()
                    .format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")), normal));
            doc.add(new Paragraph(" "));

            Proveedor p = orden.getProveedor();
            doc.add(new Paragraph("Proveedor", h2));
            doc.add(new Paragraph(nz(p.getRazonSocial()) + "  (RUC " + nz(p.getRuc()) + ")", normal));
            doc.add(new Paragraph("Contacto: " + nz(p.getContacto()) + "   Tel: " + nz(p.getTelefono())
                    + "   Correo: " + nz(p.getEmail()), normal));
            doc.add(new Paragraph("Dirección: " + nz(p.getDireccion()), normal));
            doc.add(new Paragraph(" "));

            PdfPTable tabla = new PdfPTable(5);
            tabla.setWidthPercentage(100);
            tabla.setWidths(new float[]{0.8f, 5f, 1.4f, 1.8f, 2f});
            for (String h : new String[]{"N°", "Producto", "Cant.", "P. unit.", "Subtotal"}) {
                PdfPCell c = new PdfPCell(new Phrase(h, bold));
                c.setBackgroundColor(new Color(230, 230, 230));
                c.setPadding(5);
                tabla.addCell(c);
            }

            int n = 1;
            for (DetalleOrdenCompra d : orden.getDetalles()) {
                BigDecimal sub = d.getPrecioCompraEstimado().multiply(BigDecimal.valueOf(d.getCantidadSolicitada()));
                tabla.addCell(celda(String.valueOf(n++), normal, Element.ALIGN_CENTER));
                tabla.addCell(celda(d.getProducto().getNombre(), normal, Element.ALIGN_LEFT));
                tabla.addCell(celda(String.valueOf(d.getCantidadSolicitada()), normal, Element.ALIGN_RIGHT));
                tabla.addCell(celda(money(d.getPrecioCompraEstimado()), normal, Element.ALIGN_RIGHT));
                tabla.addCell(celda(money(sub), normal, Element.ALIGN_RIGHT));
            }
            doc.add(tabla);

            Paragraph total = new Paragraph("TOTAL ESTIMADO: " + money(orden.getTotal()), h2);
            total.setAlignment(Element.ALIGN_RIGHT);
            total.setSpacingBefore(10);
            doc.add(total);

            if (orden.getNotas() != null && !orden.getNotas().isBlank()) {
                Paragraph notas = new Paragraph("Notas: " + orden.getNotas(), normal);
                notas.setSpacingBefore(12);
                doc.add(notas);
            }
        } catch (DocumentException e) {
            throw new IllegalStateException("No se pudo generar el PDF de la orden", e);
        } finally {
            if (doc.isOpen()) doc.close();
        }
        return out.toByteArray();
    }

    private PdfPCell celda(String texto, Font font, int align) {
        PdfPCell c = new PdfPCell(new Phrase(texto, font));
        c.setHorizontalAlignment(align);
        c.setPadding(5);
        return c;
    }
}

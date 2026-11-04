package com.example.proyecto.app.integration.repository;

import com.example.proyecto.app.entity.Cliente;
import com.example.proyecto.app.repository.ClienteRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Pruebas de INTEGRACIÓN del repositorio ClienteRepository.
 * Usa @DataJpaTest: levanta solo la capa JPA con H2 en memoria.
 *
 * Casos cubiertos:
 *  - CP-I03-01: RF8/RF29 — Guardar cliente y buscarlo por número de documento
 *  - CP-I03-02: RF8/RF29 — Buscar clientes por nombre (coincidencia parcial, ignore case)
 *  - CP-I03-03: RF8/RF29 — Verificar detección de documento duplicado
 */
@DataJpaTest
@ActiveProfiles("test")
@DisplayName("CP-I03 - Integración de ClienteRepository (H2)")
class ClienteRepositoryIT {

    @Autowired
    private ClienteRepository clienteRepository;

    // ==========================================================
    // CP-I03-01: Guardar y recuperar cliente por documento
    // ==========================================================
    @Test
    @DisplayName("CP-I03-01: guarda un cliente y lo busca por número de documento")
    void cpI0301_guardarYBuscarPorDocumento() {
        // GIVEN: un cliente con DNI
        Cliente cliente = Cliente.builder()
                .nombre("Juan Pérez")
                .documentoTipo(Cliente.DocumentoTipo.DNI)
                .documentoNumero("12345678")
                .telefono("999888777")
                .email("juan@test.com")
                .build();

        // WHEN: lo guardamos
        Cliente guardado = clienteRepository.save(cliente);

        // THEN: se le asigna ID y se puede recuperar por documento
        assertThat(guardado.getId()).isNotNull();
        assertThat(guardado.getId()).isPositive();

        Optional<Cliente> encontrado = clienteRepository.findByDocumentoNumero("12345678");
        assertThat(encontrado).isPresent();
        assertThat(encontrado.get().getNombre()).isEqualTo("Juan Pérez");
        assertThat(encontrado.get().getDocumentoTipo()).isEqualTo(Cliente.DocumentoTipo.DNI);
        assertThat(encontrado.get().getEmail()).isEqualTo("juan@test.com");
    }

    // ==========================================================
    // CP-I03-02: Buscar clientes por nombre (parcial, ignore case)
    // ==========================================================
    @Test
    @DisplayName("CP-I03-02: busca clientes por nombre ignorando mayúsculas y coincidencia parcial")
    void cpI0302_buscarPorNombreParcial() {
        // GIVEN: varios clientes guardados
        clienteRepository.save(Cliente.builder()
                .nombre("María López")
                .documentoTipo(Cliente.DocumentoTipo.DNI)
                .documentoNumero("11111111")
                .build());

        clienteRepository.save(Cliente.builder()
                .nombre("Marcos Ramírez")
                .documentoTipo(Cliente.DocumentoTipo.DNI)
                .documentoNumero("22222222")
                .build());

        clienteRepository.save(Cliente.builder()
                .nombre("Pedro Sánchez")
                .documentoTipo(Cliente.DocumentoTipo.DNI)
                .documentoNumero("33333333")
                .build());

        // WHEN: buscamos por "MAR" (mayúsculas, parcial)
        List<Cliente> encontrados = clienteRepository.findByNombreContainingIgnoreCase("MAR");

        // THEN: debe encontrar "María" y "Marcos", pero NO "Pedro"
        assertThat(encontrados).hasSize(2);
        assertThat(encontrados)
                .extracting(Cliente::getNombre)
                .containsExactlyInAnyOrder("María López", "Marcos Ramírez");
    }

    // ==========================================================
    // CP-I03-03: Detectar documento duplicado
    // ==========================================================
    @Test
    @DisplayName("CP-I03-03: existsByDocumentoNumero() detecta si un documento ya existe")
    void cpI0303_detectarDocumentoDuplicado() {
        // GIVEN: un cliente ya registrado con DNI 12345678
        clienteRepository.save(Cliente.builder()
                .nombre("Cliente Existente")
                .documentoTipo(Cliente.DocumentoTipo.DNI)
                .documentoNumero("12345678")
                .build());

        // WHEN / THEN: verificamos existencia/
        boolean existe = clienteRepository.existsByDocumentoNumero("12345678");
        boolean noExiste = clienteRepository.existsByDocumentoNumero("99999999");

        // THEN:
        assertThat(existe).isTrue();      // sí existe
        assertThat(noExiste).isFalse();   // no existe
    }
}
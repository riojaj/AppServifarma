package com.example.proyecto.app.unit.service;

import com.example.proyecto.app.dto.request.ClienteRequest;
import com.example.proyecto.app.dto.response.ClienteResponse;
import com.example.proyecto.app.entity.Cliente;
import com.example.proyecto.app.exception.DuplicadoException;
import com.example.proyecto.app.exception.ResourceNotFoundException;
import com.example.proyecto.app.mapper.ClienteMapper;
import com.example.proyecto.app.repository.ClienteRepository;
import com.example.proyecto.app.service.impl.ClienteServiceImpl;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Pruebas UNITARIAS del módulo Cliente (ClienteServiceImpl).
 * Se mockean todas las dependencias (repositorio y mapper).
 *
 * Casos cubiertos:
 *  - CP-U03-01: RF8  — Crear cliente con datos válidos (caso exitoso)
 *  - CP-U03-02: RF8  — Rechazar cliente con documento duplicado
 *  - CP-U03-03: RF29 — Buscar cliente por ID inexistente lanza ResourceNotFoundException
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("CP-U03 - Pruebas unitarias de ClienteServiceImpl")
class ClienteServiceImplTest {

    // ==========================================================
    // MOCKS: objetos falsos que simulan las dependencias reales
    // ==========================================================
    @Mock private ClienteRepository clienteRepository;
    @Mock private ClienteMapper clienteMapper;

    @InjectMocks
    private ClienteServiceImpl clienteService;

    // ==========================================================
    // HELPERS: métodos auxiliares para crear objetos de prueba
    // ==========================================================

    private Cliente cliente(Integer id, String nombre, String documento) {
        return Cliente.builder()
                .id(id)
                .nombre(nombre)
                .documentoTipo(Cliente.DocumentoTipo.DNI)
                .documentoNumero(documento)
                .telefono("999888777")
                .email("cliente@test.com")
                .build();
    }

    private ClienteResponse response(Integer id, String nombre) {
        return ClienteResponse.builder()
                .id(id)
                .nombre(nombre)
                .documentoTipo(Cliente.DocumentoTipo.DNI)
                .documentoNumero("12345678")
                .build();
    }

    private ClienteRequest request(String documento) {
        return ClienteRequest.builder()
                .nombre("Juan Pérez")
                .documentoTipo(Cliente.DocumentoTipo.DNI)
                .documentoNumero(documento)
                .telefono("999888777")
                .email("juan@test.com")
                .build();
    }

    // ==========================================================
    // CP-U03-01 — RF8: Crear cliente con datos válidos
    // ==========================================================
    @Test
    @DisplayName("CP-U03-01: crearCliente() guarda y devuelve el cliente correctamente")
    void cpU0301_crearCliente_exitoso() {
        // GIVEN: los mocks responden como si todo estuviera OK
        ClienteRequest req = request("12345678");
        Cliente entidad = cliente(null, "Juan Pérez", "12345678");
        Cliente guardado = cliente(1, "Juan Pérez", "12345678");
        ClienteResponse respuesta = response(1, "Juan Pérez");

        when(clienteRepository.existsByDocumentoNumero("12345678")).thenReturn(false);
        when(clienteMapper.toEntity(req)).thenReturn(entidad);
        when(clienteRepository.save(any(Cliente.class))).thenReturn(guardado);
        when(clienteMapper.toResponse(guardado)).thenReturn(respuesta);

        // WHEN: ejecutamos el método a probar
        ClienteResponse resultado = clienteService.crearCliente(req);

        // THEN: verificamos la respuesta
        assertThat(resultado).isNotNull();
        assertThat(resultado.getId()).isEqualTo(1);
        assertThat(resultado.getNombre()).isEqualTo("Juan Pérez");
        assertThat(resultado.getDocumentoNumero()).isEqualTo("12345678");
    }

    // ==========================================================
    // CP-U03-02 — RF8: Rechazar documento duplicado
    // ==========================================================
    @Test
    @DisplayName("CP-U03-02: crearCliente() con documento duplicado lanza DuplicadoException")
    void cpU0302_crearCliente_duplicado() {
        // GIVEN: ya existe un cliente con ese DNI
        ClienteRequest req = request("12345678");
        when(clienteRepository.existsByDocumentoNumero("12345678")).thenReturn(true);

        // WHEN + THEN: debe lanzar la excepción
        assertThatThrownBy(() -> clienteService.crearCliente(req))
                .isInstanceOf(DuplicadoException.class)
                .hasMessageContaining("12345678");

        // Y NO debe intentar guardar
        verify(clienteRepository, never()).save(any(Cliente.class));
    }

    // ==========================================================
    // CP-U03-03 — RF29: Buscar cliente inexistente
    // ==========================================================
    @Test
    @DisplayName("CP-U03-03: obtenerClientePorId() con ID inexistente lanza ResourceNotFoundException")
    void cpU0303_obtenerPorId_noExiste() {
        // GIVEN: el repositorio no encuentra el cliente
        when(clienteRepository.findById(99)).thenReturn(Optional.empty());

        // WHEN + THEN: se debe lanzar la excepción
        assertThatThrownBy(() -> clienteService.obtenerClientePorId(99))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("99");
    }
}
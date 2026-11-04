package com.example.proyecto.app.unit.mapper;

import com.example.proyecto.app.dto.request.CategoriaRequest;
import com.example.proyecto.app.dto.response.CategoriaResponse;
import com.example.proyecto.app.entity.Categoria;
import com.example.proyecto.app.mapper.CategoriaMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import static org.assertj.core.api.Assertions.assertThat;

/** 
 * Pruebas UNITARIAS del mapper CategoriaMapper.
 * Al ser un mapper (clase de conversión pura), NO requiere mocks.
 * Se instancia con Mappers.getMapper() (forma estándar de MapStruct).
 *
 * Casos cubiertos:
 *  - CP-U02-01: toEntity()      — Convierte CategoriaRequest → Categoria
 *  - CP-U02-02: toResponse()    — Convierte Categoria → CategoriaResponse
 *  - CP-U02-03: updateEntity()  — Actualiza una Categoria existente con datos del request
 */
@DisplayName("CP-U02 - Pruebas unitarias de CategoriaMapper")
class CategoriaMapperTest {

    /**
     * Instancia real del mapper generado por MapStruct.
     * MapStruct genera una implementación de la interfaz (CategoriaMapperImpl)
     * y Mappers.getMapper() nos la devuelve.
     */
    private final CategoriaMapper categoriaMapper = Mappers.getMapper(CategoriaMapper.class);

    // ==========================================================
    // CP-U02-01 — toEntity(): CategoriaRequest → Categoria
    // ==========================================================
    @Test
    @DisplayName("CP-U02-01: toEntity() mapea todos los campos del request")
    void cpU0201_toEntity_mapeaTodosLosCampos() {
        // GIVEN: un request con datos válidos
        CategoriaRequest request = CategoriaRequest.builder()
                .nombre("Analgésicos")
                .descripcion("Medicamentos para aliviar el dolor")
                .build();

        // WHEN: mapeamos a entidad
        Categoria entidad = categoriaMapper.toEntity(request);

        // THEN: verificamos que los campos se mapearon
        assertThat(entidad).isNotNull();
        assertThat(entidad.getNombre()).isEqualTo("Analgésicos");
        assertThat(entidad.getDescripcion()).isEqualTo("Medicamentos para aliviar el dolor");

        // Los campos que el mapper ignora NO deben tener valor
        assertThat(entidad.getId()).isNull();
        assertThat(entidad.getCreatedAt()).isNull();
        assertThat(entidad.getProductos()).isNull();
    }

    // ==========================================================
    // CP-U02-02 — toResponse(): Categoria → CategoriaResponse
    // ==========================================================
    @Test
    @DisplayName("CP-U02-02: toResponse() mapea todos los campos de la entidad")
    void cpU0202_toResponse_mapeaTodosLosCampos() {
        // GIVEN: una entidad con datos completos
        Categoria entidad = Categoria.builder()
                .id(1)
                .nombre("Antiinflamatorios")
                .descripcion("Medicamentos para reducir la inflamación")
                .build();

        // WHEN: mapeamos a response
        CategoriaResponse response = categoriaMapper.toResponse(entidad);

        // THEN: verificamos que los campos se mapearon
        assertThat(response).isNotNull();
        assertThat(response.getId()).isEqualTo(1);
        assertThat(response.getNombre()).isEqualTo("Antiinflamatorios");
        assertThat(response.getDescripcion()).isEqualTo("Medicamentos para reducir la inflamación");
    }

    // ==========================================================
    // CP-U02-03 — updateEntity(): actualiza Categoria existente
    // ==========================================================
    @Test
    @DisplayName("CP-U02-03: updateEntity() actualiza los campos de una categoría existente")
    void cpU0203_updateEntity_actualizaCampos() {
        // GIVEN: una categoría existente con datos viejos
        Categoria existente = Categoria.builder()
                .id(10)
                .nombre("Viejo nombre")
                .descripcion("Vieja descripción")
                .build();

        // GIVEN: un request con datos nuevos
        CategoriaRequest request = CategoriaRequest.builder()
                .nombre("Antibióticos")
                .descripcion("Medicamentos para combatir infecciones bacterianas")
                .build();

        // WHEN: actualizamos la entidad existente
        categoriaMapper.updateEntity(existente, request);

        // THEN: los campos se actualizaron, pero el ID se mantiene
        assertThat(existente.getId()).isEqualTo(10);  // ID no cambia
        assertThat(existente.getNombre()).isEqualTo("Antibióticos");
        assertThat(existente.getDescripcion()).isEqualTo("Medicamentos para combatir infecciones bacterianas");
    }
}
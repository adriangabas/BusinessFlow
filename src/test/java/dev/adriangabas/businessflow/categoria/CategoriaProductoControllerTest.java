package dev.adriangabas.businessflow.categoria;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import dev.adriangabas.businessflow.categoria.dto.ActualizarCategoriaRequest;
import dev.adriangabas.businessflow.categoria.dto.CategoriaResponse;
import dev.adriangabas.businessflow.categoria.dto.CrearCategoriaRequest;
import java.time.LocalDateTime;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(CategoriaProductoController.class)
class CategoriaProductoControllerTest {
    @Autowired MockMvc mockMvc;
    @MockitoBean CategoriaProductoService service;

    @Test
    void creaCategoria() throws Exception {
        when(service.crear(any(CrearCategoriaRequest.class))).thenReturn(response());
        mockMvc.perform(post("/api/categorias-producto").contentType(MediaType.APPLICATION_JSON)
                        .content("{\"codigo\":\"ALIM\",\"nombre\":\"Alimentación\"}"))
                .andExpect(status().isCreated())
                .andExpect(header().string("Location", "/api/categorias-producto/1"))
                .andExpect(jsonPath("$.codigo").value("ALIM"));
    }

    @Test
    void validaPeticion() throws Exception {
        mockMvc.perform(post("/api/categorias-producto").contentType(MediaType.APPLICATION_JSON)
                        .content("{\"codigo\":\"\",\"nombre\":\"\"}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.fieldErrors.codigo").exists())
                .andExpect(jsonPath("$.fieldErrors.nombre").exists());
    }

    @Test
    void rechazaEstadoDesconocidoConErrorConsistente() throws Exception {
        mockMvc.perform(post("/api/categorias-producto").contentType(MediaType.APPLICATION_JSON)
                        .content("{\"codigo\":\"ALIM\",\"nombre\":\"Alimentación\",\"estado\":\"DESCONOCIDO\"}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.message").value("El cuerpo de la petición no es válido"));
    }

    @Test
    void devuelveConflictoParaDuplicado() throws Exception {
        when(service.crear(any())).thenThrow(new CategoriaDuplicadaException("ALIM"));
        mockMvc.perform(post("/api/categorias-producto").contentType(MediaType.APPLICATION_JSON)
                        .content("{\"codigo\":\"ALIM\",\"nombre\":\"Alimentación\"}"))
                .andExpect(status().isConflict()).andExpect(jsonPath("$.status").value(409));
    }

    @Test
    void obtieneYListaCategorias() throws Exception {
        when(service.obtener(1L)).thenReturn(response());
        when(service.listar()).thenReturn(List.of(response()));
        mockMvc.perform(get("/api/categorias-producto/1")).andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1));
        mockMvc.perform(get("/api/categorias-producto")).andExpect(status().isOk())
                .andExpect(jsonPath("$[0].codigo").value("ALIM"));
    }

    @Test
    void devuelveNotFound() throws Exception {
        when(service.obtener(99L)).thenThrow(new CategoriaNoEncontradaException(99L));
        mockMvc.perform(get("/api/categorias-producto/99")).andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404));
    }

    @Test
    void actualizaCategoria() throws Exception {
        when(service.actualizar(eq(1L), any(ActualizarCategoriaRequest.class))).thenReturn(response());
        mockMvc.perform(put("/api/categorias-producto/1").contentType(MediaType.APPLICATION_JSON)
                        .content("{\"codigo\":\"ALIM\",\"nombre\":\"Alimentación\",\"estado\":\"ACTIVO\"}"))
                .andExpect(status().isOk()).andExpect(jsonPath("$.codigo").value("ALIM"));
    }

    @Test
    void eliminaCategoria() throws Exception {
        mockMvc.perform(delete("/api/categorias-producto/1")).andExpect(status().isNoContent());
    }

    private CategoriaResponse response() {
        LocalDateTime ahora = LocalDateTime.of(2026, 8, 27, 10, 0);
        return new CategoriaResponse(1L, "ALIM", "Alimentación", null, EstadoCategoria.ACTIVO, ahora, ahora);
    }
}

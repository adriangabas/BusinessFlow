package dev.adriangabas.businessflow.rol;

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

import dev.adriangabas.businessflow.rol.dto.RolResponse;
import java.time.LocalDateTime;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(controllers = RolController.class)
class RolControllerTest {
    @Autowired MockMvc mockMvc;
    @MockitoBean RolService service;

    @Test
    void creaCon201YValidaCampos() throws Exception {
        when(service.crear(any())).thenReturn(response());
        mockMvc.perform(post("/api/roles").contentType(MediaType.APPLICATION_JSON).content(creacionValida()))
                .andExpect(status().isCreated()).andExpect(header().string("Location", "/api/roles/2"));
        mockMvc.perform(post("/api/roles").contentType(MediaType.APPLICATION_JSON).content("{}"))
                .andExpect(status().isBadRequest()).andExpect(jsonPath("$.fieldErrors.codigo").exists())
                .andExpect(jsonPath("$.fieldErrors.nombre").exists());
    }

    @Test
    void exigeEstadoEIsSystemAlActualizar() throws Exception {
        mockMvc.perform(put("/api/roles/2").contentType(MediaType.APPLICATION_JSON)
                .content("{\"codigo\":\"ADMIN\",\"nombre\":\"Admin\"}"))
                .andExpect(status().isBadRequest()).andExpect(jsonPath("$.fieldErrors.estado").exists())
                .andExpect(jsonPath("$.fieldErrors.isSystem").exists());
    }

    @Test
    void listaObtieneActualizaEliminaYMapeaConflictos() throws Exception {
        when(service.listar()).thenReturn(List.of(response()));
        when(service.obtener(2L)).thenReturn(response());
        when(service.actualizar(eq(2L), any())).thenReturn(response());
        mockMvc.perform(get("/api/roles")).andExpect(status().isOk());
        mockMvc.perform(get("/api/roles/2")).andExpect(status().isOk());
        mockMvc.perform(put("/api/roles/2").contentType(MediaType.APPLICATION_JSON)
                .content("{\"codigo\":\"ADMIN\",\"nombre\":\"Admin\",\"estado\":\"ACTIVE\",\"isSystem\":true}"))
                .andExpect(status().isOk());
        mockMvc.perform(delete("/api/roles/2")).andExpect(status().isNoContent());
        when(service.obtener(99L)).thenThrow(new RolNoEncontradoException(99L));
        mockMvc.perform(get("/api/roles/99")).andExpect(status().isNotFound());
        when(service.crear(any())).thenThrow(new RolDuplicadoException("ADMIN"));
        mockMvc.perform(post("/api/roles").contentType(MediaType.APPLICATION_JSON).content(creacionValida()))
                .andExpect(status().isConflict());
    }

    private String creacionValida() { return "{\"codigo\":\"ADMIN\",\"nombre\":\"Administrador\"}"; }
    private RolResponse response() {
        LocalDateTime ahora = LocalDateTime.of(2026, 9, 2, 10, 0);
        return new RolResponse(2L, "ADMIN", "Administrador", null, RolEstado.ACTIVE, true, ahora, ahora);
    }
}

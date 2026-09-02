package dev.adriangabas.businessflow.usuario;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import dev.adriangabas.businessflow.rol.RolEstado;
import dev.adriangabas.businessflow.usuario.dto.UsuarioRolResponse;
import java.time.LocalDateTime;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(controllers = UsuarioRolController.class)
class UsuarioRolControllerTest {
    @Autowired MockMvc mockMvc;
    @MockitoBean UsuarioRolService service;

    @Test
    void asignaListaYQuitaRol() throws Exception {
        when(service.asignar(1L, 2L)).thenReturn(response());
        when(service.listar(1L)).thenReturn(List.of(response()));
        mockMvc.perform(post("/api/usuarios/1/roles/2")).andExpect(status().isCreated())
                .andExpect(header().string("Location", "/api/usuarios/1/roles/2"))
                .andExpect(jsonPath("$.codigo").value("ADMIN"));
        mockMvc.perform(get("/api/usuarios/1/roles")).andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(2));
        mockMvc.perform(delete("/api/usuarios/1/roles/2")).andExpect(status().isNoContent());
    }

    @Test
    void devuelveConflictoParaDuplicadaY404ParaRecursosNoValidos() throws Exception {
        when(service.asignar(1L, 2L)).thenThrow(new AsignacionRolDuplicadaException(1L, 2L));
        mockMvc.perform(post("/api/usuarios/1/roles/2")).andExpect(status().isConflict())
                .andExpect(jsonPath("$.status").value(409));
        when(service.listar(99L)).thenThrow(new UsuarioNoEncontradoException(99L));
        mockMvc.perform(get("/api/usuarios/99/roles")).andExpect(status().isNotFound());
        when(service.asignar(1L, 99L)).thenThrow(new dev.adriangabas.businessflow.rol.RolNoEncontradoException(99L));
        mockMvc.perform(post("/api/usuarios/1/roles/99")).andExpect(status().isNotFound());
    }

    private UsuarioRolResponse response() {
        return new UsuarioRolResponse(2L, "ADMIN", "Administrador", RolEstado.ACTIVE, true,
                LocalDateTime.of(2026, 9, 2, 10, 0));
    }
}

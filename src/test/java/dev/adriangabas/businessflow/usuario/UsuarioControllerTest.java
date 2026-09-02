package dev.adriangabas.businessflow.usuario;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import dev.adriangabas.businessflow.usuario.dto.ActualizarUsuarioRequest;
import dev.adriangabas.businessflow.usuario.dto.UsuarioResponse;
import java.time.LocalDateTime;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(controllers = UsuarioController.class)
class UsuarioControllerTest {
    @Autowired MockMvc mockMvc;
    @MockitoBean UsuarioService service;

    @Test
    void creaCon201SinExponerPasswordHash() throws Exception {
        when(service.crear(any())).thenReturn(response());
        mockMvc.perform(post("/api/usuarios").contentType(MediaType.APPLICATION_JSON).content(creacionValida()))
                .andExpect(status().isCreated()).andExpect(header().string("Location", "/api/usuarios/1"))
                .andExpect(jsonPath("$.email").value("persona@example.com"))
                .andExpect(jsonPath("$.passwordHash").doesNotExist());
        verify(service).crear(org.mockito.ArgumentMatchers.argThat(request ->
                request.email().equals("persona@example.com")));
    }

    @Test
    void validaEmailCamposObligatoriosYLongitudes() throws Exception {
        mockMvc.perform(post("/api/usuarios").contentType(MediaType.APPLICATION_JSON).content("{}"))
                .andExpect(status().isBadRequest()).andExpect(jsonPath("$.fieldErrors.nombre").exists())
                .andExpect(jsonPath("$.fieldErrors.email").exists())
                .andExpect(jsonPath("$.fieldErrors.passwordHash").exists());
        String invalida = """
                {"nombre":"%s","apellidos":"%s","email":"no-es-email","passwordHash":"%s"}
                """.formatted("X".repeat(101), "X".repeat(151), "X".repeat(256));
        mockMvc.perform(post("/api/usuarios").contentType(MediaType.APPLICATION_JSON).content(invalida))
                .andExpect(status().isBadRequest()).andExpect(jsonPath("$.fieldErrors.nombre").exists())
                .andExpect(jsonPath("$.fieldErrors.apellidos").exists())
                .andExpect(jsonPath("$.fieldErrors.email").exists())
                .andExpect(jsonPath("$.fieldErrors.passwordHash").exists());
    }

    @Test
    void exigeEstadoEnActualizacionPeroPermiteOmitirHash() throws Exception {
        String sinEstado = "{\"nombre\":\"Persona\",\"email\":\"persona@example.com\"}";
        mockMvc.perform(put("/api/usuarios/1").contentType(MediaType.APPLICATION_JSON).content(sinEstado))
                .andExpect(status().isBadRequest()).andExpect(jsonPath("$.fieldErrors.estado").exists());
        when(service.actualizar(eq(1L), any(ActualizarUsuarioRequest.class))).thenReturn(response());
        mockMvc.perform(put("/api/usuarios/1").contentType(MediaType.APPLICATION_JSON)
                        .content("{\"nombre\":\"Persona\",\"email\":\"persona@example.com\",\"estado\":\"ACTIVE\"}"))
                .andExpect(status().isOk()).andExpect(jsonPath("$.passwordHash").doesNotExist());
    }

    @Test
    void listaObtieneActualizaYElimina() throws Exception {
        when(service.listar()).thenReturn(List.of(response()));
        when(service.obtener(1L)).thenReturn(response());
        when(service.actualizar(eq(1L), any())).thenReturn(response());
        mockMvc.perform(get("/api/usuarios")).andExpect(status().isOk());
        mockMvc.perform(get("/api/usuarios/1")).andExpect(status().isOk());
        mockMvc.perform(put("/api/usuarios/1").contentType(MediaType.APPLICATION_JSON)
                .content("{\"nombre\":\"Persona\",\"email\":\"persona@example.com\",\"estado\":\"ACTIVE\"}"))
                .andExpect(status().isOk());
        mockMvc.perform(delete("/api/usuarios/1")).andExpect(status().isNoContent());
    }

    @Test
    void devuelve404Y409Consistentes() throws Exception {
        when(service.obtener(99L)).thenThrow(new UsuarioNoEncontradoException(99L));
        when(service.crear(any())).thenThrow(new UsuarioDuplicadoException("persona@example.com"));
        mockMvc.perform(get("/api/usuarios/99")).andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404));
        mockMvc.perform(post("/api/usuarios").contentType(MediaType.APPLICATION_JSON).content(creacionValida()))
                .andExpect(status().isConflict()).andExpect(jsonPath("$.status").value(409));
    }

    private String creacionValida() {
        return "{\"nombre\":\"Persona\",\"email\":\" persona@example.com \",\"passwordHash\":\"hash-opaco\"}";
    }

    private UsuarioResponse response() {
        LocalDateTime ahora = LocalDateTime.of(2026, 9, 2, 10, 0);
        return new UsuarioResponse(1L, "Persona", null, "persona@example.com", UsuarioEstado.ACTIVE, null, null,
                ahora, ahora);
    }
}

package dev.adriangabas.businessflow.cliente;

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

import dev.adriangabas.businessflow.cliente.dto.ActualizarClienteRequest;
import dev.adriangabas.businessflow.cliente.dto.ClienteResponse;
import java.time.LocalDateTime;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(controllers = ClienteController.class)
class ClienteControllerTest {

    @Autowired MockMvc mockMvc;
    @MockitoBean ClienteService service;

    @Test
    void creaClienteYDevuelve201ConLocation() throws Exception {
        when(service.crear(any())).thenReturn(response());

        mockMvc.perform(post("/api/clientes").contentType(MediaType.APPLICATION_JSON).content(peticionValida()))
                .andExpect(status().isCreated())
                .andExpect(header().string("Location", "/api/clientes/1"))
                .andExpect(jsonPath("$.codigo").value("CLI-1"));
    }

    @Test
    void validaCamposObligatorios() throws Exception {
        mockMvc.perform(post("/api/clientes").contentType(MediaType.APPLICATION_JSON).content("{}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.fieldErrors.codigo").exists())
                .andExpect(jsonPath("$.fieldErrors.tipoCliente").exists())
                .andExpect(jsonPath("$.fieldErrors.nombre").exists());
    }

    @Test
    void validaLongitudesDeTodosLosCamposLimitados() throws Exception {
        String json = """
                {"codigo":"%s","tipoCliente":"EMPRESA","nombre":"%s","nombreComercial":"%s",
                "identificacionFiscal":"%s","email":"%s@example.com","telefono":"%s","direccion":"%s",
                "codigoPostal":"%s","localidad":"%s","provincia":"%s","pais":"%s"}
                """.formatted("X".repeat(31), "X".repeat(201), "X".repeat(201), "X".repeat(51),
                        "x".repeat(250), "X".repeat(21), "X".repeat(256), "X".repeat(21), "X".repeat(101),
                        "X".repeat(101), "X".repeat(101));

        mockMvc.perform(post("/api/clientes").contentType(MediaType.APPLICATION_JSON).content(json))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.fieldErrors.codigo").exists())
                .andExpect(jsonPath("$.fieldErrors.nombre").exists())
                .andExpect(jsonPath("$.fieldErrors.nombreComercial").exists())
                .andExpect(jsonPath("$.fieldErrors.identificacionFiscal").exists())
                .andExpect(jsonPath("$.fieldErrors.email").exists())
                .andExpect(jsonPath("$.fieldErrors.telefono").exists())
                .andExpect(jsonPath("$.fieldErrors.direccion").exists())
                .andExpect(jsonPath("$.fieldErrors.codigoPostal").exists())
                .andExpect(jsonPath("$.fieldErrors.localidad").exists())
                .andExpect(jsonPath("$.fieldErrors.provincia").exists())
                .andExpect(jsonPath("$.fieldErrors.pais").exists());
    }

    @Test
    void aceptaEmailValidoYRechazaEmailInvalido() throws Exception {
        when(service.crear(any())).thenReturn(response());
        mockMvc.perform(post("/api/clientes").contentType(MediaType.APPLICATION_JSON).content(peticionValida()))
                .andExpect(status().isCreated());

        mockMvc.perform(post("/api/clientes").contentType(MediaType.APPLICATION_JSON)
                        .content(peticionValida().replace("cliente@example.com", "email-invalido")))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.fieldErrors.email").exists());
    }

    @Test
    void exigeEstadoEnActualizacion() throws Exception {
        String json = peticionValida().replace(",\"estado\":\"ACTIVO\"", "");

        mockMvc.perform(put("/api/clientes/1").contentType(MediaType.APPLICATION_JSON).content(json))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.fieldErrors.estado").exists());
    }

    @Test
    void listaObtieneYActualizaCliente() throws Exception {
        when(service.listar()).thenReturn(List.of(response()));
        when(service.obtener(1L)).thenReturn(response());
        when(service.actualizar(eq(1L), any(ActualizarClienteRequest.class))).thenReturn(response());

        mockMvc.perform(get("/api/clientes")).andExpect(status().isOk())
                .andExpect(jsonPath("$[0].codigo").value("CLI-1"));
        mockMvc.perform(get("/api/clientes/1")).andExpect(status().isOk())
                .andExpect(jsonPath("$.tipoCliente").value("EMPRESA"));
        mockMvc.perform(put("/api/clientes/1").contentType(MediaType.APPLICATION_JSON).content(peticionValida()))
                .andExpect(status().isOk());
    }

    @Test
    void eliminaClienteCon204() throws Exception {
        mockMvc.perform(delete("/api/clientes/1")).andExpect(status().isNoContent());
    }

    @Test
    void devuelveErrorConsistenteParaNoEncontradoYDuplicado() throws Exception {
        when(service.obtener(99L)).thenThrow(new ClienteNoEncontradoException(99L));
        when(service.crear(any())).thenThrow(new ClienteDuplicadoException("CLI-1"));

        mockMvc.perform(get("/api/clientes/99"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.path").value("/api/clientes/99"))
                .andExpect(jsonPath("$.message").exists());
        mockMvc.perform(post("/api/clientes").contentType(MediaType.APPLICATION_JSON).content(peticionValida()))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.status").value(409))
                .andExpect(jsonPath("$.error").value("Conflict"));
    }

    private String peticionValida() {
        return """
                {"codigo":"CLI-1","tipoCliente":"EMPRESA","nombre":"Cliente Uno",
                "email":"cliente@example.com","estado":"ACTIVO"}
                """;
    }

    private ClienteResponse response() {
        LocalDateTime ahora = LocalDateTime.of(2026, 8, 31, 10, 0);
        return new ClienteResponse(1L, "CLI-1", TipoCliente.EMPRESA, "Cliente Uno", null, null,
                "cliente@example.com", null, null, null, null, null, null, EstadoCliente.ACTIVO, null, ahora, ahora);
    }
}

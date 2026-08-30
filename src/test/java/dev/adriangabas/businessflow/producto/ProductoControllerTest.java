package dev.adriangabas.businessflow.producto;

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

import dev.adriangabas.businessflow.categoria.CategoriaNoEncontradaException;
import dev.adriangabas.businessflow.producto.dto.ActualizarProductoRequest;
import dev.adriangabas.businessflow.producto.dto.CategoriaProductoResumen;
import dev.adriangabas.businessflow.producto.dto.CrearProductoRequest;
import dev.adriangabas.businessflow.producto.dto.ProductoResponse;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(ProductoController.class)
class ProductoControllerTest {

    @Autowired MockMvc mockMvc;
    @MockitoBean ProductoService service;

    @Test
    void creaProducto() throws Exception {
        when(service.crear(any(CrearProductoRequest.class))).thenReturn(response());

        mockMvc.perform(post("/api/productos").contentType(MediaType.APPLICATION_JSON).content(peticionValida()))
                .andExpect(status().isCreated())
                .andExpect(header().string("Location", "/api/productos/1"))
                .andExpect(jsonPath("$.codigo").value("PROD-1"))
                .andExpect(jsonPath("$.precioVenta").value(12.34))
                .andExpect(jsonPath("$.categoria.codigo").value("ALIM"));
    }

    @Test
    void validaCamposObligatorios() throws Exception {
        mockMvc.perform(post("/api/productos").contentType(MediaType.APPLICATION_JSON).content("{}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.fieldErrors.codigo").exists())
                .andExpect(jsonPath("$.fieldErrors.nombre").exists())
                .andExpect(jsonPath("$.fieldErrors.categoriaId").exists())
                .andExpect(jsonPath("$.fieldErrors.precioVenta").exists())
                .andExpect(jsonPath("$.fieldErrors.precioCoste").exists())
                .andExpect(jsonPath("$.fieldErrors.stockMinimo").exists())
                .andExpect(jsonPath("$.fieldErrors.unidadMedida").exists());
    }

    @Test
    void rechazaPreciosYStockNegativos() throws Exception {
        String json = peticionValida().replace("12.34", "-0.01").replace("5.67", "-1.00")
                .replace("\"stockMinimo\":3", "\"stockMinimo\":-1");

        mockMvc.perform(post("/api/productos").contentType(MediaType.APPLICATION_JSON).content(json))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.fieldErrors.precioVenta").exists())
                .andExpect(jsonPath("$.fieldErrors.precioCoste").exists())
                .andExpect(jsonPath("$.fieldErrors.stockMinimo").exists());
    }

    @Test
    void rechazaUnidadDesconocida() throws Exception {
        String json = peticionValida().replace("UNIDAD", "DOCENA");

        mockMvc.perform(post("/api/productos").contentType(MediaType.APPLICATION_JSON).content(json))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400));
    }

    @Test
    void devuelveConflictoParaCodigoDuplicado() throws Exception {
        when(service.crear(any())).thenThrow(new ProductoDuplicadoException("PROD-1"));

        mockMvc.perform(post("/api/productos").contentType(MediaType.APPLICATION_JSON).content(peticionValida()))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.status").value(409));
    }

    @Test
    void devuelveNotFoundParaProductoOCategoria() throws Exception {
        when(service.obtener(99L)).thenThrow(new ProductoNoEncontradoException(99L));
        when(service.crear(any())).thenThrow(new CategoriaNoEncontradaException(99L));

        mockMvc.perform(get("/api/productos/99")).andExpect(status().isNotFound());
        mockMvc.perform(post("/api/productos").contentType(MediaType.APPLICATION_JSON).content(peticionValida()))
                .andExpect(status().isNotFound());
    }

    @Test
    void listaObtieneYActualizaProducto() throws Exception {
        when(service.listar()).thenReturn(List.of(response()));
        when(service.obtener(1L)).thenReturn(response());
        when(service.actualizar(eq(1L), any(ActualizarProductoRequest.class))).thenReturn(response());

        mockMvc.perform(get("/api/productos")).andExpect(status().isOk())
                .andExpect(jsonPath("$[0].codigo").value("PROD-1"));
        mockMvc.perform(get("/api/productos/1")).andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1));
        mockMvc.perform(put("/api/productos/1").contentType(MediaType.APPLICATION_JSON).content(peticionValida()))
                .andExpect(status().isOk());
    }

    @Test
    void eliminaProducto() throws Exception {
        mockMvc.perform(delete("/api/productos/1")).andExpect(status().isNoContent());
    }

    private String peticionValida() {
        return """
                {
                  "codigo":"PROD-1",
                  "nombre":"Producto",
                  "categoriaId":1,
                  "precioVenta":12.34,
                  "precioCoste":5.67,
                  "stockMinimo":3,
                  "unidadMedida":"UNIDAD",
                  "estado":"ACTIVO"
                }
                """;
    }

    private ProductoResponse response() {
        LocalDateTime ahora = LocalDateTime.of(2026, 8, 30, 10, 0);
        return new ProductoResponse(1L, "PROD-1", "Producto", null,
                new CategoriaProductoResumen(1L, "ALIM", "Alimentación"), new BigDecimal("12.34"),
                new BigDecimal("5.67"), 3, UnidadMedida.UNIDAD, null, EstadoProducto.ACTIVO, null, ahora, ahora);
    }
}

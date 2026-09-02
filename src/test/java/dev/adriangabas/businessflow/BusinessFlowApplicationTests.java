package dev.adriangabas.businessflow;

import dev.adriangabas.businessflow.categoria.CategoriaProductoRepository;
import dev.adriangabas.businessflow.cliente.ClienteRepository;
import dev.adriangabas.businessflow.producto.ProductoRepository;
import dev.adriangabas.businessflow.rol.RolRepository;
import dev.adriangabas.businessflow.usuario.UsuarioRepository;
import dev.adriangabas.businessflow.usuario.UsuarioRolRepository;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

@SpringBootTest(properties = {
        "spring.autoconfigure.exclude="
                + "org.springframework.boot.jdbc.autoconfigure.DataSourceAutoConfiguration,"
                + "org.springframework.boot.hibernate.autoconfigure.HibernateJpaAutoConfiguration"
})
class BusinessFlowApplicationTests {

    @MockitoBean
    private CategoriaProductoRepository categoriaProductoRepository;

    @MockitoBean
    private ClienteRepository clienteRepository;

    @MockitoBean
    private ProductoRepository productoRepository;

    @MockitoBean
    private UsuarioRepository usuarioRepository;

    @MockitoBean
    private RolRepository rolRepository;

    @MockitoBean
    private UsuarioRolRepository usuarioRolRepository;

    @Test
    void contextLoads() {
    }
}

package dev.adriangabas.businessflow;

import dev.adriangabas.businessflow.categoria.CategoriaProductoRepository;
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

    @Test
    void contextLoads() {
    }
}

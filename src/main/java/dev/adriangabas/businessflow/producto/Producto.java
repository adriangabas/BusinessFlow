package dev.adriangabas.businessflow.producto;

import dev.adriangabas.businessflow.categoria.CategoriaProducto;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "productos")
public class Producto {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(columnDefinition = "BIGINT UNSIGNED")
    private Long id;

    @Column(nullable = false, unique = true, length = 30)
    private String codigo;

    @Column(nullable = false, length = 200)
    private String nombre;

    @Column(columnDefinition = "TEXT")
    private String descripcion;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "categoria_producto_id", nullable = false)
    private CategoriaProducto categoria;

    @Column(name = "precio_venta", nullable = false, precision = 12, scale = 2)
    private BigDecimal precioVenta;

    @Column(name = "precio_coste", nullable = false, precision = 12, scale = 2)
    private BigDecimal precioCoste;

    @Column(name = "stock_minimo", nullable = false, columnDefinition = "INT UNSIGNED")
    private Integer stockMinimo;

    @Enumerated(EnumType.STRING)
    @Column(name = "unidad_medida", nullable = false,
            columnDefinition = "ENUM('UNIDAD','KG','LITRO','METRO','CAJA','PAQUETE')")
    private UnidadMedida unidadMedida;

    @Column(name = "imagen_url", length = 500)
    private String imagenUrl;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, columnDefinition = "ENUM('ACTIVO','INACTIVO')")
    private EstadoProducto estado;

    @Column(columnDefinition = "TEXT")
    private String observaciones;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    @Column(name = "deleted_at")
    private LocalDateTime deletedAt;

    protected Producto() {
    }

    public Producto(String codigo, String nombre, String descripcion, CategoriaProducto categoria,
            BigDecimal precioVenta, BigDecimal precioCoste, Integer stockMinimo, UnidadMedida unidadMedida,
            String imagenUrl, EstadoProducto estado, String observaciones) {
        actualizar(codigo, nombre, descripcion, categoria, precioVenta, precioCoste, stockMinimo, unidadMedida,
                imagenUrl, estado, observaciones);
    }

    @PrePersist
    void prePersist() {
        LocalDateTime ahora = LocalDateTime.now();
        createdAt = ahora;
        updatedAt = ahora;
    }

    @PreUpdate
    void preUpdate() {
        updatedAt = LocalDateTime.now();
    }

    public void actualizar(String codigo, String nombre, String descripcion, CategoriaProducto categoria,
            BigDecimal precioVenta, BigDecimal precioCoste, Integer stockMinimo, UnidadMedida unidadMedida,
            String imagenUrl, EstadoProducto estado, String observaciones) {
        this.codigo = codigo;
        this.nombre = nombre;
        this.descripcion = descripcion;
        this.categoria = categoria;
        this.precioVenta = precioVenta;
        this.precioCoste = precioCoste;
        this.stockMinimo = stockMinimo;
        this.unidadMedida = unidadMedida;
        this.imagenUrl = imagenUrl;
        this.estado = estado;
        this.observaciones = observaciones;
    }

    public void eliminar() {
        estado = EstadoProducto.INACTIVO;
        deletedAt = LocalDateTime.now();
    }

    public Long getId() { return id; }
    public String getCodigo() { return codigo; }
    public String getNombre() { return nombre; }
    public String getDescripcion() { return descripcion; }
    public CategoriaProducto getCategoria() { return categoria; }
    public BigDecimal getPrecioVenta() { return precioVenta; }
    public BigDecimal getPrecioCoste() { return precioCoste; }
    public Integer getStockMinimo() { return stockMinimo; }
    public UnidadMedida getUnidadMedida() { return unidadMedida; }
    public String getImagenUrl() { return imagenUrl; }
    public EstadoProducto getEstado() { return estado; }
    public String getObservaciones() { return observaciones; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public LocalDateTime getDeletedAt() { return deletedAt; }
}

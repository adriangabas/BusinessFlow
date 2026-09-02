package dev.adriangabas.businessflow.cliente;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import java.time.LocalDateTime;

@Entity
@Table(name = "clientes")
public class Cliente {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(columnDefinition = "BIGINT UNSIGNED")
    private Long id;

    @Column(nullable = false, unique = true, length = 30)
    private String codigo;

    @Enumerated(EnumType.STRING)
    @Column(name = "tipo_cliente", nullable = false, columnDefinition = "ENUM('EMPRESA','PARTICULAR')")
    private TipoCliente tipoCliente;

    @Column(nullable = false, length = 200)
    private String nombre;

    @Column(name = "nombre_comercial", length = 200)
    private String nombreComercial;

    @Column(name = "identificacion_fiscal", length = 50)
    private String identificacionFiscal;

    @Column(length = 254)
    private String email;

    @Column(length = 20)
    private String telefono;

    @Column(length = 255)
    private String direccion;

    @Column(name = "codigo_postal", length = 20)
    private String codigoPostal;

    @Column(length = 100)
    private String localidad;

    @Column(length = 100)
    private String provincia;

    @Column(length = 100)
    private String pais;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, columnDefinition = "ENUM('ACTIVO','INACTIVO')")
    private EstadoCliente estado;

    @Column(columnDefinition = "TEXT")
    private String observaciones;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    @Column(name = "deleted_at")
    private LocalDateTime deletedAt;

    protected Cliente() {
    }

    public Cliente(String codigo, TipoCliente tipoCliente, String nombre, String nombreComercial,
            String identificacionFiscal, String email, String telefono, String direccion, String codigoPostal,
            String localidad, String provincia, String pais, EstadoCliente estado, String observaciones) {
        actualizar(codigo, tipoCliente, nombre, nombreComercial, identificacionFiscal, email, telefono, direccion,
                codigoPostal, localidad, provincia, pais, estado, observaciones);
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

    public void actualizar(String codigo, TipoCliente tipoCliente, String nombre, String nombreComercial,
            String identificacionFiscal, String email, String telefono, String direccion, String codigoPostal,
            String localidad, String provincia, String pais, EstadoCliente estado, String observaciones) {
        this.codigo = codigo;
        this.tipoCliente = tipoCliente;
        this.nombre = nombre;
        this.nombreComercial = nombreComercial;
        this.identificacionFiscal = identificacionFiscal;
        this.email = email;
        this.telefono = telefono;
        this.direccion = direccion;
        this.codigoPostal = codigoPostal;
        this.localidad = localidad;
        this.provincia = provincia;
        this.pais = pais;
        this.estado = estado;
        this.observaciones = observaciones;
    }

    public void eliminar() {
        estado = EstadoCliente.INACTIVO;
        deletedAt = LocalDateTime.now();
    }

    public Long getId() { return id; }
    public String getCodigo() { return codigo; }
    public TipoCliente getTipoCliente() { return tipoCliente; }
    public String getNombre() { return nombre; }
    public String getNombreComercial() { return nombreComercial; }
    public String getIdentificacionFiscal() { return identificacionFiscal; }
    public String getEmail() { return email; }
    public String getTelefono() { return telefono; }
    public String getDireccion() { return direccion; }
    public String getCodigoPostal() { return codigoPostal; }
    public String getLocalidad() { return localidad; }
    public String getProvincia() { return provincia; }
    public String getPais() { return pais; }
    public EstadoCliente getEstado() { return estado; }
    public String getObservaciones() { return observaciones; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public LocalDateTime getDeletedAt() { return deletedAt; }
}

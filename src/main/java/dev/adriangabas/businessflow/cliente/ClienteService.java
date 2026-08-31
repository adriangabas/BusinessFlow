package dev.adriangabas.businessflow.cliente;

import dev.adriangabas.businessflow.cliente.dto.ActualizarClienteRequest;
import dev.adriangabas.businessflow.cliente.dto.ClienteResponse;
import dev.adriangabas.businessflow.cliente.dto.CrearClienteRequest;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class ClienteService {

    private final ClienteRepository repository;

    public ClienteService(ClienteRepository repository) {
        this.repository = repository;
    }

    @Transactional
    public ClienteResponse crear(CrearClienteRequest request) {
        String codigo = normalizar(request.codigo());
        comprobarCodigoDuplicado(codigo, null);
        EstadoCliente estado = request.estado() == null ? EstadoCliente.ACTIVO : request.estado();
        Cliente cliente = new Cliente(codigo, request.tipoCliente(), request.nombre().trim(), request.nombreComercial(),
                request.identificacionFiscal(), request.email(), request.telefono(), request.direccion(),
                request.codigoPostal(), request.localidad(), request.provincia(), request.pais(), estado,
                request.observaciones());
        return ClienteResponse.from(repository.save(cliente));
    }

    public List<ClienteResponse> listar() {
        return repository.findAllByDeletedAtIsNullOrderByIdAsc().stream().map(ClienteResponse::from).toList();
    }

    public ClienteResponse obtener(Long id) {
        return ClienteResponse.from(buscarNoEliminado(id));
    }

    @Transactional
    public ClienteResponse actualizar(Long id, ActualizarClienteRequest request) {
        Cliente cliente = buscarNoEliminado(id);
        String codigo = normalizar(request.codigo());
        comprobarCodigoDuplicado(codigo, id);
        cliente.actualizar(codigo, request.tipoCliente(), request.nombre().trim(), request.nombreComercial(),
                request.identificacionFiscal(), request.email(), request.telefono(), request.direccion(),
                request.codigoPostal(), request.localidad(), request.provincia(), request.pais(), request.estado(),
                request.observaciones());
        return ClienteResponse.from(repository.save(cliente));
    }

    @Transactional
    public void eliminar(Long id) {
        Cliente cliente = buscarNoEliminado(id);
        cliente.eliminar();
        repository.save(cliente);
    }

    private Cliente buscarNoEliminado(Long id) {
        return repository.findByIdAndDeletedAtIsNull(id).orElseThrow(() -> new ClienteNoEncontradoException(id));
    }

    private void comprobarCodigoDuplicado(String codigo, Long id) {
        boolean duplicado = id == null ? repository.existsByCodigo(codigo) : repository.existsByCodigoAndIdNot(codigo, id);
        if (duplicado) {
            throw new ClienteDuplicadoException(codigo);
        }
    }

    private String normalizar(String codigo) {
        return codigo.trim().toUpperCase();
    }
}

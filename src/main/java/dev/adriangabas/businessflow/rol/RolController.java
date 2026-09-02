package dev.adriangabas.businessflow.rol;

import dev.adriangabas.businessflow.rol.dto.ActualizarRolRequest;
import dev.adriangabas.businessflow.rol.dto.CrearRolRequest;
import dev.adriangabas.businessflow.rol.dto.RolResponse;
import jakarta.validation.Valid;
import java.net.URI;
import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/roles")
public class RolController {
    private final RolService service;

    public RolController(RolService service) { this.service = service; }

    @PostMapping
    public ResponseEntity<RolResponse> crear(@Valid @RequestBody CrearRolRequest request) {
        RolResponse creado = service.crear(request);
        return ResponseEntity.created(URI.create("/api/roles/" + creado.id())).body(creado);
    }

    @GetMapping
    public List<RolResponse> listar() { return service.listar(); }

    @GetMapping("/{id}")
    public RolResponse obtener(@PathVariable Long id) { return service.obtener(id); }

    @PutMapping("/{id}")
    public RolResponse actualizar(@PathVariable Long id, @Valid @RequestBody ActualizarRolRequest request) {
        return service.actualizar(id, request);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        service.eliminar(id);
        return ResponseEntity.noContent().build();
    }
}

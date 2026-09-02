package dev.adriangabas.businessflow.usuario;

import dev.adriangabas.businessflow.usuario.dto.UsuarioRolResponse;
import java.net.URI;
import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/usuarios/{usuarioId}/roles")
public class UsuarioRolController {
    private final UsuarioRolService service;

    public UsuarioRolController(UsuarioRolService service) { this.service = service; }

    @GetMapping
    public List<UsuarioRolResponse> listar(@PathVariable Long usuarioId) { return service.listar(usuarioId); }

    @PostMapping("/{rolId}")
    public ResponseEntity<UsuarioRolResponse> asignar(@PathVariable Long usuarioId, @PathVariable Long rolId) {
        UsuarioRolResponse asignado = service.asignar(usuarioId, rolId);
        return ResponseEntity.created(URI.create("/api/usuarios/" + usuarioId + "/roles/" + rolId)).body(asignado);
    }

    @DeleteMapping("/{rolId}")
    public ResponseEntity<Void> quitar(@PathVariable Long usuarioId, @PathVariable Long rolId) {
        service.quitar(usuarioId, rolId);
        return ResponseEntity.noContent().build();
    }
}

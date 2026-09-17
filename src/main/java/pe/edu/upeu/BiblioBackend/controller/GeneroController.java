package pe.edu.upeu.BiblioBackend.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pe.edu.upeu.BiblioBackend.dto.GeneroRequestDTO;
import pe.edu.upeu.BiblioBackend.dto.GeneroResponseDTO;
import pe.edu.upeu.BiblioBackend.service.service.GeneroService;

import java.util.List;

@RestController
@RequestMapping("/api/v1/generos")
@Tag(name = "generos-controller")
public class GeneroController {

    private final GeneroService generoService;

    public GeneroController(GeneroService generoService) {
        this.generoService = generoService;
    }

    @GetMapping
    @Operation(summary = "Listar generos")
    public ResponseEntity<List<GeneroResponseDTO>> getAll() {
        return ResponseEntity.ok(generoService.readAll());
    }

    @GetMapping("/{id}")
    @Operation(summary = "Obtener genero por ID")
    public ResponseEntity<GeneroResponseDTO> getById(@PathVariable Long id) {
        return ResponseEntity.ok(generoService.read(id));
    }

    @PostMapping
    @Operation(summary = "Crear nuevo genero")
    public ResponseEntity<GeneroResponseDTO> create(@Valid @RequestBody GeneroRequestDTO request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(generoService.create(request));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Actualizar genero existente")
    public ResponseEntity<GeneroResponseDTO> update(@PathVariable Long id, @Valid @RequestBody GeneroRequestDTO request) {
        return ResponseEntity.ok(generoService.update(id, request));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Eliminar genero")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        generoService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
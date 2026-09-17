package pe.edu.upeu.BiblioBackend.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pe.edu.upeu.BiblioBackend.dto.SocioRequestDTO;
import pe.edu.upeu.BiblioBackend.dto.SocioResponseDTO;
import pe.edu.upeu.BiblioBackend.service.service.SocioService;

import java.util.List;

@RestController
@RequestMapping("/api/v1/socios")
@Tag(name = "socios-controller")
public class SocioController {

    private final SocioService socioService;

    public SocioController(SocioService socioService) {
        this.socioService = socioService;
    }

    @GetMapping
    @Operation(summary = "Listar todos los socios")
    public ResponseEntity<List<SocioResponseDTO>> getAll() {
        return ResponseEntity.ok(socioService.readAll());
    }

    @GetMapping("/{id}")
    @Operation(summary = "Obtener socio por ID")
    public ResponseEntity<SocioResponseDTO> getById(@PathVariable Long id) {
        return ResponseEntity.ok(socioService.read(id));
    }

    @PostMapping
    @Operation(summary = "Registrar nuevo socio")
    public ResponseEntity<SocioResponseDTO> create(@Valid @RequestBody SocioRequestDTO request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(socioService.create(request));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Actualizar socio existente")
    public ResponseEntity<SocioResponseDTO> update(@PathVariable Long id, @Valid @RequestBody SocioRequestDTO request) {
        return ResponseEntity.ok(socioService.update(id, request));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Eliminar socio")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        socioService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
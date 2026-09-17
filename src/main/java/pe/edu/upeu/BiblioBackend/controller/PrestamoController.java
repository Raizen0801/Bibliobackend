package pe.edu.upeu.BiblioBackend.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pe.edu.upeu.BiblioBackend.dto.PrestamoRequestDTO;
import pe.edu.upeu.BiblioBackend.dto.PrestamoResponseDTO;
import pe.edu.upeu.BiblioBackend.enums.EstadoPrestamo;
import pe.edu.upeu.BiblioBackend.service.service.PrestamoService;
import java.time.LocalDate;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.List;

@RestController
@RequestMapping("/api/v1/prestamos")
@Tag(name = "prestamos-controller")
public class PrestamoController {

    private final PrestamoService prestamoService;

    public PrestamoController(PrestamoService prestamoService) {
        this.prestamoService = prestamoService;
    }

    @PostMapping
    @Operation(summary = "Registrar un nuevo prestamo con sus detalles")
    public ResponseEntity<PrestamoResponseDTO> registrar(@Valid @RequestBody PrestamoRequestDTO request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(prestamoService.registrarPrestamo(request));
    }

    @GetMapping
    @Operation(summary = "Listar prestamos")
    public ResponseEntity<List<PrestamoResponseDTO>> listar(
            @RequestParam(required = false) EstadoPrestamo estado) {
        if (estado != null) {
            return ResponseEntity.ok(prestamoService.listarPorEstado(estado));
        }
        return ResponseEntity.ok(prestamoService.listarTodos());
    }

    @GetMapping("/{id}")
    @Operation(summary = "Obtener detalle completo de un prestamo por ID")
    public ResponseEntity<PrestamoResponseDTO> obtenerPorId(@PathVariable Long id) {
        return ResponseEntity.ok(prestamoService.obtenerPorId(id));
    }

    @PatchMapping("/{id}/devolver")
    @Operation(summary = "Registrar la devolucion de un prestamo y reponer stock")
    public ResponseEntity<PrestamoResponseDTO> devolver(@PathVariable Long id) {
        return ResponseEntity.ok(prestamoService.devolverPrestamo(id));
    }

    @PatchMapping("/{id}/anular")
    @Operation(summary = "Anular un prestamo y reponer stock")
    public ResponseEntity<PrestamoResponseDTO> anular(@PathVariable Long id) {
        return ResponseEntity.ok(prestamoService.anularPrestamo(id));
    }
}
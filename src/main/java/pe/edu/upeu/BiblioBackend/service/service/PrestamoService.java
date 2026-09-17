package pe.edu.upeu.BiblioBackend.service.service;

import pe.edu.upeu.BiblioBackend.dto.PrestamoRequestDTO;
import pe.edu.upeu.BiblioBackend.dto.PrestamoResponseDTO;
import pe.edu.upeu.BiblioBackend.enums.EstadoPrestamo;

import java.time.LocalDate;
import java.util.List;

public interface PrestamoService {
    PrestamoResponseDTO registrarPrestamo(PrestamoRequestDTO request);
    PrestamoResponseDTO obtenerPorId(Long id);
    List<PrestamoResponseDTO> listarTodos();
    List<PrestamoResponseDTO> listarPorEstado(EstadoPrestamo estado);
    PrestamoResponseDTO devolverPrestamo(Long id);
    PrestamoResponseDTO anularPrestamo(Long id);
}
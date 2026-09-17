package pe.edu.upeu.BiblioBackend.service.service;

import pe.edu.upeu.BiblioBackend.dto.reporte.LibroMasPrestadoDTO;
import pe.edu.upeu.BiblioBackend.dto.reporte.PrestamoPorGeneroDTO;

import java.time.LocalDate;
import java.util.List;

public interface ReporteService {
    List<PrestamoPorGeneroDTO> obtenerPrestamosPorGenero(LocalDate desde, LocalDate hasta);
    List<LibroMasPrestadoDTO> obtenerLibrosMasPrestados(LocalDate desde, LocalDate hasta);
}
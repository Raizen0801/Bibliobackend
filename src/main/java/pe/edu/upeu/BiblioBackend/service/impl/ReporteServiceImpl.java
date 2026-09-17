package pe.edu.upeu.BiblioBackend.service.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pe.edu.upeu.BiblioBackend.dto.reporte.LibroMasPrestadoDTO;
import pe.edu.upeu.BiblioBackend.dto.reporte.PrestamoPorGeneroDTO;
import pe.edu.upeu.BiblioBackend.exception.ReglaNegocioException;
import pe.edu.upeu.BiblioBackend.repository.DetallePrestamoRepository;
import pe.edu.upeu.BiblioBackend.service.service.ReporteService;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class ReporteServiceImpl implements ReporteService {

    private static final Logger log = LoggerFactory.getLogger(ReporteServiceImpl.class);
    private final DetallePrestamoRepository detallePrestamoRepository;

    public ReporteServiceImpl(DetallePrestamoRepository detallePrestamoRepository) {
        this.detallePrestamoRepository = detallePrestamoRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public List<PrestamoPorGeneroDTO> obtenerPrestamosPorGenero(LocalDate desde, LocalDate hasta) {
        log.debug("Generando reporte de prestamos por genero. Desde: {}, Hasta: {}", desde, hasta);
        validarRangoFechas(desde, hasta);
        LocalDateTime fDesde = (desde != null) ? desde.atStartOfDay() : null;
        LocalDateTime fHasta = (hasta != null) ? hasta.atTime(23, 59, 59) : null;
        return detallePrestamoRepository.reportePorGenero(fDesde, fHasta);
    }

    @Override
    @Transactional(readOnly = true)
    public List<LibroMasPrestadoDTO> obtenerLibrosMasPrestados(LocalDate desde, LocalDate hasta) {
        log.debug("Generando reporte de libros mas prestados. Desde: {}, Hasta: {}", desde, hasta);
        validarRangoFechas(desde, hasta);
        LocalDateTime fDesde = (desde != null) ? desde.atStartOfDay() : null;
        LocalDateTime fHasta = (hasta != null) ? hasta.atTime(23, 59, 59) : null;
        return detallePrestamoRepository.reporteLibrosMasPrestados(fDesde, fHasta);
    }

    private void validarRangoFechas(LocalDate desde, LocalDate hasta) {
        if (desde != null && hasta != null && desde.isAfter(hasta)) {
            throw new ReglaNegocioException("La fecha inicial (desde) no puede ser posterior a la fecha final (hasta)");
        }
    }
}
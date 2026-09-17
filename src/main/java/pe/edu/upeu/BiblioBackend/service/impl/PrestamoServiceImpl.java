package pe.edu.upeu.BiblioBackend.service.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pe.edu.upeu.BiblioBackend.dto.DetallePrestamoRequestDTO;
import pe.edu.upeu.BiblioBackend.dto.DetallePrestamoResponseDTO;
import pe.edu.upeu.BiblioBackend.dto.PrestamoRequestDTO;
import pe.edu.upeu.BiblioBackend.dto.PrestamoResponseDTO;
import pe.edu.upeu.BiblioBackend.entity.DetallePrestamo;
import pe.edu.upeu.BiblioBackend.entity.Libro;
import pe.edu.upeu.BiblioBackend.entity.Prestamo;
import pe.edu.upeu.BiblioBackend.entity.Socio;
import pe.edu.upeu.BiblioBackend.enums.EstadoPrestamo;
import pe.edu.upeu.BiblioBackend.exception.RecursoNoEncontradoException;
import pe.edu.upeu.BiblioBackend.exception.ReglaNegocioException;
import pe.edu.upeu.BiblioBackend.repository.LibroRepository;
import pe.edu.upeu.BiblioBackend.repository.PrestamoRepository;
import pe.edu.upeu.BiblioBackend.repository.SocioRepository;
import pe.edu.upeu.BiblioBackend.service.service.PrestamoService;
import org.springframework.data.domain.Sort;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class PrestamoServiceImpl implements PrestamoService {

    private static final Logger log = LoggerFactory.getLogger(PrestamoServiceImpl.class);

    private final PrestamoRepository prestamoRepository;
    private final SocioRepository socioRepository;
    private final LibroRepository libroRepository;

    public PrestamoServiceImpl(PrestamoRepository prestamoRepository,
                               SocioRepository socioRepository,
                               LibroRepository libroRepository) {
        this.prestamoRepository = prestamoRepository;
        this.socioRepository = socioRepository;
        this.libroRepository = libroRepository;
    }

    @Override
    @Transactional
    public PrestamoResponseDTO registrarPrestamo(PrestamoRequestDTO request) {
        log.debug("Iniciando registro de prestamo para socio ID: {}", request.getSocioId());

        Socio socio = socioRepository.findById(request.getSocioId())
                .orElseThrow(() -> new RecursoNoEncontradoException("No existe el socio con ID: " + request.getSocioId()));

        if (Boolean.FALSE.equals(socio.getEstado())) {
            throw new ReglaNegocioException("El socio se encuentra inactivo y no puede realizar prestamos");
        }


        int totalLibrosSolicitados = request.getDetalles().stream()
                .mapToInt(DetallePrestamoRequestDTO::getCantidad)
                .sum();
        if (totalLibrosSolicitados > 3) {
            throw new ReglaNegocioException("Un prestamo no puede superar el maximo de 3 libros");
        }

        Prestamo prestamo = new Prestamo();
        prestamo.setSocio(socio);
        prestamo.setFecha(LocalDateTime.now());
        prestamo.setFechaDevolucionPrevista(LocalDate.now().plusDays(7));
        prestamo.setEstado(EstadoPrestamo.REGISTRADO);

        BigDecimal totalValorizado = BigDecimal.ZERO;

        for (DetallePrestamoRequestDTO detDto : request.getDetalles()) {
            Libro libro = libroRepository.findById(detDto.getLibroId())
                    .orElseThrow(() -> new RecursoNoEncontradoException("No existe el libro con ID: " + detDto.getLibroId()));

            if (Boolean.FALSE.equals(libro.getEstado())) {
                throw new ReglaNegocioException("El libro '" + libro.getTitulo() + "' esta inactivo");
            }

            if (libro.getStock() < detDto.getCantidad()) {
                throw new ReglaNegocioException("Stock insuficiente para el libro: " + libro.getTitulo() + ". Disponible: " + libro.getStock());
            }

            libro.setStock(libro.getStock() - detDto.getCantidad());
            libroRepository.save(libro);

            BigDecimal costoUnit = libro.getCostoReposicion();
            BigDecimal subtotal = costoUnit.multiply(BigDecimal.valueOf(detDto.getCantidad()));
            totalValorizado = totalValorizado.add(subtotal);

            DetallePrestamo detalle = new DetallePrestamo();
            detalle.setLibro(libro);
            detalle.setCantidad(detDto.getCantidad());
            detalle.setCostoUnitario(costoUnit);
            detalle.setSubtotal(subtotal);

            prestamo.agregarDetalle(detalle);
        }

        prestamo.setTotalValorizado(totalValorizado);
        Prestamo guardado = prestamoRepository.save(prestamo);
        log.info("Prestamo registrado exitosamente con ID: {}", guardado.getId());

        return mapToDTO(guardado);
    }

    @Override
    @Transactional(readOnly = true)
    public PrestamoResponseDTO obtenerPorId(Long id) {
        log.debug("Consultando prestamo con ID: {}", id);
        return prestamoRepository.findById(id)
                .map(this::mapToDTO)
                .orElseThrow(() -> new RecursoNoEncontradoException("No existe el prestamo con ID: " + id));
    }

    @Override
    @Transactional(readOnly = true)
    public List<PrestamoResponseDTO> listarTodos() {
        log.debug("Listando todos los prestamos");
        return prestamoRepository.findAll().stream()
                .map(this::mapToDTO)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<PrestamoResponseDTO> listarPorEstado(EstadoPrestamo estado) {
        log.debug("Listando prestamos con estado: {}", estado);
        return prestamoRepository.findAll().stream()
                .filter(p -> p.getEstado() == estado)
                .map(this::mapToDTO)
                .toList();
    }

    @Override
    @Transactional
    public PrestamoResponseDTO devolverPrestamo(Long id) {
        log.debug("Procesando devolucion del prestamo ID: {}", id);
        Prestamo prestamo = prestamoRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("No existe el prestamo con ID: " + id));

        if (prestamo.getEstado() != EstadoPrestamo.REGISTRADO) {
            throw new ReglaNegocioException("Solo se pueden devolver prestamos en estado REGISTRADO");
        }

        for (DetallePrestamo det : prestamo.getDetalles()) {
            Libro libro = det.getLibro();
            libro.setStock(libro.getStock() + det.getCantidad());
            libroRepository.save(libro);
        }

        prestamo.setEstado(EstadoPrestamo.DEVUELTO);
        prestamo.setFechaDevolucionReal(LocalDate.now());

        Prestamo actualizado = prestamoRepository.save(prestamo);
        log.info("Prestamo ID: {} devuelto correctamente", actualizado.getId());
        return mapToDTO(actualizado);
    }

    @Override
    @Transactional
    public PrestamoResponseDTO anularPrestamo(Long id) {
        log.debug("Procesando anulacion del prestamo ID: {}", id);
        Prestamo prestamo = prestamoRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("No existe el prestamo con ID: " + id));

        if (prestamo.getEstado() != EstadoPrestamo.REGISTRADO) {
            throw new ReglaNegocioException("Solo se pueden anular prestamos en estado REGISTRADO");
        }


        for (DetallePrestamo det : prestamo.getDetalles()) {
            Libro libro = det.getLibro();
            libro.setStock(libro.getStock() + det.getCantidad());
            libroRepository.save(libro);
        }

        prestamo.setEstado(EstadoPrestamo.ANULADO);

        Prestamo actualizado = prestamoRepository.save(prestamo);
        log.info("Prestamo ID: {} anulado correctamente", actualizado.getId());
        return mapToDTO(actualizado);
    }

    private PrestamoResponseDTO mapToDTO(Prestamo prestamo) {
        List<DetallePrestamoResponseDTO> detallesDTO = prestamo.getDetalles().stream()
                .map(d -> new DetallePrestamoResponseDTO(
                        d.getId(),
                        d.getLibro().getId(),
                        d.getLibro().getTitulo(),
                        d.getCantidad(),
                        d.getCostoUnitario(),
                        d.getSubtotal()
                ))
                .toList();

        return new PrestamoResponseDTO(
                prestamo.getId(),
                prestamo.getFecha(),
                prestamo.getFechaDevolucionPrevista(),
                prestamo.getFechaDevolucionReal(),
                prestamo.getTotalValorizado(),
                prestamo.getEstado(),
                prestamo.getSocio().getId(),
                prestamo.getSocio().getNombres() + " " + prestamo.getSocio().getApellidos(),
                prestamo.getSocio().getDni(),
                detallesDTO
        );
    }
}
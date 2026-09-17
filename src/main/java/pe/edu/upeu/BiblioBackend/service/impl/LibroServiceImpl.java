package pe.edu.upeu.BiblioBackend.service.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pe.edu.upeu.BiblioBackend.dto.LibroRequestDTO;
import pe.edu.upeu.BiblioBackend.dto.LibroResponseDTO;
import pe.edu.upeu.BiblioBackend.entity.Genero;
import pe.edu.upeu.BiblioBackend.entity.Libro;
import pe.edu.upeu.BiblioBackend.exception.RecursoNoEncontradoException;
import pe.edu.upeu.BiblioBackend.exception.ReglaNegocioException;
import pe.edu.upeu.BiblioBackend.repository.DetallePrestamoRepository;
import pe.edu.upeu.BiblioBackend.repository.GeneroRepository;
import pe.edu.upeu.BiblioBackend.repository.LibroRepository;
import pe.edu.upeu.BiblioBackend.service.service.LibroService;

import java.util.List;

@Service
public class LibroServiceImpl implements LibroService {

    private static final Logger log = LoggerFactory.getLogger(LibroServiceImpl.class);

    private final LibroRepository libroRepository;
    private final GeneroRepository generoRepository;
    private final DetallePrestamoRepository detallePrestamoRepository;

    public LibroServiceImpl(LibroRepository libroRepository,
                            GeneroRepository generoRepository,
                            DetallePrestamoRepository detallePrestamoRepository) {
        this.libroRepository = libroRepository;
        this.generoRepository = generoRepository;
        this.detallePrestamoRepository = detallePrestamoRepository;
    }

    @Override
    @Transactional
    public LibroResponseDTO create(LibroRequestDTO request) {
        log.debug("Iniciando creacion de libro con ISBN: {}", request.getIsbn());
        if (libroRepository.existsByIsbn(request.getIsbn().trim())) {
            throw new ReglaNegocioException("Ya existe un libro registrado con el ISBN: " + request.getIsbn());
        }

        Genero genero = generoRepository.findById(request.getGeneroId())
                .orElseThrow(() -> new RecursoNoEncontradoException("No existe un genero con ID: " + request.getGeneroId()));

        Libro libro = new Libro();
        libro.setTitulo(request.getTitulo().trim());
        libro.setAutor(request.getAutor().trim());
        libro.setIsbn(request.getIsbn().trim());
        libro.setCostoReposicion(request.getCostoReposicion());
        libro.setStock(request.getStock());
        libro.setEstado(request.getEstado());
        libro.setGenero(genero);

        Libro guardado = libroRepository.save(libro);
        log.info("Libro registrado exitosamente con ID: {}", guardado.getId());
        return mapToDTO(guardado);
    }

    @Override
    @Transactional
    public LibroResponseDTO update(Long id, LibroRequestDTO request) {
        log.debug("Actualizando libro ID: {}", id);
        Libro libro = libroRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("No existe un libro con ID: " + id));

        if (libroRepository.existsByIsbnAndIdNot(request.getIsbn().trim(), id)) {
            throw new ReglaNegocioException("Ya existe otro libro registrado con el ISBN: " + request.getIsbn());
        }

        Genero genero = generoRepository.findById(request.getGeneroId())
                .orElseThrow(() -> new RecursoNoEncontradoException("No existe un genero con ID: " + request.getGeneroId()));

        libro.setTitulo(request.getTitulo().trim());
        libro.setAutor(request.getAutor().trim());
        libro.setIsbn(request.getIsbn().trim());
        libro.setCostoReposicion(request.getCostoReposicion());
        libro.setStock(request.getStock());
        libro.setEstado(request.getEstado());
        libro.setGenero(genero);

        Libro actualizado = libroRepository.save(libro);
        log.info("Libro ID: {} actualizado exitosamente", actualizado.getId());
        return mapToDTO(actualizado);
    }

    @Override
    @Transactional(readOnly = true)
    public LibroResponseDTO read(Long id) {
        log.debug("Consultando libro ID: {}", id);
        return libroRepository.findById(id)
                .map(this::mapToDTO)
                .orElseThrow(() -> new RecursoNoEncontradoException("No existe un libro con ID: " + id));
    }

    @Override
    @Transactional
    public void delete(Long id) {
        log.debug("Eliminando libro ID: {}", id);
        if (!libroRepository.existsById(id)) {
            throw new RecursoNoEncontradoException("No existe un libro con ID: " + id);
        }
        if (detallePrestamoRepository.existsByLibroId(id)) {
            throw new ReglaNegocioException("No se puede eliminar el libro porque se encuentra registrado en prestamos");
        }
        libroRepository.deleteById(id);
        log.info("Libro ID: {} eliminado exitosamente", id);
    }

    @Override
    @Transactional(readOnly = true)
    public List<LibroResponseDTO> readAll() {
        log.debug("Consultando listado general de libros");
        return libroRepository.findAll().stream()
                .map(this::mapToDTO)
                .toList();
    }

    private LibroResponseDTO mapToDTO(Libro libro) {
        return new LibroResponseDTO(
                libro.getId(),
                libro.getTitulo(),
                libro.getAutor(),
                libro.getIsbn(),
                libro.getCostoReposicion(),
                libro.getStock(),
                libro.getEstado(),
                libro.getGenero().getId(),
                libro.getGenero().getNombre(),
                libro.getFechaCreacion(),
                libro.getFechaModificacion()
        );
    }
}
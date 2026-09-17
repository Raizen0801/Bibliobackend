package pe.edu.upeu.BiblioBackend.service.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pe.edu.upeu.BiblioBackend.dto.GeneroRequestDTO;
import pe.edu.upeu.BiblioBackend.dto.GeneroResponseDTO;
import pe.edu.upeu.BiblioBackend.entity.Genero;
import pe.edu.upeu.BiblioBackend.exception.RecursoNoEncontradoException;
import pe.edu.upeu.BiblioBackend.exception.ReglaNegocioException;
import pe.edu.upeu.BiblioBackend.repository.GeneroRepository;
import pe.edu.upeu.BiblioBackend.repository.LibroRepository;
import pe.edu.upeu.BiblioBackend.service.service.GeneroService;

import java.util.List;

@Service
public class GeneroServiceImpl implements GeneroService {

    private static final Logger log = LoggerFactory.getLogger(GeneroServiceImpl.class);

    private final GeneroRepository generoRepository;
    private final LibroRepository libroRepository;

    public GeneroServiceImpl(GeneroRepository generoRepository, LibroRepository libroRepository) {
        this.generoRepository = generoRepository;
        this.libroRepository = libroRepository;
    }

    @Override
    @Transactional
    public GeneroResponseDTO create(GeneroRequestDTO request) {
        log.debug("Iniciando creacion de genero con nombre: {}", request.getNombre());
        if (generoRepository.existsByNombreIgnoreCase(request.getNombre().trim())) {
            throw new ReglaNegocioException("Ya existe un genero con el nombre: " + request.getNombre());
        }

        Genero genero = new Genero();
        genero.setNombre(request.getNombre().trim());
        genero.setDescripcion(request.getDescripcion());
        genero.setEstado(request.getEstado());

        Genero guardado = generoRepository.save(genero);
        log.info("Genero creado exitosamente con ID: {}", guardado.getId());
        return mapToResponseDTO(guardado);
    }

    @Override
    @Transactional
    public GeneroResponseDTO update(Long id, GeneroRequestDTO request) {
        log.debug("Iniciando actualizacion de genero ID: {}", id);
        Genero genero = generoRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("No existe un genero con ID: " + id));

        if (generoRepository.existsByNombreIgnoreCaseAndIdNot(request.getNombre().trim(), id)) {
            throw new ReglaNegocioException("Ya existe otro genero con el nombre: " + request.getNombre());
        }

        genero.setNombre(request.getNombre().trim());
        genero.setDescripcion(request.getDescripcion());
        genero.setEstado(request.getEstado());

        Genero actualizado = generoRepository.save(genero);
        log.info("Genero ID: {} actualizado correctamente", actualizado.getId());
        return mapToResponseDTO(actualizado);
    }

    @Override
    @Transactional(readOnly = true)
    public GeneroResponseDTO read(Long id) {
        log.debug("Consultando genero ID: {}", id);
        return generoRepository.findById(id)
                .map(this::mapToResponseDTO)
                .orElseThrow(() -> new RecursoNoEncontradoException("No existe un genero con ID: " + id));
    }

    @Override
    @Transactional
    public void delete(Long id) {
        log.debug("Iniciando eliminacion de genero ID: {}", id);
        if (!generoRepository.existsById(id)) {
            throw new RecursoNoEncontradoException("No existe un genero con ID: " + id);
        }
        if (libroRepository.existsByGeneroId(id)) {
            throw new ReglaNegocioException("No se puede eliminar el genero porque tiene libros asociados");
        }
        generoRepository.deleteById(id);
        log.info("Genero ID: {} eliminado correctamente", id);
    }

    @Override
    @Transactional(readOnly = true)
    public List<GeneroResponseDTO> readAll() {
        log.debug("Consultando todos los generos");
        return generoRepository.findAll().stream()
                .map(this::mapToResponseDTO)
                .toList();
    }

    private GeneroResponseDTO mapToResponseDTO(Genero genero) {
        return new GeneroResponseDTO(
                genero.getId(),
                genero.getNombre(),
                genero.getDescripcion(),
                genero.getEstado(),
                genero.getFechaCreacion(),
                genero.getFechaModificacion()
        );
    }
}
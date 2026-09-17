package pe.edu.upeu.BiblioBackend.service.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pe.edu.upeu.BiblioBackend.dto.SocioRequestDTO;
import pe.edu.upeu.BiblioBackend.dto.SocioResponseDTO;
import pe.edu.upeu.BiblioBackend.entity.Socio;
import pe.edu.upeu.BiblioBackend.exception.RecursoNoEncontradoException;
import pe.edu.upeu.BiblioBackend.exception.ReglaNegocioException;
import pe.edu.upeu.BiblioBackend.repository.SocioRepository;
import pe.edu.upeu.BiblioBackend.service.service.SocioService;

import java.util.List;

@Service
public class SocioServiceImpl implements SocioService {

    private static final Logger log = LoggerFactory.getLogger(SocioServiceImpl.class);

    private final SocioRepository socioRepository;

    public SocioServiceImpl(SocioRepository socioRepository) {
        this.socioRepository = socioRepository;
    }

    @Override
    @Transactional
    public SocioResponseDTO create(SocioRequestDTO request) {
        log.debug("Creando socio con DNI: {}", request.getDni());
        if (socioRepository.existsByDni(request.getDni().trim())) {
            throw new ReglaNegocioException("Ya existe un socio registrado con el DNI: " + request.getDni());
        }

        Socio socio = new Socio();
        socio.setDni(request.getDni().trim());
        socio.setNombres(request.getNombres().trim());
        socio.setApellidos(request.getApellidos().trim());
        socio.setEmail(request.getEmail().trim());
        socio.setTelefono(request.getTelefono());
        socio.setDireccion(request.getDireccion());
        socio.setEstado(request.getEstado());

        Socio guardado = socioRepository.save(socio);
        log.info("Socio creado exitosamente con ID: {}", guardado.getId());
        return mapToDTO(guardado);
    }

    @Override
    @Transactional
    public SocioResponseDTO update(Long id, SocioRequestDTO request) {
        log.debug("Actualizando socio ID: {}", id);
        Socio socio = socioRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("No existe un socio con ID: " + id));

        if (socioRepository.existsByDniAndIdNot(request.getDni().trim(), id)) {
            throw new ReglaNegocioException("Ya existe otro socio registrado con el DNI: " + request.getDni());
        }

        socio.setDni(request.getDni().trim());
        socio.setNombres(request.getNombres().trim());
        socio.setApellidos(request.getApellidos().trim());
        socio.setEmail(request.getEmail().trim());
        socio.setTelefono(request.getTelefono());
        socio.setDireccion(request.getDireccion());
        socio.setEstado(request.getEstado());

        Socio actualizado = socioRepository.save(socio);
        log.info("Socio ID: {} actualizado correctamente", actualizado.getId());
        return mapToDTO(actualizado);
    }

    @Override
    @Transactional(readOnly = true)
    public SocioResponseDTO read(Long id) {
        log.debug("Consultando socio ID: {}", id);
        return socioRepository.findById(id)
                .map(this::mapToDTO)
                .orElseThrow(() -> new RecursoNoEncontradoException("No existe un socio con ID: " + id));
    }

    @Override
    @Transactional
    public void delete(Long id) {
        log.debug("Eliminando socio ID: {}", id);
        if (!socioRepository.existsById(id)) {
            throw new RecursoNoEncontradoException("No existe un socio con ID: " + id);
        }
        socioRepository.deleteById(id);
        log.info("Socio ID: {} eliminado correctamente", id);
    }

    @Override
    @Transactional(readOnly = true)
    public List<SocioResponseDTO> readAll() {
        log.debug("Listando todos los socios");
        return socioRepository.findAll().stream()
                .map(this::mapToDTO)
                .toList();
    }

    private SocioResponseDTO mapToDTO(Socio socio) {
        return new SocioResponseDTO(
                socio.getId(),
                socio.getDni(),
                socio.getNombres(),
                socio.getApellidos(),
                socio.getEmail(),
                socio.getTelefono(),
                socio.getDireccion(),
                socio.getEstado(),
                socio.getFechaCreacion(),
                socio.getFechaModificacion()
        );
    }
}
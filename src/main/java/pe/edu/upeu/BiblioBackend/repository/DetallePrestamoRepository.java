package pe.edu.upeu.BiblioBackend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import pe.edu.upeu.BiblioBackend.dto.reporte.LibroMasPrestadoDTO;
import pe.edu.upeu.BiblioBackend.dto.reporte.PrestamoPorGeneroDTO;
import pe.edu.upeu.BiblioBackend.entity.DetallePrestamo;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface DetallePrestamoRepository extends JpaRepository<DetallePrestamo, Long> {

    boolean existsByLibroId(Long libroId);

    @Query("""
        select new pe.edu.upeu.BiblioBackend.dto.reporte.PrestamoPorGeneroDTO(
            g.id,
            g.nombre,
            sum(cast(d.cantidad as long)),
            sum(d.subtotal)
        )
        from DetallePrestamo d
        join d.prestamo p
        join d.libro l
        join l.genero g
        where p.estado in (pe.edu.upeu.BiblioBackend.enums.EstadoPrestamo.REGISTRADO, pe.edu.upeu.BiblioBackend.enums.EstadoPrestamo.DEVUELTO)
          and (:desde is null or p.fecha >= :desde)
          and (:hasta is null or p.fecha <= :hasta)
        group by g.id, g.nombre
        order by sum(cast(d.cantidad as long)) desc
    """)
    List<PrestamoPorGeneroDTO> reportePorGenero(
            @Param("desde") LocalDateTime desde,
            @Param("hasta") LocalDateTime hasta
    );

    @Query("""
        select new pe.edu.upeu.BiblioBackend.dto.reporte.LibroMasPrestadoDTO(
            l.id,
            l.titulo,
            g.nombre,
            sum(cast(d.cantidad as long)),
            sum(d.subtotal)
        )
        from DetallePrestamo d
        join d.prestamo p
        join d.libro l
        join l.genero g
        where p.estado in (pe.edu.upeu.BiblioBackend.enums.EstadoPrestamo.REGISTRADO, pe.edu.upeu.BiblioBackend.enums.EstadoPrestamo.DEVUELTO)
          and (:desde is null or p.fecha >= :desde)
          and (:hasta is null or p.fecha <= :hasta)
        group by l.id, l.titulo, g.nombre
        order by sum(cast(d.cantidad as long)) desc
    """)
    List<LibroMasPrestadoDTO> reporteLibrosMasPrestados(
            @Param("desde") LocalDateTime desde,
            @Param("hasta") LocalDateTime hasta
    );
}
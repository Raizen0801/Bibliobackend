package pe.edu.upeu.BiblioBackend.repository;

import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import pe.edu.upeu.BiblioBackend.entity.Prestamo;
import pe.edu.upeu.BiblioBackend.enums.EstadoPrestamo;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface PrestamoRepository extends JpaRepository<Prestamo, Long> {

    @Query("""
        select distinct p from Prestamo p
        left join fetch p.socio s
        left join fetch p.detalles d
        left join fetch d.libro l
        where (:socioId is null or s.id = :socioId)
          and (:estado is null or p.estado = :estado)
          and (:desde is null or p.fecha >= :desde)
          and (:hasta is null or p.fecha <= :hasta)
    """)
    List<Prestamo> buscarConFiltros(
            @Param("socioId") Long socioId,
            @Param("estado") EstadoPrestamo estado,
            @Param("desde") LocalDateTime desde,
            @Param("hasta") LocalDateTime hasta,
            Sort sort
    );
}
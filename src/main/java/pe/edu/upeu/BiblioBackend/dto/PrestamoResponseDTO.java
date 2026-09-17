package pe.edu.upeu.BiblioBackend.dto;

import lombok.*;
import pe.edu.upeu.BiblioBackend.enums.EstadoPrestamo;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PrestamoResponseDTO {
    private Long id;
    private LocalDateTime fecha;
    private LocalDate fechaDevolucionPrevista;
    private LocalDate fechaDevolucionReal;
    private BigDecimal totalValorizado;
    private EstadoPrestamo estado;
    private Long socioId;
    private String socioNombreCompleto;
    private String socioDni;
    private List<DetallePrestamoResponseDTO> detalles;
}
package pe.edu.upeu.BiblioBackend.dto;

import lombok.*;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class DetallePrestamoResponseDTO {
    private Long id;
    private Long libroId;
    private String libroTitulo;
    private Integer cantidad;
    private BigDecimal costoUnitario;
    private BigDecimal subtotal;
}
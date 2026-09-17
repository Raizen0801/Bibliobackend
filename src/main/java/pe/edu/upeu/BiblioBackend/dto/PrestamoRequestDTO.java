package pe.edu.upeu.BiblioBackend.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PrestamoRequestDTO {

    @NotNull(message = "El ID del socio es obligatorio")
    private Long socioId;

    @NotEmpty(message = "El prestamo debe contener al menos un detalle de libro")
    @Valid
    private List<DetallePrestamoRequestDTO> detalles;
}
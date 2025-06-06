
package com.ampuero.msvc.boleta.dtos;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import java.time.LocalDateTime;
import java.util.Date;

// dtos/BoletaDTO.java
@Data
@NoArgsConstructor
@AllArgsConstructor
public class BoletaDTO {
    @NotBlank(message = "La descripción no puede estar vacía")
    private String descripcionBoleta;

    @NotNull(message = "El ID del clientes es obligatorio")
    private Long idClientePojo;
}

// dtos/BoletaResponseDTO.java


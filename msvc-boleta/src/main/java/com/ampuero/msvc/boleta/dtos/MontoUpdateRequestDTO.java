package com.ampuero.msvc.boleta.dtos;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MontoUpdateRequestDTO {
    @NotNull(message = "El monto no puede ser nulo")
    @Positive(message = "El monto debe ser mayor a 0")
    private Double monto;
}
package com.ampuero.msvc.boleta.dtos;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MontoUpdateRequestDTO {
    @NotNull
    private Double monto;
}
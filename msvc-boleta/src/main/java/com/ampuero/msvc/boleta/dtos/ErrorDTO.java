package com.ampuero.msvc.boleta.dtos;

import lombok.Data;

import java.time.LocalDate;
import java.util.Map;

@Data
public class ErrorDTO {
    private int status;
    private LocalDate localDate;
    private Map<String, String> errors;
}


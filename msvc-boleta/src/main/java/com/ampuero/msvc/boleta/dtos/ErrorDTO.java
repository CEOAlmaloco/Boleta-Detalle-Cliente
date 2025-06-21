package com.ampuero.msvc.boleta.dtos;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDate;
import java.util.Map;

@Data
@Schema(description = "Informacion de Error cuando algo es invalido")
public class ErrorDTO {
    private int status;
    private LocalDate localDate;
    private Map<String, String> errors;

    @Override
    public String toString(){
        return "{" +
                "status="+status+
                ",date=" +localDate+
                "errors= "+errors+
                "}";
    }
}


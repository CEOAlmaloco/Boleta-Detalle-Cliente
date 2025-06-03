package com.ampuero.msvc.boleta.dtos;

import lombok.Data;
import java.util.Date;
import java.util.Map;

@Data
public class ErrorDTO {
    private int status;
    private Date date;
    private Map<String, String> errors;
}


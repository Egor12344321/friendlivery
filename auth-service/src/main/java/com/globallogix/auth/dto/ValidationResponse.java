package com.globallogix.auth.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ValidationResponse {
    private boolean valid;
    private String message;
}

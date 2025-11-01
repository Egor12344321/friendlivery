package com.globallogix.auth.dto;


import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class HealthResponse {
    private String status;
    private String service;
    private LocalDateTime timeStamp;
    private String version;
}

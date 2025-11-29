package com.globallogix.auth.dto.response.authorization;

import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class UpdateTokens {
    private String refreshToken;
    private String accessToken;
    private String message;
}

package com.globallogix.auth.dto.request;

import jakarta.validation.constraints.NotBlank;
import org.hibernate.validator.constraints.URL;

public record DocumentVerificationRequest(
        @NotBlank @URL
        String documentPageUrl,
        @NotBlank @URL
        String photoWithPassportUrl
){
}

package com.globallogix.auth.dto.request;

import jakarta.validation.constraints.NotBlank;
import org.hibernate.validator.constraints.URL;

public class DocumentVerificationRequest{
    @NotBlank @URL
    private String documentPageUrl;

    @NotBlank @URL
    private String photoWithPassportUrl;
}

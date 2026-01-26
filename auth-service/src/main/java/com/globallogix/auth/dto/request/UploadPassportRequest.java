package com.globallogix.auth.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record UploadPassportRequest(
        @NotNull
        @NotBlank(message = "Серия паспорта обязательна к заполнению")
        @Size(max = 4, min = 4, message = "Серия паспорта должна состоять из 4 символов")
        String series,

        @NotNull
        @NotBlank(message = "Номер паспорта обязателен к заполнению")
        @Size(max = 6, min = 6, message = "Номер паспорта должен состоять из 6 символов")
        String number
) {
}

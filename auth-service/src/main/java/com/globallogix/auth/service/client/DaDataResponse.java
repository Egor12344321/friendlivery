package com.globallogix.auth.service.client;

public record DaDataResponse(
        String source,
        String series,
        String number,
        Integer qc
) {
}

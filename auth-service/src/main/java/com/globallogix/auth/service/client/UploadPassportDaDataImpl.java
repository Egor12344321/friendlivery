package com.globallogix.auth.service.client;

import com.globallogix.auth.dto.request.UploadPassportRequest;
import com.globallogix.auth.dto.response.PassportVerificationResponse;
import com.globallogix.auth.exception.InternalClientException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeoutException;


@Slf4j
@RequiredArgsConstructor
@Service
public class UploadPassportDaDataImpl implements UploadPassportService{

    private final String DADATA_CLIENT_URL = "https://cleaner.dadata.ru/api/v1/clean/passport";
    private final RestTemplate restTemplate;

    @Value("${dadata.api.key}")
    private String daDataApiKey;

    @Value("${dadata.secret.key}")
    private String secretKey;

    public PassportVerificationResponse uploadPassportData(UploadPassportRequest request, String username) {

        log.info("Started sending passport data to DaData service for user: {}", username);

        DaDataRequest daDataRequest = mapFromUserRequestToDaDataRequest(request);
        ResponseEntity<DaDataResponse> response = sendPassportDataToDaData(daDataRequest);
        if (response.getStatusCode().is5xxServerError()) {
            throw new InternalClientException("Ошибка при проверке паспорта");
        }
        if (response.getBody() == null) throw new InternalClientException("Ошибка при проверке паспорта");

        return handleClientResponse(response.getBody());
    }

    private ResponseEntity<DaDataResponse> sendPassportDataToDaData(DaDataRequest daDataRequest){
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setAccept(List.of(MediaType.APPLICATION_JSON));
        headers.set("Authorization", "Token " + daDataApiKey);
        headers.set("X-Secret", secretKey);

        HttpEntity<DaDataRequest> requestEntity = new HttpEntity<>(daDataRequest, headers);

        return restTemplate.exchange(
                DADATA_CLIENT_URL,
                HttpMethod.POST,
                requestEntity,
                DaDataResponse.class);
    }

    private DaDataRequest mapFromUserRequestToDaDataRequest(UploadPassportRequest request){
        String passportData = request.series() + " " + request.number();
        return new DaDataRequest(passportData);
    }

    private PassportVerificationResponse handleClientResponse(DaDataResponse response){
        Integer qc = response.qc();
        return switch (qc) {
            case 0 -> new PassportVerificationResponse(true, "Паспорт прошел верификацию");
            case 10 -> new PassportVerificationResponse(false, "Паспорт недействительный");
            case 1 -> new PassportVerificationResponse(false, "Неверный формат введенных данных");
            default -> new PassportVerificationResponse(false, "Паспорт не прошел верификацию");
        };
    }
}

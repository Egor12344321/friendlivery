package com.globallogix.auth.service.client;

import com.globallogix.auth.dto.request.UploadPassportRequest;
import com.globallogix.auth.dto.response.PassportVerificationResponse;
import com.globallogix.auth.entity.User;
import com.globallogix.auth.entity.UserDocuments;
import com.globallogix.auth.entity.enums.VerificationDocumentsStatus;
import com.globallogix.auth.exception.InternalClientException;
import com.globallogix.auth.repository.UserDocumentRepository;
import com.globallogix.auth.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeoutException;


@Slf4j
@RequiredArgsConstructor
@Service
@Transactional
public class UploadPassportDaDataImpl implements UploadPassportService {

    private final String DADATA_CLIENT_URL = "https://cleaner.dadata.ru/api/v1/clean/passport";
    private final RestTemplate restTemplate;
    private final UserRepository userRepository;
    private final UserDocumentRepository userDocumentRepository;

    @Value("${dadata.api.key}")
    private String daDataApiKey;

    @Value("${dadata.secret.key}")
    private String secretKey;

    @Override
    public PassportVerificationResponse uploadPassportData(UploadPassportRequest request, String username) {
        log.info("Started sending passport data to DaData service for user: {}", username);

        String passportData = mapFromUserRequestToDaDataRequest(request);
        ResponseEntity<DaDataResponse[]> response = sendPassportDataToDaData(passportData);

        if (!response.getStatusCode().is2xxSuccessful()) {
            log.error("Ошибка проверки паспорта для пользователя: {}", username);
            throw new InternalClientException("Ошибка при проверке паспорта: " + response.getStatusCode());
        }

        if (response.getBody() == null || response.getBody().length == 0) {
            log.error("Пустой ответ от DaData для пользователя: {}", username);
            throw new InternalClientException("Пустой ответ от сервиса проверки паспорта");
        }

        log.info("Получен response для username: {}", username);
        return handleClientResponse(response.getBody()[0], username);
    }

    private ResponseEntity<DaDataResponse[]> sendPassportDataToDaData(String passportData) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setAccept(List.of(MediaType.APPLICATION_JSON));
        headers.set("Authorization", "Token " + daDataApiKey);
        headers.set("X-Secret", secretKey);

        List<String> requestBody = List.of(passportData);
        HttpEntity<List<String>> requestEntity = new HttpEntity<>(requestBody, headers);

        try {
            return restTemplate.exchange(
                    DADATA_CLIENT_URL,
                    HttpMethod.POST,
                    requestEntity,
                    DaDataResponse[].class);
        } catch (Exception e) {
            log.error("Ошибка при вызове DaData: {}", e.getMessage(), e);
            throw new InternalClientException("Ошибка соединения с сервисом проверки паспорта");
        }
    }

    private String mapFromUserRequestToDaDataRequest(UploadPassportRequest request) {
        return request.series() + " " + request.number();
    }

    private PassportVerificationResponse handleClientResponse(DaDataResponse response, String username) {
        Integer qc = response.qc();

        return switch (qc) {
            case 0 -> handleVerifiedPassport(response, username);
            case 10 -> handleInvalidPassport(response, username);
            case 1 -> new PassportVerificationResponse(false, "Неверный формат введенных данных");
            default -> handleUnknownStatus(response, username);
        };
    }

    private PassportVerificationResponse handleVerifiedPassport(DaDataResponse response, String username) {
        User user = userRepository.findByUsernameWithDocuments(username)
                .orElseThrow(() -> new UsernameNotFoundException("Пользователь не найден: " + username));

        user.setVerificationStatus(VerificationDocumentsStatus.VERIFIED);

        UserDocuments documents = getOrCreateUserDocuments(user);
        documents.setDocumentsVerificationStatus(VerificationDocumentsStatus.VERIFIED);
        updatePassportData(response, documents);

        log.info("Паспорт верифицирован для пользователя: {}", username);
        return new PassportVerificationResponse(true, "Паспорт прошел верификацию");
    }

    private PassportVerificationResponse handleInvalidPassport(DaDataResponse response, String username) {
        User user = userRepository.findByUsernameWithDocuments(username)
                .orElseThrow(() -> new UsernameNotFoundException("Пользователь не найден: " + username));

        user.setVerificationStatus(VerificationDocumentsStatus.CANCELLED);

        UserDocuments documents = getOrCreateUserDocuments(user);
        documents.setDocumentsVerificationStatus(VerificationDocumentsStatus.CANCELLED);
        updatePassportData(response, documents);

        log.warn("Недействительный паспорт для пользователя: {}", username);
        return new PassportVerificationResponse(false, "Паспорт недействительный, данные дополнительно будут проверяться администраторами");
    }

    private PassportVerificationResponse handleUnknownStatus(DaDataResponse response, String username) {
        User user = userRepository.findByUsernameWithDocuments(username)
                .orElseThrow(() -> new UsernameNotFoundException("Пользователь не найден: " + username));

        user.setVerificationStatus(VerificationDocumentsStatus.NOT_VERIFIED);

        UserDocuments documents = getOrCreateUserDocuments(user);
        documents.setDocumentsVerificationStatus(VerificationDocumentsStatus.NOT_VERIFIED);
        updatePassportData(response, documents);

        log.warn("Неизвестный статус паспорта для пользователя: {}", username);
        return new PassportVerificationResponse(false, "Паспорт не прошел верификацию");
    }

    private UserDocuments getOrCreateUserDocuments(User user) {
        UserDocuments documents = user.getDocuments();

        if (documents == null) {
            documents = UserDocuments.builder()
                    .user(user)
                    .documentsVerificationStatus(VerificationDocumentsStatus.NOT_VERIFIED)
                    .build();
            user.setDocuments(documents);
        }

        return documents;
    }

    private void updatePassportData(DaDataResponse response, UserDocuments documents) {
        documents.setPassportSeries(response.series());
        documents.setPassportNumber(response.number());
    }
}
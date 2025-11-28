package com.globallogix.auth.service;


import com.globallogix.auth.repository.UserDocumentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class KycService {
    private final UserDocumentRepository userDocumentRepository;


}

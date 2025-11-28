package com.globallogix.auth.service;


import com.globallogix.auth.entity.User;
import com.globallogix.auth.entity.enums.VerificationDocumentsStatus;
import com.globallogix.auth.exception.UserNotFoundException;
import com.globallogix.auth.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class AdminService {
    private final UserRepository userRepository;

    public void approveVerification(Long userId){
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("Пользователь с таким id: " + userId + " - не найден"));
        user.setVerificationStatus(VerificationDocumentsStatus.VERIFIED);
        userRepository.save(user);
    }

    public void cancelVerification(Long userId){
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("Пользователь с таким id: " + userId + " - не найден"));
        user.setVerificationStatus(VerificationDocumentsStatus.CANCELLED);
        userRepository.save(user);
    }
}

package com.globallogix.auth.service.admin_abilities;


import com.globallogix.auth.entity.User;
import com.globallogix.auth.entity.enums.VerificationDocumentsStatus;
import com.globallogix.auth.exception.UserNotFoundException;
import com.globallogix.auth.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class AdminVerificationService {
    private final UserRepository userRepository;

    @Transactional
    public void approveVerification(Long userId){
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("Пользователь с таким id: " + userId + " - не найден"));
        user.setVerificationStatus(VerificationDocumentsStatus.VERIFIED);
        userRepository.save(user);
    }

    @Transactional
    public void cancelVerification(Long userId){
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("Пользователь с таким id: " + userId + " - не найден"));
        user.setVerificationStatus(VerificationDocumentsStatus.CANCELLED);
        userRepository.save(user);
    }
}

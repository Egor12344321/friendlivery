package com.globallogix.auth.service;


import com.globallogix.auth.entity.User;
import com.globallogix.auth.entity.enums.UserRoles;
import com.globallogix.auth.entity.enums.VerificationStatus;
import com.globallogix.auth.exception.UserNotFoundException;
import com.globallogix.auth.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserService {
    private final UserRepository userRepository;


    public void updateVerificationStatus(VerificationStatus status, String username){
        log.debug("Started updating verification status");
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UserNotFoundException("User with this username not found"));
        user.setVerificationStatus(status);
        userRepository.save(user);
        log.info("Verification status was updated to {}", status);
    }

    public void becomeSender(String username){
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UserNotFoundException("User with this username not found"));
        user.getRoles().add(UserRoles.SENDER);
        userRepository.save(user);
        log.info("Added SENDER role successfully");
    }
}

package com.bash.bank_sampah.restful.service;

import com.bash.bank_sampah.restful.entity.User;
import com.bash.bank_sampah.restful.model.LoginUserRequest;
import com.bash.bank_sampah.restful.model.TokenResponse;
import com.bash.bank_sampah.restful.repository.UserRepository;
import com.bash.bank_sampah.restful.security.BCrypt;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.UUID;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final ValidationService validationService;

    @Autowired
    public AuthService(UserRepository userRepository, ValidationService validationService) {
        this.userRepository = userRepository;
        this.validationService = validationService;
    }

    @Transactional
    public TokenResponse login(LoginUserRequest loginUserRequest) {
        validationService.validate(loginUserRequest);

        User user = userRepository.findById(loginUserRequest.getUsername())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "User not found"));

        if (BCrypt.checkpw(loginUserRequest.getPassword(), user.getPassword())){
            user.getName();
            user.setToken(UUID.randomUUID().toString());
            user.setTokenExpiredAt(next30Days());
            userRepository.save(user);

            return TokenResponse.builder()
                    .name(user.getName())
                    .token(user.getToken())
                    .expiredAt(user.getTokenExpiredAt())
                    .build();
        } else {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Wrong Username or Password");
        }
    }

    @Transactional
    public void logout(User user){
        user.setToken(null);
        user.setTokenExpiredAt(null);

        userRepository.save(user);
    }

    // Method for expired token
    private Long next30Days(){
        return System.currentTimeMillis()+1000000000000000000L;
    }
}

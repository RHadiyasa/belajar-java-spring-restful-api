package com.bash.bank_sampah.restful.service;

import com.bash.bank_sampah.restful.entity.User;
import com.bash.bank_sampah.restful.model.RegisterUserRequest;
import com.bash.bank_sampah.restful.model.UpdateUserRequest;
import com.bash.bank_sampah.restful.model.UserResponse;
import com.bash.bank_sampah.restful.repository.UserRepository;
import com.bash.bank_sampah.restful.security.BCrypt;
import jakarta.transaction.Transactional;
import jakarta.validation.Validator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.Objects;


@Service
public class UserService{

    private final UserRepository userRepository;
    private final Validator validator; // From jakarta.validation
    private final ValidationService validationService;

    @Autowired
    public UserService(UserRepository userRepository, Validator validator, ValidationService validationService) {
        this.userRepository = userRepository;
        this.validator = validator;
        this.validationService = validationService;
    }

    // Register user validation
    @Transactional
    public void register(RegisterUserRequest registerUserRequest) {

        validationService.validate(registerUserRequest);

        if (userRepository.existsById(registerUserRequest.getUsername())){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Username already registered.");
        }

        User user = new User();
        user.setUsername(registerUserRequest.getUsername());
        user.setPassword(BCrypt.hashpw(registerUserRequest.getPassword(), BCrypt.gensalt()));
        user.setName(registerUserRequest.getName());

        userRepository.save(user);
    }

    public UserResponse get(User user){
        return UserResponse.builder()
                .username(user.getUsername())
                .name(user.getName())
                .build();
    }

    public UserResponse update(User user, UpdateUserRequest updateUserRequest){
        validationService.validate(updateUserRequest);
        if(Objects.nonNull(updateUserRequest.getName())){
            user.setName(updateUserRequest.getName());
        }

        if(Objects.nonNull(updateUserRequest.getPassword())){
            user.setPassword(BCrypt.hashpw(updateUserRequest.getPassword(), BCrypt.gensalt()));
        }

        userRepository.save(user);

        return UserResponse.builder()
                .name(user.getName())
                .username(user.getUsername())
                .build();
    }
}

package com.pk.vgsms.controller.v1;

import com.pk.vgsms.model.dto.UserRegistrationDto;
import com.pk.vgsms.service.RegisterService;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/v1")
@Slf4j
public class RegisterController {

    private final RegisterService registerService;

    @Autowired
    public RegisterController(RegisterService registerService) {
        this.registerService = registerService;
    }

    @PostMapping("/register")
    public ResponseEntity<String> createUserAccount(@Valid @RequestBody UserRegistrationDto userRegistrationDto) {
        // TO DO: check if user exists by username or mail

        userRegistrationDto.setAuthorityName("USER");
        try {
            registerService.registerUser(userRegistrationDto);
        } catch (Exception exception) {
            //log.error(exception.getMessage());
            exception.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error in registration");
        }

        return ResponseEntity.ok("Successfully registered.");
    }
}

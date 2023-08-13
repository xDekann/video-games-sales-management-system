package com.pk.vgsms.controller.v1;

import com.pk.vgsms.model.dto.LoginDto;
import com.pk.vgsms.service.LoginService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/v1")
public class LoginController {

    private final LoginService loginService;

    @Autowired
    public LoginController(LoginService loginService) {
        this.loginService = loginService;
    }

    @PostMapping("/login")
    public ResponseEntity<String> login(@RequestBody LoginDto loginDto, HttpServletRequest request, HttpServletResponse response) {
        try {
            return loginService.setUserSession(request, response, loginDto);
        } catch (Exception exception) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Incorrect username or password!");
        }
    }

    @GetMapping("/login")
    public ResponseEntity<String> showLogoutMessage(@RequestParam(name = "logout", required = false) String logout) {
        if (logout != null) {
            return ResponseEntity.ok("You have been successfully logged out.");
        }
        return ResponseEntity.ok("Login page.");
    }
}

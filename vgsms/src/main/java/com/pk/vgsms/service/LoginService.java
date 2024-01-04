package com.pk.vgsms.service;

import com.pk.vgsms.model.dto.LoginDto;
import com.pk.vgsms.model.entity.User;
import com.pk.vgsms.repository.UserRepository;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.stereotype.Service;

import java.util.Base64;

@Service
public class LoginService {

    private final PasswordEncoder passwordEncoder;
    private final UserRepository userRepository;
    private final AuthenticationManager authenticationManager;

    @Autowired
    public LoginService(PasswordEncoder passwordEncoder, UserRepository userRepository, AuthenticationManager authenticationManager) {
        this.passwordEncoder = passwordEncoder;
        this.userRepository = userRepository;
        this.authenticationManager = authenticationManager;
    }

    public void roleCookieCreation(HttpServletResponse response) {
        final int COOKIE_DURATION = 3600 * 24;
        final String COOKIE_PATH = "/";
        final String COOKIE_NAME = "ROLE";

        UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String role = userDetails.getAuthorities().iterator().next().getAuthority();
        Cookie cookie = new Cookie(COOKIE_NAME, Base64.getEncoder().encodeToString(role.getBytes()));
        cookie.setPath(COOKIE_PATH);
        cookie.setMaxAge(COOKIE_DURATION); // seconds provided (1 day)
        response.addCookie(cookie);
    }

    public ResponseEntity<String> setUserSession(HttpServletRequest request, HttpServletResponse response, LoginDto loginDto) {
        Authentication authentication = null;

        if (loginDto != null) {
            User userFromDb = userRepository.findUserByName(loginDto.getUsername());
            if (userFromDb != null && !userFromDb.getEnabled()) return new ResponseEntity<>("Account has not been activated yet!", HttpStatus.FORBIDDEN);
            authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(loginDto.getUsername(), loginDto.getPassword()));
        }
        SecurityContextHolder.getContext().setAuthentication(authentication);
        roleCookieCreation(response);
        HttpSession session = request.getSession();
        session.setAttribute(HttpSessionSecurityContextRepository.SPRING_SECURITY_CONTEXT_KEY, SecurityContextHolder.getContext());
        return ResponseEntity.ok("Login successful!");
    }

}

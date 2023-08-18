package com.pk.vgsms.service;

import com.pk.vgsms.model.dto.UserRegistrationDto;
import com.pk.vgsms.model.entity.Authority;
import com.pk.vgsms.model.entity.User;
import com.pk.vgsms.model.entity.UserDetails;
import com.pk.vgsms.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class RegisterService {

    private final PasswordEncoder passwordEncoder;
    private final UserRepository userRepository;

    @Autowired
    public RegisterService(PasswordEncoder passwordEncoder, UserRepository userRepository) {
        this.passwordEncoder = passwordEncoder;
        this.userRepository = userRepository;
    }

    public void registerUser(UserRegistrationDto userRegistrationDto) {
        User user = dtoToUser(userRegistrationDto);
        UserDetails userDetails = dtoToUserDetails(userRegistrationDto);
        Authority authority = userRepository.findAuthorityByAuthorityName(userRegistrationDto.getAuthorityName());
        user.connectUserDetails(userDetails);
        user.connectAuthority(authority);
        userDetails.connectUser(user);
        authority.getUsers().add(user);
        userRepository.save(user);
    }

    private UserDetails dtoToUserDetails(UserRegistrationDto userRegistrationDto) {
        return UserDetails.builder()
                .name(userRegistrationDto.getName())
                .surname(userRegistrationDto.getSurname())
                .email(userRegistrationDto.getEmail())
                .city(userRegistrationDto.getCity())
                .address(userRegistrationDto.getAddress())
                .build();
    }

    private User dtoToUser(UserRegistrationDto userRegistrationDto) {
        return User.builder()
                .username(userRegistrationDto.getUsername())
                .password(passwordEncoder.encode(userRegistrationDto.getPassword()))
                .enabled(true)
                .build();
    }
}

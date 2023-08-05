package com.pk.vgsms.security;

import com.pk.vgsms.model.entity.User;
import com.pk.vgsms.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class ClientDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    @Autowired
    public ClientDetailsService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = Optional.of(userRepository.findUserByName(username)).orElseThrow(() -> new UsernameNotFoundException("Username not found!"));

        return  org.springframework.security.core.userdetails.User.builder()
                .username(user.getUsername())
                .password(user.getPassword())
                .roles(user.getAuthority().getAuthorityName())
                .build();
    }
}

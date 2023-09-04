package com.pk.vgsms.service;

import com.pk.vgsms.model.dto.UserDto;
import com.pk.vgsms.model.dto.UserPaginatedDto;
import com.pk.vgsms.model.dto.UserRegistrationDto;
import com.pk.vgsms.model.entity.Authority;
import com.pk.vgsms.model.entity.User;
import com.pk.vgsms.model.entity.UserDetails;
import com.pk.vgsms.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class AdminService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final RegisterService registerService;
    @Autowired
    public AdminService(UserRepository userRepository, PasswordEncoder passwordEncoder, RegisterService registerService) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.registerService = registerService;
    }

    public UserPaginatedDto getUsers(Pageable pageable, String username) {
        String loggedUserName = SecurityContextHolder.getContext().getAuthentication().getName();
        Page<User> userPagesFromDb = userRepository.findAllByUsernameContainingIgnoreCaseAndUsernameNotOrderByUsername(username, loggedUserName, pageable);
        List<UserDto> users = usersToUsersDto(userPagesFromDb.getContent(), loggedUserName);
        return UserPaginatedDto.builder()
                .users(users)
                .totalPages(userPagesFromDb.getTotalPages())
                .build();
    }

    private List<UserDto> usersToUsersDto(List<User> userList, String loggedUserName) {
        return userList.stream()
                .filter(user -> !user.getUsername().equals(loggedUserName))
                .map(user -> UserDto.builder()
                        .id(user.getId())
                        .name(user.getUserDetails().getName())
                        .surname(user.getUserDetails().getSurname())
                        .email(user.getUserDetails().getEmail())
                        .city(user.getUserDetails().getCity())
                        .address(user.getUserDetails().getAddress())
                        .username(user.getUsername())
                        .authorityName(user.getAuthority().getAuthorityName())
                        .enabled(user.getEnabled())
                        .build())
                .collect(Collectors.toList());
    }

    public UserDto getUser(Long id) {
        final int FIRST_PERSON_INDEX = 0;
        User userFromDb = userRepository.findUserById(id);
        String loggedUserName = SecurityContextHolder.getContext().getAuthentication().getName();
        return usersToUsersDto(List.of(userFromDb), loggedUserName).get(FIRST_PERSON_INDEX);
    }

    public void updateUser(UserDto userDto) {
        User userToUpdate = userRepository.findUserById(userDto.getId());
        mergeUserWithDto(userToUpdate, userDto);
        userRepository.save(userToUpdate);
    }

    private void mergeUserWithDto(User user, UserDto userDto) {
        user.setUsername(userDto.getUsername());
        user.setPassword(userDto.getPassword() == null || userDto.getPassword().isBlank() ?
                user.getPassword() : passwordEncoder.encode(userDto.getPassword()));
        Authority dtoAuthority = userRepository.findAuthorityByAuthorityName(userDto.getAuthorityName());
        if (!dtoAuthority.equals(user.getAuthority())) {
            Authority usersAuthority = user.getAuthority();
            usersAuthority.getUsers().remove(user);
            user.setAuthority(dtoAuthority);
        }
        UserDetails userDetails = user.getUserDetails();
        userDetails.setAddress(userDto.getAddress());
        userDetails.setCity(userDto.getCity());
        userDetails.setName(userDto.getName());
        userDetails.setSurname(userDto.getSurname());
        userDetails.setEmail(userDto.getEmail());
    }

    public void deleteUser(Long id) {
        User userToDelete = userRepository.findUserById(id);
        if (userToDelete.getEnabled()) {
            userToDelete.setEnabled(false);
        } else {
            userToDelete.setEnabled(true);
        }
        userRepository.save(userToDelete);
    }

    public void registerUser(UserRegistrationDto userRegistrationDto) {
        registerService.registerUser(userRegistrationDto);
    }
}

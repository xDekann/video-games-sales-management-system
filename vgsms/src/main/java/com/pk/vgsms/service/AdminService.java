package com.pk.vgsms.service;

import com.pk.vgsms.model.dto.UserDto;
import com.pk.vgsms.model.dto.UserPaginatedDto;
import com.pk.vgsms.model.entity.User;
import com.pk.vgsms.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class AdminService {

    private final UserRepository userRepository;

    @Autowired
    public AdminService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public UserPaginatedDto getUsers(Pageable pageable, String username) {
        String loggedUserName = SecurityContextHolder.getContext().getAuthentication().getName();
        if (username != null && !username.isBlank()) {
            User pickedUser = userRepository.findUserByName(username);
            List<UserDto> users = usersToUsersDto(List.of(pickedUser), loggedUserName);
            return UserPaginatedDto.builder()
                    .users(users)
                    .totalPages(1)
                    .build();
        }
        Specification<User> spec = (root, query, criteriaBuilder) ->
                criteriaBuilder.notEqual(root.get("username"), loggedUserName);
        Page<User> userPagesFromDb = userRepository.findAll(spec, pageable);
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
                        .name(user.getUserDetails().getName())
                        .surname(user.getUserDetails().getSurname())
                        .email(user.getUserDetails().getEmail())
                        .city(user.getUserDetails().getCity())
                        .address(user.getUserDetails().getAddress())
                        .username(user.getUsername())
                        .authorityName(user.getAuthority().getAuthorityName())
                        .build())
                .collect(Collectors.toList());
    }

}

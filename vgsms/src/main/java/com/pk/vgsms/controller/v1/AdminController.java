package com.pk.vgsms.controller.v1;

import com.pk.vgsms.model.dto.UserDto;
import com.pk.vgsms.model.dto.UserPaginatedDto;
import com.pk.vgsms.model.dto.UserRegistrationDto;
import com.pk.vgsms.service.AdminService;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/v1/admin")
@Slf4j
public class AdminController {

    private final AdminService adminService;

    @Autowired
    public AdminController(AdminService adminService) {
        this.adminService = adminService;
    }

    @GetMapping("/users")
    public UserPaginatedDto getUsers(Pageable pageable, @RequestParam(name = "phrase", required = false) String username) {
        try {
            return adminService.getUsers(pageable, username);
        } catch (Exception exception) {
            log.error("Error while trying to retrieve a list of users." + exception.getMessage());
            return new UserPaginatedDto();
        }
    }

    @GetMapping("/user")
    public UserDto getUser(@RequestParam("id") Long id) {
        try {
            return adminService.getUser(id);
        } catch (Exception exception) {
            log.error("Error while trying to obtain a certain user." + exception.getMessage());
            return new UserDto();
        }
    }

    @PutMapping("/user")
    public ResponseEntity<String> updateUser(@Valid @RequestBody UserDto userDto) {
        try {
            if (adminService.updateUser(userDto) == null) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Username or email already exists");
            }
            return ResponseEntity.ok().build();
        } catch (Exception exception) {
            log.error("Error while trying to update a certain user." + exception.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @DeleteMapping("/user")
    public ResponseEntity<String> deleteUser(@RequestParam("id") Long id) {
        try {
            adminService.deleteUser(id);
            return ResponseEntity.ok().build();
        } catch (Exception exception) {
            log.error("Error while trying to obtain a certain user." + exception.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PostMapping("/user")
    public ResponseEntity<String> addUser(@RequestBody @Valid UserRegistrationDto userRegistrationDto) {
        String role = userRegistrationDto.getAuthorityName();
        if (!role.equals("USER") && !role.equals("EMPLOYEE") && !role.equals("ADMIN")) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid role");
        }
        try {
            if (adminService.registerUser(userRegistrationDto) == null) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Username or email already exists");
            }
        } catch (Exception exception) {
            log.error(exception.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error in registration");
        }

        return ResponseEntity.ok("Successfully registered.");
    }
}

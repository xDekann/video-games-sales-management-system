package com.pk.vgsms.controller.v1;

import com.pk.vgsms.model.dto.UserPaginatedDto;
import com.pk.vgsms.service.AdminService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.GetMapping;
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
            log.error("Error while trying to retrieve a list of users.");
            return new UserPaginatedDto();
        }
    }
}

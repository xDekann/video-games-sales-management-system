package com.pk.vgsms.controller.v1;

import com.pk.vgsms.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/v1/webhook/user")
public class WebhookController {

    private final UserService userService;
    @Autowired
    public WebhookController(UserService userService) {
        this.userService = userService;
    }

    @DeleteMapping
    public ResponseEntity<String> deleteUsersCartPostTransaction(@RequestBody String userId) {
        try {
            userService.deleteTransactionItemsAfterSucceed(userId);
            System.out.println("Successful delete");
            return ResponseEntity.status(HttpStatus.OK).build();
        } catch (Exception exception) {
            exception.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

}

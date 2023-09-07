package com.pk.vgsms.controller.v1;

import com.pk.vgsms.model.dto.CartPurchaseDto;
import com.pk.vgsms.model.dto.GamePaginatedDto;
import com.pk.vgsms.model.dto.UserDto;
import com.pk.vgsms.model.entity.Product;
import com.pk.vgsms.model.entity.Purchase;
import com.pk.vgsms.service.StripeService;
import com.pk.vgsms.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/v1/user")
@Slf4j
public class UserController {

    private final UserService userService;
    private final StripeService stripeService;

    @Autowired
    public UserController(UserService userService, StripeService stripeService) {
        this.userService = userService;
        this.stripeService = stripeService;
    }

    @GetMapping("/game")
    public GamePaginatedDto getProducts(@RequestParam("phrase") String phrase, @RequestParam("category") String category, Pageable pageable) {
        try {
            Page<Product> receivedProducts = userService.getProducts(phrase, category, pageable);
            return GamePaginatedDto.builder()
                    .products(receivedProducts.getContent())
                    .totalPages(receivedProducts.getTotalPages())
                    .build();
        } catch (Exception exception) {
            log.error(exception.getMessage());
            exception.printStackTrace();
            return GamePaginatedDto.builder()
                    .products(List.of())
                    .totalPages(0)
                    .build();
        }
    }

    @PostMapping("/game")
    public ResponseEntity<String> addGameToCart(@RequestParam("id") Long gameId, @RequestParam("amount") Integer amount) {
        try {
            Purchase receivedPurchase = userService.addGameToCart(gameId, amount);
            if (receivedPurchase != null) {
                return ResponseEntity.ok().build();
            } else {
                return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
            }
        } catch (Exception exception) {
            exception.printStackTrace();
            log.error(exception.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/price")
    public Double cartPrice() {
        try {
            return userService.getCartPrice();
        } catch (Exception exception) {
            exception.printStackTrace();
            log.error(exception.getMessage());
            return 0.0;
        }
    }

    @GetMapping("/cart")
    public List<CartPurchaseDto> userCartContent() {
        try {
            return userService.getUsersCartItems();
        } catch (Exception exception) {
            exception.printStackTrace();
            log.error(exception.getMessage());
            return List.of();
        }
    }

    @DeleteMapping("/cart")
    public ResponseEntity<String> deleteItemFromCart(@RequestParam("itemId") Long itemId) {
        try {
            if (userService.removeItemFromCart(itemId) == null) {
                return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
            } else {
                return ResponseEntity.status(HttpStatus.OK).build();
            }
        } catch (Exception exception) {
            exception.printStackTrace();
            log.error(exception.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping
    public UserDto getUserDetails() {
        try {
            return userService.getUserDetails();
        } catch (Exception exception) {
            exception.printStackTrace();
            log.error(exception.getMessage());
            return new UserDto();
        }
    }

    @GetMapping("/checkout")
    public ResponseEntity<String> getStripeCheckoutUrl() {
        try {
            if (!stripeService.confirmProductAvailability().isEmpty()) {
                return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
            }
            String url = stripeService.createStripeCheckoutSession();
            System.out.println(url);
            return ResponseEntity.status(HttpStatus.OK).body(url);
        } catch (Exception exception) {
            exception.printStackTrace();
            log.error(exception.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }
}

package com.pk.vgsms.controller.v1;

import com.pk.vgsms.model.dto.GameDto;
import com.pk.vgsms.model.dto.GamePaginatedDto;
import com.pk.vgsms.model.entity.Product;
import com.pk.vgsms.service.EmployeeService;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
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

import java.util.List;

@RestController
@RequestMapping("/v1/employee")
@Slf4j
public class EmployeeController {

    private final EmployeeService employeeService;

    @Autowired
    public EmployeeController(EmployeeService employeeService) {
        this.employeeService = employeeService;
    }

    @PostMapping("/game")
    public ResponseEntity<String> addProduct(@Valid @RequestBody GameDto gameDto) {
        try {
            if (employeeService.addProductToDb(gameDto) == null) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Product already exists");
            }
            return ResponseEntity.status(HttpStatus.OK).body("Successfully added product.");
        } catch (Exception exception) {
            log.error(exception.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error in product addition");
        }
    }

    @GetMapping("/game")
    public GamePaginatedDto getProducts(@RequestParam("phrase") String phrase, @RequestParam("category") String category, Pageable pageable) {
        try {
            Page<Product> receivedProducts = employeeService.getProducts(phrase, category, pageable);
            return GamePaginatedDto.builder()
                    .products(receivedProducts.getContent())
                    .totalPages(receivedProducts.getTotalPages())
                    .build();
        } catch (Exception exception) {
            log.error(exception.getMessage());
            return GamePaginatedDto.builder()
                    .products(List.of())
                    .totalPages(0)
                    .build();
        }
    }

    @PutMapping("/game/replenish")
    public ResponseEntity<String> replenishProduct(@RequestParam("id") Long productId, @RequestParam("amount") Long amount) {
        if (amount < 0) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Amount cannot be negative.");
        }
        try {
            if (employeeService.replenishProduct(productId, amount, true) != null) {
                return ResponseEntity.status(HttpStatus.OK).build();
            } else {
                return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
            }
        } catch (Exception exception) {
            log.error(exception.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An error has occurred during replenish operation.");
        }
    }

    @PutMapping("/game/deplenish")
    public ResponseEntity<String> deplenishProduct(@RequestParam("id") Long productId, @RequestParam("amount") Long amount) {
        try {
            if (employeeService.replenishProduct(productId, amount, false) != null) {
                return ResponseEntity.status(HttpStatus.OK).build();
            } else {
                return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
            }
        } catch (Exception exception) {
            log.error(exception.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An error has occurred during deplenish operation.");
        }
    }

    @DeleteMapping("/game")
    public ResponseEntity<String> deleteProduct(@RequestParam("id") Long productId) {
        try {
            employeeService.deleteProduct(productId);
        } catch (Exception exception) {
            log.error(exception.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An error has occurred during deletion operation.");
        }
        return ResponseEntity.status(HttpStatus.OK).build();
    }
}

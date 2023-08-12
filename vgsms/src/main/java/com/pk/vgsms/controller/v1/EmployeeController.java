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
import org.springframework.web.bind.annotation.GetMapping;
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

    @PutMapping("/game")
    public ResponseEntity<String> addProduct(@Valid @RequestBody GameDto gameDto) {
        // validate if the game with the given name already exists in db
        try {
            employeeService.addProductToDb(gameDto);
        } catch (Exception exception) {
            log.error(exception.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error in product addition");
        }
        return ResponseEntity.status(HttpStatus.OK).body("Successfully added product.");
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
}

package com.pk.vgsms.service;

import com.pk.vgsms.model.entity.Product;
import com.pk.vgsms.model.entity.Purchase;
import com.pk.vgsms.model.entity.User;
import com.pk.vgsms.repository.ProductRepository;
import com.pk.vgsms.repository.PurchaseRepository;
import com.pk.vgsms.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UserService {

    private final ProductRepository productRepository;
    private final EmployeeService employeeService;
    private final UserRepository userRepository;
    private final PurchaseRepository purchaseRepository;

    @Autowired
    public UserService(ProductRepository productRepository, EmployeeService employeeService, UserRepository userRepository, PurchaseRepository purchaseRepository) {
        this.productRepository = productRepository;
        this.employeeService = employeeService;
        this.userRepository = userRepository;
        this.purchaseRepository = purchaseRepository;
    }

    public Page<Product> getProducts(String phrase, String category, Pageable pageable) {
        return employeeService.getProducts(phrase, category, pageable);
    }

    public Purchase addGameToCart(Long gameId, Integer amount) {
        Optional<Product> wantedGameDbResponse = productRepository.findById(gameId);
        String loggedUserName = SecurityContextHolder.getContext().getAuthentication().getName();
        Product wantedGame = null;

        if (wantedGameDbResponse.isPresent()) {
            wantedGame = wantedGameDbResponse.get();
        }
        if (wantedGame == null || wantedGame.getAmount() < amount) {
            return null;
        }

        User loggedUser = userRepository.findUserByName(loggedUserName);
        Purchase cartRecord = Purchase.builder()
                .user(loggedUser)
                .product(wantedGame)
                .quantity(amount)
                .build();
        loggedUser.addPurchase(cartRecord);
        wantedGame.addPurchase(cartRecord);
        purchaseRepository.save(cartRecord);
        return cartRecord;
    }
}

package com.pk.vgsms.service;

import com.pk.vgsms.model.dto.CartPurchaseDto;
import com.pk.vgsms.model.entity.Product;
import com.pk.vgsms.model.entity.Purchase;
import com.pk.vgsms.model.entity.PurchaseId;
import com.pk.vgsms.model.entity.User;
import com.pk.vgsms.repository.ProductRepository;
import com.pk.vgsms.repository.PurchaseRepository;
import com.pk.vgsms.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

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

    private User getLoggedUser() {
        String loggedUserName = SecurityContextHolder.getContext().getAuthentication().getName();
        return userRepository.findUserByName(loggedUserName);
    }

    public Purchase addGameToCart(Long gameId, Integer amount) {
        Optional<Product> wantedGameDbResponse = productRepository.findById(gameId);
        Product wantedGame = null;

        if (wantedGameDbResponse.isPresent()) {
            wantedGame = wantedGameDbResponse.get();
        }
        if (wantedGame == null || wantedGame.getAmount() < amount) {
            return null;
        }
        User loggedUser = getLoggedUser();
        Purchase cartRecord = getPurchaseRecord(gameId, loggedUser.getId());
        if (cartRecord != null) {
            loggedUser.getPurchases().remove(cartRecord);
            wantedGame.getPurchases().remove(cartRecord);
            cartRecord.setQuantity(cartRecord.getQuantity() + amount);
        } else {
            cartRecord = Purchase.builder()
                    .purchaseId(PurchaseId.builder()
                            .user(loggedUser.getId())
                            .product(wantedGame.getId())
                            .build())
                    .user(loggedUser)
                    .product(wantedGame)
                    .quantity(amount)
                    .build();
        }
        loggedUser.addPurchase(cartRecord);
        wantedGame.addPurchase(cartRecord);
        purchaseRepository.save(cartRecord);
        return cartRecord;
    }

    private List<Purchase> getUserPurchases() {
        return purchaseRepository.findByUserId(getLoggedUser().getId());
    }

    private Purchase getPurchaseRecord(Long gameId, Long userId) {
        return purchaseRepository.findByPurchaseId(PurchaseId.builder()
                .user(userId)
                .product(gameId)
                .build());
    }

    public Double getCartPrice() {
        List<Purchase> userPurchases = getUserPurchases();
        return userPurchases.stream()
                .map(purchase -> {
                    BigDecimal price = BigDecimal.valueOf(purchase.getProduct().getPrice());
                    int quantity = purchase.getQuantity();
                    return price.multiply(BigDecimal.valueOf(quantity));
                })
                .reduce(BigDecimal.ZERO, BigDecimal::add).doubleValue();
    }

    public List<CartPurchaseDto> getUsersCartItems() {
        List<Purchase> userPurchases = getUserPurchases();
        return userPurchases.stream()
                .map(purchase -> CartPurchaseDto.builder()
                        .itemId(purchase.getProduct().getId())
                        .amount(purchase.getQuantity())
                        .amountLeft(purchase.getProduct().getAmount())
                        .price((BigDecimal.valueOf(purchase.getProduct().getPrice())
                                .multiply(BigDecimal.valueOf(purchase.getQuantity()))).doubleValue())
                        .name(purchase.getProduct().getName())
                        .build())
                .collect(Collectors.toList());
    }

    public Purchase removeItemFromCart(Long itemId) {
        List<Purchase> userPurchases = getUserPurchases();
        User loggedUser = getLoggedUser();
        Optional<Purchase> isPurchaseToDelete = userPurchases.stream()
                .filter(purchase -> purchase.getProduct().getId().equals(itemId))
                .findFirst();
        Optional<Product> isProductToDeleteFrom = userPurchases.stream()
                .map(Purchase::getProduct)
                .filter(product -> product.getId().equals(itemId))
                .findFirst();

        if (isPurchaseToDelete.isPresent() && isProductToDeleteFrom.isPresent()) {
            Purchase purchaseToDelete = isPurchaseToDelete.get();
            Product productFromPurchase = isProductToDeleteFrom.get();
            loggedUser.getPurchases().remove(purchaseToDelete);
            productFromPurchase.getPurchases().remove(purchaseToDelete);
            purchaseToDelete.setUser(null);
            purchaseToDelete.setProduct(null);
            purchaseRepository.delete(purchaseToDelete);
            return purchaseToDelete;
        }
        return null;
    }
}

package com.pk.transactionservice.service;

import com.pk.transactionservice.model.entity.PurchaseDetailsDto;
import com.pk.transactionservice.model.entity.PurchaseDto;
import com.pk.transactionservice.model.entity.PurchaseDtoPaginated;
import com.pk.transactionservice.model.entity.purchase.Purchase;
import com.pk.transactionservice.repository.PurchaseRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.util.List;

@Service
public class PurchaseService {

    private final PurchaseRepository purchaseRepository;

    @Autowired
    public PurchaseService(PurchaseRepository purchaseRepository) {
        this.purchaseRepository = purchaseRepository;
    }

    public PurchaseDtoPaginated getPurchases(Pageable pageable) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        System.out.println(username);
        Page<Purchase> receivedProducts =  purchaseRepository.findAllByUsernameOrderByTransactionDateDesc(username, pageable);
        List<Purchase> purchaseList = receivedProducts.getContent();
        SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm");
        return PurchaseDtoPaginated.builder()
                .purchases(purchaseList.stream()
                .map(purchase -> PurchaseDto.builder()
                        .id(purchase.getTransactionId())
                        .deliveryMethod(purchase.getDeliveryMethod())
                        .value(purchase.getTransactionValue())
                        .date(dateFormat.format(purchase.getTransactionDate()))
                        .status(purchase.getStatus().toString())
                        .items(purchase.getPurchaseItems().stream()
                                .map(cartItem -> PurchaseDetailsDto.builder()
                                        .name(cartItem.getName())
                                        .price(cartItem.getPrice())
                                        .amount(cartItem.getAmount())
                                        .build())
                                .toList())
                        .build())
                .toList())
                .totalPages(receivedProducts.getTotalPages())
                .build();
    }
}

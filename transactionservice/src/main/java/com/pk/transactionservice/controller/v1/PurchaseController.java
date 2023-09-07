package com.pk.transactionservice.controller.v1;

import com.pk.transactionservice.model.entity.PurchaseDto;
import com.pk.transactionservice.model.entity.PurchaseDtoPaginated;
import com.pk.transactionservice.service.PurchaseService;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/transactionservice/v1/purchase")
public class PurchaseController {

    private final PurchaseService purchaseService;

    public PurchaseController(PurchaseService purchaseService) {
        this.purchaseService = purchaseService;
    }

    @GetMapping
    public PurchaseDtoPaginated getTransactionHistory(Pageable pageable) {
        try {
            return purchaseService.getPurchases(pageable);
        } catch (Exception exception) {
            exception.printStackTrace();
            return new PurchaseDtoPaginated();
        }
    }
}

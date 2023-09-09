package com.pk.transactionservice.controller.v1;

import com.pk.transactionservice.model.entity.PurchaseDtoPaginated;
import com.pk.transactionservice.service.PurchaseService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/transactionservice/v1/purchase")
@Slf4j
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
            log.error(exception.getMessage());
            return new PurchaseDtoPaginated();
        }
    }

    @GetMapping("/pdf")
    public ResponseEntity<byte[]> getTransactionPdf(@RequestParam("transactionId") String transactionId) {
        try {
            byte[] pdfBlob = purchaseService.getTransactionPdf(transactionId);
            if (pdfBlob == null) {
                return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
            }
            HttpHeaders headers = new HttpHeaders();
            headers.add(HttpHeaders.CONTENT_TYPE, "application/pdf");
            headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=invoice.pdf");
            return ResponseEntity.ok()
                    .headers(headers)
                    .body(pdfBlob);
        } catch (Exception exception) {
            log.error(exception.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}

package com.pk.transactionservice.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.pk.transactionservice.model.entity.purchase.CartItem;
import com.pk.transactionservice.model.entity.purchase.Purchase;
import com.pk.transactionservice.model.entity.purchase.Status;
import com.pk.transactionservice.model.entity.purchase.UserDetails;
import com.pk.transactionservice.repository.PurchaseRepository;
import com.stripe.model.Event;
import com.stripe.model.EventDataObjectDeserializer;
import com.stripe.model.PaymentIntent;
import com.stripe.net.Webhook;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Date;
import java.util.List;
import java.util.Map;

@Service
public class StripeWebhookService {

    private final String WEBHOOK_SECRET;
    private final ObjectMapper objectMapper;
    private final PurchaseRepository purchaseRepository;
    private final String PURCHASE_REMOVAL_URL;
    private final RestTemplate restTemplate;

    public StripeWebhookService(@Value("${webhook.secretKey}") String webhookSecret,
                                ObjectMapper objectMapper, PurchaseRepository purchaseRepository,
                                @Value("${purchaseRemovalUrl}") String purchaseRemovalUrl, RestTemplate restTemplate) {
        this.WEBHOOK_SECRET = webhookSecret;
        this.objectMapper = objectMapper;
        this.purchaseRepository = purchaseRepository;
        this.PURCHASE_REMOVAL_URL = purchaseRemovalUrl;
        this.restTemplate = restTemplate;
    }

    public Purchase handleWebhook(String payload, String signatureHeader) throws Exception {
        Event webhookEvent = Webhook.constructEvent(payload, signatureHeader, WEBHOOK_SECRET);
        EventDataObjectDeserializer dataObjectDeserializer = webhookEvent.getDataObjectDeserializer();
        PaymentIntent paymentIntent = (PaymentIntent) dataObjectDeserializer.getObject().orElse(null);

        if (paymentIntent == null) {
            return null;
        }

        Purchase purchase = preparePurchaseDbObject(paymentIntent);
        switch (webhookEvent.getType()) {
            case "payment_intent.succeeded" -> {
                if (!removePurchaseFromMainDb(purchase.getUserId()).is2xxSuccessful()) {
                    throw new Exception("Main service failure");
                }
                purchase.setStatus(Status.SUCCESS);
            }
            case "payment_intent.canceled" -> {
                purchase.setStatus(Status.CANCELED);
            }
            default -> {
                purchase.setStatus(Status.PROCESSING);
            }
        }
        purchaseRepository.save(purchase);
        return purchase;
    }

    private Purchase preparePurchaseDbObject(PaymentIntent paymentIntent) throws JsonProcessingException {
        Map<String, String> metadata = paymentIntent.getMetadata();
        Purchase purchase = purchaseRepository.findByTransactionId(metadata.get("transactionId"));
        if (purchase != null) {
            return purchase;
        }
        return Purchase.builder()
                .userId(metadata.get("userId"))
                .username(metadata.get("username"))
                .transactionId(metadata.get("transactionId"))
                .transactionValue(paymentIntent.getAmount() / 100.00)
                .userDetails(objectMapper.readValue(metadata.get("userDetails"), UserDetails.class))
                .purchaseItems(objectMapper.readValue(metadata.get("purchase"), new TypeReference<>() {}))
                .transactionDate(objectMapper.readValue(metadata.get("transactionDate"), Date.class))
                .deliveryMethod(objectMapper.readValue(metadata.get("deliveryMethod"), String.class))
                .build();
    }

    private HttpStatusCode removePurchaseFromMainDb(String userId) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<String> requestEntity = new HttpEntity<>(userId, headers);
        ResponseEntity<String> responseEntity = restTemplate.exchange(
                PURCHASE_REMOVAL_URL,
                HttpMethod.DELETE,
                requestEntity,
                String.class
        );
        return responseEntity.getStatusCode();
    }
}

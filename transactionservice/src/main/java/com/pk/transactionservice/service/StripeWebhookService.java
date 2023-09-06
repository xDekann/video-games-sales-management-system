package com.pk.transactionservice.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.pk.transactionservice.model.entity.purchase.CartItem;
import com.pk.transactionservice.model.entity.purchase.Purchase;
import com.pk.transactionservice.model.entity.purchase.Status;
import com.pk.transactionservice.model.entity.purchase.UserDetails;
import com.pk.transactionservice.repository.PurchaseRepository;
import com.stripe.exception.SignatureVerificationException;
import com.stripe.model.Event;
import com.stripe.model.EventDataObjectDeserializer;
import com.stripe.model.PaymentIntent;
import com.stripe.net.Webhook;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public class StripeWebhookService {

    private final String webhookSecret;
    private final ObjectMapper objectMapper;
    private final PurchaseRepository purchaseRepository;

    public StripeWebhookService(@Value("${webhook.secretKey}") String webhookSecret,
                                ObjectMapper objectMapper, PurchaseRepository purchaseRepository) {
        this.webhookSecret = webhookSecret;
        this.objectMapper = objectMapper;
        this.purchaseRepository = purchaseRepository;
    }

    public Purchase handleWebhook(String payload, String signatureHeader) throws SignatureVerificationException, JsonProcessingException {
        Event webhookEvent = Webhook.constructEvent(payload, signatureHeader, webhookSecret);
        EventDataObjectDeserializer dataObjectDeserializer = webhookEvent.getDataObjectDeserializer();
        PaymentIntent paymentIntent = (PaymentIntent) dataObjectDeserializer.getObject().orElse(null);

        if (paymentIntent == null) {
            return null;
        }

        Purchase purchase = createPurchaseDbObject(paymentIntent);
        switch (webhookEvent.getType()) {
            case "payment_intent.succeeded" -> {
                purchase.setStatus(Status.SUCCESS);
                // send id to the main service in order to clear db and decrement order amount
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

    private Purchase createPurchaseDbObject(PaymentIntent paymentIntent) throws JsonProcessingException {
        Map<String, String> metadata = paymentIntent.getMetadata();
        return Purchase.builder()
                .userId(metadata.get("userId"))
                .transactionId(metadata.get("transactionId"))
                .transactionValue(paymentIntent.getAmount() / 100.00)
                .userDetails(objectMapper.readValue(metadata.get("userDetails"), UserDetails.class))
                .purchaseItems(objectMapper.readValue(metadata.get("purchase"), new TypeReference<List<CartItem>>() {}))
                .build();
    }
}

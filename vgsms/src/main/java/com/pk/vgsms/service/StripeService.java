package com.pk.vgsms.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.pk.vgsms.config.StripeConfig;
import com.pk.vgsms.model.entity.User;
import com.pk.vgsms.repository.ProductRepository;
import com.pk.vgsms.repository.PurchaseRepository;
import com.stripe.exception.StripeException;
import com.stripe.model.checkout.Session;
import com.stripe.param.checkout.SessionCreateParams;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class StripeService {

    private final StripeConfig stripeConfig;
    private final UserService userService;
    private final PurchaseRepository purchaseRepository;
    private final ProductRepository productRepository;
    private final String successUrl;
    private final String cancelUrl;
    private final ObjectMapper objectMapper;

    @Autowired
    public StripeService(StripeConfig stripeConfig, @Value("${successUrl}") String successUrl,
                         @Value("${cancelUrl}") String cancelUrl, UserService userService, ProductRepository productRepository,
                         PurchaseRepository purchaseRepository, ObjectMapper objectMapper) {
        this.stripeConfig = stripeConfig;
        this.successUrl = successUrl;
        this.cancelUrl = cancelUrl;
        this.userService = userService;
        this.purchaseRepository = purchaseRepository;
        this.productRepository = productRepository;
        this.objectMapper = objectMapper;
    }

    public String createStripeCheckoutSession() throws StripeException, JsonProcessingException {
        User loggedUser = userService.getLoggedUser();
        final String TRANSACTION_UUID = UUID.randomUUID().toString();
        SessionCreateParams.Builder stripeCheckoutBuilder = SessionCreateParams.builder()
                .addPaymentMethodType(SessionCreateParams.PaymentMethodType.PAYPAL)
                .setMode(SessionCreateParams.Mode.PAYMENT)
                .setSuccessUrl(successUrl)
                .setCancelUrl(cancelUrl)
                .setCustomerEmail(loggedUser.getUserDetails().getEmail())
                .addLineItem(
                        SessionCreateParams.LineItem.builder()
                                .setQuantity(1L)
                                .setPriceData(
                                        SessionCreateParams.LineItem.PriceData.builder()
                                                .setUnitAmount((long) (userService.getCartPrice() * 100.00))
                                                .setCurrency("pln")
                                                .setProductData(
                                                        SessionCreateParams.LineItem.PriceData.ProductData.builder()
                                                                .setName(TRANSACTION_UUID)
                                                                .build()
                                                )
                                                .build()
                                )
                                .build()
                )
                .setPaymentIntentData(
                        SessionCreateParams.PaymentIntentData.builder()
                                .putMetadata("userId", loggedUser.getId().toString())
                                .putMetadata("transactionId", TRANSACTION_UUID)
                                .putMetadata("purchase", objectMapper.writeValueAsString(userService.getUsersCartItems()))
                                .putMetadata("userDetails", objectMapper.writeValueAsString(loggedUser.getUserDetails()))
                                .build()
                );
        Session session = Session.create(stripeCheckoutBuilder.build());
        return session.getUrl();
    }
}

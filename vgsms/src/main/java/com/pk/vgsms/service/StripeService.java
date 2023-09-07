package com.pk.vgsms.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.pk.vgsms.config.StripeConfig;
import com.pk.vgsms.model.entity.Product;
import com.pk.vgsms.model.entity.Purchase;
import com.pk.vgsms.model.entity.User;
import com.pk.vgsms.model.entity.payment.Deliveries;
import com.pk.vgsms.model.entity.payment.Payments;
import com.pk.vgsms.repository.ProductRepository;
import com.pk.vgsms.repository.PurchaseRepository;
import com.stripe.exception.StripeException;
import com.stripe.model.checkout.Session;
import com.stripe.param.SubscriptionUpdateParams;
import com.stripe.param.checkout.SessionCreateParams;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.sql.Timestamp;
import java.util.Date;
import java.util.List;
import java.util.UUID;

@Service
public class StripeService {

    private final UserService userService;
    private final String successUrl;
    private final String cancelUrl;
    private final ObjectMapper objectMapper;

    @Autowired
    public StripeService(@Value("${successUrl}") String successUrl,
                         @Value("${cancelUrl}") String cancelUrl, UserService userService,
                         ObjectMapper objectMapper) {
        this.successUrl = successUrl;
        this.cancelUrl = cancelUrl;
        this.userService = userService;
        this.objectMapper = objectMapper;
    }

    public String createStripeCheckoutSession(String paymentMethod, String deliveryMethod) throws StripeException, JsonProcessingException {
        User loggedUser = userService.getLoggedUser();
        final String TRANSACTION_UUID = UUID.randomUUID().toString();

        SessionCreateParams.Builder stripeCheckoutBuilder = SessionCreateParams.builder()
                .addPaymentMethodType(getPaymentMethodType(paymentMethod))
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
                                .putMetadata("username", loggedUser.getUsername())
                                .putMetadata("transactionId", TRANSACTION_UUID)
                                .putMetadata("purchase", objectMapper.writeValueAsString(userService.getUsersCartItems()))
                                .putMetadata("userDetails", objectMapper.writeValueAsString(loggedUser.getUserDetails()))
                                .putMetadata("transactionDate", objectMapper.writeValueAsString(new Date(System.currentTimeMillis())))
                                .putMetadata("deliveryMethod", objectMapper.writeValueAsString(deliveryMethod))
                                .build()
                );
        Session session = Session.create(stripeCheckoutBuilder.build());
        return session.getUrl();
    }

    public List<Purchase> confirmProductAvailability() {
        return userService.getUserPurchases().stream()
                .filter(purchase -> purchase.getQuantity() > purchase.getProduct().getAmount())
                .toList();
    }

    public Boolean checkPaymentAndDelivery(String paymentMethod, String deliveryMethod) {
        return Payments.containsPayment(paymentMethod) && Deliveries.containsDelivery(deliveryMethod);
    }

    private SessionCreateParams.PaymentMethodType getPaymentMethodType(String paymentMethod) {
        if (paymentMethod.equals(Payments.CARD.paymentName)) {
            return SessionCreateParams.PaymentMethodType.CARD;
        } else {
            return SessionCreateParams.PaymentMethodType.PAYPAL;
        }
    }
}

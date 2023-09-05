package com.pk.transactionservice.config;

import com.stripe.Stripe;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class StripeConfig {

    public StripeConfig(@Value("${stripe.secretKey}") String stripeSecretKey) {
        Stripe.apiKey = stripeSecretKey;
    }
}

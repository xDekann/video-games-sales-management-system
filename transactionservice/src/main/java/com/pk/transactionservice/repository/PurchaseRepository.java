package com.pk.transactionservice.repository;

import com.pk.transactionservice.model.entity.purchase.Purchase;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PurchaseRepository extends MongoRepository<Purchase, String> {
    Purchase findByTransactionId(String transactionId);
}

package com.pk.vgsms.repository;

import com.pk.vgsms.model.entity.Purchase;
import com.pk.vgsms.model.entity.PurchaseId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PurchaseRepository extends JpaRepository<Purchase, PurchaseId> {
    List<Purchase> findByUserId(Long userId);
    Purchase findByPurchaseId(PurchaseId purchaseId);
    List<Purchase> findAllByUserId(Long userId);
}

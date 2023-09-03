package com.pk.vgsms.model.entity;

import jakarta.persistence.Column;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.MapsId;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Objects;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "purchase")
public class Purchase {

    @EmbeddedId
    private PurchaseId purchaseId;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("user")
    @JoinColumn(name = "fk_user_id")
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("product")
    @JoinColumn(name = "fk_product_id")
    private Product product;

    private int quantity;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Purchase purchase = (Purchase) o;
        return quantity == purchase.quantity && Objects.equals(user, purchase.user) && Objects.equals(product, purchase.product);
    }

    @Override
    public int hashCode() {
        return Objects.hash(user, product, quantity);
    }
}
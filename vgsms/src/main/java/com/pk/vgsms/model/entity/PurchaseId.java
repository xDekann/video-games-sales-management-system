package com.pk.vgsms.model.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;
import java.util.Objects;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Setter
@Getter
public class PurchaseId implements Serializable {

    private Long user;
    private Long product;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PurchaseId that = (PurchaseId) o;
        return Objects.equals(user, that.user) && Objects.equals(product, that.product);
    }

    @Override
    public int hashCode() {
        return Objects.hash(user, product);
    }
}

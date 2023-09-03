package com.pk.vgsms.model.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.util.Objects;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
public class CartPurchaseDto {

    private Long itemId;
    private String name;
    private Double price;
    private Integer amount;
    private Long amountLeft;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CartPurchaseDto that = (CartPurchaseDto) o;
        return Objects.equals(name, that.name) && Objects.equals(price, that.price) && Objects.equals(amount, that.amount) && Objects.equals(amountLeft, that.amountLeft);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, price, amount, amountLeft);
    }
}

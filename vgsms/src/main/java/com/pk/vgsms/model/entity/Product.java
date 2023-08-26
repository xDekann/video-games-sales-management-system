package com.pk.vgsms.model.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "product")
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_product")
    private Long id;

    @Column(name = "name")
    private String name;

    @Column(name = "price")
    private Double price;

    @Column(name = "amount_available")
    private Long amount;

    @Column(name = "category")
    private String category;

    @Column(name = "producer")
    private String producer;

    @OneToMany(mappedBy = "product")
    @JsonIgnore
    private List<Purchase> purchases;

    public void addPurchase(Purchase purchase) {
        if (purchase == null) {return;}
        if (purchases == null) {purchases = new ArrayList<>();}
        purchases.add(purchase);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Product product = (Product) o;
        return Objects.equals(name, product.name) && Objects.equals(price, product.price) && Objects.equals(amount, product.amount) && Objects.equals(category, product.category) && Objects.equals(producer, product.producer);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, price, amount, category, producer);
    }
}

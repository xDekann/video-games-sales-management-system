package com.pk.transactionservice.model.entity.purchase;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.Value;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;
import java.util.List;

@Setter
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@ToString
@Document(collection = "orders")
@JsonIgnoreProperties(ignoreUnknown = true)
public class Purchase {
    @Id
    private String id;
    private String username;
    private List<CartItem> purchaseItems;
    private String transactionId;
    private UserDetails userDetails;
    private String userId;
    private Double transactionValue;
    private Status status;
    private Date transactionDate;
    private String deliveryMethod;
}

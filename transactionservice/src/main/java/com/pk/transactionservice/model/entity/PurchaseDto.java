package com.pk.transactionservice.model.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
public class PurchaseDto {
    private String id;
    private Double value;
    private String status;
    private String date;
    private String deliveryMethod;
    private List<PurchaseDetailsDto> items;
}

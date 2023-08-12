package com.pk.vgsms.model.dto;

import com.pk.vgsms.model.entity.Product;
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
public class GamePaginatedDto {
    private List<Product> products;
    private int totalPages;
}

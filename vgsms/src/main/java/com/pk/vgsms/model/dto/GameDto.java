package com.pk.vgsms.model.dto;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
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
public class GameDto {

    @NotBlank(message = "Game name must not be blank!")
    @Size(min = 3, max = 150, message = "Game name must contain more than 2 and less than 150 characters!")
    private String name;

    @NotNull(message = "Price must not be null")
    @DecimalMin(value = "1.00", message = "Price must be at least 1.00")
    @DecimalMax(value = "999.99", message = "Price must be less than 999.99")
    private Double price;

    @NotBlank(message = "Category must not be blank!")
    @Size(min = 3, max = 45, message = "Category must contain more than 2 and less than 46 characters!")
    private String category;

    @NotBlank(message = "Producer's name must not be blank!")
    @Size(min = 3, max = 45, message = "Producer's name must contain more than 2 and less than 46 characters!")
    private String producer;

    private Long amountAvailable;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        GameDto gameDto = (GameDto) o;
        return Objects.equals(name, gameDto.name) && Objects.equals(price, gameDto.price) && Objects.equals(category, gameDto.category) && Objects.equals(producer, gameDto.producer);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, price, category, producer);
    }
}

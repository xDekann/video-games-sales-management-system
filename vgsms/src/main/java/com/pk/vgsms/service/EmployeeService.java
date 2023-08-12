package com.pk.vgsms.service;

import com.pk.vgsms.model.dto.GameDto;
import com.pk.vgsms.model.entity.Product;
import com.pk.vgsms.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
public class EmployeeService {

    private final ProductRepository productRepository;

    @Autowired
    public EmployeeService(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    public void addProductToDb(GameDto gameDto) {
        Product product = Product.builder()
                .name(gameDto.getName())
                .amount(1L)
                .price(gameDto.getPrice())
                .category(gameDto.getCategory())
                .producer(gameDto.getProducer())
                .build();
        productRepository.save(product);
    }

    public Page<Product> getProducts(String phrase, String category, Pageable pageable) {
        if (category == null || category.isBlank() || category.equals("Select Category")) {
            if (phrase == null || phrase.isBlank()) {
                return productRepository.findAll(pageable);
            }
            return productRepository.findAllByNameContainingIgnoreCaseOrderByName(phrase, pageable);
        } else {
            return productRepository.findAllByNameContainingIgnoreCaseAndCategoryOrderByName(phrase, category, pageable);
        }
    }
}

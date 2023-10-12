package com.pk.vgsms.service;

import com.pk.vgsms.model.dto.GameDto;
import com.pk.vgsms.model.entity.Product;
import com.pk.vgsms.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class EmployeeService {

    private final ProductRepository productRepository;

    @Autowired
    public EmployeeService(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    public Product addProductToDb(GameDto gameDto) {
        if (productRepository.findProductByName(gameDto.getName()) != null) {
            return null;
        }
        Long idForImage = 1L;
        Optional<Product> lastProduct = productRepository.findTopByOrderByIdDesc();
        if (lastProduct.isPresent()) {
            idForImage = lastProduct.get().getId() + 1;
        }

        Product product = Product.builder()
                .name(gameDto.getName())
                .amount(1L)
                .price(gameDto.getPrice())
                .category(gameDto.getCategory())
                .producer(gameDto.getProducer())
                .imageUrl("https://picsum.photos/id/" + idForImage + "/300") // stock image for the project: https://picsum.photos
                .build();
        productRepository.save(product);
        return product;
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

    public Product replenishProduct(Long id, Long amount, Boolean addition) {
        Optional<Product> possibleProduct = productRepository.findById(id);
        if (possibleProduct.isPresent()) {
            Product product = possibleProduct.get();
            if (addition) {
                product.setAmount(product.getAmount() + amount);
            } else {
                if (product.getAmount() - amount < 0) {
                    product.setAmount(0L);
                } else {
                    product.setAmount(product.getAmount() - amount);
                }
            }
            productRepository.save(product);
            return product;
        }
        return null;
    }

    public void deleteProduct(Long id) {
        productRepository.deleteById(id);
    }

}

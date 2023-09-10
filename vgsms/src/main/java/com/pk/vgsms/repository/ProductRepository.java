package com.pk.vgsms.repository;

import com.pk.vgsms.model.entity.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {
    Page<Product> findAllByNameContainingIgnoreCaseAndCategoryOrderByName(String phrase, String category, Pageable pageable);
    Page<Product> findAllByNameContainingIgnoreCaseOrderByName(String phrase, Pageable pageable);
    Page<Product> findAll(Pageable pageable);
    Product findProductByName(String name);
}

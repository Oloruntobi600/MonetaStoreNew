package com.monetastore.monetastore.services;

import com.monetastore.monetastore.Models.Product;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

// in other to read and write from the data base
public interface ProductRepository extends JpaRepository<Product,Integer> {
    List<Product> findByUserId(Long userId, Sort sort);
}

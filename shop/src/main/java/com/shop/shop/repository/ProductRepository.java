package com.shop.shop.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.shop.shop.entity.Product;


import java.util.*;


@Repository
public interface ProductRepository extends JpaRepository <Product, Integer> {

    Optional<Product> findById(int id);
    
    @Modifying
    @Query(nativeQuery = true, value = "UPDATE products p SET p.quantity= ?1 WHERE p.id=?2")
    public void updateQuantity (double quantity, int id);
}

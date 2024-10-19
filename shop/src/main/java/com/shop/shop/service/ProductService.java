package com.shop.shop.service;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.shop.shop.entity.Product;
import com.shop.shop.repository.ProductRepository;
import com.shop.shop.repository.UserRepository;

@Service
public class ProductService {

    private ProductRepository productRepository;

    public ProductService(ProductRepository productRepository){
        this.productRepository = productRepository;
    }

    public Product findProductById(int id){
        Optional<Product> product = productRepository.findById(id);
        return product.orElse(null);
    }

    public List<Product> findAllProducts(){
        return productRepository.findAll();
    }

    public boolean createProduct(Product product) {
        Product p = productRepository.save(product);
        if ( p != null){
            return true;
        }
        return false;
    }

    
}


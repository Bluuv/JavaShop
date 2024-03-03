package com.shop.shop.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.shop.shop.entity.Account;
import java.util.*;


public interface UserRepository extends JpaRepository<Account, Integer> {

    Optional<Account> findById(int id);

    Optional<Account> findByUsername(String username);

    
}

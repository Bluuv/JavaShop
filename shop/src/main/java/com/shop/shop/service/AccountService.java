package com.shop.shop.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.shop.shop.entity.Account;
import com.shop.shop.entity.MySession;
import com.shop.shop.entity.Product;
import com.shop.shop.repository.ProductRepository;
import com.shop.shop.repository.UserRepository;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

import java.util.Optional;

@Service
@AllArgsConstructor
@NoArgsConstructor
public class AccountService{
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private MySession session;
    @Autowired
    private ProductService productService;
    @Autowired
    private ProductRepository productRepository;

    public AccountService (UserRepository userRepository, ProductService productService, ProductRepository productRepository){
        this.userRepository = userRepository;
        this.productService = productService;
        this.productRepository = productRepository;
    }

     public boolean createAccount(Account account){
        Account acc = userRepository.save(account);

        if (acc!=null){
            return true;
        }
        return false;
     }

     public MySession login(Account reqAccount){
        Optional<Account> opacc = userRepository.findByUsername(reqAccount.getUsername());
        Account dbAccount = opacc.orElse(null);

        if (dbAccount != null && dbAccount.getPassword().equals(reqAccount.getPassword())){
            session.setSessionAccount(dbAccount);

        }else {
            throw new RuntimeException("Invalid username or password.");
        }
        return session;
     }

     public boolean logout(){
        Account acc = session.getSessionAccount();
        if (acc!=null){
            session.clearSession();
            return true;
        }
        return false;
     }

     public boolean addMoney(Double cash){
        Account acc = session.getSessionAccount();
        if (acc!=null){
            acc.setMoney(acc.getMoney() + cash);
            userRepository.save(acc);
            return true;
        }
        return false;
     }

     public boolean addToShoppingCart(int id){
        Product product = productService.findProductById(id);
        if (product!=null && product.getQuantity()>0){
            session.addToCart(product);
            return true;
        }
        return false;
     }

}
package com.shop.shop.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.shop.shop.entity.Account;
import com.shop.shop.entity.MySession;
import com.shop.shop.entity.Product;
import com.shop.shop.repository.ProductRepository;
import com.shop.shop.repository.UserRepository;
import java.util.Optional;

@Service
public class AccountService{
    private UserRepository userRepository;
    private MySession session;
    private ProductService productService;
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

        if (dbAccount != null && dbAccount.getHash().equals(reqAccount.getHash())){
            session = new MySession();
            session.setSessionAccount(dbAccount);

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

     @Transactional
     public boolean buyCart(){
        
        double cartSum = session.getCart().stream().mapToDouble(p -> p.getPrice()).sum();
        if (cartSum <= session.getSessionAccount().getMoney()){
            session.getSessionAccount().setMoney(session.getSessionAccount().getMoney()-cartSum);
            userRepository.save(session.getSessionAccount());

            session.getCart().forEach(p -> productRepository.updateQuantity(p.getQuantity()-1, p.getId()));
            return true;
        }
        return false;
     }
}
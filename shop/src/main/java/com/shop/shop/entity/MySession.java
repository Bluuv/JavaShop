package com.shop.shop.entity;

import java.util.ArrayList;
import java.util.List;

import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.stereotype.Component;
import org.springframework.web.context.annotation.ApplicationScope;
import org.springframework.web.context.annotation.SessionScope;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@Component
@Data
@NoArgsConstructor
@AllArgsConstructor
@ApplicationScope
public class MySession {

    private Account account;
    private List<Product> cart;


    public void setSessionAccount(Account account){
        this.account=account;
    }

    public Account getSessionAccount(){
        return this.account;
    }

    public void clearSession(){
        this.account = null;
    }
    
    public void addToCart(Product product){
        if (this.cart==null){
            this.cart = new ArrayList<>();
        }
        this.cart.add(product);
    }

    public List<Product> getCart(){
        return this.cart;
    }
}

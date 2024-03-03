package com.shop.shop.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.shop.shop.entity.Account;
import com.shop.shop.entity.MySession;
import com.shop.shop.entity.Product;
import com.shop.shop.service.AccountService;
import com.shop.shop.service.ProductService;
import org.springframework.web.bind.annotation.PostMapping;


@Controller
public class ShopController {

    private ProductService productService;
    private AccountService accountService;
    private MySession session;

    public ShopController (ProductService productService, AccountService accountService) {
        this.productService = productService;
        this.accountService = accountService;
    }

    @GetMapping("/shop/product")
    @ResponseBody
    public Product getProduct(@RequestParam(name="id") int id){
        Product product = productService.findProductById(id);
        return product;
    }

    @PostMapping("/shop/addproduct")
    public ResponseEntity<Product> addProduct(@RequestBody(required = true) Product product){
        boolean dbresult = productService.createProduct(product);
        if (dbresult==true){
            return ResponseEntity.ok().build();
        }
        return ResponseEntity.status(HttpStatus.CONFLICT).build();
    }

    @PostMapping("/shop/register")
    public ResponseEntity<Account> registerAccount(@RequestBody Account account) {
         boolean dbresult = accountService.createAccount(account);
         if(dbresult==true){
            return ResponseEntity.ok().build();
         }
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();

    }

    @PostMapping("/shop/login")
    public ResponseEntity<Account> loginAccount (@RequestBody Account account){
        MySession result = accountService.login(account);

        if(result!=null){
            return ResponseEntity.ok().build();
        }
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
    }

    @GetMapping("/shop/logout")
    public ResponseEntity<Account> logoutAccount () {
        boolean result = accountService.logout();

        if(result==true){
            return ResponseEntity.ok().build();
        }
        return ResponseEntity.status(HttpStatus.CONFLICT).build();
    }

    @PostMapping("/shop/manage-account/add-money")
    public ResponseEntity<Account> addMoney (@RequestParam(name="money") double mon){
        boolean result = accountService.addMoney(mon);

        if(result==true){
            return ResponseEntity.ok().build();
        }
        return ResponseEntity.status(HttpStatus.NOT_IMPLEMENTED).build();
    }

    @PostMapping("/shop/add-to-cart")
    public ResponseEntity<Account> addToShoppingCart(@RequestParam(name="product-id") int id){
        boolean result = accountService.addToShoppingCart(id);

        if(result==true){
            return ResponseEntity.ok().build();
        }
        return ResponseEntity.status(HttpStatus.CONFLICT).build();
    }

    @PostMapping("/shop/buy-cart")
    public ResponseEntity<Account> buyShoppingCart (){
        boolean result = accountService.buyCart();

        if(result==true){
            return ResponseEntity.ok().build();
        }
        return ResponseEntity.status(HttpStatus.CONFLICT).build();
    }
}
    

    
       

    
    

    

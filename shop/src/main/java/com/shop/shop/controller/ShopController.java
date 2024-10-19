package com.shop.shop.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.shop.shop.dto.ProductInCartDTO;
import com.shop.shop.entity.Account;
import com.shop.shop.entity.Cart;
import com.shop.shop.entity.MySession;
import com.shop.shop.entity.Product;
import com.shop.shop.service.AccountService;
import com.shop.shop.service.CartService;
import com.shop.shop.service.ProductService;
import org.springframework.web.bind.annotation.PostMapping;

@CrossOrigin(origins = "*")
@RestController
public class ShopController {

    private ProductService productService;
    private AccountService accountService;
    private CartService cartService;
    private MySession session;

    public ShopController (ProductService productService, AccountService accountService, 
                            CartService cartService, MySession session) {
        this.productService = productService;
        this.accountService = accountService;
        this.cartService = cartService;
        this.session = session;
    }

    

    @GetMapping("/shop/product")
    @ResponseBody
    public Product getProduct(@RequestParam(name="id") int id){
        Product product = productService.findProductById(id);
        return product;
    }


    @GetMapping("/shop")
    @ResponseBody
    public List<Product> getAllProducts(){
        List<Product> product = productService.findAllProducts();
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
        accountService.login(account);

        if(session!=null && session.getAccount()!=null){
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


    @PostMapping("/addproducttocart")
    public ResponseEntity<Cart> addProductToCart
    (@RequestParam(name="product-id") int productId,
    @RequestParam(name = "quantity", defaultValue = "1") int quantity) {
        Cart updatedCart = cartService.addProductToCart(productId, quantity);
        
        return ResponseEntity.ok(updatedCart);
    }

    @PostMapping("/incrementproduct")
    public ResponseEntity<Cart> incrementProductInCart(@RequestParam(name="product-id") int productId){
        Cart updatedCart = cartService.incrementProductInCart(productId);
        return ResponseEntity.ok(updatedCart);
    }

    @PostMapping("/decrementproduct")
    public ResponseEntity<Cart> decrementProductInCart(@RequestParam(name="product-id") int productId){
        Cart updatedCart = cartService.decrementProductInCart(productId);
        return ResponseEntity.ok(updatedCart);
    }

    @PostMapping("/checkout")
    public ResponseEntity<String> checkout () {
        try{
            cartService.checkout();
            return ResponseEntity.ok("Purchase completed successfully");
        }catch (RuntimeException e){
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @DeleteMapping("/removeproductfromcart")
    public ResponseEntity<Cart> removeProductFromCart(@RequestParam(name="product-id") int productId){
        try{
            Cart updatedCart = cartService.removeProductFromCart(productId);
            return ResponseEntity.ok(updatedCart);
        }catch (RuntimeException e){
            return ResponseEntity.badRequest().body(null);            
        }
    }
    
    @DeleteMapping("/clearcart")
    public ResponseEntity<Cart> clearCart(){
        try{
            Cart clearedCart = cartService.clearCart();
            return ResponseEntity.ok(clearedCart);
        }catch (RuntimeException e){
            return ResponseEntity.badRequest().body(null);                
        }
    }


    @GetMapping("/cart")
    public ResponseEntity<List<ProductInCartDTO>> getProductsInCart() {
        try {
            List<ProductInCartDTO> productsInCart = cartService.getProductsInCart();
            return ResponseEntity.ok(productsInCart);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(null);
        }
}

    

}
    

    
       

    
    

    

package com.shop.shop.service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.shop.shop.dto.ProductInCartDTO;
import com.shop.shop.entity.Account;
import com.shop.shop.entity.Cart;
import com.shop.shop.entity.CartProduct;
import com.shop.shop.entity.MySession;
import com.shop.shop.entity.Product;
import com.shop.shop.repository.CartRepository;
import com.shop.shop.repository.ProductRepository;
import com.shop.shop.repository.UserRepository;

@Service
public class CartService {
    
    @Autowired
    private CartRepository cartRepository;
    @Autowired
    private ProductRepository productRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private MySession mySession;

    public Cart addProductToCart(int productId, int quantity) {
        Account account = mySession.getSessionAccount();
        if (account == null) {
            throw new RuntimeException("You need to log in");
        }

        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new RuntimeException("Product not found"));

        Optional<Cart> optionalCart = cartRepository.findByAccount(account);
        
        Cart cart;
        if (optionalCart.isPresent()) {
            cart = optionalCart.get();
        } else {
            cart = new Cart();
            cart.setAccount(account);
        }


        CartProduct cartProduct = cart.getCartProducts().stream()
                .filter(cp -> cp.getProduct().getId() == productId)
                .findFirst()
                .orElse(null);

        if (cartProduct != null) {

            cartProduct.setQuantity(cartProduct.getQuantity() + quantity);
        } else {

            cartProduct = new CartProduct();
            cartProduct.setCart(cart);
            cartProduct.setProduct(product);
            cartProduct.setQuantity(quantity);
            cart.getCartProducts().add(cartProduct);
        }

        cart.calculateTotalPrice(); 
        return cartRepository.save(cart); 
    }

    public Cart incrementProductInCart(int productId){

        Account account = mySession.getSessionAccount();
        if (account == null) {
            throw new RuntimeException("You need to log in");
        }

        Optional<Cart> optionalCart = cartRepository.findByAccount(account);

        if(optionalCart.isEmpty()){
            throw new RuntimeException("Cart not found for user");
        }

        Cart cart = optionalCart.get();

        CartProduct cartProduct = cart.getCartProducts().stream()
                        .filter(cp -> cp.getProduct().getId() == productId)
                        .findFirst().orElseThrow(() -> new RuntimeException("Product not found in cart"));
        
        cartProduct.setQuantity(cartProduct.getQuantity() + 1);

        cart.calculateTotalPrice();

        return cartRepository.save(cart); 
    }

    public Cart decrementProductInCart(int productId){

        Account account = mySession.getSessionAccount();
        if (account == null) {
            throw new RuntimeException("You need to log in");
        }

        Optional<Cart> optionalCart = cartRepository.findByAccount(account);

        if(optionalCart.isEmpty()){
            throw new RuntimeException("Cart not found for user");
        }

        Cart cart = optionalCart.get();

        CartProduct cartProduct = cart.getCartProducts().stream()
                        .filter(cp -> cp.getProduct().getId() == productId)
                        .findFirst().orElseThrow(() -> new RuntimeException("Product not found in cart"));
        
        if(cartProduct.getQuantity() > 1){
            cartProduct.setQuantity(cartProduct.getQuantity() - 1);
        } else{
            cart.getCartProducts().remove(cartProduct);
        }

        cart.calculateTotalPrice();

        return cartRepository.save(cart); 
    }

 
    public Cart checkout(){

        Account account = mySession.getSessionAccount();

        if(account == null){
            throw new RuntimeException("You need to log in");
        }

        Optional<Cart> optionalCart = cartRepository.findByAccount(account);

        if(optionalCart.isEmpty()){
            throw new RuntimeException("Cart not found for user");
        }

        Cart cart = optionalCart.get();

        if(account.getMoney() < cart.getTotalPrice()){
            throw new RuntimeException("Insufficient funds. Please recharge your account's wallet");
        }

        for (CartProduct cartProduct : cart.getCartProducts()){
            Product product = cartProduct.getProduct();
            double availableStock = product.getQuantity();
            double quantityToPurchase = cartProduct.getQuantity();

            if(availableStock < quantityToPurchase){
                throw new RuntimeException("Insufficient stock for product: " + product.getName());
            }

            product.setQuantity(availableStock - quantityToPurchase);
            productRepository.save(product);
        }

        account.setMoney(account.getMoney() - cart.getTotalPrice());
        userRepository.save(account);

        cart.getCartProducts().clear();
        cart.calculateTotalPrice();
        cartRepository.save(cart);

        return cart;
    }

    public Cart removeProductFromCart(int productId){

        Account account = mySession.getSessionAccount();

        if(account == null){
            throw new RuntimeException("You need to log in");
        }

        Optional<Cart> optionalCart = cartRepository.findByAccount(account);
        if(optionalCart.isEmpty()){
            throw new RuntimeException("Cart not found for user");
        }

        Cart cart = optionalCart.get();

        CartProduct cartProduct = cart.getCartProducts().stream()
        .filter(cp -> cp.getProduct().getId() == productId)
        .findFirst().orElseThrow(() -> new RuntimeException("Product not found in cart"));

        cart.getCartProducts().remove(cartProduct);
        cart.calculateTotalPrice();

        return cartRepository.save(cart);

    }

    public Cart clearCart(){

        Account account = mySession.getSessionAccount();

        if(account == null){
            throw new RuntimeException("You need to log in");
        }

        Optional<Cart> optionalCart = cartRepository.findByAccount(account);
        if(optionalCart.isEmpty()){
            throw new RuntimeException("Cart not found for user");
        }   

        Cart cart = optionalCart.get();

        cart.getCartProducts().clear();
        cart.calculateTotalPrice();

        return cartRepository.save(cart);
    }


    public Cart saveCart(Cart cart){
        return cartRepository.save(cart);
    }

    public List<ProductInCartDTO> getProductsInCart() {

    Account account = mySession.getSessionAccount();

    if (account == null) {
        throw new RuntimeException("You need to log in to view the cart");
    }


    Optional<Cart> optionalCart = cartRepository.findByAccount(account);

    if (optionalCart.isEmpty()) {
        throw new RuntimeException("Cart not found for the current user");
    }

    Cart cart = optionalCart.get();


    return cart.getCartProducts().stream()
               .map(cartProduct -> new ProductInCartDTO(cartProduct.getProduct(), cartProduct.getQuantity()))
               .collect(Collectors.toList());
    }
    
}

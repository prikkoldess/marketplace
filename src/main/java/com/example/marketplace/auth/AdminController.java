package com.example.marketplace.auth;

import java.util.List;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.marketplace.basket.BasketService;
import com.example.marketplace.basket.dto.AdminBasketDto;
import com.example.marketplace.order.OrderService;
import com.example.marketplace.order.OrderStatus;
import com.example.marketplace.order.dto.CheckoutGroupDto;
import com.example.marketplace.order.dto.OrderDto;
import com.example.marketplace.product.ProductService;
import com.example.marketplace.product.dto.ProductDto;
import com.example.marketplace.user.UserService;

@RestController
@RequestMapping("admin")
public class AdminController {
    private final BasketService basketService;
    private final OrderService orderService;
    private final UserService userService;
    private final ProductService productService;

    public AdminController(BasketService basketService, OrderService orderService, UserService userService,
            ProductService productService) {
        this.basketService = basketService;
        this.orderService = orderService;
        this.userService = userService;
        this.productService = productService;
    }

    @GetMapping("/get-user-basket")
    public AdminBasketDto getUserBasketForAdmin(@RequestParam Long userId) {
        return basketService.getUserBasketForAdmin(userId);
    }

    @GetMapping("/buyer/orders")
    public List<CheckoutGroupDto> getBuyerOrders(@RequestParam Long userId) {
        return orderService.getBuyerOrders(userId);
    }

    @GetMapping("/seller/orders")
    public List<OrderDto> getSellerOrders(@RequestParam Long userId) {
        return orderService.getSellerOrders(userId);
    }

    @PatchMapping("/order/status")
    public void updateOrderStatusForAdmin(
            @RequestParam OrderStatus status,
            @RequestParam Long orderId,
            @RequestParam Long userId) {

        orderService.updateOrderStatusForAdmin(status, orderId, userId);
    }

    @PatchMapping("/users/block")
    public void blockUser(@RequestParam Long userId) {
        userService.blockUser(userId);
    }

    @PatchMapping("/users/activate")
    public void activateUser(@RequestParam Long userId) {
        userService.activateUser(userId);
    }

    @GetMapping("seller/proucts")
    public List<ProductDto> getAllSellerProduct(@RequestParam Long userId) {
        return productService.getAllSellerProduct(userId);
    }

    @GetMapping("/products")
    public List<ProductDto> getAllProducts() {
        return productService.getAllProducts();
    }

    @PatchMapping("/product/status/hide")
    public void hideProductByAdmin(@RequestParam Long productId) {
        productService.hideProductByAdmin(productId);
    }

    @PatchMapping("/product/status/unlock")
    public void unlockProduct(@RequestParam Long productId) {
        productService.unlockProductByAdmin(productId);
    }

    @DeleteMapping("/product/delete")
    public void deleteProductByAdmin(@RequestParam Long productId) {
        productService.deleteProductByAdmin(productId);
    }

}

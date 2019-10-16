package com.atguigu.gmall.cart.service;

import com.atguigu.gmall.pms.vo.Cart;

import java.util.List;

public interface CartService {
    void addCart(Cart cart);

    List<Cart> queryCart();

    void deleteCart(Long skuId);
}

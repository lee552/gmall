package com.atguigu.gmall.cart.controller;


import com.atguigu.core.bean.Resp;
import com.atguigu.gmall.cart.service.CartService;
import com.atguigu.gmall.pms.vo.Cart;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("cart")
public class CartController {

    @Autowired
    private CartService cartService;

    @PostMapping
    public Resp<Object> addCart(@RequestBody Cart cart){
        cartService.addCart(cart);

        return Resp.ok(null);
    }

    @GetMapping
    public Resp<List<Cart>> queryCart(){
        List<Cart> carts = cartService.queryCart();
        return Resp.ok(carts);
    }


    @GetMapping("{skuId}")
    public Resp<Object> deleteCart(@PathVariable("skuId")Long skuId){
        cartService.deleteCart(skuId);
        return Resp.ok(null);

    }
}

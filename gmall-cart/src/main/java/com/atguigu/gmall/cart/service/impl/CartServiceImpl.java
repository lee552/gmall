package com.atguigu.gmall.cart.service.impl;

import com.alibaba.fastjson.JSON;
import com.atguigu.core.bean.Resp;
import com.atguigu.gmall.cart.feign.GmallPmsFeign;
import com.atguigu.gmall.cart.interceptor.LoginInterceptor;
import com.atguigu.gmall.cart.service.CartService;
import com.atguigu.gmall.pms.vo.Cart;
import com.atguigu.gmall.cart.vo.UserInfo;
import com.atguigu.gmall.pms.entity.SkuInfoEntity;
import com.atguigu.gmall.pms.entity.SkuSaleAttrValueEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.BoundHashOperations;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class CartServiceImpl implements CartService {

    @Autowired
    private StringRedisTemplate redisTemplate;

    @Autowired
    private GmallPmsFeign gmallPmsFeign;

    public static final String CART_PREFIX = "GMALL:CART:";

    public static final String CURRENT_PRICE = "GMALL:CART:CURRENTPRICE:";

    @Override
    public void addCart(Cart cart) {

        UserInfo userInfo = LoginInterceptor.getUserInfo();

        String key = getKey(userInfo);
        BoundHashOperations<String, Object, Object> ops = redisTemplate.boundHashOps(key);

        Integer count = cart.getCount();
        Long skuId = cart.getSkuId();

        if(ops.hasKey(skuId.toString())){
            cart = JSON.parseObject(ops.get(skuId.toString()).toString(), Cart.class);

            cart.setCount(cart.getCount()+count);


        }else {
            Resp<SkuInfoEntity> skuInfoEntityResp = gmallPmsFeign.infoSku(skuId);
            SkuInfoEntity skuInfoEntity = skuInfoEntityResp.getData();
            Resp<List<SkuSaleAttrValueEntity>> saleAttrResp = gmallPmsFeign.queryAttrInfo(skuId);
            List<SkuSaleAttrValueEntity> attrValueEntities = saleAttrResp.getData();
            if (skuInfoEntity != null){
                cart.setDefaultImage(skuInfoEntity.getSkuDefaultImg());
                cart.setSkuId(skuId);
                cart.setPrice(skuInfoEntity.getPrice());
                cart.setCount(count);
                if(attrValueEntities != null) {
                    cart.setSkuAttrValue(attrValueEntities);
                }
                cart.setSkuSaleVO(null);
            }
            redisTemplate.opsForValue().setIfAbsent(CURRENT_PRICE+skuInfoEntity.getSkuId(),skuInfoEntity.getPrice().toString());
        }
            ops.put(skuId.toString(),JSON.toJSONString(cart));

    }



    @Override
    public List<Cart> queryCart() {

        UserInfo userInfo = LoginInterceptor.getUserInfo();

        Long id = userInfo.getId();
        String userKey = userInfo.getUserKey();

        String unLoginkey = CART_PREFIX+userKey;
        List<Cart> carts = new ArrayList<>();
        BoundHashOperations<String, Object, Object> unLoginops = redisTemplate.boundHashOps(unLoginkey);
        if(unLoginops != null){
            List<Object> cartsJsonStrings = unLoginops.values();
            carts = cartsJsonStrings.stream().map(cart -> {
                Cart cart1 = JSON.parseObject(cart.toString(), Cart.class);
                String currentPriceString = redisTemplate.opsForValue().get(CURRENT_PRICE + cart1.getSkuId());
                cart1.setCurrentPrice(new BigDecimal(currentPriceString));
                return cart1;
            }).collect(Collectors.toList());
        }

        if(id == null){
            return carts;
        }

        String loginKey = CART_PREFIX+id;
        BoundHashOperations<String, Object, Object> loginOps = redisTemplate.boundHashOps(loginKey);

        if(carts != null){
            carts.forEach(cart -> {
                Long skuId = cart.getSkuId();
                Integer count = cart.getCount();
                if(loginOps.hasKey(skuId.toString())){
                    cart = JSON.parseObject(loginOps.get(skuId.toString()).toString(), Cart.class);
                    cart.setCount(cart.getCount()+count);
                }
                loginOps.put(skuId.toString(),JSON.toJSONString(cart));

            });
            redisTemplate.delete(unLoginkey);
        }

        List<Object> objects = loginOps.values();
        carts = objects.stream().map(cartString -> {

            Cart cart = JSON.parseObject(cartString.toString(), Cart.class);
            String currentPriceString = redisTemplate.opsForValue().get(CURRENT_PRICE + cart.getSkuId());
            cart.setCurrentPrice(new BigDecimal(currentPriceString));
            return cart;
        }).collect(Collectors.toList());
        return carts;
    }

    @Override
    public void deleteCart(Long skuId) {
        UserInfo userInfo = LoginInterceptor.getUserInfo();
        String key = getKey(userInfo);
        BoundHashOperations<String, Object, Object> ops = redisTemplate.boundHashOps(key);
        ops.delete(skuId.toString());


    }


    private String getKey(UserInfo userInfo) {
        String userKey = userInfo.getUserKey();
        Long id = userInfo.getId();
        String key = id == null ?userKey:id.toString();
        key = CART_PREFIX+key;
        return key;
    }
}

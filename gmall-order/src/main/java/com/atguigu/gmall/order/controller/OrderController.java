package com.atguigu.gmall.order.controller;


import com.atguigu.core.bean.Resp;
import com.atguigu.gmall.order.service.OrderService;
import com.atguigu.gmall.order.vo.OrderConfirmVO;
import com.atguigu.gmall.order.vo.OrderSubmitResponseVO;
import com.atguigu.gmall.oms.entity.OrderSubmitVO;
import com.atguigu.gmall.order.vo.PayAsyncVo;
import com.atguigu.gmall.order.vo.PayVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("order")
public class OrderController {

    @Autowired
    private OrderService orderService;

    @PostMapping("confirm")
    public Resp<OrderConfirmVO> orderConfirm( @RequestBody Map<Long,Integer> map){
        OrderConfirmVO orderConfirmVO = this.orderService.orderConfirm(map);
        return Resp.ok(orderConfirmVO);
    }


    @PostMapping("submit")
    public Resp<OrderSubmitResponseVO> orderSubmit(@RequestBody OrderSubmitVO orderSubmitVO){
        OrderSubmitResponseVO orderSubmitResponseVO = this.orderService.orderSubmit(orderSubmitVO);

        return Resp.ok(orderSubmitResponseVO);
    }

    @PostMapping("pay")
    public Resp<Object> orderPay(@RequestBody OrderSubmitResponseVO orderSubmitResponseVO){
        String result = orderService.orderPay(orderSubmitResponseVO);
        return Resp.ok(result);
    }

    @RequestMapping("pay/alipay/success")
    public Resp<Object> paySuccess(PayAsyncVo payAsyncVo){

        this.orderService.paySuccess(payAsyncVo);

        return Resp.ok("支付成功！");
    }

}


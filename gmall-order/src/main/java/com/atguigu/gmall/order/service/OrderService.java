package com.atguigu.gmall.order.service;

import com.atguigu.gmall.order.vo.OrderConfirmVO;
import com.atguigu.gmall.order.vo.OrderSubmitResponseVO;
import com.atguigu.gmall.oms.entity.OrderSubmitVO;
import com.atguigu.gmall.order.vo.PayAsyncVo;

import java.util.Map;

public interface OrderService {
    OrderConfirmVO orderConfirm(Map<Long, Integer> map);

    OrderSubmitResponseVO orderSubmit(OrderSubmitVO orderSubmitVO);

    String orderPay(OrderSubmitResponseVO orderSubmitResponseVO);

    void paySuccess(PayAsyncVo payAsyncVo);
}

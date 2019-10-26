package com.atguigu.gmall.oms.entity;

import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
public class OrderSubmitVO {

    //提交上次订单确认页给你的令牌；
    private String orderToken;

    private BigDecimal totalPrice; // 校验总价格时，拿计算价格和这个价格比较

    private Integer payType;//0-在线支付  1-货到付款

    private String delivery_company; // 配送方式

    private List<OrderItemVO> orderItems; // 订单清单

    // 地址信息，不需要id及memberId
    private String name;
    private String phone;
    private String postCode;
    private String province;
    private String city;
    private String region;
    private String detailAddress;
    private String areacode;
    private Integer defaultStatus;

    // TODO：发票相关信息略

    // TODO：营销信息等

}
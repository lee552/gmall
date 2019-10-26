package com.atguigu.gmall.wms.entity;

import lombok.Data;

@Data
public class SkuLockVO {

    private Long skuId;

    private Long wareSkuId; // wms_ware_sku表的主键

    private Integer num; // 锁定数量

    private String orderToken; // 那个订单（订单编号）
}
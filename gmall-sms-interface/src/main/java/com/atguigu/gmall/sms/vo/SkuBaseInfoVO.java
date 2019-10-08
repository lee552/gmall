package com.atguigu.gmall.sms.vo;

import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
public class SkuBaseInfoVO {
    private Long skuId;

    private BigDecimal buyBounds;

    private BigDecimal growBounds;

    private Integer fullCount;

    private BigDecimal discount;

    private BigDecimal fullPrice;

    private BigDecimal reducePrice;

    private Integer ladderAddOther;

    private Integer fullAddOther;

    private List<Integer> work;
}

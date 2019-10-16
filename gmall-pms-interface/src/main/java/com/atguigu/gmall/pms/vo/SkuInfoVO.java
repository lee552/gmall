package com.atguigu.gmall.pms.vo;

import com.atguigu.gmall.pms.entity.SkuInfoEntity;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
public class SkuInfoVO extends SkuInfoEntity{

    private List<String> images;

    private List<SkuSaleVO> saleAttrs;

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

package com.atguigu.gmall.pms.vo;

import com.atguigu.gmall.pms.entity.SpuInfoEntity;
import lombok.Data;

import java.util.List;

@Data
public class SpuInfoVO extends SpuInfoEntity {
    /*id spuName spuDescription catalogId brandId publishStatus createTime uodateTime*/

    private List<String> spuImages;

    private List<ProductAttrValueVO> baseAttrs;

    private List<SkuInfoVO> skus;
}

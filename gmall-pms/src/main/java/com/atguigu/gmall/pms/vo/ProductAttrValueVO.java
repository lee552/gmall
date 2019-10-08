package com.atguigu.gmall.pms.vo;

import com.atguigu.gmall.pms.entity.ProductAttrValueEntity;
import lombok.Data;
import org.apache.commons.lang3.StringUtils;

import java.util.List;

@Data
public class ProductAttrValueVO extends ProductAttrValueEntity {


    public void setValueSelected(List<String> valueSelected) {
        String value = StringUtils.join(valueSelected, ",");
        setAttrValue(value);
    }
}

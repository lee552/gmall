package com.atguigu.gmall.pms.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 基本属性名及值
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class BaseAttrVO {

    private Long attrId;
    private String attrName;
    private String[]  attrValues;
}
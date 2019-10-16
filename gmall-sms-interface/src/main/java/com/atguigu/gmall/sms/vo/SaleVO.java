package com.atguigu.gmall.sms.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 所有的优惠信息
 */
@Data

public class SaleVO {

    // 0-优惠券    1-满减    2-阶梯
    private Integer type;

    private String name;//促销信息/优惠券的名字

}
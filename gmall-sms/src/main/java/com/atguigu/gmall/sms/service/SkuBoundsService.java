package com.atguigu.gmall.sms.service;

import com.atguigu.gmall.sms.vo.SkuBaseInfoVO;
import com.baomidou.mybatisplus.extension.service.IService;
import com.atguigu.gmall.sms.entity.SkuBoundsEntity;
import com.atguigu.core.bean.PageVo;
import com.atguigu.core.bean.QueryCondition;


/**
 * 商品sku积分设置
 *
 * @author lee552
 * @email lee552@yeah.net
 * @date 2019-09-22 16:05:17
 */
public interface SkuBoundsService extends IService<SkuBoundsEntity> {

    PageVo queryPage(QueryCondition params);

    void saveBounds(SkuBaseInfoVO skuBaseInfoVO);
}


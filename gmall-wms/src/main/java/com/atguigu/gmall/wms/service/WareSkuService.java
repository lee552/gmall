package com.atguigu.gmall.wms.service;

import com.atguigu.gmall.wms.entity.SkuLockVO;
import com.baomidou.mybatisplus.extension.service.IService;
import com.atguigu.gmall.wms.entity.WareSkuEntity;
import com.atguigu.core.bean.PageVo;
import com.atguigu.core.bean.QueryCondition;

import java.util.List;


/**
 * 商品库存
 *
 * @author lee552
 * @email lee552@yeah.net
 * @date 2019-09-22 16:08:12
 */
public interface WareSkuService extends IService<WareSkuEntity> {

    PageVo queryPage(QueryCondition params);

    List<WareSkuEntity> queryWareSkuById(Long skuId);

    String checkAndLockStock(List<SkuLockVO> skuLockVOS);
}


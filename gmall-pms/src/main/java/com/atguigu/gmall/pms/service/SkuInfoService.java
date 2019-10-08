package com.atguigu.gmall.pms.service;

import com.atguigu.gmall.pms.entity.SpuInfoEntity;
import com.atguigu.gmall.pms.vo.SkuInfoVO;
import com.baomidou.mybatisplus.extension.service.IService;
import com.atguigu.gmall.pms.entity.SkuInfoEntity;
import com.atguigu.core.bean.PageVo;
import com.atguigu.core.bean.QueryCondition;

import java.util.List;


/**
 * sku信息
 *
 * @author lee552
 * @email lee552@yeah.net
 * @date 2019-09-22 16:03:31
 */
public interface SkuInfoService extends IService<SkuInfoEntity> {

    PageVo queryPage(QueryCondition params);

    List<SkuInfoEntity> querySkusBySpuId(Long spuId);

    Long saveSkuInfoAndGetSkuId(SpuInfoEntity spuInfoEntity, Long spuId, SkuInfoVO skuInfoVO);
}


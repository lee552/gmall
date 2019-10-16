package com.atguigu.gmall.pms.service;

import com.atguigu.gmall.pms.vo.SpuInfoVO;
import com.baomidou.mybatisplus.extension.service.IService;
import com.atguigu.gmall.pms.entity.SpuInfoEntity;
import com.atguigu.core.bean.PageVo;
import com.atguigu.core.bean.QueryCondition;


/**
 * spu信息
 *
 * @author lee552
 * @email lee552@yeah.net
 * @date 2019-09-22 16:03:31
 */
public interface SpuInfoService extends IService<SpuInfoEntity> {

    PageVo queryPage(QueryCondition params);

    PageVo querySpuByCarIdAndKey(QueryCondition condition,String key, Long catId);

    void saveSpuAndSku(SpuInfoVO spuInfoVO);

    Long saveSpuInfoAndGetSpuId(SpuInfoVO spuInfoVO, SpuInfoEntity spuInfoEntity);
}


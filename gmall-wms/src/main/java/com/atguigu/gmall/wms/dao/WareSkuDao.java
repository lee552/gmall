package com.atguigu.gmall.wms.dao;

import com.atguigu.gmall.wms.entity.SkuLockVO;
import com.atguigu.gmall.wms.entity.WareSkuEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.security.core.parameters.P;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * 商品库存
 * 
 * @author lee552
 * @email lee552@yeah.net
 * @date 2019-09-22 16:08:12
 */
@Mapper
@Component
public interface WareSkuDao extends BaseMapper<WareSkuEntity> {
    List<WareSkuEntity> checkSkuWare(SkuLockVO skuLockVO);

    long lock(@Param("id")Long id,@Param("num")Integer num);

    void unLock(SkuLockVO skuLockVO);


    void skuWareDown(SkuLockVO lockVO);
}

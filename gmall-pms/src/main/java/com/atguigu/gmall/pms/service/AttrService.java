package com.atguigu.gmall.pms.service;

import com.atguigu.gmall.pms.entity.SkuSaleAttrValueEntity;
import com.atguigu.gmall.pms.vo.AttrVO;
import com.atguigu.gmall.pms.vo.BaseGroupVO;
import com.baomidou.mybatisplus.extension.service.IService;
import com.atguigu.gmall.pms.entity.AttrEntity;
import com.atguigu.core.bean.PageVo;
import com.atguigu.core.bean.QueryCondition;

import java.util.List;


/**
 * 商品属性
 *
 * @author lee552
 * @email lee552@yeah.net
 * @date 2019-09-22 16:03:31
 */
public interface AttrService extends IService<AttrEntity> {

    PageVo queryPage(QueryCondition params);

    PageVo queryAttrByTypeOrCid(QueryCondition condition, Long cid, Integer type);

    void saveAttr(AttrVO attr);

    List<SkuSaleAttrValueEntity> queryAttrInfo(Long skuId);
}


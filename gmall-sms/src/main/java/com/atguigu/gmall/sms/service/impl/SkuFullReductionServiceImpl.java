package com.atguigu.gmall.sms.service.impl;

import com.atguigu.gmall.sms.vo.SkuBaseInfoVO;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.atguigu.core.bean.PageVo;
import com.atguigu.core.bean.Query;
import com.atguigu.core.bean.QueryCondition;

import com.atguigu.gmall.sms.dao.SkuFullReductionDao;
import com.atguigu.gmall.sms.entity.SkuFullReductionEntity;
import com.atguigu.gmall.sms.service.SkuFullReductionService;
import org.springframework.transaction.annotation.Transactional;


@Service("skuFullReductionService")
public class SkuFullReductionServiceImpl extends ServiceImpl<SkuFullReductionDao, SkuFullReductionEntity> implements SkuFullReductionService {

    @Override
    public PageVo queryPage(QueryCondition params) {
        IPage<SkuFullReductionEntity> page = this.page(
                new Query<SkuFullReductionEntity>().getPage(params),
                new QueryWrapper<SkuFullReductionEntity>()
        );

        return new PageVo(page);
    }
    @Autowired
    SkuFullReductionService skuFullReductionService;

    @Override
    @Transactional
    public void saveFullReduction(SkuBaseInfoVO skuBaseInfoVO) {
        SkuFullReductionEntity skuFullReductionEntity = new SkuFullReductionEntity();
        BeanUtils.copyProperties(skuBaseInfoVO,skuFullReductionEntity);
        skuFullReductionEntity.setAddOther(skuBaseInfoVO.getFullAddOther());
        skuFullReductionService.save(skuFullReductionEntity);

    }

}
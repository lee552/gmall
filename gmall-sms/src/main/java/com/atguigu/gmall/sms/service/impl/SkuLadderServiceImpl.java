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

import com.atguigu.gmall.sms.dao.SkuLadderDao;
import com.atguigu.gmall.sms.entity.SkuLadderEntity;
import com.atguigu.gmall.sms.service.SkuLadderService;
import org.springframework.transaction.annotation.Transactional;


@Service("skuLadderService")
public class SkuLadderServiceImpl extends ServiceImpl<SkuLadderDao, SkuLadderEntity> implements SkuLadderService {

    @Override
    public PageVo queryPage(QueryCondition params) {
        IPage<SkuLadderEntity> page = this.page(
                new Query<SkuLadderEntity>().getPage(params),
                new QueryWrapper<SkuLadderEntity>()
        );

        return new PageVo(page);
    }
    @Autowired
    SkuLadderService skuLadderService;

    @Override
    @Transactional
    public void saveLadder(SkuBaseInfoVO skuBaseInfoVO) {
        SkuLadderEntity skuLadderEntity = new SkuLadderEntity();
        BeanUtils.copyProperties(skuBaseInfoVO,skuLadderEntity);
        skuLadderEntity.setAddOther(skuBaseInfoVO.getLadderAddOther());
        skuLadderService.save(skuLadderEntity);
    }

}
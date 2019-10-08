package com.atguigu.gmall.sms.service.impl;

import com.atguigu.gmall.sms.vo.SkuBaseInfoVO;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.atguigu.core.bean.PageVo;
import com.atguigu.core.bean.Query;
import com.atguigu.core.bean.QueryCondition;

import com.atguigu.gmall.sms.dao.SkuBoundsDao;
import com.atguigu.gmall.sms.entity.SkuBoundsEntity;
import com.atguigu.gmall.sms.service.SkuBoundsService;
import org.springframework.transaction.annotation.Transactional;


@Service("skuBoundsService")
public class SkuBoundsServiceImpl extends ServiceImpl<SkuBoundsDao, SkuBoundsEntity> implements SkuBoundsService {

    @Override
    public PageVo queryPage(QueryCondition params) {
        IPage<SkuBoundsEntity> page = this.page(
                new Query<SkuBoundsEntity>().getPage(params),
                new QueryWrapper<SkuBoundsEntity>()
        );

        return new PageVo(page);
    }

    @Autowired
    SkuBoundsService skuBoundsService;

    @Override
    @Transactional
    public void saveBounds(SkuBaseInfoVO skuBaseInfoVO) {
        List<Integer> works = skuBaseInfoVO.getWork();
        Integer work = works.get(3)*1+works.get(2)*2+works.get(1)*4+works.get(0)*8;
        SkuBoundsEntity skuBoundsEntity = new SkuBoundsEntity();
        BeanUtils.copyProperties(skuBaseInfoVO,skuBoundsEntity);
        skuBoundsEntity.setWork(work);
        skuBoundsService.save(skuBoundsEntity);
    }

}
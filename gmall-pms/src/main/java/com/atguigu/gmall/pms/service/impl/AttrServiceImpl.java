package com.atguigu.gmall.pms.service.impl;

import com.atguigu.gmall.pms.dao.AttrAttrgroupRelationDao;
import com.atguigu.gmall.pms.dao.SkuInfoDao;
import com.atguigu.gmall.pms.dao.SkuSaleAttrValueDao;
import com.atguigu.gmall.pms.entity.AttrAttrgroupRelationEntity;
import com.atguigu.gmall.pms.entity.SkuInfoEntity;
import com.atguigu.gmall.pms.entity.SkuSaleAttrValueEntity;
import com.atguigu.gmall.pms.vo.AttrVO;
import com.atguigu.gmall.pms.vo.BaseGroupVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.atguigu.core.bean.PageVo;
import com.atguigu.core.bean.Query;
import com.atguigu.core.bean.QueryCondition;

import com.atguigu.gmall.pms.dao.AttrDao;
import com.atguigu.gmall.pms.entity.AttrEntity;
import com.atguigu.gmall.pms.service.AttrService;

import java.util.List;
import java.util.stream.Collectors;


@Service("attrService")
public class AttrServiceImpl extends ServiceImpl<AttrDao, AttrEntity> implements AttrService {
    @Autowired
    AttrAttrgroupRelationDao attrAttrgroupRelationDao;
    @Autowired
    AttrDao attrDao;

    @Override
    public PageVo queryPage(QueryCondition params) {
        IPage<AttrEntity> page = this.page(
                new Query<AttrEntity>().getPage(params),
                new QueryWrapper<AttrEntity>()
        );

        return new PageVo(page);
    }

    @Override
    public PageVo queryAttrByTypeOrCid(QueryCondition condition, Long cid, Integer type) {
        QueryWrapper<AttrEntity> wrapper = new QueryWrapper<>();

        wrapper.eq("catelog_id",cid).eq("attr_type",type);




        IPage<AttrEntity> page = this.page(
                new Query<AttrEntity>().getPage(condition),
                wrapper
        );

        return new PageVo(page);
    }

    @Override
    public void saveAttr(AttrVO attr) {
        attrDao.insert(attr);
        AttrAttrgroupRelationEntity attrAttrgroupRelationEntity = new AttrAttrgroupRelationEntity();
        attrAttrgroupRelationEntity.setAttrGroupId(attr.getAttrGroupId());
        attrAttrgroupRelationEntity.setAttrId(attr.getAttrId());
        attrAttrgroupRelationDao.insert(attrAttrgroupRelationEntity);

    }

    @Autowired
    private SkuInfoDao skuInfoDao;
    @Autowired
    private SkuSaleAttrValueDao skuSaleAttrValueDao;

    @Override
    public List<SkuSaleAttrValueEntity> queryAttrInfo(Long skuId) {

        List<SkuSaleAttrValueEntity> skuSaleAttrValueEntities = skuSaleAttrValueDao.selectList(new QueryWrapper<SkuSaleAttrValueEntity>().eq("sku_id", skuId));
        /*List<Long> attrIds = skuSaleAttrValueEntities.stream().map(skuSaleAttrValueEntity -> {
            return skuSaleAttrValueEntity.getAttrId();
        }).collect(Collectors.toList());

        List<AttrEntity> attrEntities = attrDao.selectList(new QueryWrapper<AttrEntity>().in("attr_id", attrIds));


        List<SkuInfoEntity> skuInfoEntities = skuInfoDao.selectList(new QueryWrapper<SkuInfoEntity>().eq("sku_id", skuId));*/


        return skuSaleAttrValueEntities;
    }


}
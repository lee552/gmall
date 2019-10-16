package com.atguigu.gmall.pms.service.impl;

import com.atguigu.gmall.pms.dao.*;
import com.atguigu.gmall.pms.entity.*;
import com.atguigu.gmall.pms.service.AttrAttrgroupRelationService;
import com.atguigu.gmall.pms.vo.AttrGroupVO;
import com.atguigu.gmall.pms.vo.BaseAttrVO;
import com.atguigu.gmall.pms.vo.BaseGroupVO;
import com.atguigu.gmall.sms.vo.SaleVO;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.atguigu.core.bean.PageVo;
import com.atguigu.core.bean.Query;
import com.atguigu.core.bean.QueryCondition;

import com.atguigu.gmall.pms.service.AttrGroupService;
import org.springframework.util.CollectionUtils;


@Service("attrGroupService")
public class AttrGroupServiceImpl extends ServiceImpl<AttrGroupDao, AttrGroupEntity> implements AttrGroupService {

    @Autowired
    AttrGroupDao attrGroupDao;
    @Autowired
    AttrAttrgroupRelationDao attrAttrgroupRelationDao;
    @Autowired
    AttrDao attrDao;
    @Override
    public PageVo queryPage(QueryCondition params) {
        IPage<AttrGroupEntity> page = this.page(
                new Query<AttrGroupEntity>().getPage(params),
                new QueryWrapper<AttrGroupEntity>()
        );

        return new PageVo(page);
    }

    @Override
    public PageVo queryAttrGroupByCatId(QueryCondition condition, Long catId) {

        IPage<AttrGroupEntity> page = this.page(
                new Query<AttrGroupEntity>().getPage(condition),
                new QueryWrapper<AttrGroupEntity>().eq("catelog_id",catId)
        );

        return new PageVo(page);
    }

    @Override
    public AttrGroupVO queryGroupAndAttrAndRelationByGid(Long gId) {
        AttrGroupVO attrGroupVO = new AttrGroupVO();

        //查询属性组
        AttrGroupEntity attrGroupEntity = attrGroupDao.selectById(gId);
        BeanUtils.copyProperties(attrGroupEntity,attrGroupVO);

        //查询中间表

        List<AttrAttrgroupRelationEntity> relationEntities = attrAttrgroupRelationDao.selectList(new QueryWrapper<AttrAttrgroupRelationEntity>().eq("attr_group_id", gId));
        if(!CollectionUtils.isEmpty(relationEntities)){
            attrGroupVO.setRelations(relationEntities);
        }


        //查询规格参数
        List<Long> idList = relationEntities.stream().map(t -> t.getAttrId()).collect(Collectors.toList());
        if(!CollectionUtils.isEmpty(idList)){
            List<AttrEntity> attrEntities = attrDao.selectBatchIds(idList);
            attrGroupVO.setAttrEntities(attrEntities);
        }



        return attrGroupVO;
    }

    @Override
    public List<AttrGroupVO> queryAttrGroupAndAttr(Long catId) {
        QueryWrapper<AttrGroupEntity> wrapper = new QueryWrapper<>();
        if(catId != 0 ){
            wrapper.eq("catelog_id", catId);
        }
        List<AttrGroupEntity> attrGroupEntities = attrGroupDao.selectList(wrapper);
        List<AttrGroupVO> attrGroupVOS = new ArrayList<AttrGroupVO>();
        attrGroupEntities.forEach(
                attrGroupEntity -> {
                    AttrGroupVO attrGroupVO = this.queryGroupAndAttrAndRelationByGid(attrGroupEntity.getAttrGroupId());
                    attrGroupVO.setRelations(null);
                    attrGroupVOS.add(attrGroupVO);
                });

        return attrGroupVOS;
    }

    @Override
    public AttrGroupEntity queryAttrGroupByAttrId(Long attrId) {

        AttrAttrgroupRelationEntity attrAttrgroupRelationEntity = attrAttrgroupRelationDao.selectOne(new QueryWrapper<AttrAttrgroupRelationEntity>().eq("attr_id", attrId));

        AttrGroupEntity attrGroupEntity = this.getById(attrAttrgroupRelationEntity.getAttrGroupId());

        return attrGroupEntity;
    }

    @Autowired
    private ProductAttrValueDao productAttrValueDao;

    @Autowired
    private SkuSaleAttrValueDao skuSaleAttrValueDao;

    @Override
    public List<BaseGroupVO> queryGroupWithAttrValueByCid(Long catId, Long spuId, Long skuId) {

        // 根据分类的id查询组及组下的规格参数
        List<AttrGroupVO> attrGroupVOS = this.queryAttrGroupAndAttr(catId);

        return attrGroupVOS.stream().map(attrGroupVO -> {
            BaseGroupVO groupVO = new BaseGroupVO();

            List<AttrEntity> attrEntities = attrGroupVO.getAttrEntities();
            if (!CollectionUtils.isEmpty(attrEntities)){
                List<Long> attrIds = attrEntities.stream().map(attrEntity -> attrEntity.getAttrId()).collect(Collectors.toList());

                // 根据attrId和spuId查询规格属性值
                List<ProductAttrValueEntity> productAttrValueEntities = this.productAttrValueDao.selectList(new QueryWrapper<ProductAttrValueEntity>().in("attr_id", attrIds).eq("spu_id", spuId));
                // 根据attrId和skuId查询销售属性值
                List<SkuSaleAttrValueEntity> skuSaleAttrValueEntities = this.skuSaleAttrValueDao.selectList(new QueryWrapper<SkuSaleAttrValueEntity>().in("attr_id", attrIds).eq("sku_id", skuId));

                List<BaseAttrVO> attrValueVOS = new ArrayList<>();

                // 通用的属性值
                if (!CollectionUtils.isEmpty(productAttrValueEntities)){
                    for (ProductAttrValueEntity productAttrValueEntity : productAttrValueEntities) {
                        attrValueVOS.add(new BaseAttrVO(productAttrValueEntity.getAttrId(), productAttrValueEntity.getAttrName(),productAttrValueEntity.getAttrValue().split(",")));
                    }

                }
                // 特殊的属性值
                if (!CollectionUtils.isEmpty(skuSaleAttrValueEntities)){
                    for (SkuSaleAttrValueEntity skuSaleAttrValueEntity : skuSaleAttrValueEntities) {
                        attrValueVOS.add(new BaseAttrVO(skuSaleAttrValueEntity.getAttrId(), skuSaleAttrValueEntity.getAttrName(), skuSaleAttrValueEntity.getAttrValue().split(",")));
                    }
                }
                groupVO.setAttrs(attrValueVOS);
            }
            groupVO.setName(attrGroupVO.getAttrGroupName());
            return groupVO;
        }).collect(Collectors.toList());

    }

}
package com.atguigu.gmall.pms.service.impl;

import com.atguigu.gmall.pms.dao.AttrAttrgroupRelationDao;
import com.atguigu.gmall.pms.dao.AttrDao;
import com.atguigu.gmall.pms.entity.AttrAttrgroupRelationEntity;
import com.atguigu.gmall.pms.entity.AttrEntity;
import com.atguigu.gmall.pms.vo.AttrGroupVO;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.atguigu.core.bean.PageVo;
import com.atguigu.core.bean.Query;
import com.atguigu.core.bean.QueryCondition;

import com.atguigu.gmall.pms.dao.AttrGroupDao;
import com.atguigu.gmall.pms.entity.AttrGroupEntity;
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

}
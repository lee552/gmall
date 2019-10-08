package com.atguigu.gmall.pms.service;

import com.atguigu.gmall.pms.vo.AttrGroupVO;
import com.baomidou.mybatisplus.extension.service.IService;
import com.atguigu.gmall.pms.entity.AttrGroupEntity;
import com.atguigu.core.bean.PageVo;
import com.atguigu.core.bean.QueryCondition;

import java.util.List;


/**
 * 属性分组
 *
 * @author lee552
 * @email lee552@yeah.net
 * @date 2019-09-22 16:03:31
 */
public interface AttrGroupService extends IService<AttrGroupEntity> {

    PageVo queryPage(QueryCondition params);

    PageVo queryAttrGroupByCatId(QueryCondition condition, Long catId);

    AttrGroupVO queryGroupAndAttrAndRelationByGid(Long gId);

    List<AttrGroupVO> queryAttrGroupAndAttr(Long catId);
}


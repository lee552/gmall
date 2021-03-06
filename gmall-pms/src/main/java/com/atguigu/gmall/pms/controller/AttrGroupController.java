package com.atguigu.gmall.pms.controller;

import java.util.Arrays;
import java.util.List;


import com.atguigu.core.bean.PageVo;
import com.atguigu.core.bean.QueryCondition;
import com.atguigu.core.bean.Resp;
import com.atguigu.gmall.pms.service.AttrAttrgroupRelationService;
import com.atguigu.gmall.pms.vo.AttrGroupVO;
import com.atguigu.gmall.pms.vo.BaseGroupVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import com.atguigu.gmall.pms.entity.AttrGroupEntity;
import com.atguigu.gmall.pms.service.AttrGroupService;




/**
 * 属性分组
 *
 * @author lee552
 * @email lee552@yeah.net
 * @date 2019-09-22 16:03:31
 */
@Api(tags = "属性分组 管理")
@RestController
@RequestMapping("pms/attrgroup")
public class AttrGroupController {
    @Autowired
    private AttrGroupService attrGroupService;


    @GetMapping("{catId}/{spuId}/{skuId}")
    public Resp<List<BaseGroupVO>> queryGroupWithAttrValueByCid(@PathVariable("catId")Long catId,@PathVariable("spuId")Long spuId,@PathVariable("skuId")Long skuId){

        List<BaseGroupVO> baseGroupVOS = attrGroupService.queryGroupWithAttrValueByCid(catId, spuId, skuId);

        return Resp.ok(baseGroupVOS);

    }


    @GetMapping("{attrId}")
    public Resp<AttrGroupEntity> queryAttrGroupByAttrId(@PathVariable("attrId")Long attrId){
        AttrGroupEntity attrGroupEntity = attrGroupService.queryAttrGroupByAttrId(attrId);

        return Resp.ok(attrGroupEntity);
    }

    @GetMapping("/withattrs/cat/{catId}")
    public Resp<List<AttrGroupVO>> queryAttrGroupAndAttr(@PathVariable(value = "catId")Long catId){

        List<AttrGroupVO> attrGroupVOS = attrGroupService.queryAttrGroupAndAttr(catId);
        return Resp.ok(attrGroupVOS);
    }


    @GetMapping("/withattr/{gid}")
    public Resp<AttrGroupVO> queryGroupAndAttrByGid(@PathVariable(value = "gid")Long gId){
        AttrGroupVO attrGroupVO = attrGroupService.queryGroupAndAttrAndRelationByGid(gId);
        return Resp.ok(attrGroupVO);
    }

    @ApiOperation("根据cId查询三级分类的分组")
    @GetMapping("/{catId}")
    public Resp<PageVo> queryAttrGroupByCatId(QueryCondition condition
                                              ,@PathVariable("catId")Long catId){

        PageVo pageVo = attrGroupService.queryAttrGroupByCatId(condition,catId);

        return Resp.ok(pageVo);
    }

    /**
     * 列表
     */
    @ApiOperation("分页查询(排序)")
    @GetMapping("/list")
    @PreAuthorize("hasAuthority('pms:attrgroup:list')")
    public Resp<PageVo> list(QueryCondition queryCondition) {
        PageVo page = attrGroupService.queryPage(queryCondition);

        return Resp.ok(page);
    }


    /**
     * 信息
     */
    @ApiOperation("详情查询")
    @GetMapping("/info/{attrGroupId}")
    @PreAuthorize("hasAuthority('pms:attrgroup:info')")
    public Resp<AttrGroupEntity> info(@PathVariable("attrGroupId") Long attrGroupId){
		AttrGroupEntity attrGroup = attrGroupService.getById(attrGroupId);

        return Resp.ok(attrGroup);
    }

    /**
     * 保存
     */
    @ApiOperation("保存")
    @PostMapping("/save")
    @PreAuthorize("hasAuthority('pms:attrgroup:save')")
    public Resp<Object> save(@RequestBody AttrGroupEntity attrGroup){
		attrGroupService.save(attrGroup);

        return Resp.ok(null);
    }

    /**
     * 修改
     */
    @ApiOperation("修改")
    @PostMapping("/update")
    @PreAuthorize("hasAuthority('pms:attrgroup:update')")
    public Resp<Object> update(@RequestBody AttrGroupEntity attrGroup){
		attrGroupService.updateById(attrGroup);

        return Resp.ok(null);
    }

    /**
     * 删除
     */
    @ApiOperation("删除")
    @PostMapping("/delete")
    @PreAuthorize("hasAuthority('pms:attrgroup:delete')")
    public Resp<Object> delete(@RequestBody Long[] attrGroupIds){
		attrGroupService.removeByIds(Arrays.asList(attrGroupIds));

        return Resp.ok(null);
    }

}

package com.atguigu.gmall.sms.controller;

import java.util.Arrays;


import com.atguigu.core.bean.PageVo;
import com.atguigu.core.bean.QueryCondition;
import com.atguigu.core.bean.Resp;
import com.atguigu.gmall.sms.vo.SaleVO;
import com.atguigu.gmall.sms.vo.SkuBaseInfoVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import com.atguigu.gmall.sms.entity.SkuLadderEntity;
import com.atguigu.gmall.sms.service.SkuLadderService;




/**
 * 商品阶梯价格
 *
 * @author lee552
 * @email lee552@yeah.net
 * @date 2019-09-22 16:05:17
 */
@Api(tags = "商品阶梯价格 管理")
@RestController
@RequestMapping("sms/skuladder")
public class SkuLadderController {
    @Autowired
    private SkuLadderService skuLadderService;

    @GetMapping("{skuId}")
    public Resp<SaleVO> querySkuladderBySkuId(@PathVariable("skuId")Long skuId){
        SaleVO saleVO = skuLadderService.querySkuladderBySkuId(skuId);

        return Resp.ok(saleVO);
    }

    /**
     * 列表
     */
    @ApiOperation("分页查询(排序)")
    @GetMapping("/list")
    @PreAuthorize("hasAuthority('sms:skuladder:list')")
    public Resp<PageVo> list(QueryCondition queryCondition) {
        PageVo page = skuLadderService.queryPage(queryCondition);

        return Resp.ok(page);
    }


    /**
     * 信息
     */
    @ApiOperation("详情查询")
    @GetMapping("/info/{id}")
    @PreAuthorize("hasAuthority('sms:skuladder:info')")
    public Resp<SkuLadderEntity> info(@PathVariable("id") Long id){
		SkuLadderEntity skuLadder = skuLadderService.getById(id);

        return Resp.ok(skuLadder);
    }

    /**
     * 保存
     */
    @ApiOperation("保存")
    @PostMapping("/save")
    @PreAuthorize("hasAuthority('sms:skuladder:save')")
    public Resp<Object> saveLadder(@RequestBody SkuBaseInfoVO skuBaseInfoVO){
		skuLadderService.saveLadder(skuBaseInfoVO);

        return Resp.ok(null);
    }

    /**
     * 修改
     */
    @ApiOperation("修改")
    @PostMapping("/update")
    @PreAuthorize("hasAuthority('sms:skuladder:update')")
    public Resp<Object> update(@RequestBody SkuLadderEntity skuLadder){
		skuLadderService.updateById(skuLadder);

        return Resp.ok(null);
    }

    /**
     * 删除
     */
    @ApiOperation("删除")
    @PostMapping("/delete")
    @PreAuthorize("hasAuthority('sms:skuladder:delete')")
    public Resp<Object> delete(@RequestBody Long[] ids){
		skuLadderService.removeByIds(Arrays.asList(ids));

        return Resp.ok(null);
    }

}

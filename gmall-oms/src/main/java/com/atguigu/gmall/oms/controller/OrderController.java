package com.atguigu.gmall.oms.controller;

import java.util.Arrays;
import java.util.Map;


import com.atguigu.core.bean.PageVo;
import com.atguigu.core.bean.QueryCondition;
import com.atguigu.core.bean.Resp;
import com.atguigu.gmall.oms.entity.OrderSubmitVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import com.atguigu.gmall.oms.entity.OrderEntity;
import com.atguigu.gmall.oms.service.OrderService;




/**
 * 订单
 *
 * @author lee552
 * @email lee552@yeah.net
 * @date 2019-09-22 16:01:12
 */
@Api(tags = "订单 管理")
@RestController
@RequestMapping("oms/order")
public class OrderController {
    @Autowired
    private OrderService orderService;

    @GetMapping("closeOrder/{orderToken}")
    public Resp<Object> closeOrder(@PathVariable("orderToken")String orderToken){
            int i = orderService.closeOrder(orderToken);

            return Resp.ok(i);
    }

    @PostMapping("{userId}")
    public Resp<OrderEntity> saveOrder(@RequestBody OrderSubmitVO orderSubmitVO, @PathVariable("userId")Long userId){

        OrderEntity orderEntity = this.orderService.saveOrder(orderSubmitVO, userId);

        return Resp.ok(orderEntity);
    }


    /**
     * 列表
     */
    @ApiOperation("分页查询(排序)")
    @GetMapping("/list")
    @PreAuthorize("hasAuthority('oms:order:list')")
    public Resp<PageVo> list(QueryCondition queryCondition) {
        PageVo page = orderService.queryPage(queryCondition);

        return Resp.ok(page);
    }


    /**
     * 信息
     */
    @ApiOperation("详情查询")
    @GetMapping("/info/{id}")
    @PreAuthorize("hasAuthority('oms:order:info')")
    public Resp<OrderEntity> info(@PathVariable("id") Long id){
		OrderEntity order = orderService.getById(id);

        return Resp.ok(order);
    }

    /**
     * 保存
     */
    @ApiOperation("保存")
    @PostMapping("/save")
    @PreAuthorize("hasAuthority('oms:order:save')")
    public Resp<Object> save(@RequestBody OrderEntity order){
		orderService.save(order);

        return Resp.ok(null);
    }

    /**
     * 修改
     */
    @ApiOperation("修改")
    @PostMapping("/update")
    @PreAuthorize("hasAuthority('oms:order:update')")
    public Resp<Object> update(@RequestBody OrderEntity order){
		orderService.updateById(order);

        return Resp.ok(null);
    }

    /**
     * 删除
     */
    @ApiOperation("删除")
    @PostMapping("/delete")
    @PreAuthorize("hasAuthority('oms:order:delete')")
    public Resp<Object> delete(@RequestBody Long[] ids){
		orderService.removeByIds(Arrays.asList(ids));

        return Resp.ok(null);
    }

}

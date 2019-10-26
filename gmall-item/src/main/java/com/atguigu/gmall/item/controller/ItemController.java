package com.atguigu.gmall.item.controller;

import com.atguigu.core.bean.Resp;
import com.atguigu.gmall.item.service.ItemService;
import com.atguigu.gmall.item.vo.ItemVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.concurrent.ExecutionException;

@RestController
@RequestMapping("item")
public class ItemController {

    @Autowired
    private ItemService itemService;


    @GetMapping("{skuId}")
    public Resp<ItemVO> getItem(@PathVariable("skuId")Long skuId) throws ExecutionException, InterruptedException {
        ItemVO itemVO = itemService.getItem(skuId);

        return Resp.ok(itemVO);
    }

}

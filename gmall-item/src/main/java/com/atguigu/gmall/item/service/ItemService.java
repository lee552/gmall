package com.atguigu.gmall.item.service;

import com.atguigu.gmall.item.vo.ItemVO;

import java.util.concurrent.ExecutionException;

public interface ItemService {
    ItemVO getItem(Long skuId) throws ExecutionException, InterruptedException;
}

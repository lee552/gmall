package com.atguigu.gmall.sms.api;

import com.atguigu.core.bean.Resp;
import com.atguigu.gmall.sms.vo.SaleVO;
import com.atguigu.gmall.sms.vo.SkuBaseInfoVO;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

public interface GmallSmsApi {
    @PostMapping("sms/skubounds/save")
    Resp<Object> saveBounds(@RequestBody SkuBaseInfoVO skuBaseInfoVO);

    @PostMapping("sms/skufullreduction/save")
    Resp<Object> saveFullReduction(@RequestBody SkuBaseInfoVO skuBaseInfoVO);

    @PostMapping("sms/skuladder/save")
    Resp<Object> saveLadder(@RequestBody SkuBaseInfoVO skuBaseInfoVO);

    @GetMapping("sms/skubounds/{skuId}")
    public Resp<SaleVO> queryBoundsBySkuId(@PathVariable("skuId")Long skuId);

    @GetMapping("sms/skufullreduction/{skuId}")
    public Resp<SaleVO> queryFullRedutionBySkuId(@PathVariable("skuId")Long skuId);

    @GetMapping("sms/skuladder/{skuId}")
    public Resp<SaleVO> querySkuladderBySkuId(@PathVariable("skuId")Long skuId);
}

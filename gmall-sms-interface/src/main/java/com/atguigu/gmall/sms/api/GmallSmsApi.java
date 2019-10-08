package com.atguigu.gmall.sms.api;

import com.atguigu.core.bean.Resp;
import com.atguigu.gmall.sms.vo.SkuBaseInfoVO;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

public interface GmallSmsApi {
    @PostMapping("sms/skubounds/save")
    Resp<Object> saveBounds(@RequestBody SkuBaseInfoVO skuBaseInfoVO);

    @PostMapping("sms/skufullreduction/save")
    Resp<Object> saveFullReduction(@RequestBody SkuBaseInfoVO skuBaseInfoVO);

    @PostMapping("sms/skuladder/save")
    Resp<Object> saveLadder(@RequestBody SkuBaseInfoVO skuBaseInfoVO);
}

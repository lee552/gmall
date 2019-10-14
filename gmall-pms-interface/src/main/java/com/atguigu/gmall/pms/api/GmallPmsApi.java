package com.atguigu.gmall.pms.api;

import com.atguigu.core.bean.QueryCondition;
import com.atguigu.core.bean.Resp;
import com.atguigu.gmall.pms.entity.*;
import com.atguigu.gmall.pms.vo.CategoryVO;
import org.springframework.web.bind.annotation.*;

import java.util.List;

public interface GmallPmsApi {
    @GetMapping("pms/skuinfo/info/{skuId}")
    public Resp<SkuInfoEntity> infoSku(@PathVariable("skuId") Long skuId);

    @GetMapping("pms/category/info/{catId}")
    public Resp<CategoryEntity> infoCat(@PathVariable("catId") Long catId);

    @GetMapping("pms/brand/info/{brandId}")
    public Resp<BrandEntity> infoBrand(@PathVariable("brandId") Long brandId);

    @GetMapping("pms/productattrvalue/{spuId}")
    public Resp<List<ProductAttrValueEntity>> querySearchAttrValueBySpuId(@PathVariable("spuId")Long spuId);

    @PostMapping("pms/spuinfo/{status}")
    public Resp<List<SpuInfoEntity>> querySpuByStatus(@RequestBody QueryCondition condition, @PathVariable("status")Integer status);

    @GetMapping("pms/skuinfo/{spuId}")
    public Resp<List<SkuInfoEntity>> querySkusBySpuId(@PathVariable(value = "spuId")Long spuId);

    @GetMapping("pms/category")
    public Resp<List<CategoryEntity>> categoryTree(@RequestParam(value = "level",defaultValue = "0")Integer level, @RequestParam(value = "parentCid",required = false)Integer parentCid);

    @GetMapping("pms/category/{pid}")
    public Resp<List<CategoryVO>> queryCatesAndSubs(@PathVariable("pid")Long pid);
}

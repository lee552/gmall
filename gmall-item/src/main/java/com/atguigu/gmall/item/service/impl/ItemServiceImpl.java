package com.atguigu.gmall.item.service.impl;

import com.atguigu.core.bean.Resp;
import com.atguigu.gmall.item.annotation.ItemCache;
import com.atguigu.gmall.item.feign.GmallPmsFeign;
import com.atguigu.gmall.item.feign.GmallSmsFeign;
import com.atguigu.gmall.item.feign.GmallWmsFeign;
import com.atguigu.gmall.item.service.ItemService;
import com.atguigu.gmall.item.vo.ItemVO;
import com.atguigu.gmall.pms.entity.ProductAttrValueEntity;
import com.atguigu.gmall.pms.entity.SkuInfoEntity;
import com.atguigu.gmall.pms.entity.SkuSaleAttrValueEntity;
import com.atguigu.gmall.pms.vo.BaseGroupVO;
import com.atguigu.gmall.sms.vo.SaleVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ThreadPoolExecutor;

@Service
public class ItemServiceImpl implements ItemService {

    @Autowired
    private GmallPmsFeign gmallPmsFeign;

    @Autowired
    private GmallSmsFeign gmallSmsFeign;

    @Autowired
    private GmallWmsFeign gmallWmsFeign;

    @Autowired
    private ThreadPoolExecutor threadPoolExecutor;

    @Override
    @ItemCache("GMALL:SKU:ITEM:")
    public ItemVO getItem(Long skuId) throws ExecutionException, InterruptedException {
        ItemVO item = new ItemVO();

        CompletableFuture<SkuInfoEntity> future = CompletableFuture.supplyAsync(() -> {
            Resp<SkuInfoEntity> skuInfoEntityResp = gmallPmsFeign.infoSku(skuId);
            SkuInfoEntity skuInfoEntity = skuInfoEntityResp.getData();
        /*  private Long skuId;
            private Long spuId;
            private Long catalogId;
            private Long brandId;
            private String skuTitle;
            private String skuSubtitle;
            private BigDecimal price;
            private BigDecimal weight;*/
            item.setSkuId(skuInfoEntity.getSkuId());
            item.setSpuId(skuInfoEntity.getSpuId());
            item.setCatalogId(skuInfoEntity.getCatalogId());
            item.setBrandId(skuInfoEntity.getBrandId());
            item.setSkuTitle(skuInfoEntity.getSkuTitle());
            item.setSkuSubtitle(skuInfoEntity.getSkuDesc());
            item.setPrice(skuInfoEntity.getPrice());
            item.setWeight(skuInfoEntity.getWeight());
            return skuInfoEntity;
        }, threadPoolExecutor);


        CompletableFuture<Void> imagesFuture = CompletableFuture.runAsync(() -> {
            Resp<List<String>> imagesResp = gmallPmsFeign.queryImagesBySkuId(skuId);
            List<String> images = imagesResp.getData();
            item.setPics(images);
        }, threadPoolExecutor);


        List<SaleVO> saleVOS = new ArrayList<>();

        CompletableFuture<Void> boundsFuture = CompletableFuture.runAsync(() -> {
            Resp<SaleVO> saleVOResp = gmallSmsFeign.queryBoundsBySkuId(skuId);
            saleVOS.add(saleVOResp.getData());
        }, threadPoolExecutor);


        CompletableFuture<Void> fullRedutionFuture = CompletableFuture.runAsync(() -> {
            Resp<SaleVO> saleVOResp = gmallSmsFeign.queryFullRedutionBySkuId(skuId);
            saleVOS.add(saleVOResp.getData());
        }, threadPoolExecutor);


        CompletableFuture<Void> ladderFuture = CompletableFuture.runAsync(() -> {
            Resp<SaleVO> saleVOResp = gmallSmsFeign.querySkuladderBySkuId(skuId);
            saleVOS.add(saleVOResp.getData());
        }, threadPoolExecutor);


        item.setSales(saleVOS);

        CompletableFuture<Void> SaleAttrFuture = CompletableFuture.runAsync(() -> {
            Resp<List<SkuSaleAttrValueEntity>> listResp = gmallPmsFeign.queryListBySkuId(skuId);
            List<SkuSaleAttrValueEntity> skuSaleAttrValueEntities = listResp.getData();
            item.setSaleAttrs(skuSaleAttrValueEntities);
        }, threadPoolExecutor);


        CompletableFuture<Void> productAttrFuture = future.thenAcceptAsync((skuInfoEntity) -> {
            Resp<List<ProductAttrValueEntity>> proDuctAttrValueResp = gmallPmsFeign.querySearchAttrValueBySpuId(skuInfoEntity.getSpuId());
            List<ProductAttrValueEntity> productAttrValueEntities = proDuctAttrValueResp.getData();
        }, threadPoolExecutor);


        CompletableFuture<Void> baseGroupFuture = future.thenAcceptAsync((skuInfoEntity) -> {
            Resp<List<BaseGroupVO>> baseGroupVOResp = gmallPmsFeign.queryGroupWithAttrValueByCid(skuInfoEntity.getCatalogId(), skuInfoEntity.getSpuId(), skuId);
            List<BaseGroupVO> baseGroupVOS = baseGroupVOResp.getData();
            item.setAttrGroups(baseGroupVOS);
        }, threadPoolExecutor);

        CompletableFuture.allOf(future, imagesFuture, boundsFuture, fullRedutionFuture, ladderFuture, SaleAttrFuture, productAttrFuture, baseGroupFuture).get();

        return item;
    }
}

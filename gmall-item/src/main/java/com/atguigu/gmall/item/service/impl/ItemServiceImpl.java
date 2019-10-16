package com.atguigu.gmall.item.service.impl;

import com.atguigu.core.bean.Resp;
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

@Service
public class ItemServiceImpl implements ItemService {

    @Autowired
    private GmallPmsFeign gmallPmsFeign;

    @Autowired
    private GmallSmsFeign gmallSmsFeign;

    @Autowired
    private GmallWmsFeign gmallWmsFeign;

    @Override
    public ItemVO getItem(Long skuId) {
        ItemVO item = new ItemVO();
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
        item.setCatalogId(skuInfoEntity.getCatalogId());
        item.setBrandId(skuInfoEntity.getBrandId());
        item.setSkuTitle(skuInfoEntity.getSkuTitle());
        item.setSkuSubtitle(skuInfoEntity.getSkuDesc());
        item.setPrice(skuInfoEntity.getPrice());
        item.setWeight(skuInfoEntity.getWeight());

        Resp<List<String>> imagesResp = gmallPmsFeign.queryImagesBySkuId(skuId);
        List<String> images = imagesResp.getData();
        item.setPics(images);

        List<SaleVO> saleVOS = new ArrayList<>();
        Resp<SaleVO> saleVOResp = gmallSmsFeign.queryBoundsBySkuId(skuId);
        saleVOS.add(saleVOResp.getData());
        Resp<SaleVO> saleVOResp1 = gmallSmsFeign.queryFullRedutionBySkuId(skuId);
        saleVOS.add(saleVOResp1.getData());
        Resp<SaleVO> saleVOResp2 = gmallSmsFeign.querySkuladderBySkuId(skuId);
        saleVOS.add(saleVOResp2.getData());
        item.setSales(saleVOS);

        Resp<List<SkuSaleAttrValueEntity>> listResp = gmallPmsFeign.queryListBySkuId(skuId);
        List<SkuSaleAttrValueEntity> skuSaleAttrValueEntities = listResp.getData();
        item.setSaleAttrs(skuSaleAttrValueEntities);

        Long spuId = skuInfoEntity.getSpuId();
        Resp<List<ProductAttrValueEntity>> proDuctAttrValueResp = gmallPmsFeign.querySearchAttrValueBySpuId(spuId);
        List<ProductAttrValueEntity> productAttrValueEntities = proDuctAttrValueResp.getData();

        Resp<List<BaseGroupVO>> baseGroupVOResp = gmallPmsFeign.queryGroupWithAttrValueByCid(skuInfoEntity.getCatalogId(), skuInfoEntity.getSpuId(), skuId);
        List<BaseGroupVO> baseGroupVOS = baseGroupVOResp.getData();
        item.setAttrGroups(baseGroupVOS);

        return item;
    }
}

package com.atguigu.gmall.search.listener;


import com.atguigu.core.bean.Resp;
import com.atguigu.gmall.pms.entity.BrandEntity;
import com.atguigu.gmall.pms.entity.CategoryEntity;
import com.atguigu.gmall.pms.entity.ProductAttrValueEntity;
import com.atguigu.gmall.pms.entity.SkuInfoEntity;
import com.atguigu.gmall.search.feign.GmallPmsFeign;
import com.atguigu.gmall.search.feign.GmallWmsFeign;
import com.atguigu.gmall.search.vo.GoodsVO;
import com.atguigu.gmall.search.vo.SpuAttributeValueVO;
import com.atguigu.gmall.wms.entity.WareSkuEntity;
import io.searchbox.client.JestClient;
import io.searchbox.core.Index;
import org.springframework.amqp.core.ExchangeTypes;
import org.springframework.amqp.rabbit.annotation.Exchange;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.QueueBinding;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
public class SpuListener {

    @Autowired
    private JestClient jestClient;
    @Autowired
    private GmallPmsFeign gmallPmsFeign;
    @Autowired
    private GmallWmsFeign gmallWmsFeign;

    @RabbitListener(bindings = @QueueBinding(
            value = @Queue(value = "SPU_SKU_TB_QUEUE",durable = "ture"),
            exchange = @Exchange(value = "SPU_SKU_OPTION_EXCHANGE",
                                ignoreDeclarationExceptions = "true",
            type = ExchangeTypes.TOPIC),
            key = "item.insert"
    ))
    public void recieveMessega(Map<String,Object> map){
        Long spuId = (Long)map.get("spuId");
        Resp<List<SkuInfoEntity>> skuInfoResp = this.gmallPmsFeign.querySkusBySpuId(spuId);
        List<SkuInfoEntity> skuInfoEntities = skuInfoResp.getData();
        if (!CollectionUtils.isEmpty(skuInfoEntities)){
            skuInfoEntities.forEach(skuInfoEntity -> {
                GoodsVO goodsVO = new GoodsVO();
                goodsVO.setId(skuInfoEntity.getSkuId());
                goodsVO.setName(skuInfoEntity.getSkuName());
                goodsVO.setPic(skuInfoEntity.getSkuDefaultImg());
                goodsVO.setPrice(skuInfoEntity.getPrice());
                goodsVO.setSale(0); // 销量，数据库暂没设计
                goodsVO.setSort(0);
                // 设置库存
                Resp<List<WareSkuEntity>> wareSkuResp = this.gmallWmsFeign.queryWareSkuById(skuInfoEntity.getSkuId());
                List<WareSkuEntity> wareSkuEntities = wareSkuResp.getData();
                if (!CollectionUtils.isEmpty(wareSkuEntities)) {
                    long sum = wareSkuEntities.stream().mapToLong(WareSkuEntity::getSkuId).sum();
                    goodsVO.setStock(sum);
                }
                // 设置品牌
                goodsVO.setBrandId(skuInfoEntity.getBrandId());
                if (skuInfoEntity.getBrandId() != null) {
                    Resp<BrandEntity> brandEntityResp = this.gmallPmsFeign.infoBrand(skuInfoEntity.getBrandId());
                    if (brandEntityResp.getData() != null) {
                        goodsVO.setBrandName(brandEntityResp.getData().getName());
                    }
                }
                // 设置分类
                goodsVO.setProductCategoryId(skuInfoEntity.getCatalogId());
                if (skuInfoEntity.getCatalogId() != null) {
                    Resp<CategoryEntity> categoryEntityResp = this.gmallPmsFeign.infoCat(skuInfoEntity.getCatalogId());
                    if (categoryEntityResp.getData() != null) {
                        goodsVO.setProductCategoryName(categoryEntityResp.getData().getName());
                    }
                }
                // 设置搜索的规格属性
                Resp<List<ProductAttrValueEntity>> listResp = this.gmallPmsFeign.querySearchAttrValueBySpuId(spuId);
                if (!CollectionUtils.isEmpty(listResp.getData())) {
                    List<SpuAttributeValueVO> spuAttributeValueVOS = listResp.getData().stream().map(productAttrValueEntity -> {
                        SpuAttributeValueVO spuAttributeValueVO = new SpuAttributeValueVO();
                        spuAttributeValueVO.setId(productAttrValueEntity.getId());
                        spuAttributeValueVO.setName(productAttrValueEntity.getAttrName());
                        spuAttributeValueVO.setValue(productAttrValueEntity.getAttrValue());
                        spuAttributeValueVO.setProductAttributeId(productAttrValueEntity.getAttrId());
                        spuAttributeValueVO.setSpuId(productAttrValueEntity.getSpuId());
                        return spuAttributeValueVO;
                    }).collect(Collectors.toList());
                    goodsVO.setAttrValueList(spuAttributeValueVOS);
                }

                Index action = new Index.Builder(goodsVO).index("goods").type("info").id(skuInfoEntity.getSkuId().toString()).build();
                try {
                    jestClient.execute(action);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });
        }
    }

}

package com.atguigu.gmall.search;

import com.atguigu.core.bean.QueryCondition;
import com.atguigu.core.bean.Resp;
import com.atguigu.gmall.pms.entity.*;
import com.atguigu.gmall.search.feign.GmallPmsFeign;
import com.atguigu.gmall.search.feign.GmallWmsFeign;
import com.atguigu.gmall.search.vo.GoodsVO;
import com.atguigu.gmall.search.vo.SpuAttributeValueVO;
import com.atguigu.gmall.wms.entity.WareSkuEntity;
import io.searchbox.client.JestClient;
import io.searchbox.core.Index;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.util.CollectionUtils;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

@RunWith(SpringRunner.class)
@SpringBootTest
public class GmallSearchApplicationTests {

    @Autowired
    JestClient jestClient;

    @Autowired
    private GmallPmsFeign gmallPmsFeign;

    @Autowired
    private GmallWmsFeign gmallWmsFeign;

    @Test
    public void importData() throws IOException {

        //System.out.println(gmallPmsFeign.(7l));
        Long pageSize = 100l;
        Long page = 1l;
        do {
            // 分页查询已上架商品，即spu中publish_status=1的商品
            QueryCondition queryCondition = new QueryCondition();
            queryCondition.setPage(1l);
            queryCondition.setLimit(10l);
            Resp<List<SpuInfoEntity>> resp = this.gmallPmsFeign.querySpuByStatus(queryCondition, 1);
            //System.out.println(resp);
            List<SpuInfoEntity> spuInfoEntities = resp.getData();
            // 当前页的记录数
            pageSize = (long)spuInfoEntities.size();

            spuInfoEntities.forEach(spuInfoEntity -> {
                Resp<List<SkuInfoEntity>> skuInfoResp = this.gmallPmsFeign.querySkusBySpuId(spuInfoEntity.getId());
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
                        Resp<List<ProductAttrValueEntity>> listResp = this.gmallPmsFeign.querySearchAttrValueBySpuId(spuInfoEntity.getId());
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
            });

            page++;
        } while (pageSize == 100); // 当前页记录数不能与100，则退出循环

    }

}

package com.atguigu.gmall.pms.service.impl;

import com.atguigu.gmall.pms.dao.SpuInfoDescDao;
import com.atguigu.gmall.pms.entity.*;
import com.atguigu.gmall.pms.feign.SmsFeign;
import com.atguigu.gmall.pms.service.*;
import com.atguigu.gmall.pms.vo.*;
import com.atguigu.gmall.sms.vo.SkuBaseInfoVO;
import io.seata.spring.annotation.GlobalTransactional;
import org.apache.commons.lang3.StringUtils;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.atguigu.core.bean.PageVo;
import com.atguigu.core.bean.Query;
import com.atguigu.core.bean.QueryCondition;

import com.atguigu.gmall.pms.dao.SpuInfoDao;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;


@Service("spuInfoService")
public class SpuInfoServiceImpl extends ServiceImpl<SpuInfoDao, SpuInfoEntity> implements SpuInfoService {


    @Override
    public PageVo queryPage(QueryCondition params) {
        IPage<SpuInfoEntity> page = this.page(
                new Query<SpuInfoEntity>().getPage(params),
                new QueryWrapper<SpuInfoEntity>()
        );

        return new PageVo(page);
    }

    @Override
    public PageVo querySpuByCarIdAndKey(QueryCondition condition,String key, Long catId) {

        QueryWrapper<SpuInfoEntity> queryWrapper = new QueryWrapper<>();

        if(catId != 0) {
            queryWrapper.eq("catalog_id",catId);
        }


        if(!StringUtils.isEmpty(key)){
            queryWrapper.and(t ->
             t.eq("id",key).or().like("spu_name",key)
            );
        }

        IPage<SpuInfoEntity> page = this.page(
                new Query<SpuInfoEntity>().getPage(condition),
                queryWrapper
        );

        return new PageVo(page);
    }

    @Autowired
    SpuInfoDescService spuInfoDescService;
    @Autowired
    ProductAttrValueService productAttrValueService;
    @Autowired
    SkuInfoService skuInfoService;
    @Autowired
    SkuSaleAttrValueService skuSaleAttrValueService;
    @Autowired
    SmsFeign smsFeign;
    @Autowired
    private AmqpTemplate amqpTemplate;

    @GlobalTransactional
    @Override
    public void saveSpuAndSku(SpuInfoVO spuInfoVO) {

        SpuInfoEntity spuInfoEntity = new SpuInfoEntity();

        //存储spu相关信息并返回主键

        Long spuId = this.saveSpuInfoAndGetSpuId(spuInfoVO, spuInfoEntity);

        List<SkuInfoVO> skus = spuInfoVO.getSkus();
        skus.forEach(skuInfoVO -> {
            //存储sku的相关信息并返回主键
            Long skuId = skuInfoService.saveSkuInfoAndGetSkuId(spuInfoEntity, spuId, skuInfoVO);

            SkuBaseInfoVO skuBaseInfoVO = new SkuBaseInfoVO();
            BeanUtils.copyProperties(skuInfoVO,skuBaseInfoVO);
            skuBaseInfoVO.setSkuId(skuId);

            smsFeign.saveBounds(skuBaseInfoVO);

            smsFeign.saveFullReduction(skuBaseInfoVO);

            smsFeign.saveLadder(skuBaseInfoVO);

        });

        String key = "insert";


        sendMessage(spuId, key);

        //int a = 1/0;



        /*skus.forEach(skuInfoVO -> {

        SkuInfoEntity skuInfoEntity = new SkuInfoEntity();
        BeanUtils.copyProperties(skuInfoVO,skuInfoEntity);
        skuInfoEntity.setBrandId(spuInfoEntity.getBrandId());
        skuInfoEntity.setSpuId(spuId);
        skuInfoEntity.setCatalogId(spuInfoEntity.getCatalogId());
        List<String> images = skuInfoVO.getImages();
        if(!CollectionUtils.isEmpty(images)){
            skuInfoEntity.setSkuDefaultImg(images.get(0));
        }

        skuInfoService.save(skuInfoEntity);

    });*/

        /*skus.forEach(skuInfoVO -> {
            List<String> images = skuInfoVO.getImages();
            images.forEach(image->{
                SkuImagesEntity skuImagesEntity = new SkuImagesEntity();
                if (StringUtils.equals(image,skuInfoEntities.get(0).getSkuDefaultImg())){
                    skuImagesEntity.setDefaultImg(1);
                }
                skuImagesEntity.setSkuId(skuId);
                skuImagesEntity.setImgSort(1);
                skuImagesEntity.setImgUrl(image);
                skuImagesService.save(skuImagesEntity);
            });

        });*/

        /*skus.forEach(skuInfoVO -> {



            });

        });
*/
    }

    private void sendMessage(Long spuId, String key) {
        Map<String,Object> map= new HashMap<>();
        map.put("spuId",spuId);
        map.put("key",key);
        amqpTemplate.convertAndSend("SPU_SKU_OPTION_EXCHANGE","item.insert",map);
    }

    @Transactional
    @Override
    public Long saveSpuInfoAndGetSpuId(SpuInfoVO spuInfoVO, SpuInfoEntity spuInfoEntity) {
        BeanUtils.copyProperties(spuInfoVO,spuInfoEntity);
        this.save(spuInfoEntity);

        Long spuId = spuInfoEntity.getId();

        //保存spu信息描述
        List<String> spuImages = spuInfoVO.getSpuImages();
        SpuInfoDescEntity spuInfoDescEntity = new SpuInfoDescEntity();
        spuInfoDescEntity.setSpuId(spuId);
        spuInfoDescEntity.setDecript(StringUtils.join(spuImages,","));
        spuInfoDescService.save(spuInfoDescEntity);

        //保存spu生产属性
        List<ProductAttrValueVO> baseAttrs = spuInfoVO.getBaseAttrs();
        baseAttrs.forEach(baseAttr -> {
            baseAttr.setSpuId(spuId);
            baseAttr.setAttrSort(1);
            baseAttr.setQuickShow(1);
            productAttrValueService.save(baseAttr);
        });
        return spuId;
    }




}
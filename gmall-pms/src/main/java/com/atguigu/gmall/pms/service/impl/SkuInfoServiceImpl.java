package com.atguigu.gmall.pms.service.impl;

import com.atguigu.gmall.pms.entity.*;
import com.atguigu.gmall.pms.service.AttrService;
import com.atguigu.gmall.pms.service.SkuImagesService;
import com.atguigu.gmall.pms.service.SkuSaleAttrValueService;
import com.atguigu.gmall.pms.vo.SkuInfoVO;
import com.atguigu.gmall.pms.vo.SkuSaleAttrValueVO;
import net.bytebuddy.asm.Advice;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.atguigu.core.bean.PageVo;
import com.atguigu.core.bean.Query;
import com.atguigu.core.bean.QueryCondition;

import com.atguigu.gmall.pms.dao.SkuInfoDao;
import com.atguigu.gmall.pms.service.SkuInfoService;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;


@Service("skuInfoService")
public class SkuInfoServiceImpl extends ServiceImpl<SkuInfoDao, SkuInfoEntity> implements SkuInfoService {

    @Autowired
    SkuInfoDao skuInfoDao;
    @Autowired
    SkuInfoService skuInfoService;
    @Autowired
    SkuImagesService skuImagesService;
    @Autowired
    AttrService attrService;
    @Autowired
    SkuSaleAttrValueService skuSaleAttrValueService;

    @Override
    public PageVo queryPage(QueryCondition params) {
        IPage<SkuInfoEntity> page = this.page(
                new Query<SkuInfoEntity>().getPage(params),
                new QueryWrapper<SkuInfoEntity>()
        );

        return new PageVo(page);
    }

    @Override
    public List<SkuInfoEntity> querySkusBySpuId(Long spuId) {
        QueryWrapper<SkuInfoEntity> wrapper = new QueryWrapper<>();
        wrapper.eq("spu_id",spuId);
        List<SkuInfoEntity> skuInfoEntities = skuInfoDao.selectList(wrapper);


        return skuInfoEntities;
    }

    @Transactional
    @Override
    public Long saveSkuInfoAndGetSkuId(SpuInfoEntity spuInfoEntity, Long spuId, SkuInfoVO skuInfoVO) {
        SkuInfoEntity skuInfoEntity = new SkuInfoEntity();
        BeanUtils.copyProperties(skuInfoVO, skuInfoEntity);
        skuInfoEntity.setBrandId(spuInfoEntity.getBrandId());
        skuInfoEntity.setSpuId(spuId);
        skuInfoEntity.setCatalogId(spuInfoEntity.getCatalogId());
        List<String> images = skuInfoVO.getImages();
        if (!CollectionUtils.isEmpty(images)) {
            skuInfoEntity.setSkuDefaultImg(images.get(0));
        }
        skuInfoService.save(skuInfoEntity);
        Long skuId = skuInfoEntity.getSkuId();
        //保存sku图片信息
        images.forEach(image->{
            SkuImagesEntity skuImagesEntity = new SkuImagesEntity();
            skuImagesEntity.setDefaultImg(0);
            if (StringUtils.equals(image,skuInfoEntity.getSkuDefaultImg())){
                skuImagesEntity.setDefaultImg(1);
            }
            skuImagesEntity.setSkuId(skuId);
            skuImagesEntity.setImgSort(1);
            skuImagesEntity.setImgUrl(image);
            skuImagesService.save(skuImagesEntity);
        });

        List<SkuSaleAttrValueVO> saleAttrs = skuInfoVO.getSaleAttrs();
        //保存sku销售属性信息
        saleAttrs.forEach(saleAttr->{
            SkuSaleAttrValueEntity skuSaleAttrValueEntity = new SkuSaleAttrValueEntity();
            skuSaleAttrValueEntity.setSkuId(skuId);
            skuSaleAttrValueEntity.setAttrId(saleAttr.getAttrId());
            skuSaleAttrValueEntity.setAttrValue(saleAttr.getAttrValue());
            skuSaleAttrValueEntity.setAttrSort(1);
            AttrEntity attrEntity = attrService.getById(saleAttr.getAttrId());
            skuSaleAttrValueEntity.setAttrName(attrEntity.getAttrName());
            skuSaleAttrValueService.save(skuSaleAttrValueEntity);
        });
        return skuId;
    }

}
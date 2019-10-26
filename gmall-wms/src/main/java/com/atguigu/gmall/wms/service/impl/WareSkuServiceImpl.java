package com.atguigu.gmall.wms.service.impl;

import com.alibaba.fastjson.JSON;
import com.atguigu.gmall.wms.entity.SkuLockVO;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.atguigu.core.bean.PageVo;
import com.atguigu.core.bean.Query;
import com.atguigu.core.bean.QueryCondition;

import com.atguigu.gmall.wms.dao.WareSkuDao;
import com.atguigu.gmall.wms.entity.WareSkuEntity;
import com.atguigu.gmall.wms.service.WareSkuService;
import org.springframework.util.CollectionUtils;


@Service("wareSkuService")
public class WareSkuServiceImpl extends ServiceImpl<WareSkuDao, WareSkuEntity> implements WareSkuService {
    @Autowired
    WareSkuDao wareSkuDao;

    @Autowired
    private StringRedisTemplate redisTemplate;

    @Autowired
    private RedissonClient redissonClient;


    public static final String STOCK_LOCK_PREFIX = "GMALL:WARE:LOCKKEY:";

    public static final String SKU_LOCK_PREFIX = "GMALL:SKU:ORDERLOCK:";

    @Override
    public PageVo queryPage(QueryCondition params) {
        IPage<WareSkuEntity> page = this.page(
                new Query<WareSkuEntity>().getPage(params),
                new QueryWrapper<WareSkuEntity>()
        );

        return new PageVo(page);
    }

    @Override
    public List<WareSkuEntity> queryWareSkuById(Long skuId) {
        QueryWrapper<WareSkuEntity> wrapper = new QueryWrapper<WareSkuEntity>().eq("sku_id", skuId);
        List<WareSkuEntity> wareSkuEntities = wareSkuDao.selectList(wrapper);
        return wareSkuEntities;
    }

    @Override
    public String checkAndLockStock(List<SkuLockVO> skuLockVOS) {
        List<SkuLockVO> lockVOs = new ArrayList<>();
        List<SkuLockVO> unLockVOs = new ArrayList<>();


        for (SkuLockVO skuLockVO : skuLockVOS) {
            lockSKu(lockVOs,unLockVOs,skuLockVO);
        }

        if (CollectionUtils.isEmpty(unLockVOs)){
            String orderToken = skuLockVOS.get(0).getOrderToken();
            redisTemplate.opsForValue().set(SKU_LOCK_PREFIX+orderToken, JSON.toJSONString(skuLockVOS));
            return null;
        }

        for (SkuLockVO lockVO : lockVOs) {
            wareSkuDao.unLock(lockVO);
        }
        List<Long> list = unLockVOs.stream().map((skuLockVO) -> skuLockVO.getSkuId()).collect(Collectors.toList());

        return "锁定商品:"+list.toString()+"失败，库存不足";





    }

    private void lockSKu(List<SkuLockVO> lockVOs, List<SkuLockVO> unLockVOs, SkuLockVO skuLockVO) {

        RLock lock = redissonClient.getFairLock(STOCK_LOCK_PREFIX + skuLockVO.getSkuId());
        lock.lock();

        List<WareSkuEntity> wareSkuEntities = wareSkuDao.checkSkuWare(skuLockVO);
        if(wareSkuEntities != null && wareSkuEntities.size()>0){

            WareSkuEntity wareSkuEntity = wareSkuEntities.get(0);
            long l = wareSkuDao.lock(wareSkuEntity.getId(), skuLockVO.getNum());
            if(l>0){
                skuLockVO.setWareSkuId(wareSkuEntity.getId());
                lockVOs.add(skuLockVO);
            }else{
                unLockVOs.add(skuLockVO);
            }

        }else {
            unLockVOs.add(skuLockVO);
        }

        lock.unlock();


    }

}
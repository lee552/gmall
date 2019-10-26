package com.atguigu.gmall.oms.service.impl;

import com.atguigu.core.bean.Resp;
import com.atguigu.gmall.oms.dao.OrderItemDao;
import com.atguigu.gmall.oms.entity.OrderItemEntity;
import com.atguigu.gmall.oms.entity.OrderItemVO;
import com.atguigu.gmall.oms.feign.GmallPmsFeign;
import com.atguigu.gmall.oms.feign.GmallUmsFeign;
import com.atguigu.gmall.oms.entity.OrderSubmitVO;
import com.atguigu.gmall.pms.entity.BrandEntity;
import com.atguigu.gmall.pms.entity.SkuInfoEntity;
import com.atguigu.gmall.pms.entity.SpuInfoEntity;
import com.atguigu.gmall.ums.entity.MemberEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.atguigu.core.bean.PageVo;
import com.atguigu.core.bean.Query;
import com.atguigu.core.bean.QueryCondition;

import com.atguigu.gmall.oms.dao.OrderDao;
import com.atguigu.gmall.oms.entity.OrderEntity;
import com.atguigu.gmall.oms.service.OrderService;


@Service("orderService")
public class OrderServiceImpl extends ServiceImpl<OrderDao, OrderEntity> implements OrderService {

    @Autowired
    private GmallUmsFeign gmallUmsFeign;
    @Autowired
    private OrderItemDao orderItemDao;
    @Autowired
    private GmallPmsFeign gmallPmsFeign;


    @Override
    public PageVo queryPage(QueryCondition params) {
        IPage<OrderEntity> page = this.page(
                new Query<OrderEntity>().getPage(params),
                new QueryWrapper<OrderEntity>()
        );

        return new PageVo(page);
    }

    @Override
    public OrderEntity saveOrder(OrderSubmitVO orderSubmitVO, Long userId) {
        OrderEntity orderEntity = new OrderEntity();
        orderEntity.setOrderSn(orderSubmitVO.getOrderToken());
        orderEntity.setCreateTime(new Date());
        Resp<MemberEntity> resp = gmallUmsFeign.info(userId);
        MemberEntity memberEntity = resp.getData();
        orderEntity.setMemberId(userId);
        orderEntity.setMemberUsername(memberEntity.getUsername());
        orderEntity.setTotalAmount(orderSubmitVO.getTotalPrice());
        orderEntity.setPayAmount(orderSubmitVO.getTotalPrice());
        orderEntity.setFreightAmount(new BigDecimal(20));
        orderEntity.setPayType(1);
        orderEntity.setSourceType(0);
        orderEntity.setStatus(0);
        orderEntity.setBillType(0);
        orderEntity.setBillReceiverPhone(orderSubmitVO.getPhone());
        orderEntity.setReceiverName(orderSubmitVO.getName());
        orderEntity.setReceiverPostCode(orderSubmitVO.getPostCode());
        orderEntity.setReceiverProvince(orderSubmitVO.getProvince());
        orderEntity.setReceiverCity(orderSubmitVO.getCity());
        orderEntity.setReceiverRegion(orderSubmitVO.getRegion());
        orderEntity.setReceiverDetailAddress(orderSubmitVO.getDetailAddress());
        orderEntity.setNote("请速速发货");

        this.save(orderEntity);

        List<OrderItemVO> orderItems = orderSubmitVO.getOrderItems();
        OrderItemEntity orderItemEntity = new OrderItemEntity();
        for (OrderItemVO orderItem : orderItems) {
            orderItemEntity.setOrderId(orderEntity.getId());
            orderItemEntity.setOrderSn(orderEntity.getOrderSn());
            Resp<SkuInfoEntity> skuInfoEntityResp = gmallPmsFeign.infoSku(orderItem.getSkuId());
            SkuInfoEntity skuInfoEntity = skuInfoEntityResp.getData();
            Resp<SpuInfoEntity> spuInfoEntityResp = gmallPmsFeign.info(skuInfoEntity.getSpuId());
            SpuInfoEntity spuInfoEntity = spuInfoEntityResp.getData();
            orderItemEntity.setSkuId(orderItem.getSkuId());
            orderItemEntity.setSpuId(skuInfoEntity.getSpuId());
            orderItemEntity.setSpuName(spuInfoEntity.getSpuName());
            orderItemEntity.setCategoryId(spuInfoEntity.getCatalogId());
            Resp<BrandEntity> brandEntityResp = gmallPmsFeign.infoBrand(spuInfoEntity.getBrandId());
            BrandEntity brandEntity = brandEntityResp.getData();
            orderItemEntity.setSpuBrand(brandEntity.getName());
            orderItemEntity.setSkuPic(skuInfoEntity.getSkuDefaultImg());
            orderItemEntity.setSkuPrice(skuInfoEntity.getPrice());
            orderItemEntity.setSkuQuantity(orderItem.getCount());

            orderItemDao.insert(orderItemEntity);
        }


        return orderEntity;
    }

    @Autowired
    private OrderDao orderDao;

    @Override
    public int closeOrder(String orderToken) {

        int i = orderDao.closeOrder(orderToken);

        return i;
    }

}
package com.atguigu.gmall.order.service.impl;

import com.alipay.api.AlipayApiException;
import com.atguigu.core.bean.Resp;
import com.atguigu.core.bean.UserInfo;
import com.atguigu.gmall.oms.entity.OrderEntity;
import com.atguigu.gmall.order.feign.*;
import com.atguigu.gmall.order.interceptor.LoginInterceptor;
import com.atguigu.gmall.order.service.OrderService;
import com.atguigu.gmall.order.vo.*;
import com.atguigu.gmall.oms.entity.OrderItemVO;
import com.atguigu.gmall.oms.entity.OrderSubmitVO;
import com.atguigu.gmall.pms.entity.SkuInfoEntity;
import com.atguigu.gmall.pms.entity.SkuSaleAttrValueEntity;
import com.atguigu.gmall.sms.vo.SaleVO;
import com.atguigu.gmall.ums.entity.MemberEntity;
import com.atguigu.gmall.ums.entity.MemberReceiveAddressEntity;
import com.atguigu.gmall.wms.entity.SkuLockVO;
import com.baomidou.mybatisplus.core.toolkit.IdWorker;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.BoundHashOperations;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.stream.Collectors;

@Service
public class OrderServiceImpl implements OrderService {

    @Autowired
    private GmallPmsFeign gmallPmsFeign;
    @Autowired
    private GmallUmsFeign gmallUmsFeign;
    @Autowired
    private GmallSmsFeign gmallSmsFeign;
    @Autowired
    private GmallWmsFeign gmallWmsFeign;
    @Autowired
    private GmallOmsFeign gmallOmsFeign;

    @Autowired
    private StringRedisTemplate redisTemplate;
    @Autowired
    private ThreadPoolExecutor threadPoolExecutor;
    @Autowired
    private JedisPool jedisPool;
    @Autowired
    private AmqpTemplate amqpTemplate;

    private static final String TOKEN_PREFIX = "order:token:";




    @Override
    public OrderConfirmVO orderConfirm(Map<Long, Integer> map) {
        OrderConfirmVO orderConfirmVO = new OrderConfirmVO();
        UserInfo userInfo = LoginInterceptor.getUserInfo();
        CompletableFuture<Void> orderItemsFuture = CompletableFuture.runAsync(() -> {
            List<OrderItemVO> orderItems = map.entrySet().stream().map(entry -> {
                Long skuId = entry.getKey();
                Integer count = entry.getValue();
                Resp<SkuInfoEntity> skuInfoEntityResp = gmallPmsFeign.infoSku(skuId);
                SkuInfoEntity skuInfoEntity = skuInfoEntityResp.getData();
                OrderItemVO orderItemVO = new OrderItemVO();
                orderItemVO.setSkuId(skuId);
                orderItemVO.setDefaultImage(skuInfoEntity.getSkuDefaultImg());
                orderItemVO.setCount(count);
                orderItemVO.setPrice(skuInfoEntity.getPrice());
                orderItemVO.setTitle(skuInfoEntity.getSkuTitle());
                Resp<List<SkuSaleAttrValueEntity>> attrResp = gmallPmsFeign.queryAttrInfo(skuId);
                orderItemVO.setSkuAttrValue(attrResp.getData());//商品规格参数
                Resp<SaleVO> boundsSaleVOResp = gmallSmsFeign.queryBoundsBySkuId(skuId);
                Resp<SaleVO> fullRedutionSaleVOResp = gmallSmsFeign.queryFullRedutionBySkuId(skuId);
                Resp<SaleVO> ladderSaleVOResp = gmallSmsFeign.querySkuladderBySkuId(skuId);
                orderItemVO.setSaleVOs(Arrays.asList(boundsSaleVOResp.getData(), fullRedutionSaleVOResp.getData(), ladderSaleVOResp.getData()));//商品营销信息

                return orderItemVO;

            }).collect(Collectors.toList());

            orderConfirmVO.setOrderItems(orderItems);


        }, threadPoolExecutor);

        CompletableFuture<Void> addressFutrue = CompletableFuture.runAsync(() -> {
            Resp<List<MemberReceiveAddressEntity>> reciveAddress = gmallUmsFeign.queryReciveAddress(userInfo.getId());
            orderConfirmVO.setAddresses(reciveAddress.getData());
        }, threadPoolExecutor);

        CompletableFuture<Void> IntegrationFuture = CompletableFuture.runAsync(() -> {
            Resp<MemberEntity> memberEntityResp = gmallUmsFeign.info(userInfo.getId());
            MemberEntity memberEntity = memberEntityResp.getData();
            orderConfirmVO.setBounds(memberEntity.getIntegration());
        }, threadPoolExecutor);


        String orderToken = IdWorker.getTimeId().toString();

        redisTemplate.opsForValue().set(TOKEN_PREFIX+orderToken,orderToken);
        orderConfirmVO.setOrderToken(orderToken);

        try {
            CompletableFuture.allOf(orderItemsFuture,addressFutrue,IntegrationFuture).get();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return orderConfirmVO;


    }

    @Override
    public OrderSubmitResponseVO orderSubmit(OrderSubmitVO orderSubmitVO) {
        OrderSubmitResponseVO responseVO = new OrderSubmitResponseVO();
        String orderToken = orderSubmitVO.getOrderToken();

        String script = "if redis.call('get', KEYS[1]) == ARGV[1] then return redis.call('del', KEYS[1]) else return 0 end";
        Jedis jedis = jedisPool.getResource();
        try {
            Long i = (Long)jedis.eval(script, Arrays.asList(TOKEN_PREFIX + orderToken), Arrays.asList(orderToken));

            if(i == 0){
                responseVO.setCode(1);
                return responseVO;
            }
        } finally {
            jedis.close();
        }

        BigDecimal totalPrice = orderSubmitVO.getTotalPrice(); //获取接受数据的总价格

        List<OrderItemVO> orderItems = orderSubmitVO.getOrderItems();
        if(CollectionUtils.isEmpty(orderItems)){
            responseVO.setCode(4);
            return responseVO;
        }

        BigDecimal currentPrice = new BigDecimal(0);
        for (OrderItemVO orderItem : orderItems) {
            Resp<SkuInfoEntity> skuInfoEntityResp = gmallPmsFeign.infoSku(orderItem.getSkuId());
            SkuInfoEntity skuInfoEntity = skuInfoEntityResp.getData();
            BigDecimal bigDecimal = skuInfoEntity.getPrice().multiply(new BigDecimal(orderItem.getCount()));
            currentPrice = currentPrice.add(bigDecimal);
        }

        if (totalPrice.compareTo(currentPrice) != 0){
            responseVO.setCode(3);
            return responseVO;
        }

        List<SkuLockVO> skuLockVOS = orderItems.stream().map((orderItemVO) -> {
            SkuLockVO skuLockVO = new SkuLockVO();
            skuLockVO.setNum(orderItemVO.getCount());
            skuLockVO.setSkuId(orderItemVO.getSkuId());
            skuLockVO.setOrderToken(orderSubmitVO.getOrderToken());
            return skuLockVO;
        }).collect(Collectors.toList());

        Resp<Object> resp = gmallWmsFeign.checkAndLockStock(skuLockVOS);

        Object data = resp.getData();
        if (data != null){
            responseVO.setCode(2);
            return responseVO;
        }

        UserInfo userInfo = LoginInterceptor.getUserInfo();

        try {

            CompletableFuture<Void> orderEntityFuture = CompletableFuture.runAsync(() -> {
                Resp<OrderEntity> orderEntityResp = gmallOmsFeign.saveOrder(orderSubmitVO, userInfo.getId());
                OrderEntity orderEntity = orderEntityResp.getData();

                responseVO.setOrderEntity(orderEntity);

            });

            CompletableFuture.allOf(orderEntityFuture).get();

        } catch (Exception e) {
            e.printStackTrace();
            responseVO.setCode(5);
            amqpTemplate.convertAndSend("GMALL_WARE_EXCHANGE","UNLOCK_WARE",orderToken);
            return responseVO;

        }

        List<Long> skuIds = orderItems.stream().map((itemVO) -> itemVO.getSkuId()).collect(Collectors.toList());
        Map<String,Object> map = new HashMap<>();
        map.put("skuIds",skuIds);
        map.put("userId",userInfo.getId());
        amqpTemplate.convertAndSend("GMALL_CART_EXCHANGE","CART_DELETE",map);

        amqpTemplate.convertAndSend("ORDER_TTL_DEAD_EXCHANGE","ORDER-CREATE",orderSubmitVO.getOrderToken());



        return responseVO;



    }

    @Autowired
    private AlipayTemplate alipayTemplate;

    @Override
    public String orderPay(OrderSubmitResponseVO orderSubmitResponseVO) {

        if(orderSubmitResponseVO.getCode() == null){
            PayVo payVo = new PayVo();
            OrderEntity orderEntity = orderSubmitResponseVO.getOrderEntity();
            payVo.setOut_trade_no(orderEntity.getOrderSn());
            payVo.setTotal_amount(orderEntity.getTotalAmount().toString());

            payVo.setSubject("gmall");
            payVo.setBody("gmall");
            try {
                String pay = alipayTemplate.pay(payVo);
                return pay;
            } catch (AlipayApiException e) {
                e.printStackTrace();

            }

        }
        return "支付出现异常";

    }

    @Override
    public void paySuccess(PayAsyncVo payAsyncVo) {
        amqpTemplate.convertAndSend("GMALL_ORDER_EXCHANGE","PAY_SUCCESS",payAsyncVo.getOut_trade_no());
    }


}

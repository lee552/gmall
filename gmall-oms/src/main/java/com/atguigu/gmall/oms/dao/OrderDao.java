package com.atguigu.gmall.oms.dao;

import com.atguigu.gmall.oms.entity.OrderEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Component;

/**
 * 订单
 * 
 * @author lee552
 * @email lee552@yeah.net
 * @date 2019-09-22 16:01:12
 */
@Mapper
@Component
public interface OrderDao extends BaseMapper<OrderEntity> {

    int closeOrder(String orderToken);

    int orderSuccess(String orderToken);

    Integer selectOrderByorderToken(String orderToken);
}

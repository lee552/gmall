package com.atguigu.gmall.oms.listener;


import com.atguigu.gmall.oms.dao.OrderDao;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.amqp.core.ExchangeTypes;
import org.springframework.amqp.rabbit.annotation.Exchange;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.QueueBinding;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class OrderListenner {

    @Autowired
    private OrderDao orderDao;
    @Autowired
    private AmqpTemplate amqpTemplate;


    @RabbitListener(queues = {"ORDER_DEAD_QUEUE"})
    public void orderClose(String orderToken){
       if(orderDao.selectOrderByorderToken(orderToken) == 0){
           orderDao.closeOrder(orderToken);

           amqpTemplate.convertAndSend("WARE_UNLOCK_EXCHANGE","WARE_UNLOCK",orderToken);
       }
    }

    @RabbitListener(bindings = @QueueBinding(value = @Queue(value = "ORDER_PAY_SUCCESS_QUEUE"),
                                             exchange = @Exchange(value = "GMALL_ORDER_EXCHANGE",ignoreDeclarationExceptions = "true",type = ExchangeTypes.TOPIC),
                                             key = "PAY_SUCCESS"))
    public void paySuccess(String orderToken){
            orderDao.orderSuccess(orderToken);
            amqpTemplate.convertAndSend("GMALL_WARE_EXCHANGE","WARE_DOWN",orderToken);
    }


}

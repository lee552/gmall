package com.atguigu.gmall.mms.listener;


import com.aliyuncs.CommonRequest;
import com.aliyuncs.CommonResponse;
import com.aliyuncs.DefaultAcsClient;
import com.aliyuncs.IAcsClient;
import com.aliyuncs.exceptions.ClientException;
import com.aliyuncs.exceptions.ServerException;
import com.aliyuncs.http.MethodType;
import com.aliyuncs.profile.DefaultProfile;
import org.springframework.amqp.core.ExchangeTypes;
import org.springframework.amqp.rabbit.annotation.Exchange;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.QueueBinding;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class MessageListener {

    @Autowired
    private StringRedisTemplate redisTemplate;


    @RabbitListener(bindings = @QueueBinding(value = @Queue(value = "REGIST_CODE_QUEUE"),
                                             exchange = @Exchange(value = "GMALL_CODE_EXCHANGE",ignoreDeclarationExceptions = "true",type = ExchangeTypes.TOPIC),
                                             key = "REGIST_CODE"))
    public void messageListen(Map<String,String> map){
        String code = map.get("code");
        String phoneNum = map.get("phoneNum");

        SendSms(phoneNum,code);

    }

    public static void SendSms(String phoneNum,String code) {



        DefaultProfile profile = DefaultProfile.getProfile("cn-hangzhou", "LTAI0kjiiI9srN3t", "AAYFHEqJd1oh7sQf9cZO70C4I7DFaS");
        IAcsClient client = new DefaultAcsClient(profile);

        CommonRequest request = new CommonRequest();
        request.setMethod(MethodType.POST);
        request.setDomain("dysmsapi.aliyuncs.com");
        request.setVersion("2017-05-25");
        request.setAction("SendSms");
        request.putQueryParameter("RegionId", "cn-hangzhou");
        request.putQueryParameter("PhoneNumbers", phoneNum);
        request.putQueryParameter("SignName", "天空之境");
        request.putQueryParameter("TemplateCode", "SMS_172695171");
        request.putQueryParameter("TemplateParam", "{'code':'"+code+"'}");
        try {
            CommonResponse response = client.getCommonResponse(request);
            System.out.println(response.getData());
        } catch (ServerException e) {
            e.printStackTrace();
        } catch (ClientException e) {
            e.printStackTrace();
        }
    }

}

package com.atguigu.gmall.gateway.config;

import com.atguigu.core.utils.RsaUtils;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;
import javax.annotation.PostConstruct;
import java.security.PublicKey;


@Component
@ConfigurationProperties("gateway.jwt")
@Data
@Slf4j
public class JwtProperties {
    /*pubKeyPath: D:\\tmp\\rsa\\rsa.pub
    cookieName: MEMBER_TOKEN*/
    private String pubKeyPath;

    private String cookieName;

    @PostConstruct
    public void init(){

        try {
            PublicKey publicKey = RsaUtils.getPublicKey(pubKeyPath);
        } catch (Exception e) {
            e.printStackTrace();
            log.error("初始化公钥失败");
            throw new RuntimeException();
        }

    }

}

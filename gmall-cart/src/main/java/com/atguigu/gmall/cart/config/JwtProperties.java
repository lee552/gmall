package com.atguigu.gmall.cart.config;


import com.atguigu.core.utils.RsaUtils;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.security.PublicKey;

@ConfigurationProperties("cart.jwt")
@Component
@Data
@Slf4j
public class JwtProperties {
    /*pubKeyPath: D:\\tmp\\rsa\\rsa.pub
    cookieName: MEMBER_TOKEN*/

    private String pubKeyPath;

    private String cookieName;

    private PublicKey publicKey;

    private String userKeyName;

    private String expire;


    @PostConstruct
    public void init(){

        try {
            this.publicKey = RsaUtils.getPublicKey(pubKeyPath);
        } catch (Exception e) {
            e.printStackTrace();
            log.error("初始化公钥失败");
        }


    }

}

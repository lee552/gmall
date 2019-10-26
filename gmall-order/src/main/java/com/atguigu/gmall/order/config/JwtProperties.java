package com.atguigu.gmall.order.config;


import com.atguigu.core.utils.JwtUtils;
import com.atguigu.core.utils.RsaUtils;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.security.PublicKey;

@ConfigurationProperties("order.jwt")
@Data
@Slf4j
@Component
public class JwtProperties {
/*  pubKeyPath: D:\\tmp\\rsa\\rsa.pub
    cookieName: MEMBER_TOKEN
    userKeyName: user_key
    expire: 43200*/
    private String pubKeyPath;

    private PublicKey publicKey;

    private String cookieName;

    private String uerKeyName;

    private Integer expire;


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

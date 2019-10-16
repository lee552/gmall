package com.atguigu.gmall.auth.service.impl;

import com.atguigu.core.bean.Resp;
import com.atguigu.core.utils.JwtUtils;
import com.atguigu.gmall.auth.config.JwtProperties;
import com.atguigu.gmall.auth.feign.GmallUmsFeign;
import com.atguigu.gmall.auth.service.AuthService;
import com.atguigu.gmall.ums.entity.MemberEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class AuthServiceImpl implements AuthService {

    @Autowired
    private GmallUmsFeign gmallUmsFeign;

    @Autowired
    private JwtProperties jwtProperties;

    @Override
    public String authentication(String username, String password) {
        Resp<MemberEntity> entityResp = gmallUmsFeign.query(username, password);
        MemberEntity memberEntity = entityResp.getData();
        if(memberEntity == null){
            return null;
        }
        Map<String,Object> map = new HashMap<>();
        map.put("id",memberEntity.getId());
        map.put("username",memberEntity.getUsername());
        String token = null;
        try {
            token = JwtUtils.generateToken(map, jwtProperties.getPrivateKey(), jwtProperties.getExpire());
        } catch (Exception e) {
            e.printStackTrace();
        }

        return token;
    }
}

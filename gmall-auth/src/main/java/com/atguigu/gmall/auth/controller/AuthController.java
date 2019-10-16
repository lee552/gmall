package com.atguigu.gmall.auth.controller;


import com.atguigu.core.bean.Resp;
import com.atguigu.core.utils.CookieUtils;
import com.atguigu.gmall.auth.config.JwtProperties;
import com.atguigu.gmall.auth.service.AuthService;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@RestController
@RequestMapping("auth")
@EnableConfigurationProperties({JwtProperties.class})
public class AuthController {

    @Autowired
    private AuthService authService;

    @Autowired
    private JwtProperties jwtProperties;

    @PostMapping("accredit")
    public Resp<Object> authentication(@RequestParam("username")String username,
                                       @RequestParam("password")String password,
                                       HttpServletRequest request, HttpServletResponse response){
            String token = authService.authentication(username,password);
            if (StringUtils.isEmpty(token)){
                return Resp.fail("用户名或密码错误");
            }
            CookieUtils.setCookie(request,response,jwtProperties.getCookieName(),token,jwtProperties.getExpire(),null,true);
            return Resp.ok("登陆成功");
    }
}

package com.atguigu.gmall.cart.interceptor;

import com.atguigu.core.utils.CookieUtils;
import com.atguigu.core.utils.JwtUtils;
import com.atguigu.gmall.cart.config.JwtProperties;
import com.atguigu.gmall.cart.vo.UserInfo;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Map;
import java.util.UUID;

@Component
@EnableConfigurationProperties(JwtProperties.class)
public class LoginInterceptor extends HandlerInterceptorAdapter {

    public static final ThreadLocal<UserInfo> threadLocal = new ThreadLocal<>();

    @Autowired
    private JwtProperties jwtProperties;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {

        String token = CookieUtils.getCookieValue(request, jwtProperties.getCookieName());
        String userKey = CookieUtils.getCookieValue(request, jwtProperties.getUserKeyName());

        if(StringUtils.isEmpty(token) && StringUtils.isEmpty(userKey)){
            userKey = UUID.randomUUID().toString();
            CookieUtils.setCookie(request,response,jwtProperties.getUserKeyName(),userKey,Integer.valueOf(jwtProperties.getExpire()));

        }

        UserInfo userInfo = new UserInfo();
        userInfo.setUserKey(userKey);

        if(!StringUtils.isEmpty(token)){
            Map<String, Object> info = JwtUtils.getInfoFromToken(token, jwtProperties.getPublicKey());
            ;
            userInfo.setId(new Long(info.get("id").toString()));
        }
        threadLocal.set(userInfo);

        return true;
    }

    public static UserInfo getUserInfo(){
        return threadLocal.get();
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        threadLocal.remove();
    }
}

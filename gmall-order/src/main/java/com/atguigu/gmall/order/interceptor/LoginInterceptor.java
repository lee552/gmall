package com.atguigu.gmall.order.interceptor;

import com.atguigu.core.bean.UserInfo;
import com.atguigu.core.utils.CookieUtils;
import com.atguigu.core.utils.JwtUtils;
import com.atguigu.gmall.order.config.JwtProperties;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Map;

@EnableConfigurationProperties({JwtProperties.class})
@Component
public class LoginInterceptor extends HandlerInterceptorAdapter {

    private static final ThreadLocal<UserInfo> theadLocal = new ThreadLocal();

    @Autowired
    private JwtProperties jwtProperties;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String token = CookieUtils.getCookieValue(request, jwtProperties.getCookieName());
        UserInfo userInfo = new UserInfo();
        if(StringUtils.isEmpty(token)){
            response.sendRedirect("http://localhost:2000/login?returnUrl="+request.getRequestURL());
        }else{
            try {
                Map<String, Object> map = JwtUtils.getInfoFromToken(token, jwtProperties.getPublicKey());
                userInfo.setId(Long.valueOf(map.get("id").toString()));
                theadLocal.set(userInfo);
            } catch (Exception e) {
                e.printStackTrace();
                response.sendRedirect("http://localhost:2000/login?returnUrl="+request.getRequestURL());
            }
        }
        return true;
    }

    public static UserInfo getUserInfo(){
        return theadLocal.get();
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        theadLocal.remove();
    }
}

package com.atguigu.gmall.gateway.filter;

import com.atguigu.core.utils.JwtUtils;
import com.atguigu.core.utils.RsaUtils;
import com.atguigu.gmall.gateway.config.JwtProperties;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.http.HttpCookie;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import org.springframework.util.MultiValueMap;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

@Component
@EnableConfigurationProperties({JwtProperties.class})
public class AuthGatewayFilter implements GatewayFilter {

    @Autowired
    private JwtProperties jwtProperties;

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {

        ServerHttpRequest request = exchange.getRequest();
        ServerHttpResponse response = exchange.getResponse();
        MultiValueMap<String, HttpCookie> cookies = request.getCookies();
        if(CollectionUtils.isEmpty(cookies) || !cookies.containsKey(jwtProperties.getCookieName())){

            response.setStatusCode(HttpStatus.UNAUTHORIZED);

            return response.setComplete();

        }

        HttpCookie cookie = cookies.getFirst(jwtProperties.getCookieName());

        try {
            JwtUtils.getInfoFromToken(cookie.getValue(),RsaUtils.getPublicKey(jwtProperties.getPubKeyPath()));
        } catch (Exception e) {
            e.printStackTrace();
            response.setStatusCode(HttpStatus.UNAUTHORIZED);

            return response.setComplete();
        }

        return chain.filter(exchange);
    }
}

package com.example.ag_gateway.config;

import jakarta.servlet.Filter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.context.request.RequestContextListener;
import org.springframework.web.filter.RequestContextFilter;
import org.springframework.web.servlet.function.RouterFunction;
import org.springframework.web.servlet.function.ServerResponse;

import static org.springframework.cloud.gateway.server.mvc.handler.GatewayRouterFunctions.route;
import static org.springframework.cloud.gateway.server.mvc.handler.HandlerFunctions.http;
import static org.springframework.cloud.gateway.server.mvc.predicate.GatewayRequestPredicates.path;


@Configuration
public class RouteConfig {

    private final String groupInvestmentUrl;
    private final String stockUrl;
    private final String investRefUrl;
    private final String chatUrl;
    private final String alarmUrl;

    public RouteConfig(@Value("${route.url.group-investment}")String groupInvestmentUrl,
                       @Value("${route.url.stock-system}")String stockUrl,
                       @Value("${route.url.invest-reference}")String investRefUrl,
                       @Value("${route.url.chatting}")String chatUrl,
                       @Value("${route.url.alarm}")String alarmUrl) {
        this.groupInvestmentUrl = groupInvestmentUrl;
        this.stockUrl = stockUrl;
        this.investRefUrl = investRefUrl;
        this.chatUrl = chatUrl;
        this.alarmUrl = alarmUrl;
    }

    @Bean
    public Filter requestContextFilter() {
        return new RequestContextFilter();
    }

    @Bean
    public RequestContextListener requestContextListener() {
        return new RequestContextListener();
    }

    @Bean
    public RouterFunction<ServerResponse> groupRouterFunctionsMethod() {
        return route("group-investment")
                .route(path("/api/users/**", "/api/groups/**", "/api/auth/**", "/api/teams/**"), http(groupInvestmentUrl))
                .build();
    }

    @Bean
    public RouterFunction<ServerResponse> stockRouter(){
        return route("stock-system")
                .route(path("/api/stocks/**"), http(stockUrl))
                .build();
    }

    @Bean
    public RouterFunction<ServerResponse> investRefRouter(){
        return route("invest-reference")
                .route(path("/api/news/**", "/api/suggestion/**"), http(investRefUrl))
                .build();
    }

    @Bean
    public RouterFunction<ServerResponse> chatRouter(){
        return route("chatting")
                .route(path("/api/chat/**"), http(chatUrl))
                .build();
    }

    @Bean
    public RouterFunction<ServerResponse> alarmRouter(){
        return route("alarm")
                .route(path("/api/alarm/**"), http(alarmUrl))
                .build();
    }

}

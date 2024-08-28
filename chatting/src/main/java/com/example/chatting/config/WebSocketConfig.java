package com.example.chatting.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    @Value("${chat.path}")
    private String path;

    @Value("localhost")
    private String rabbitmqHost;

    @Value("5672")
    private int rabbitmqPort;

    @Value("guest")
    private String rabbitmqUsername;

    @Value("guest")
    private String rabbitmqPassword;

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        registry.addEndpoint("/stomp/chat").setAllowedOrigins("*");
        //registry.addEndpoint("/ws").setAllowedOrigins(path).withSockJS();
    }

    public void configureMessageBroker(MessageBrokerRegistry registry) {
        registry.setPathMatcher(new AntPathMatcher(".")).setApplicationDestinationPrefixes("/pub");

        registry.enableStompBrokerRelay("/queue","/topic","/exchange", "/amq/queue")
                .setClientLogin(rabbitmqUsername)
                .setClientPasscode(rabbitmqPassword)
                .setVirtualHost("/");
//                .setVirtualHost(rabbitmqHost);
//                .setClientLogin(rabbitmqUsername)
//                .setSystemPasscode(rabbitmqPassword);
    }


}

package com.care4u.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    @Override
    public void configureMessageBroker(MessageBrokerRegistry config) {
        config.enableSimpleBroker("/topic"); // 클라이언트에게 메시지 전달을 위한 브로커 구성
        config.setApplicationDestinationPrefixes("/care4u"); // 클라이언트에서 메시지를 수신하기 위한 엔드포인트 prefix 설정
    }

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        registry.addEndpoint("/care4u_websocket").withSockJS(); // 클라이언트가 웹 소켓에 연결할 때 사용할 엔드포인트 설정
    }
}

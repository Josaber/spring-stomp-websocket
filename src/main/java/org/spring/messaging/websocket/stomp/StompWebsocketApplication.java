package org.spring.messaging.websocket.stomp;

import org.spring.messaging.websocket.stomp.config.WebSocketProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@SpringBootApplication
@EnableConfigurationProperties(WebSocketProperties.class)
public class StompWebsocketApplication {

    public static void main(String[] args) {
        SpringApplication.run(StompWebsocketApplication.class, args);
    }
}

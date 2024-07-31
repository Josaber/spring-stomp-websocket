package org.spring.messaging.websocket.stomp.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties("app.web-socket")
public record WebSocketProperties(
        String endpoint,
        String channel,
        Broker broker
) {
    public record Broker(
            String destinationPrefix,
            String host,
            Integer port
    ) {
    }
}

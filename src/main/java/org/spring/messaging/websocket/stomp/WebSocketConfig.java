package org.spring.messaging.websocket.stomp;

import org.springframework.boot.autoconfigure.jms.activemq.ActiveMQProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

@Configuration
@EnableWebSocketMessageBroker
@EnableConfigurationProperties(ActiveMQProperties.class)
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {
    private final ActiveMQProperties activeMQProperties;

    public WebSocketConfig(ActiveMQProperties activeMQProperties) {
        this.activeMQProperties = activeMQProperties;
    }

    @Override
    public void configureMessageBroker(final MessageBrokerRegistry config) {
        // Redis doesn't support the STOMP protocol.
        // https://stomp.github.io/implementations.html
        // config.enableSimpleBroker("/topic");
        // enable a simple memory-based message broker
        // to carry the greeting messages back to the client on destinations prefixed with `/topic`
        config.enableStompBrokerRelay("/topic")
                .setRelayHost("localhost")
                .setRelayPort(61616)
                .setSystemLogin(activeMQProperties.getUser())
                .setSystemPasscode(activeMQProperties.getPassword())
                .setClientLogin(activeMQProperties.getUser())
                .setClientPasscode(activeMQProperties.getPassword());
        config.setApplicationDestinationPrefixes("/app");
        // designates the `/app` prefix for messages that are bound for methods annotated with `@MessageMapping`
        // `/app/hello`
    }

    @Override
    public void registerStompEndpoints(final StompEndpointRegistry registry) {
        registry.addEndpoint("/gs-guide-websocket")
                .setAllowedOrigins("http://127.0.0.1:5500", "http://localhost:5500");
        // Not using SockJS .withSockJS();
    }

}

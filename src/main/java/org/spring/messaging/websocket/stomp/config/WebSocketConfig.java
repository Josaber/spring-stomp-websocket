package org.spring.messaging.websocket.stomp.config;

import org.springframework.boot.autoconfigure.jms.activemq.ActiveMQProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

import java.util.Arrays;

@Configuration
@EnableWebSocketMessageBroker
@EnableConfigurationProperties(ActiveMQProperties.class)
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {
    private final ActiveMQProperties activeMqProperties;

    private final WebSocketProperties webSocketProperties;

    private final Environment environment;

    public WebSocketConfig(ActiveMQProperties activeMqProperties, WebSocketProperties webSocketProperties, Environment environment) {
        this.activeMqProperties = activeMqProperties;
        this.webSocketProperties = webSocketProperties;
        this.environment = environment;
    }

    @Override
    public void configureMessageBroker(final MessageBrokerRegistry config) {
        WebSocketProperties.Broker broker = webSocketProperties.broker();
        if (Arrays.stream(environment.getActiveProfiles()).anyMatch("demo"::equalsIgnoreCase)) {
            config.enableSimpleBroker(broker.destinationPrefix());
        } else {
            // outbound `/topic/**`
            config.enableStompBrokerRelay(broker.destinationPrefix())
                    .setRelayHost(broker.host())
                    .setRelayPort(broker.port())
                    .setSystemLogin(activeMqProperties.getUser())
                    .setSystemPasscode(activeMqProperties.getPassword())
                    .setClientLogin(activeMqProperties.getUser())
                    .setClientPasscode(activeMqProperties.getPassword());
        }
        // inbound `/app/hello`
        config.setApplicationDestinationPrefixes("/app");
    }

    @Override
    public void registerStompEndpoints(final StompEndpointRegistry registry) {
        String[] allowedOrigins = {"http://127.0.0.1:5500", "http://localhost:5500"};
        registry.addEndpoint(webSocketProperties.endpoint()).setAllowedOriginPatterns("*");
        registry.addEndpoint(webSocketProperties.endpoint()).setAllowedOrigins(allowedOrigins).withSockJS();
    }

}

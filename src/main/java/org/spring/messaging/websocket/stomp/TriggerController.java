package org.spring.messaging.websocket.stomp;

import io.swagger.v3.oas.annotations.Operation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.spring.messaging.websocket.stomp.config.WebSocketProperties;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController("ws")
public class TriggerController {
    private static final Logger log = LoggerFactory.getLogger(TriggerController.class);

    private final SimpMessagingTemplate messagingTemplate;

    private final WebSocketProperties webSocketProperties;

    public TriggerController(SimpMessagingTemplate messagingTemplate, WebSocketProperties webSocketProperties) {
        this.messagingTemplate = messagingTemplate;
        this.webSocketProperties = webSocketProperties;
    }

    @PostMapping("trigger/message")
    @Operation(summary = "Trigger Hello Message"/*, security = @SecurityRequirement(name = "bearerAuth")*/)
    public Greeting sendFiles() {
        log.info("WebSocket message triggered");
        Greeting greeting = new Greeting("Hello, TRIGGER!");
        messagingTemplate.convertAndSend(webSocketProperties.channel(), greeting);
        return greeting;
    }

}

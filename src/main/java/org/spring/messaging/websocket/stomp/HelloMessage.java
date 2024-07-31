package org.spring.messaging.websocket.stomp;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public record HelloMessage (
        String name
) {
}

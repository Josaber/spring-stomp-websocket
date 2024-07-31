package org.spring.messaging.websocket.stomp;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.messaging.simp.stomp.StompSessionHandler;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicReference;

import static org.junit.jupiter.api.Assertions.assertEquals;

class GreetingIntegrationTests extends BaseIntegrationTests {

    @Test
    @DisplayName("should get greeting from subscription")
    void should_get_greeting_from_subscription() throws Exception {

        final CountDownLatch latch = new CountDownLatch(1);
        final AtomicReference<Throwable> failure = new AtomicReference<>();

        StompSessionHandler handler = new TestSessionFrameHandler<>(
                failure,
                latch,
                Greeting.class,
                greeting -> assertEquals("Hello, Spring!", greeting.content()),
                session -> session.send("/app/hello", new HelloMessage("Spring"))
        );

        this.stompClient.connectAsync("ws://localhost:{port}/ws", this.headers, handler, this.port);

        assetFailure(latch, failure, "Greeting not received");
    }
}

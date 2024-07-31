package org.spring.messaging.websocket.stomp;

import org.junit.jupiter.api.BeforeEach;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.messaging.converter.MappingJackson2MessageConverter;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompFrameHandler;
import org.springframework.messaging.simp.stomp.StompHeaders;
import org.springframework.messaging.simp.stomp.StompSession;
import org.springframework.messaging.simp.stomp.StompSessionHandlerAdapter;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.web.socket.WebSocketHttpHeaders;
import org.springframework.web.socket.client.WebSocketClient;
import org.springframework.web.socket.client.standard.StandardWebSocketClient;
import org.springframework.web.socket.messaging.WebSocketStompClient;

import java.lang.reflect.Type;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Consumer;

import static org.junit.jupiter.api.Assertions.fail;

@ActiveProfiles("demo")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class BaseIntegrationTests {

    protected final WebSocketHttpHeaders headers = new WebSocketHttpHeaders();

    @LocalServerPort
    protected int port;

    protected WebSocketStompClient stompClient;

    protected static void assetFailure(CountDownLatch latch, AtomicReference<Throwable> failure, String failureMessage) throws InterruptedException {
        if (latch.await(3, TimeUnit.SECONDS)) {
            if (failure.get() != null) {
                throw new AssertionError("", failure.get());
            }
        } else {
            fail(failureMessage);
        }
    }

    @BeforeEach
    public void setup() {
        WebSocketClient webSocketClient = new StandardWebSocketClient();
        this.stompClient = new WebSocketStompClient(webSocketClient);
        this.stompClient.setMessageConverter(new MappingJackson2MessageConverter());
    }

    protected static class TestSessionHandler extends StompSessionHandlerAdapter {

        protected final AtomicReference<Throwable> failure;

        public TestSessionHandler(AtomicReference<Throwable> failure) {
            this.failure = failure;
        }

        @Override
        public void handleFrame(StompHeaders headers, Object payload) {
            this.failure.set(new Exception(headers.toString()));
        }

        @Override
        public void handleException(StompSession s, StompCommand c, StompHeaders h, byte[] p, Throwable ex) {
            this.failure.set(ex);
        }

        @Override
        public void handleTransportError(StompSession session, Throwable ex) {
            this.failure.set(ex);
        }
    }

    protected static class TestSessionFrameHandler<T> extends TestSessionHandler {

        private final CountDownLatch latch;

        private final Class<T> payloadType;

        private final Consumer<T> assertion;

        private final Consumer<StompSession> execution;


        public TestSessionFrameHandler(AtomicReference<Throwable> failure,
                                       CountDownLatch latch,
                                       Class<T> payloadType,
                                       Consumer<T> assertion,
                                       Consumer<StompSession> execution) {
            super(failure);
            this.latch = latch;
            this.payloadType = payloadType;
            this.assertion = assertion;
            this.execution = execution;
        }

        @Override
        public void afterConnected(final StompSession session, StompHeaders connectedHeaders) {
            session.subscribe("/topic/greetings", new StompFrameHandler() {
                @Override
                public Type getPayloadType(StompHeaders headers) {
                    return payloadType;
                }

                @Override
                public void handleFrame(StompHeaders headers, Object payload) {
                    T payloadT = (T) payload;
                    try {
                        assertion.accept(payloadT);
                    } catch (Throwable t) {
                        failure.set(t);
                    } finally {
                        session.disconnect();
                        latch.countDown();
                    }
                }
            });
            try {
                execution.accept(session);
            } catch (Throwable t) {
                failure.set(t);
                latch.countDown();
            }
        }
    }
}

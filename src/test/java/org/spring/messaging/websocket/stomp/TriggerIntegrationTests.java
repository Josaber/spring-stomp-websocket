package org.spring.messaging.websocket.stomp;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.messaging.simp.stomp.StompSessionHandler;
import org.springframework.test.web.servlet.MockMvc;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicReference;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@AutoConfigureMockMvc
class TriggerIntegrationTests extends BaseIntegrationTests {

    @Autowired
    private MockMvc mockMvc;

    @Test
    @DisplayName("should get greeting from trigger")
    void should_get_greeting_from_trigger() throws Exception {

        final CountDownLatch latch = new CountDownLatch(1);
        final AtomicReference<Throwable> failure = new AtomicReference<>();

        StompSessionHandler handler = new TestSessionFrameHandler<>(
                failure,
                latch,
                Greeting.class,
                greeting -> assertEquals("Hello, TRIGGER!", greeting.content()),
                session -> {
                    try {
                        mockMvc.perform(post("http://localhost:" + this.port + "/websocket/trigger/message")
                                        .contentType(MediaType.APPLICATION_JSON))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.content").value("Hello, TRIGGER!"));
                    } catch (Exception e) {
                        failure.set(e);
                        latch.countDown();
                    }
                }
        );

        this.stompClient.connectAsync("ws://localhost:{port}/ws", this.headers, handler, this.port);

        assetFailure(latch, failure, "Greeting not received");
    }

}

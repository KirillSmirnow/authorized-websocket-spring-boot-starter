package example;

import example.client.ChatWebSocketClient;
import example.client.PostWebSocketClient;
import example.data.Message;
import example.data.Post;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import java.time.Duration;

@Slf4j
@Component
@RequiredArgsConstructor
public class ExampleRunner {

    private final PostWebSocketClient postWebSocketClient;
    private final ChatWebSocketClient chatWebSocketClient;

    @EventListener(ApplicationReadyEvent.class)
    public void run() {
        log.info("Open http://localhost:8080");
        new Thread(this::sendEvents).start();
    }

    @SneakyThrows
    private void sendEvents() {
        while (true) {
            postWebSocketClient.sendPost("Alice", Post.get());
            postWebSocketClient.sendPost("Bob", Post.get());
            chatWebSocketClient.sendMessage("Alice", "AliceBobChat", Message.get());
            chatWebSocketClient.sendMessage("Alice", "AliceChat", Message.get());
            chatWebSocketClient.sendMessage("Bob", "AliceBobChat", Message.get());
            chatWebSocketClient.sendMessage("Bob", "BobChat", Message.get());
            Thread.sleep(Duration.ofSeconds(10));
        }
    }
}

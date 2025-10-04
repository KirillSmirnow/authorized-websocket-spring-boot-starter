package example.data;

import lombok.Data;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;
import java.util.function.Supplier;

import static java.util.UUID.randomUUID;

@Data
public class Message {

    private static final List<Supplier<Message>> MESSAGES = getSamples();

    private final UUID id;
    private final ZonedDateTime sentAt;
    private final String text;

    public static Message get() {
        var index = ThreadLocalRandom.current().nextInt(MESSAGES.size());
        return MESSAGES.get(index).get();
    }

    private static List<Supplier<Message>> getSamples() {
        return List.of(
                () -> new Message(randomUUID(), ZonedDateTime.now(), "It's great to see you online!"),
                () -> new Message(randomUUID(), ZonedDateTime.now(), "Never Better! Having a brilliant time in Japan..."),
                () -> new Message(randomUUID(), ZonedDateTime.now(), "What's cooking, good-looking?")
        );
    }
}

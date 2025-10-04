package example.data;

import lombok.Data;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;
import java.util.function.Supplier;

import static java.util.UUID.randomUUID;

@Data
public class Post {

    private static final List<Supplier<Post>> POSTS = getSamples();

    private final UUID id;
    private final ZonedDateTime publishedAt;
    private final String title;
    private final String text;

    public static Post get() {
        var index = ThreadLocalRandom.current().nextInt(POSTS.size());
        return POSTS.get(index).get();
    }

    private static List<Supplier<Post>> getSamples() {
        return List.of(
                () -> new Post(randomUUID(), ZonedDateTime.now(), "What to Know About Japan’s Leadership Election", "Japan’s beleaguered governing party will convene for a crucial election that could pave the way for the first female prime minister or the youngest leader in 140 years."),
                () -> new Post(randomUUID(), ZonedDateTime.now(), "Taylor Swift Fans Try to Decode ‘The Life of a Showgirl’", "Speculation abounds as to which songs have references to the various celebrities in Ms. Swift’s life."),
                () -> new Post(randomUUID(), ZonedDateTime.now(), "In the Arctic, the U.S. Shifts Focus From Climate Research to Security", "The Trump administration is emphasizing defense concerns instead of climate research in the rapidly warming Arctic region.")
        );
    }
}

package example.security;

import org.springframework.stereotype.Component;
import websocket.security.api.WebSocketUserSupplier;

import java.util.Map;

@Component
public class WebSocketUserSupplierImpl implements WebSocketUserSupplier {

    private final Map<String, String> userIdsByAccessToken = Map.of(
            "AliceToken", "Alice",
            "BobToken", "Bob",
            "CharlieToken", "Charlie"
    );

    @Override
    public Object getUserId(String accessToken) {
        return userIdsByAccessToken.get(accessToken);
    }
}

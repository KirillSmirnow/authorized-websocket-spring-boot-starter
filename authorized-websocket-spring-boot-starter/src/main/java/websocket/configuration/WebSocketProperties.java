package websocket.configuration;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.Set;

import static java.util.Collections.emptySet;

@Data
@ConfigurationProperties("websocket")
public class WebSocketProperties {

    private final Set<String> allowedOrigins;

    public Set<String> getAllowedOrigins() {
        return allowedOrigins != null ? allowedOrigins : emptySet();
    }

    public String[] getAllowedOriginsArray() {
        return getAllowedOrigins().toArray(String[]::new);
    }
}

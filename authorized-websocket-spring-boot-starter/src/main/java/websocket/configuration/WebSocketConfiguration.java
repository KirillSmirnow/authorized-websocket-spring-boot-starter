package websocket.configuration;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.ChannelRegistration;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;
import websocket.security.impl.WebSocketInboundChannelSecurer;

@Configuration
@EnableWebSocketMessageBroker
@RequiredArgsConstructor
public class WebSocketConfiguration implements WebSocketMessageBrokerConfigurer {

    private static final String CONNECTION_URI = "/websocket";

    private final WebSocketProperties webSocketProperties;
    private final WebSocketInboundChannelSecurer webSocketInboundChannelSecurer;

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        registry.addEndpoint(CONNECTION_URI).setAllowedOrigins(webSocketProperties.getAllowedOriginsArray());
    }

    @Override
    public void configureClientInboundChannel(ChannelRegistration registration) {
        registration.interceptors(webSocketInboundChannelSecurer);
    }
}

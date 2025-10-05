package websocket.security.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.SimpMessageType;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.stereotype.Component;
import websocket.security.api.WebSocketUserSupplier;

import java.util.Set;

@Slf4j
@Component
@RequiredArgsConstructor
public class WebSocketInboundChannelSecurer implements ChannelInterceptor {

    private static final Set<SimpMessageType> ALLOWED_MESSAGE_TYPES = Set.of(
            SimpMessageType.CONNECT, SimpMessageType.DISCONNECT,
            SimpMessageType.SUBSCRIBE, SimpMessageType.UNSUBSCRIBE
    );

    private final WebSocketUserSupplier webSocketUserSupplier;

    @Override
    public Message<?> preSend(Message<?> message, MessageChannel channel) {
        var headerAccessor = SimpMessageHeaderAccessor.wrap(message);
        validateMessageType(headerAccessor);
        authorizeSubscription(headerAccessor);
        return message;
    }

    private void validateMessageType(SimpMessageHeaderAccessor headerAccessor) {
        var messageType = headerAccessor.getMessageType();
        if (!ALLOWED_MESSAGE_TYPES.contains(messageType)) {
            log.debug("Invalid message type {}: {}", messageType, headerAccessor);
            throw new IllegalArgumentException("Forbidden");
        }
    }

    private void authorizeSubscription(SimpMessageHeaderAccessor headerAccessor) {
        if (headerAccessor.getMessageType() != SimpMessageType.SUBSCRIBE) return;
        var accessToken = headerAccessor.getFirstNativeHeader(WebSocketUserSupplier.ACCESS_TOKEN_HEADER_NAME);
        if (accessToken == null || accessToken.isBlank()) {
            log.debug("Access token is missing: {}", headerAccessor);
            throw new IllegalArgumentException("Forbidden");
        }
        var userId = webSocketUserSupplier.getUserId(accessToken).toString();
        var expectedPathPrefix = "/users/%s/".formatted(userId);
        if (!headerAccessor.getDestination().startsWith(expectedPathPrefix)) {
            log.debug("Unauthorized subscription path for user {}: {}", userId, headerAccessor);
            throw new IllegalArgumentException("Forbidden");
        }
    }
}

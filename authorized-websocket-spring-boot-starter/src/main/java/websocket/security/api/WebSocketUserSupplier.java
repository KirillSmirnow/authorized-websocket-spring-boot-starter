package websocket.security.api;

public interface WebSocketUserSupplier {

    String ACCESS_TOKEN_HEADER_NAME = "access-token";

    Object getUserId(String accessToken);
}

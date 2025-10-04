package example.client;

import example.data.Post;
import websocket.client.api.UserPath;
import websocket.client.api.WebSocketClient;

@WebSocketClient
public interface PostWebSocketClient {

    @UserPath("/posts")
    void sendPost(String userId, Post post);
}

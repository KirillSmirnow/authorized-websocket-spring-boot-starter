package example.client;

import example.data.Message;
import websocket.client.api.UserPath;
import websocket.client.api.WebSocketClient;

@WebSocketClient
public interface ChatWebSocketClient {

    @UserPath("/chats/{chatId}/messages")
    void sendMessage(String userId, String chatId, Message message);
}

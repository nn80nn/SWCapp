package nvi.messenger.swcapp;

import okhttp3.*;

public class WebSocketClient {
    private WebSocket webSocket;
    private OkHttpClient client = new OkHttpClient();

    public void connect(String token, WebSocketListener listener) {
        Request request = new Request.Builder()
                .url("ws://185.141.216.57:8080/ws?token=" + token)
                .build();
        webSocket = client.newWebSocket(request, listener);
    }

    public void sendMessage(String message) {
        if (webSocket != null) webSocket.send(message);
    }

    public void disconnect() {
        if (webSocket != null) webSocket.close(1000, "Закрытие соединения");
    }
}

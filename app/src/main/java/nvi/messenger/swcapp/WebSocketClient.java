package nvi.messenger.swcapp;

import okhttp3.*;

public class WebSocketClient {

    private static final String SERVER_URL = "wss://example.com/socket";
    private WebSocket webSocket;

    public void connect() {
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder().url(SERVER_URL).build();
        webSocket = client.newWebSocket(request, new WebSocketListener() {
            @Override
            public void onOpen(WebSocket webSocket, Response response) {
                System.out.println("WebSocket открыт");
            }

            @Override
            public void onMessage(WebSocket webSocket, String text) {
                System.out.println("Получено сообщение: " + text);
                // TODO: Обработать входящее сообщение.
            }

            @Override
            public void onFailure(WebSocket webSocket, Throwable t, Response response) {
                System.err.println("Ошибка WebSocket: " + t.getMessage());
            }

            @Override
            public void onClosing(WebSocket webSocket, int code, String reason) {
                System.out.println("WebSocket закрывается: " + reason);
                webSocket.close(code, reason);
            }
        });
    }

    public void sendMessage(String message) {
        if (webSocket != null) {
            webSocket.send(message);
        }
    }

    public void disconnect() {
        if (webSocket != null) {
            webSocket.close(1000, "Закрытие соединения");
        }
    }
}

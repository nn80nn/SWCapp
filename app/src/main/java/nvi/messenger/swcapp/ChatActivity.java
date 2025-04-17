package nvi.messenger.swcapp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.room.Room;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import okhttp3.WebSocket;
import okhttp3.WebSocketListener;
import okhttp3.Response;
import org.json.JSONObject;
import java.util.List;
import java.util.concurrent.Executors;
import android.graphics.Rect;
import android.view.View;
import androidx.annotation.NonNull;

public class ChatActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private MessageAdapter adapter;
    private EditText editTextMessage;
    private Button buttonSendMessage;

    private AppDatabase db;
    private WebSocketClient wsClient;
    private String token;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        recyclerView = findViewById(R.id.recyclerViewChat);
        editTextMessage = findViewById(R.id.editTextMessage);
        buttonSendMessage = findViewById(R.id.buttonSendMessage);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new MessageAdapter();
        recyclerView.setAdapter(adapter);

        // Добавляем небольшой отступ между элементами
        int spacePx = 0; // или 1-2 для минимального отступа
        recyclerView.addItemDecoration(new SmallSpaceItemDecoration(spacePx));

        db = Room.databaseBuilder(getApplicationContext(), AppDatabase.class, "chat-db").build();
        token = getIntent().getStringExtra("TOKEN");

        loadMessages();

        wsClient = new WebSocketClient();
        wsClient.connect(token, new WebSocketListener() {
            @Override
            public void onOpen(WebSocket webSocket, Response response) {
                runOnUiThread(() -> adapter.addMessage(new Message("System", "WebSocket открыт", System.currentTimeMillis())));
            }

            @Override
            public void onMessage(WebSocket webSocket, String text) {
                try {
                    JSONObject json = new JSONObject(text);
                    String sender = json.optString("sender", "Собеседник");
                    String content = json.optString("content", text);

                    Message msg = new Message(sender, content, System.currentTimeMillis());

                    Executors.newSingleThreadExecutor().execute(() -> db.messageDao().insert(msg));
                    runOnUiThread(() -> adapter.addMessage(msg));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        buttonSendMessage.setOnClickListener(v -> {
            String message = editTextMessage.getText().toString();
            if (!message.isEmpty()) {
                wsClient.sendMessage(message);

                Message msg = new Message("Вы", message, System.currentTimeMillis());

                Executors.newSingleThreadExecutor().execute(() -> db.messageDao().insert(msg));
                adapter.addMessage(msg);

                editTextMessage.setText("");
            }
        });
    }

    private void loadMessages() {
        Executors.newSingleThreadExecutor().execute(() -> {
            List<Message> messages = db.messageDao().getAllMessages();
            runOnUiThread(() -> adapter.setMessages(messages));
        });
    }

    // Класс для ItemDecoration
    public static class SmallSpaceItemDecoration extends RecyclerView.ItemDecoration {
        private final int spacePx;

        public SmallSpaceItemDecoration(int spacePx) {
            this.spacePx = spacePx;
        }

        @Override
        public void getItemOffsets(@NonNull Rect outRect, @NonNull View view,
                                   @NonNull RecyclerView parent, @NonNull RecyclerView.State state) {
            outRect.top = spacePx;
            outRect.bottom = 0;
            outRect.left = 0;
            outRect.right = 0;
        }
    }
}

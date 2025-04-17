package nvi.messenger.swcapp;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

public class MainActivity extends AppCompatActivity {

    private Button buttonOpenChat;
    private String token;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        buttonOpenChat = findViewById(R.id.buttonOpenChat);
        token = getIntent().getStringExtra("TOKEN");

        buttonOpenChat.setOnClickListener(v -> {
            Intent intent = new Intent(this, ChatActivity.class);
            intent.putExtra("TOKEN", token);
            startActivity(intent);
        });
    }
}

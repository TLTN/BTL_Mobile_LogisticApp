package com.example.btl;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class MainActivity extends AppCompatActivity {
    Button button;
    TextView title, subtitle, welcome;

    private void Animation()
    {
        title.animate().alpha(1f).translationY(0f).setDuration(800).setStartDelay(200).start();
        subtitle.animate().alpha(1f).translationY(0f).setDuration(800).setStartDelay(400).start();
        welcome.animate().alpha(1f).translationY(0f).setDuration(800).setStartDelay(600).start();
        button.animate().alpha(1f).translationY(0f).setDuration(800).setStartDelay(800).start();
    }
    private void Init()
    {
        button = findViewById(R.id.btn);
        title = findViewById(R.id.textView);
        subtitle = findViewById(R.id.textView2);
        welcome = findViewById(R.id.textView3);
    }

    private void Event()
    {
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, trang_dang_nhap.class);
                startActivity(intent);
                overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right);
            }
        });
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        Init();
        Animation();
        Event();

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }
}

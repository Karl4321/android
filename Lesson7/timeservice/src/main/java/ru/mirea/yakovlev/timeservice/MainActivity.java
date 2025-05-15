package ru.mirea.yakovlev.timeservice;

import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {
    private TextView timeTextView;
    private Button getTimeButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        timeTextView = findViewById(R.id.timeTextView);
        getTimeButton = findViewById(R.id.getTimeButton);

        getTimeButton.setOnClickListener(v -> {
            NetworkTask task = new NetworkTask(timeTextView);
            task.execute();
        });
    }
}
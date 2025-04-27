package ru.mirea.yakovlev.serviceapp;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.widget.Button;
import android.Manifest;
import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class MainActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            requestPermissions(
                    new String[]{
                            Manifest.permission.POST_NOTIFICATIONS,
                            Manifest.permission.FOREGROUND_SERVICE_MEDIA_PLAYBACK // Для API 34+
                    },
                    1
            );
        }

        Button btnStart = findViewById(R.id.btn_start);
        btnStart.setOnClickListener(v -> startService());

        Button btnStop = findViewById(R.id.btn_stop);
        btnStop.setOnClickListener(v -> stopService());
    }

    private void startService() {
        Intent serviceIntent = new Intent(this, PlayerService.class);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            startForegroundService(serviceIntent);
        } else {
            startService(serviceIntent);
        }
    }

    private void stopService() {
        Intent serviceIntent = new Intent(this, PlayerService.class);
        stopService(serviceIntent);
    }
}
package ru.mirea.yakovlev.viewbinding;

import android.os.Bundle;
import android.view.View;
import androidx.appcompat.app.AppCompatActivity;

import ru.mirea.yakovlev.viewbinding.databinding.ActivityMainBinding;

public class MainActivity extends AppCompatActivity {
    private ActivityMainBinding binding;
    private boolean isPlaying = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.textTitle.setText("Новый трек");
        binding.textArtist.setText("Неизвестный исполнитель");

        binding.btnPlay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isPlaying = !isPlaying;
                binding.btnPlay.setText(isPlaying ? "Pause" : "Play");
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        binding = null;
    }
}
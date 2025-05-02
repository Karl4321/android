package ru.mirea.yakovlev.audiorecord;

import android.Manifest;
import android.content.pm.PackageManager;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import java.io.File;
import java.io.IOException;

public class MainActivity extends AppCompatActivity {

    private static final int PERMISSION_CODE = 123;
    private String audioPath;
    private boolean permissionGranted = false;
    private boolean isRecording = false;
    private boolean isPlaying = false;

    private MediaRecorder mediaRecorder;
    private MediaPlayer mediaPlayer;

    private Button btnRecord;
    private Button btnPlay;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btnRecord = findViewById(R.id.btnRecord);
        btnPlay = findViewById(R.id.btnPlay);

        btnPlay.setEnabled(false);
        audioPath = new File(getExternalFilesDir(Environment.DIRECTORY_MUSIC),
                "recording_sample.3gp").getAbsolutePath();

        checkAndRequestPermissions();

        btnRecord.setOnClickListener(v -> {
            if (!isRecording) {
                startAudioRecording();
                btnRecord.setText("Стоп запись");
                btnPlay.setEnabled(false);
            } else {
                stopAudioRecording();
                btnRecord.setText("Записать");
                btnPlay.setEnabled(true);
            }
            isRecording = !isRecording;
        });

        btnPlay.setOnClickListener(v -> {
            if (!isPlaying) {
                startAudioPlayback();
                btnPlay.setText("Стоп воспроизведение");
                btnRecord.setEnabled(false);
            } else {
                stopAudioPlayback();
                btnPlay.setText("Воспроизвести");
                btnRecord.setEnabled(true);
            }
            isPlaying = !isPlaying;
        });
    }

    private void checkAndRequestPermissions() {
        int audioPermission = ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO);
        int storagePermission = ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE);

        if (audioPermission == PackageManager.PERMISSION_GRANTED &&
                storagePermission == PackageManager.PERMISSION_GRANTED) {
            permissionGranted = true;
        } else {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.RECORD_AUDIO, Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    PERMISSION_CODE);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        permissionGranted = true;
        for (int result : grantResults) {
            if (result != PackageManager.PERMISSION_GRANTED) {
                permissionGranted = false;
                break;
            }
        }
    }

    private void startAudioRecording() {
        mediaRecorder = new MediaRecorder();
        mediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
        mediaRecorder.setOutputFile(audioPath);
        mediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);

        try {
            mediaRecorder.prepare();
            mediaRecorder.start();
            Toast.makeText(this, "Начата запись", Toast.LENGTH_SHORT).show();
        } catch (IOException e) {
            Log.e("AudioRecordApp", "Ошибка при подготовке: " + e.getMessage());
        }
    }

    private void stopAudioRecording() {
        if (mediaRecorder != null) {
            mediaRecorder.stop();
            mediaRecorder.release();
            mediaRecorder = null;
            Toast.makeText(this, "Запись завершена", Toast.LENGTH_SHORT).show();
        }
    }

    private void startAudioPlayback() {
        mediaPlayer = new MediaPlayer();
        try {
            mediaPlayer.setDataSource(audioPath);
            mediaPlayer.prepare();
            mediaPlayer.start();
            mediaPlayer.setOnCompletionListener(mp -> {
                stopAudioPlayback();
                btnPlay.setText("Воспроизвести");
                btnRecord.setEnabled(true);
                isPlaying = false;
            });
            Toast.makeText(this, "Воспроизведение", Toast.LENGTH_SHORT).show();
        } catch (IOException e) {
            Log.e("AudioRecordApp", "Ошибка воспроизведения: " + e.getMessage());
        }
    }

    private void stopAudioPlayback() {
        if (mediaPlayer != null) {
            mediaPlayer.release();
            mediaPlayer = null;
            Toast.makeText(this, "Воспроизведение остановлено", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (mediaRecorder != null) {
            mediaRecorder.release();
            mediaRecorder = null;
        }
        if (mediaPlayer != null) {
            mediaPlayer.release();
            mediaPlayer = null;
        }
    }
}

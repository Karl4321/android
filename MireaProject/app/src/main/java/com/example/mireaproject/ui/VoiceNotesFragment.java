package com.example.mireaproject.ui;

import android.Manifest;
import android.content.pm.PackageManager;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.example.mireaproject.R;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class VoiceNotesFragment extends Fragment {

    private static final String TAG = "VoiceNotesFragment";
    private static final String AUDIO_FILE_PREFIX = "AUD_";
    private static final String AUDIO_FILE_EXTENSION = ".3gp";

    private Button recordButton;
    private Button playButton;
    private MediaRecorder mediaRecorder;
    private MediaPlayer mediaPlayer;
    private File audioFile;
    private boolean isRecording = false;
    private boolean isPlaying = false;
    private boolean permissionGranted = false;

    private final ActivityResultLauncher<String> requestPermissionLauncher = registerForActivityResult(
            new ActivityResultContracts.RequestPermission(),
            isGranted -> {
                permissionGranted = isGranted;
                if (!isGranted) {
                    showToast("Разрешение на запись обязательно");
                }
            });

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        checkPermissions();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_voice_notes, container, false);
        setupViews(view);
        return view;
    }

    private void checkPermissions() {
        if (ContextCompat.checkSelfPermission(requireContext(),
                Manifest.permission.RECORD_AUDIO) == PackageManager.PERMISSION_GRANTED) {
            permissionGranted = true;
        } else {
            requestPermissionLauncher.launch(Manifest.permission.RECORD_AUDIO);
        }
    }

    private void setupViews(View rootView) {
        recordButton = rootView.findViewById(R.id.record_button);
        playButton = rootView.findViewById(R.id.play_button);
        playButton.setEnabled(false);

        recordButton.setOnClickListener(v -> handleRecordAction());
        playButton.setOnClickListener(v -> handlePlayAction());
    }

    private void handleRecordAction() {
        if (!permissionGranted) {
            showToast("Требуется разрешение на запись");
            return;
        }

        if (isRecording) {
            stopRecording();
        } else {
            startRecording();
        }
        updateUIState();
    }

    private void startRecording() {
        try {
            prepareRecorder();
            mediaRecorder.prepare();
            mediaRecorder.start();
            isRecording = true;
        } catch (IOException | IllegalStateException e) {
            handleRecordingError(e);
        }
    }

    private void prepareRecorder() throws IOException {
        mediaRecorder = new MediaRecorder();
        mediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
        mediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
        mediaRecorder.setOutputFile(createAudioFile().getAbsolutePath());
    }

    private File createAudioFile() throws IOException {
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date());
        String fileName = AUDIO_FILE_PREFIX + timeStamp + AUDIO_FILE_EXTENSION;
        File storageDir = requireContext().getExternalFilesDir(Environment.DIRECTORY_MUSIC);
        audioFile = new File(storageDir, fileName);
        if (!audioFile.createNewFile()) {
            throw new IOException("Failed to create audio file");
        }
        return audioFile;
    }

    private void stopRecording() {
        try {
            mediaRecorder.stop();
        } catch (IllegalStateException e) {
            Log.e(TAG, "Stop recording failed", e);
        } finally {
            releaseRecorder();
            isRecording = false;
        }
    }

    private void releaseRecorder() {
        if (mediaRecorder != null) {
            mediaRecorder.release();
            mediaRecorder = null;
        }
    }

    private void handlePlayAction() {
        if (isPlaying) {
            stopPlaying();
        } else {
            startPlaying();
        }
        updateUIState();
    }

    private void startPlaying() {
        try {
            preparePlayer();
            mediaPlayer.start();
            isPlaying = true;
            setupCompletionListener();
        } catch (IOException e) {
            handlePlaybackError(e);
        }
    }

    private void preparePlayer() throws IOException {
        mediaPlayer = new MediaPlayer();
        mediaPlayer.setDataSource(audioFile.getAbsolutePath());
        mediaPlayer.prepare();
    }

    private void setupCompletionListener() {
        mediaPlayer.setOnCompletionListener(mp -> {
            stopPlaying();
            updateUIState();
        });
    }

    private void stopPlaying() {
        if (mediaPlayer != null) {
            mediaPlayer.release();
            mediaPlayer = null;
        }
        isPlaying = false;
    }

    private void updateUIState() {
        recordButton.setText(isRecording ? "Остановить запись" : "Начать запись");
        playButton.setEnabled(!isRecording && audioFile != null);
        playButton.setText(isPlaying ? "Пауза" : "Воспроизвести");
    }

    private void handleRecordingError(Exception e) {
        Log.e(TAG, "Recording error", e);
        showToast("Ошибка записи: " + e.getMessage());
        releaseRecorder();
        isRecording = false;
        updateUIState();
    }

    private void handlePlaybackError(Exception e) {
        Log.e(TAG, "Playback error", e);
        showToast("Ошибка воспроизведения: " + e.getMessage());
        stopPlaying();
        updateUIState();
    }

    private void showToast(String message) {
        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        releaseRecorder();
        stopPlaying();
    }
}
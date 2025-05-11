package com.example.mireaproject.ui;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import com.example.mireaproject.R;

public class CollageFragment extends Fragment {

    private final ImageView[] imageViews = new ImageView[4];
    private final Uri[] photoUris = new Uri[4];
    private int activePosition = 0;

    private ActivityResultLauncher<Intent> captureImageLauncher;
    private ActivityResultLauncher<String> requestPermissionLauncher;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requestPermissionLauncher = registerForActivityResult(
                new ActivityResultContracts.RequestPermission(),
                isGranted -> {
                    if (isGranted) {
                        startCameraProcess();
                    } else {
                        showToast("Для работы с камерой необходимо разрешение");
                    }
                });

        captureImageLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == getActivity().RESULT_OK) {
                        updateCollageWithNewImage();
                    }
                });
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View fragmentView = inflater.inflate(R.layout.fragment_collage, container, false);

        initializeViews(fragmentView);
        setupButtonListener(fragmentView);

        return fragmentView;
    }

    private void initializeViews(View rootView) {
        imageViews[0] = rootView.findViewById(R.id.collage_image1);
        imageViews[1] = rootView.findViewById(R.id.collage_image2);
        imageViews[2] = rootView.findViewById(R.id.collage_image3);
        imageViews[3] = rootView.findViewById(R.id.collage_image4);
    }

    private void setupButtonListener(View rootView) {
        Button photoButton = rootView.findViewById(R.id.add_photo_button);
        photoButton.setOnClickListener(view -> checkPermissionsAndCapture());
    }

    private void checkPermissionsAndCapture() {
        if (activePosition >= imageViews.length) {
            showToast("Коллаж заполнен!");
            return;
        }

        if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.CAMERA)
                == PackageManager.PERMISSION_GRANTED) {
            startCameraProcess();
        } else {
            requestPermissionLauncher.launch(Manifest.permission.CAMERA);
        }
    }

    private void startCameraProcess() {
        try {
            File imageFile = createImageFile();
            Uri imageUri = getFileUri(imageFile);
            photoUris[activePosition] = imageUri;

            Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
            cameraIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);

            Intent chooser = Intent.createChooser(cameraIntent, "Выберите камеру");

            if (chooser.resolveActivity(requireActivity().getPackageManager()) != null) {
                captureImageLauncher.launch(chooser);
            } else {
                showToast("Не найдено ни одного приложения");
            }
        } catch (IOException | SecurityException e) {
            e.printStackTrace();
            showToast("Ошибка создания файла");
        }
    }

    private File createImageFile() throws IOException {
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date());
        String fileName = "JPEG_" + timeStamp + "_";
        File storageDir = requireContext().getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        return File.createTempFile(fileName, ".jpg", storageDir);
    }

    private Uri getFileUri(File file) {
        return FileProvider.getUriForFile(
                requireContext(),
                requireContext().getPackageName() + ".fileprovider",
                file
        );
    }

    private void updateCollageWithNewImage() {
        if (photoUris[activePosition] != null) {
            imageViews[activePosition].setImageURI(photoUris[activePosition]);
            activePosition++;

            if (activePosition >= imageViews.length) {
                showToast("Коллаж готов!");
            }
        }
    }

    private void showToast(String message) {
        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show();
    }
}
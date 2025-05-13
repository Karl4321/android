package ru.mirea.yakovlev.mireaproject.ui;

import android.app.AlertDialog;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import ru.mirea.yakovlev.mireaproject.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.switchmaterial.SwitchMaterial;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

public class FileOperationsFragment extends Fragment {

    private RecyclerView fileListView;
    private TextView fileContentDisplay, contentLabel;
    private ScrollView contentScrollView;
    private Button cryptoButton;
    private FloatingActionButton createFileButton;
    private FileListAdapter fileAdapter;
    private List<FileEntry> fileEntries = new ArrayList<>();
    private File currentSelectedFile;
    private boolean encryptionActive = false;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_file_operations, container, false);

        initializeViews(rootView);
        setupFileList();
        loadFileEntries();
        setupButtonListeners();

        return rootView;
    }

    private void initializeViews(View view) {
        fileListView = view.findViewById(R.id.fileList);
        fileContentDisplay = view.findViewById(R.id.fileContentText);
        contentLabel = view.findViewById(R.id.contentLabel);
        contentScrollView = view.findViewById(R.id.contentScrollContainer);
        cryptoButton = view.findViewById(R.id.cryptoActionButton);
        createFileButton = view.findViewById(R.id.createFileButton);
    }

    private void setupFileList() {
        fileAdapter = new FileListAdapter(fileEntries, file -> {
            currentSelectedFile = file;
            encryptionActive = file.getName().endsWith(".enc");
            updateCryptoButtonState();
            showFileContent(file);
        });

        fileListView.setLayoutManager(new LinearLayoutManager(requireContext()));
        fileListView.setAdapter(fileAdapter);
    }

    private void setupButtonListeners() {
        createFileButton.setOnClickListener(v -> displayFileCreationDialog());

        cryptoButton.setOnClickListener(v -> {
            if (currentSelectedFile != null) {
                if (encryptionActive) {
                    performFileDecryption(currentSelectedFile);
                } else {
                    performFileEncryption(currentSelectedFile);
                }
            }
        });
    }

    private void updateCryptoButtonState() {
        cryptoButton.setText(encryptionActive ? "Decrypt File" : "Encrypt File");
        cryptoButton.setVisibility(View.VISIBLE);
        contentLabel.setVisibility(View.VISIBLE);
        contentScrollView.setVisibility(View.VISIBLE);
    }

    private void showFileContent(File file) {
        try {
            String content = readFileContents(file);
            fileContentDisplay.setText(content);
        } catch (IOException e) {
            showToast("File read error");
        }
    }

    private void loadFileEntries() {
        fileEntries.clear();
        File appFilesDir = requireContext().getFilesDir();
        File[] files = appFilesDir.listFiles();

        if (files != null) {
            for (File file : files) {
                fileEntries.add(new FileEntry(file.getName(), file));
            }
        }

        fileAdapter.notifyDataSetChanged();
    }

    private void displayFileCreationDialog() {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(requireContext());
        dialogBuilder.setTitle("Create New File");

        View dialogLayout = LayoutInflater.from(requireContext())
                .inflate(R.layout.dialog_create_file, null);

        EditText filenameInput = dialogLayout.findViewById(R.id.filenameInput);
        EditText contentInput = dialogLayout.findViewById(R.id.contentInput);
        SwitchMaterial encryptionSwitch = dialogLayout.findViewById(R.id.encryptionSwitch);

        dialogBuilder.setView(dialogLayout);
        dialogBuilder.setPositiveButton("Create", (dialog, which) -> {
            String filename = filenameInput.getText().toString();
            String content = contentInput.getText().toString();
            boolean encrypt = encryptionSwitch.isChecked();

            if (!filename.isEmpty() && !content.isEmpty()) {
                createNewFile(filename, content, encrypt);
            } else {
                showToast("Please fill all fields");
            }
        });
        dialogBuilder.setNegativeButton("Cancel", null);
        dialogBuilder.show();
    }

    private void createNewFile(String filename, String content, boolean encrypt) {
        try {
            String actualFilename = encrypt ? filename + ".enc" : filename;
            File newFile = new File(requireContext().getFilesDir(), actualFilename);

            if (encrypt) {
                content = applyEncryption(content);
            }

            writeToFile(newFile, content);
            loadFileEntries();
            showToast("File created successfully");
        } catch (IOException e) {
            showToast("File creation failed");
        }
    }

    private void performFileEncryption(File file) {
        try {
            String originalContent = readFileContents(file);
            String encryptedContent = applyEncryption(originalContent);

            File encryptedFile = new File(requireContext().getFilesDir(), file.getName() + ".enc");
            writeToFile(encryptedFile, encryptedContent);

            file.delete();
            refreshFileListAndSelect(encryptedFile);
            showToast("File encrypted");
        } catch (IOException e) {
            showToast("Encryption failed");
        }
    }

    private void performFileDecryption(File file) {
        try {
            String encryptedContent = readFileContents(file);
            String decryptedContent = removeEncryption(encryptedContent);

            String originalName = file.getName().replace(".enc", "");
            File decryptedFile = new File(requireContext().getFilesDir(), originalName);

            writeToFile(decryptedFile, decryptedContent);
            file.delete();
            refreshFileListAndSelect(decryptedFile);
            showToast("File decrypted");
        } catch (IOException e) {
            showToast("Decryption failed");
        }
    }

    private void refreshFileListAndSelect(File file) {
        loadFileEntries();
        currentSelectedFile = file;
        encryptionActive = file.getName().endsWith(".enc");
        updateCryptoButtonState();
        showFileContent(file);
    }

    private String readFileContents(File file) throws IOException {
        FileInputStream inputStream = new FileInputStream(file);
        byte[] contentBytes = new byte[(int) file.length()];
        inputStream.read(contentBytes);
        inputStream.close();
        return new String(contentBytes, StandardCharsets.UTF_8);
    }

    private void writeToFile(File file, String content) throws IOException {
        FileOutputStream outputStream = new FileOutputStream(file);
        outputStream.write(content.getBytes(StandardCharsets.UTF_8));
        outputStream.close();
    }

    private String applyEncryption(String content) {
        return android.util.Base64.encodeToString(
                content.getBytes(StandardCharsets.UTF_8),
                android.util.Base64.DEFAULT
        );
    }

    private String removeEncryption(String encryptedContent) {
        byte[] decodedBytes = android.util.Base64.decode(
                encryptedContent,
                android.util.Base64.DEFAULT
        );
        return new String(decodedBytes, StandardCharsets.UTF_8);
    }

    private void showToast(String message) {
        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show();
    }
}
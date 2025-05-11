package ru.mirea.yakovlev.notebook;

import android.os.Bundle;
import android.os.Environment;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;

public class MainActivity extends AppCompatActivity {
    private EditText filenameEditText, quoteEditText;
    private Button saveButton, loadButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        filenameEditText = findViewById(R.id.filenameEditText);
        quoteEditText = findViewById(R.id.quoteEditText);
        saveButton = findViewById(R.id.saveButton);
        loadButton = findViewById(R.id.loadButton);

        saveButton.setOnClickListener(v -> saveToFile());
        loadButton.setOnClickListener(v -> loadFromFile());
    }

    private void saveToFile() {
        String filename = filenameEditText.getText().toString();
        String quote = quoteEditText.getText().toString();

        if (filename.isEmpty() || quote.isEmpty()) {
            Toast.makeText(this, "Заполните все поля", Toast.LENGTH_SHORT).show();
            return;
        }

        if (!isExternalStorageWritable()) {
            Toast.makeText(this, "Внешнее хранилище недоступно", Toast.LENGTH_SHORT).show();
            return;
        }

        File path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS);
        File file = new File(path, filename + ".txt");

        try {
            path.mkdirs();
            FileOutputStream fos = new FileOutputStream(file);
            OutputStreamWriter osw = new OutputStreamWriter(fos);
            osw.write(quote);
            osw.close();

            Toast.makeText(this, "Файл сохранен: " + file.getAbsolutePath(), Toast.LENGTH_LONG).show();
        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(this, "Ошибка сохранения", Toast.LENGTH_SHORT).show();
        }
    }

    private void loadFromFile() {
        String filename = filenameEditText.getText().toString();

        if (filename.isEmpty()) {
            Toast.makeText(this, "Введите название файла", Toast.LENGTH_SHORT).show();
            return;
        }

        if (!isExternalStorageReadable()) {
            Toast.makeText(this, "Внешнее хранилище недоступно", Toast.LENGTH_SHORT).show();
            return;
        }

        File path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS);
        File file = new File(path, filename + ".txt");

        try {
            FileInputStream fis = new FileInputStream(file);
            InputStreamReader isr = new InputStreamReader(fis);
            BufferedReader br = new BufferedReader(isr);
            StringBuilder sb = new StringBuilder();
            String line;

            while ((line = br.readLine()) != null) {
                sb.append(line).append("\n");
            }

            quoteEditText.setText(sb.toString().trim());
            br.close();
        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(this, "Файл не найден", Toast.LENGTH_SHORT).show();
        }
    }

    public boolean isExternalStorageWritable() {
        String state = Environment.getExternalStorageState();
        return Environment.MEDIA_MOUNTED.equals(state);
    }

    public boolean isExternalStorageReadable() {
        String state = Environment.getExternalStorageState();
        return Environment.MEDIA_MOUNTED.equals(state) ||
                Environment.MEDIA_MOUNTED_READ_ONLY.equals(state);
    }
}
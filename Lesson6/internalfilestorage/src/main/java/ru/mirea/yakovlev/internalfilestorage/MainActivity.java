package ru.mirea.yakovlev.internalfilestorage;

import android.content.Context;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;

public class MainActivity extends AppCompatActivity {
    private EditText dateEditText, descriptionEditText;
    private Button saveButton;
    private final String FILENAME = "history_date.txt";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        dateEditText = findViewById(R.id.dateEditText);
        descriptionEditText = findViewById(R.id.descriptionEditText);
        saveButton = findViewById(R.id.saveButton);

        saveButton.setOnClickListener(v -> saveToFile());

        loadFromFile();
    }

    private void saveToFile() {
        String date = dateEditText.getText().toString();
        String description = descriptionEditText.getText().toString();

        if (date.isEmpty() || description.isEmpty()) {
            Toast.makeText(this, "Заполните все поля", Toast.LENGTH_SHORT).show();
            return;
        }

        try (FileOutputStream fos = openFileOutput(FILENAME, Context.MODE_PRIVATE);
             OutputStreamWriter osw = new OutputStreamWriter(fos)) {

            osw.write(date + "\n");
            osw.write(description);

            Toast.makeText(this, "Данные сохранены", Toast.LENGTH_SHORT).show();
        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(this, "Ошибка сохранения", Toast.LENGTH_SHORT).show();
        }
    }

    private void loadFromFile() {
        try (FileInputStream fis = openFileInput(FILENAME);
             InputStreamReader isr = new InputStreamReader(fis);
             BufferedReader br = new BufferedReader(isr)) {

            String date = br.readLine();
            StringBuilder description = new StringBuilder();
            String line;

            while ((line = br.readLine()) != null) {
                description.append(line).append("\n");
            }

            dateEditText.setText(date);
            descriptionEditText.setText(description.toString().trim());
        } catch (IOException e) {}
    }
}
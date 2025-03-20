package ru.mirea.yakovlevma.toastapp;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void onClickShowToast(View view) {
        EditText editText = findViewById(R.id.editTextInput);
        String inputText = editText.getText().toString();

        String studentNumber = "28";
        String groupNumber = "БСБО-04-23";

        int charCount = inputText.length();

        String message = "СТУДЕНТ № " + studentNumber +
                " ГРУППА " + groupNumber +
                " Количество символов - " + charCount;

        Toast.makeText(this, message, Toast.LENGTH_LONG).show();
    }
}
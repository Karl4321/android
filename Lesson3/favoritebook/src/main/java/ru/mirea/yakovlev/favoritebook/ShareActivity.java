package ru.mirea.yakovlev.favoritebook;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class ShareActivity extends AppCompatActivity {
    private EditText editTextBookName;
    private EditText editTextQuote;
    private TextView textViewDevBook;
    private TextView textViewDevQuote;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_share);

        // Инициализация элементов интерфейса
        editTextBookName = findViewById(R.id.editTextBookName);
        editTextQuote = findViewById(R.id.editTextQuote);
        textViewDevBook = findViewById(R.id.textViewMyBook);
        textViewDevQuote = findViewById(R.id.textViewMyQuote);

        // Получение данных из MainActivity
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            String book_name = extras.getString(MainActivity.BOOK_NAME_KEY);
            String quotes_name = extras.getString(MainActivity.QUOTES_KEY);

            // Установка текста в два отдельных TextView
            textViewDevBook.setText("Любимая книга разработчика: " + book_name);
            textViewDevQuote.setText("Цитата: " + quotes_name);
        }
    }

    public void onSendDataClick(View view) {
        String bookName = editTextBookName.getText().toString();
        String quote = editTextQuote.getText().toString();

        // Формируем сообщение для отправки
        String message = "Название Вашей любимой книги: " + bookName +
                "\nЦитата: " + quote;

        Intent data = new Intent();
        data.putExtra(MainActivity.USER_MESSAGE, message);
        setResult(Activity.RESULT_OK, data);
        finish();
    }
}
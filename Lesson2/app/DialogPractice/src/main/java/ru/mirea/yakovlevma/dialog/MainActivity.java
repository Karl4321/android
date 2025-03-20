package ru.mirea.yakovlevma.dialog;

import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.material.snackbar.Snackbar;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void onClickTime(View view) {
        MyTimeDialogFragment timeDialog = new MyTimeDialogFragment();
        timeDialog.show(getSupportFragmentManager(), "timePicker");
    }

    public void onClickDate(View view) {
        MyDateDialogFragment dateDialog = new MyDateDialogFragment();
        dateDialog.show(getSupportFragmentManager(), "datePicker");
    }

    public void onClickProgress(View view) {
        MyProgressDialogFragment progressDialog = new MyProgressDialogFragment();
        progressDialog.show(getSupportFragmentManager(), "progressDialog");

        // Закрыть через 3 секунды (имитация загрузки)
        new Handler().postDelayed(() -> {
            if (progressDialog.getDialog() != null) {
                progressDialog.dismiss();
                Snackbar.make(
                        findViewById(android.R.id.content),
                        "Загрузка завершена!",
                        Snackbar.LENGTH_SHORT
                ).show();
            }
        }, 3000);
    }
}
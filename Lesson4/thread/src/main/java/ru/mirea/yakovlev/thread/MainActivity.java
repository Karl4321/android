package ru.mirea.yakovlev.thread;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import java.util.Arrays;

import ru.mirea.yakovlev.thread.databinding.ActivityMainBinding;

public class MainActivity extends AppCompatActivity {
    private ActivityMainBinding binding;
    private int counter = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        Thread mainThread = Thread.currentThread();
        binding.textView.setText("Имя текущего потока: " + mainThread.getName());
        mainThread.setName("Группа: БСБО-04-23, Список: 28, Фильм: Матрица");
        binding.textView.append("\nНовое имя: " + mainThread.getName());
        Log.d("ThreadInfo", "Stack: " + Arrays.toString(mainThread.getStackTrace()));
        Log.d("ThreadInfo", "Group: " + mainThread.getThreadGroup());

        binding.buttonMirea.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String totalPairsStr = binding.editTextTotalPairs.getText().toString();
                String studyDaysStr = binding.editTextStudyDays.getText().toString();

                if (totalPairsStr.isEmpty() || studyDaysStr.isEmpty()) {
                    Toast.makeText(MainActivity.this, "Введите данные", Toast.LENGTH_SHORT).show();
                    return;
                }

                try {
                    int totalPairs = Integer.parseInt(totalPairsStr);
                    int studyDays = Integer.parseInt(studyDaysStr);

                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            int threadNumber = counter++;
                            Log.d("ThreadProject", "Поток №" + threadNumber + " запущен");

                            long endTime = System.currentTimeMillis() + 1000;
                            while (System.currentTimeMillis() < endTime) {
                                synchronized (this) {
                                    try {
                                        wait(endTime - System.currentTimeMillis());
                                    } catch (Exception e) {
                                        Log.e("ThreadError", "Ошибка: ", e);
                                    }
                                }
                            }

                            final double average = (double) totalPairs / studyDays;
                            runOnUiThread(() -> binding.textView.append(
                                    "\nСреднее: " + String.format("%.2f", average)));

                            Log.d("ThreadProject", "Поток №" + threadNumber + " завершён");
                        }
                    }).start();

                } catch (NumberFormatException e) {
                    Toast.makeText(MainActivity.this, "Ошибка ввода", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}
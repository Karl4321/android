package ru.mirea.yakovlevma.activitylifecycle;

import android.os.Bundle;
import android.util.Log;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class Main2Activity extends AppCompatActivity {
    private static final String TAG = "ActivityLifecycle";
/*
    Ответы на вопросы:
    1. onCreate НЕ вызывается после нажатия Home и возврата в приложение.
       - При нажатии Home: onPause → onStop → (возможно onSaveInstanceState).
       - При возврате: onRestart → onStart → onResume.

    2. Значение EditText СОХРАНИТСЯ после нажатия Home и возврата:
       - Если активность не уничтожена системой (остается в стеке), EditText сохраняет состояние автоматически благодаря android:id.
       - Если система уничтожила активность (например, при нехватке памяти), текст восстановится через onRestoreInstanceState,
         ТОЛЬКО если добавить сохранение/восстановление вручную (см. примечание ниже).

    3. Значение EditText НЕ СОХРАНИТСЯ после нажатия Back и возврата:
       - Нажатие Back вызывает onDestroy → активность удаляется из стека.
       - Повторный запуск приложения вызовет onCreate → EditText будет пустым.
*/

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Log.d(TAG, "onCreate");
    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.d(TAG, "onStart");
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d(TAG, "onResume");
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.d(TAG, "onPause");
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.d(TAG, "onStop");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "onDestroy");
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        Log.d(TAG, "onRestart");
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        Log.d(TAG, "onSaveInstanceState");
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        Log.d(TAG, "onRestoreInstanceState");
    }
}
package ru.mirea.yakovlev.accelerometer;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class MainActivity extends AppCompatActivity implements SensorEventListener {
    private SensorManager sensorManager;
    private Sensor accelerometer;
    private TextView azimuthTextView;
    private TextView pitchTextView;
    private TextView rollTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Инициализация TextView'ов
        azimuthTextView = findViewById(R.id.textViewAzimuth);
        pitchTextView = findViewById(R.id.textViewPitch);
        rollTextView = findViewById(R.id.textViewRoll);

        // Инициализация SensorManager и акселерометра
        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        if (sensorManager != null) {
            accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        }

        // Настройка отступов с учетом системных панелей (Edge-to-Edge)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Регистрируем слушатель при возобновлении активности
        if (accelerometer != null) {
            sensorManager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_NORMAL);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        // Отменяем регистрацию слушателя для экономии ресурсов
        sensorManager.unregisterListener(this);
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
            float x = event.values[0];
            float y = event.values[1];
            float z = event.values[2];

            // Обновляем текстовые поля
            azimuthTextView.setText(String.format("Azimuth (X): %.2f", x));
            pitchTextView.setText(String.format("Pitch (Y): %.2f", y));
            rollTextView.setText(String.format("Roll (Z): %.2f", z));
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
        String accuracyMessage;

        switch (accuracy) {
            case SensorManager.SENSOR_STATUS_ACCURACY_HIGH:
                accuracyMessage = "Высокая точность";
                break;
            case SensorManager.SENSOR_STATUS_ACCURACY_MEDIUM:
                accuracyMessage = "Средняя точность";
                break;
            case SensorManager.SENSOR_STATUS_ACCURACY_LOW:
                accuracyMessage = "Низкая точность";
                break;
            case SensorManager.SENSOR_STATUS_UNRELIABLE:
                accuracyMessage = "Данные ненадежны!";
                break;
            default:
                accuracyMessage = "Неизвестный статус";
        }

        Log.d("SensorAccuracy", "Точность датчика " + sensor.getName() + ": " + accuracyMessage);
    }
}

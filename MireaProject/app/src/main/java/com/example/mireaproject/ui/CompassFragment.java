package com.example.mireaproject.ui;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.example.mireaprojectm.R;

public class CompassFragment extends Fragment implements SensorEventListener {

    private ImageView compassImage;
    private SensorManager sensorManager;
    private Sensor accelerometer;
    private Sensor magnetometer;

    private final float[] lastAccelerometer = new float[3];
    private final float[] lastMagnetometer = new float[3];
    private final float[] rotationMatrix = new float[9];
    private final float[] orientation = new float[3];

    private boolean lastAccelerometerSet = false;
    private boolean lastMagnetometerSet = false;

    private static final float ALPHA = 0.15f; // Коэффициент сглаживания

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_compass, container, false);
        setupViews(root);
        setupSensors();
        return root;
    }

    private void setupViews(View root) {
        compassImage = root.findViewById(R.id.compass_image);
        compassImage.post(() -> {
            compassImage.setPivotX(compassImage.getWidth() / 2f);
            compassImage.setPivotY(compassImage.getHeight() / 2f);
        });
    }

    private void setupSensors() {
        sensorManager = (SensorManager) requireActivity().getSystemService(Context.SENSOR_SERVICE);

        if (sensorManager != null) {
            accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
            magnetometer = sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);
        }

        if (accelerometer == null || magnetometer == null) {
            Toast.makeText(getContext(),
                    "Необходимые датчики отсутствуют!",
                    Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        registerSensorListeners();
    }

    private void registerSensorListeners() {
        if (accelerometer != null) {
            sensorManager.registerListener(this,
                    accelerometer,
                    SensorManager.SENSOR_DELAY_GAME);
        }
        if (magnetometer != null) {
            sensorManager.registerListener(this,
                    magnetometer,
                    SensorManager.SENSOR_DELAY_GAME);
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        unregisterSensorListeners();
    }

    private void unregisterSensorListeners() {
        sensorManager.unregisterListener(this);
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
            lowPassFilter(event.values.clone(), lastAccelerometer);
            lastAccelerometerSet = true;
        } else if (event.sensor.getType() == Sensor.TYPE_MAGNETIC_FIELD) {
            lowPassFilter(event.values.clone(), lastMagnetometer);
            lastMagnetometerSet = true;
        }

        if (lastAccelerometerSet && lastMagnetometerSet) {
            updateCompass();
        }
    }

    private void lowPassFilter(float[] input, float[] output) {
        for (int i = 0; i < input.length; i++) {
            output[i] = output[i] + ALPHA * (input[i] - output[i]);
        }
    }

    private void updateCompass() {
        if (SensorManager.getRotationMatrix(rotationMatrix, null,
                lastAccelerometer, lastMagnetometer)) {

            float[] adjustedMatrix = new float[9];
            SensorManager.remapCoordinateSystem(
                    rotationMatrix,
                    SensorManager.AXIS_X,
                    SensorManager.AXIS_Z,
                    adjustedMatrix
            );

            SensorManager.getOrientation(adjustedMatrix, orientation);

            float azimuth = (float) Math.toDegrees(orientation[0]);
            azimuth = (azimuth + 360) % 360; // Нормализация 0-360

            updateCompassUI(azimuth);
        }
    }

    private void updateCompassUI(float azimuth) {
        if (getActivity() != null) {
            getActivity().runOnUiThread(() ->
                    compassImage.setRotation(-azimuth)
            );
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
    }
}
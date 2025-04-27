package ru.mirea.yakovlev.looper;

import android.os.*;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;

import ru.mirea.yakovlev.looper.databinding.ActivityMainBinding;

public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding;
    private MyLooper myLooper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        Handler mainHandler = new Handler(Looper.getMainLooper()) {
            @Override
            public void handleMessage(Message msg) {
                Bundle bundle = msg.getData();
                String result = bundle.getString("result");
                Log.d("LooperDemo", result);
            }
        };

        myLooper = new MyLooper(mainHandler);
        myLooper.start();

        binding.btnSend.setOnClickListener(v -> {
            String ageStr = binding.etAge.getText().toString();
            String job = binding.etJob.getText().toString();

            if (!ageStr.isEmpty() && !job.isEmpty()) {
                int age = Integer.parseInt(ageStr);

                Message msg = Message.obtain();
                Bundle bundle = new Bundle();
                bundle.putString("KEY_JOB", job);
                bundle.putInt("KEY_DELAY", age);
                msg.setData(bundle);

                if (myLooper.mHandler != null) {
                    myLooper.mHandler.sendMessage(msg);
                }
            }
        });
    }
}
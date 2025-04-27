package ru.mirea.yakovlev.looper;

import android.os.*;
import android.util.Log;

public class MyLooper extends Thread {
    public Handler mHandler;
    private final Handler mainHandler;

    public MyLooper(Handler mainHandler) {
        this.mainHandler = mainHandler;
    }

    @Override
    public void run() {
        Looper.prepare();
        mHandler = new Handler(Looper.myLooper()) {
            @Override
            public void handleMessage(Message msg) {
                Bundle bundle = msg.getData();
                String job = bundle.getString("KEY_JOB");
                int delaySeconds = bundle.getInt("KEY_DELAY", 0);

                mHandler.postDelayed(() -> {
                    int length = job.length();
                    Message resultMsg = new Message();
                    Bundle resultBundle = new Bundle();
                    resultBundle.putString("result",
                            "Профессия: " + job + ", Длина: " + length);
                    resultMsg.setData(resultBundle);
                    mainHandler.sendMessage(resultMsg);
                }, delaySeconds * 1000L);
            }
        };
        Looper.loop();
    }
}
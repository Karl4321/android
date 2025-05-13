package ru.mirea.yakovlev.mireaproject;

import android.content.Context;
import android.util.Log;
import androidx.annotation.NonNull;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

public class BackgroundWorker extends Worker {
    private static final String TAG = "BackgroundWorker";

    public BackgroundWorker(
            @NonNull Context context,
            @NonNull WorkerParameters workerParams
    ) {
        super(context, workerParams);
    }

    @NonNull
    @Override
    public Result doWork() {
        Log.d(TAG, "Фоновая задача начата");
        try {
            Thread.sleep(10000);
        } catch (InterruptedException e) {
            return Result.failure();
        }
        Log.d(TAG, "Фоновая задача завершена");
        return Result.success();
    }
}
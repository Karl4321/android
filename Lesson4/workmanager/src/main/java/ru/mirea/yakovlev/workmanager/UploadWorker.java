package ru.mirea.yakovlev.workmanager;

import android.content.Context;
import android.util.Log;
import androidx.annotation.NonNull;
import androidx.work.Worker;
import androidx.work.WorkerParameters;
import java.util.concurrent.TimeUnit;

public class UploadWorker extends Worker {
    static final String TAG = "UploadWorker";

    public UploadWorker(@NonNull Context context, @NonNull WorkerParameters params) {
        super(context, params);
    }

    @Override
    public Result doWork() {
        Log.d(TAG, "doWork: start");
        try {
            TimeUnit.SECONDS.sleep(10); // Имитация долгой задачи
        } catch (InterruptedException e) {
            Log.e(TAG, "doWork: error", e);
            return Result.failure();
        }
        Log.d(TAG, "doWork: end");
        return Result.success();
    }
}
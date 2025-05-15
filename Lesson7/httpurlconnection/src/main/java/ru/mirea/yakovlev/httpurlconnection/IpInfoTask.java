package ru.mirea.yakovlev.httpurlconnection;

import android.os.AsyncTask;

import java.io.IOException;
import java.lang.ref.WeakReference;

public class IpInfoTask extends AsyncTask<String, Void, String> {
    private WeakReference<IpInfoCallback> callback;

    public IpInfoTask(IpInfoCallback callback) {
        this.callback = new WeakReference<>(callback);
    }

    @Override
    protected String doInBackground(String... urls) {
        try {
            return NetworkHelper.fetchData(urls[0]);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    protected void onPostExecute(String result) {
        if (callback.get() != null && result != null) {
            callback.get().onIpInfoReceived(result);
        }
    }
}
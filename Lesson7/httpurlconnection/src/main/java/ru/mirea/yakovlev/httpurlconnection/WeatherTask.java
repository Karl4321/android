package ru.mirea.yakovlev.httpurlconnection;

import android.os.AsyncTask;

import java.io.IOException;
import java.lang.ref.WeakReference;

public class WeatherTask extends AsyncTask<String, Void, String> {
    private WeakReference<WeatherCallback> callback;

    public WeatherTask(WeatherCallback callback) {
        this.callback = new WeakReference<>(callback);
    }

    @Override
    protected String doInBackground(String... locations) {
        String[] coords = locations[0].split(",");
        String url = "https://api.open-meteo.com/v1/forecast?latitude=" + coords[0] +
                "&longitude=" + coords[1] + "&current_weather=true";
        try {
            return NetworkHelper.fetchData(url);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    protected void onPostExecute(String result) {
        if (callback.get() != null && result != null) {
            callback.get().onWeatherReceived(result);
        }
    }
}
package ru.mirea.yakovlev.httpurlconnection;

import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Locale;

public class MainActivity extends AppCompatActivity implements IpInfoCallback, WeatherCallback {
    private TextView tvIp, tvCity, tvRegion, tvCountry, tvLoc, tvWeather;
    private Button btnFetch;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initViews();
        setupButtonListener();
    }

    private void initViews() {
        tvIp = findViewById(R.id.tv_ip);
        tvCity = findViewById(R.id.tv_city);
        tvRegion = findViewById(R.id.tv_region);
        tvCountry = findViewById(R.id.tv_country);
        tvLoc = findViewById(R.id.tv_loc);
        tvWeather = findViewById(R.id.tv_weather);
        btnFetch = findViewById(R.id.btn_fetch);
    }

    private void setupButtonListener() {
        btnFetch.setOnClickListener(v -> checkNetworkConnection());
    }

    private void checkNetworkConnection() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm != null ? cm.getActiveNetworkInfo() : null;

        if (netInfo != null && netInfo.isConnected()) {
            new IpInfoTask(this).execute("https://ipinfo.io/json");
        } else {
            Toast.makeText(this, "No internet connection", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onIpInfoReceived(String result) {
        try {
            JSONObject json = new JSONObject(result);
            updateIpInfoViews(json);
            String loc = json.optString("loc", "");
            if (!loc.isEmpty()) new WeatherTask(this).execute(loc);
        } catch (JSONException e) {
            e.printStackTrace();
            Toast.makeText(this, "Error parsing IP info", Toast.LENGTH_SHORT).show();
        }
    }

    private void updateIpInfoViews(JSONObject json) {
        runOnUiThread(() -> {
            tvIp.setText("IP: " + json.optString("ip", "N/A"));
            tvCity.setText("City: " + json.optString("city", "N/A"));
            tvRegion.setText("Region: " + json.optString("region", "N/A"));
            tvCountry.setText("Country: " + json.optString("country", "N/A"));
            tvLoc.setText("Coordinates: " + json.optString("loc", "N/A"));
        });
    }

    @Override
    public void onWeatherReceived(String result) {
        try {
            JSONObject json = new JSONObject(result);
            JSONObject currentWeather = json.getJSONObject("current_weather");
            double temperature = currentWeather.getDouble("temperature");
            int weatherCode = currentWeather.getInt("weathercode");

            runOnUiThread(() -> {
                tvWeather.setText(String.format(Locale.getDefault(),
                        "Current temperature: %.1f°C\nWeather code: %d",
                        temperature, weatherCode));
            });
        } catch (JSONException e) {
            e.printStackTrace();
            runOnUiThread(() -> {
                Toast.makeText(this, "Error parsing weather data", Toast.LENGTH_SHORT).show();
            });
        }
    }
}
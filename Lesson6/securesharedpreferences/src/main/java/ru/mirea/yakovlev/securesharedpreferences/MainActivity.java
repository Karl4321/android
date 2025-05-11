package ru.mirea.yakovlev.securesharedpreferences;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.security.keystore.KeyGenParameterSpec;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.security.crypto.EncryptedSharedPreferences;
import androidx.security.crypto.MasterKeys;

import java.io.IOException;
import java.security.GeneralSecurityException;

public class MainActivity extends AppCompatActivity {
    private TextView poetTextView;
    private ImageView poetImageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        poetTextView = findViewById(R.id.poetTextView);
        poetImageView = findViewById(R.id.poetImageView);

        try {
            KeyGenParameterSpec keyGenParameterSpec = MasterKeys.AES256_GCM_SPEC;
            String mainKeyAlias = MasterKeys.getOrCreate(keyGenParameterSpec);

            SharedPreferences sharedPreferences = EncryptedSharedPreferences.create(
                    "secret_shared_prefs",
                    mainKeyAlias,
                    getApplicationContext(),
                    EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
                    EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
            );

            sharedPreferences.edit()
                    .putString("POET_NAME", "Антон Павлович Чехов")
                    .putString("POET_IMAGE", "chehov.jpg")
                    .apply();

            String poetName = sharedPreferences.getString("POET_NAME", "Неизвестный поэт");
            String poetImage = sharedPreferences.getString("POET_IMAGE", "");

            poetTextView.setText("Любимый поэт: " + poetName);

            if (!poetImage.isEmpty()) {
                String resourceName = poetImage.replace(".jpg", "");
                int resId = getResources().getIdentifier(resourceName, "drawable", getPackageName());
                poetImageView.setImageResource(resId);
            }

        } catch (GeneralSecurityException | IOException e) {
            e.printStackTrace();
        }
    }
}
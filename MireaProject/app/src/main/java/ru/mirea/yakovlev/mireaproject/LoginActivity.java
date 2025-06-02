package ru.mirea.yakovlev.mireaproject;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;

public class LoginActivity extends AppCompatActivity {
    private FirebaseAuth firebaseAuth;
    private EditText emailInput;
    private EditText passwordInput;
    private ProgressBar loadingIndicator;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_auth);

        firebaseAuth = FirebaseAuth.getInstance();
        emailInput = findViewById(R.id.emailInput);
        passwordInput = findViewById(R.id.passwordInput);
        loadingIndicator = findViewById(R.id.loadingIndicator);

        findViewById(R.id.signInButton).setOnClickListener(v -> authenticateUser());
        findViewById(R.id.createAccountButton).setOnClickListener(v -> registerUser());
    }

    private void authenticateUser() {
        String email = emailInput.getText().toString().trim();
        String password = passwordInput.getText().toString().trim();

        if (email.isEmpty() || password.isEmpty()) {
            showToast("Please fill all fields");
            return;
        }

        loadingIndicator.setVisibility(View.VISIBLE);
        firebaseAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(task -> {
                    loadingIndicator.setVisibility(View.GONE);
                    if (task.isSuccessful()) {
                        startMainActivity();
                    } else {
                        showToast("Authentication failed");
                    }
                });
    }

    private void registerUser() {
        String email = emailInput.getText().toString().trim();
        String password = passwordInput.getText().toString().trim();

        if (email.isEmpty() || password.isEmpty()) {
            showToast("Please fill all fields");
            return;
        }

        loadingIndicator.setVisibility(View.VISIBLE);
        firebaseAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(task -> {
                    loadingIndicator.setVisibility(View.GONE);
                    if (task.isSuccessful()) {
                        startMainActivity();
                    } else {
                        showToast("Registration failed");
                    }
                });
    }

    private void startMainActivity() {
        startActivity(new Intent(this, MainActivity.class));
        finish();
    }

    private void showToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onStart() {
        super.onStart();
//        FirebaseAuth.getInstance().signOut();
        if (firebaseAuth.getCurrentUser() != null) {
            startMainActivity();
        }
    }
}
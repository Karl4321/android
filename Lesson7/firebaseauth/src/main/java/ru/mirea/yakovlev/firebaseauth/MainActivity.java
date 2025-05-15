package ru.mirea.yakovlev.firebaseauth;

import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import ru.mirea.yakovlev.firebaseauth.databinding.ActivityMainBinding;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "FirebaseAuth";
    private ActivityMainBinding binding;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Инициализация Firebase Auth
        mAuth = FirebaseAuth.getInstance();

        // Слушатели кнопок
        binding.createAccountButton.setOnClickListener(v -> {
            String email = binding.emailField.getText().toString().trim();
            String password = binding.passwordField.getText().toString().trim();
            createAccount(email, password);
        });

        binding.signInButton.setOnClickListener(v -> {
            String email = binding.emailField.getText().toString().trim();
            String password = binding.passwordField.getText().toString().trim();
            signIn(email, password);
        });

        binding.signOutButton.setOnClickListener(v -> signOut());
        binding.verifyEmailButton.setOnClickListener(v -> sendEmailVerification());
    }

    @Override
    protected void onStart() {
        super.onStart();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        updateUI(currentUser);
    }

    private void createAccount(String email, String password) {
        if (!validateForm()) return;

        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        Log.d(TAG, "createUserWithEmail:success");
                        FirebaseUser user = mAuth.getCurrentUser();
                        updateUI(user);
                    } else {
                        Log.w(TAG, "createUserWithEmail:failure", task.getException());
                        Toast.makeText(this, "Registration failed", Toast.LENGTH_SHORT).show();
                        updateUI(null);
                    }
                });
    }

    private void signIn(String email, String password) {
        if (!validateForm()) return;

        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        Log.d(TAG, "signInWithEmail:success");
                        FirebaseUser user = mAuth.getCurrentUser();
                        updateUI(user);
                    } else {
                        Log.w(TAG, "signInWithEmail:failure", task.getException());
                        Toast.makeText(this, "Authentication failed", Toast.LENGTH_SHORT).show();
                        updateUI(null);
                    }
                });
    }

    private void signOut() {
        mAuth.signOut();
        updateUI(null);
    }

    private void sendEmailVerification() {
        FirebaseUser user = mAuth.getCurrentUser();
        if (user != null) {
            user.sendEmailVerification()
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            Toast.makeText(this, "Verification email sent", Toast.LENGTH_SHORT).show();
                        }
                    });
        }
    }

    private boolean validateForm() {
        boolean isValid = true;

        // Валидация email
        if (TextUtils.isEmpty(binding.emailField.getText())) {
            binding.emailField.setError("Required");
            isValid = false;
        } else {
            binding.emailField.setError(null);
        }

        // Валидация пароля
        if (TextUtils.isEmpty(binding.passwordField.getText())) {
            binding.passwordField.setError("Required");
            isValid = false;
        } else {
            binding.passwordField.setError(null);
        }

        return isValid;
    }

    private void updateUI(FirebaseUser user) {
        if (user != null) {
            // Показать блок для авторизованных пользователей
            binding.signedInButtons.setVisibility(View.VISIBLE);
            binding.emailPasswordButtons.setVisibility(View.GONE);
            binding.emailPasswordFields.setVisibility(View.GONE);

            // Обновить статус
            binding.statusTextView.setText(
                    String.format("Email: %s\nVerified: %s",
                            user.getEmail(),
                            user.isEmailVerified())
            );

            binding.detailTextView.setText(
                    String.format("UID: %s", user.getUid())
            );

            // Управление кнопкой верификации
            binding.verifyEmailButton.setEnabled(!user.isEmailVerified());
        } else {
            // Показать форму входа
            binding.signedInButtons.setVisibility(View.GONE);
            binding.emailPasswordButtons.setVisibility(View.VISIBLE);
            binding.emailPasswordFields.setVisibility(View.VISIBLE);

            // Сбросить статус
            binding.statusTextView.setText("Signed out");
            binding.detailTextView.setText("");
        }
    }
}
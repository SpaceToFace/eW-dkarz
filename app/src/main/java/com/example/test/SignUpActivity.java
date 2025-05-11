package com.example.test;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.text.InputType;
import android.util.Patterns;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.WindowCompat;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

import java.util.regex.Pattern;

public class SignUpActivity extends AppCompatActivity {

    EditText user_email, user_password, user_confirm_password;
    Button sign_up;
    TextView login_text;
    private FirebaseAuth auth;

    // Wzorzec dla silnego hasła: min 8 znaków, przynajmniej jedna wielka litera, mała litera, cyfra i znak specjalny
    private static final Pattern STRONG_PASSWORD_PATTERN = Pattern.compile(
            "^" +
                    "(?=.*[0-9])" +         // Przynajmniej jedna cyfra
                    "(?=.*[a-z])" +         // Przynajmniej jedna mała litera
                    "(?=.*[A-Z])" +         // Przynajmniej jedna wielka litera
                    "(?=.*[@#$%^&+=!])" +   // Przynajmniej jeden znak specjalny
                    ".{8,}" +               // Co najmniej 8 znaków
                    "$");

    @SuppressLint({"MissingInflatedId", "ClickableViewAccessibility"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        WindowCompat.setDecorFitsSystemWindows(getWindow(), false);
        setContentView(R.layout.activity_sign_up);

        auth = FirebaseAuth.getInstance();

        user_email = findViewById(R.id.user_email);
        user_password = findViewById(R.id.user_password);
        user_confirm_password = findViewById(R.id.user_confirm_password);
        sign_up = findViewById(R.id.sign_up_button);
        login_text = findViewById(R.id.login_text);

        final Typeface originalTypeface = user_password.getTypeface();

        // 🔒 Obsługa podglądu hasła dla user_password
        setupPasswordToggle(user_password, originalTypeface);

        // 🔒 Obsługa podglądu hasła dla user_confirm_password
        setupPasswordToggle(user_confirm_password, originalTypeface);

        sign_up.setOnClickListener(v -> {
            String email = user_email.getText().toString().trim();
            String password = user_password.getText().toString().trim();
            String confirmPassword = user_confirm_password.getText().toString().trim();

            if (!validateEmail(email)) {
                user_email.setError("Valid email is required");
                return;
            }

            if (!validatePassword(password)) {
                user_password.setError("Password must be at least 8 characters, include a digit, uppercase, lowercase, and special character");
                return;
            }

            if (!password.equals(confirmPassword)) {
                user_confirm_password.setError("Passwords do not match");
                return;
            }

            createUser(email, password);
        });

        login_text.setOnClickListener(v -> {
            Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
            startActivity(intent);
            finish();
        });
    }

    // 🔑 Metoda do obsługi przełączania widoczności hasła
    @SuppressLint("ClickableViewAccessibility")
    private void setupPasswordToggle(EditText passwordField, Typeface originalTypeface) {
        passwordField.setOnTouchListener((v, event) -> {
            final int DRAWABLE_END = 2; // Pozycja drawableEnd (prawa strona)
            if (event.getAction() == MotionEvent.ACTION_UP) {
                if (event.getRawX() >= (passwordField.getRight() - passwordField.getCompoundDrawables()[DRAWABLE_END].getBounds().width())) {
                    int selection = passwordField.getSelectionEnd(); // Zapamiętaj pozycję kursora

                    if ((passwordField.getInputType() & InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD) == InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD) {
                        // Ukryj hasło
                        passwordField.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                        passwordField.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.baseline_visibility_off_24, 0);
                    } else {
                        // Pokaż hasło
                        passwordField.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
                        passwordField.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.baseline_visibility_24, 0);
                    }

                    // Przywróć oryginalny styl czcionki po zmianie InputType
                    passwordField.setTypeface(originalTypeface);

                    passwordField.setSelection(selection); // Przywróć pozycję kursora
                    return true;
                }
            }
            return false;
        });

        login_text = findViewById(R.id.login_text);
        login_text.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }

    private boolean validateEmail(String email) {
        if (email.isEmpty()) {
            return false;
        }
        return Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }

    private boolean validatePassword(String password) {
        return STRONG_PASSWORD_PATTERN.matcher(password).matches();
    }

    private void createUser(String email, String password) {
        auth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(getApplicationContext(), "Sign Up Successfully", Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
                            startActivity(intent);
                        } else {
                            Toast.makeText(getApplicationContext(), "Sign Up Failed: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }
}
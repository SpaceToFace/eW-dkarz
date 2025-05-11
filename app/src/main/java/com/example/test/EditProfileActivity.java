package com.example.test;

import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class EditProfileActivity extends AppCompatActivity {

    private EditText editOldPasswordEditText, editPasswordEditText;
    private Button saveButton;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);

        mAuth = FirebaseAuth.getInstance();

        editOldPasswordEditText = findViewById(R.id.editOldPasswordEditText);
        editPasswordEditText = findViewById(R.id.editPasswordEditText);
        saveButton = findViewById(R.id.saveButton);

        saveButton.setOnClickListener(v -> saveNewPassword());
    }

    private void saveNewPassword() {
        String oldPassword = editOldPasswordEditText.getText().toString().trim();
        String newPassword = editPasswordEditText.getText().toString().trim();

        if (TextUtils.isEmpty(oldPassword)) {
            editOldPasswordEditText.setError("Podaj aktualne hasło");
            return;
        }

        if (TextUtils.isEmpty(newPassword)) {
            editPasswordEditText.setError("Podaj nowe hasło");
            return;
        }

        FirebaseUser currentUser = mAuth.getCurrentUser();

        if (currentUser != null && currentUser.getEmail() != null) {
            AuthCredential credential = EmailAuthProvider.getCredential(
                    currentUser.getEmail(), oldPassword
            );

            currentUser.reauthenticate(credential).addOnCompleteListener(reAuthTask -> {
                if (reAuthTask.isSuccessful()) {
                    currentUser.updatePassword(newPassword)
                            .addOnCompleteListener(task -> {
                                if (task.isSuccessful()) {
                                    Toast.makeText(this, "Hasło zostało zmienione", Toast.LENGTH_SHORT).show();
                                    finish();
                                } else {
                                    Toast.makeText(this, "Błąd zmiany hasła: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                                }
                            });
                } else {
                    Toast.makeText(this, "Nieprawidłowe hasło: " + reAuthTask.getException().getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        } else {
            Toast.makeText(this, "Użytkownik niezalogowany", Toast.LENGTH_SHORT).show();
        }
    }
}

package com.example.test;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Typeface;
import android.location.Location;
import android.os.Bundle;
import android.text.InputType;
import android.util.Patterns;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;

public class LoginActivity extends AppCompatActivity {

    EditText user_name,user_password;
    Button login_button;
    CheckBox remember_me;
    TextView sign_up_text;

    public static final String SHARED_PREFS = "sharedPrefs";

    private  FirebaseAuth auth;
    private FusedLocationProviderClient fusedLocationClient;
    static final int LOCATION_PERMISSION_REQUEST_CODE = 1;


    @SuppressLint({"MissingInflatedId", "ClickableViewAccessibility"})


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        // Sprawdzenie i prośba o uprawnienia do lokalizacji
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_PERMISSION_REQUEST_CODE);
        }
        checkAutoLogin();

        auth = FirebaseAuth.getInstance();
        user_name = findViewById(R.id.user_name);
        user_password = findViewById(R.id.user_password);
        login_button = findViewById(R.id.login_button);
        remember_me = findViewById(R.id.remember_me);
        sign_up_text = findViewById(R.id.sign_up_text);

        //obsługa widoczności hasła
        final Typeface originalTypeface = user_password.getTypeface();

        user_password.setOnTouchListener((v, event) -> {
            final int DRAWABLE_END = 2; //
            if (event.getAction() == MotionEvent.ACTION_UP) {
                if (event.getRawX() >= (user_password.getRight() - user_password.getCompoundDrawables()[DRAWABLE_END].getBounds().width())) {
                    int selection = user_password.getSelectionEnd();

                    if ((user_password.getInputType() & InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD) == InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD) {
                        // Ukryj hasło
                        user_password.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                        user_password.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.baseline_visibility_off_24, 0);
                    } else {
                        // Pokaż hasło
                        user_password.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
                        user_password.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.baseline_visibility_24, 0);
                    }


                    user_password.setTypeface(originalTypeface);

                    user_password.setSelection(selection);
                    return true;
                }
            }
            return false;
        });


        login_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = user_name.getText().toString();
                String password = user_password.getText().toString();

                if (!email.isEmpty() && Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                    if (!password.isEmpty()) {
                        auth.signInWithEmailAndPassword(email, password)
                                .addOnSuccessListener(authResult -> {
                                    if (remember_me.isChecked()) {
                                        SharedPreferences sharedPreferences = getSharedPreferences(SHARED_PREFS, MODE_PRIVATE);
                                        SharedPreferences.Editor editor = sharedPreferences.edit();
                                        editor.putBoolean("isLoggedIn", true);
                                        editor.apply();
                                    }
                                    getCurrentLocation();
                                    Toast.makeText(LoginActivity.this, "Login Successful", Toast.LENGTH_SHORT).show();

                                    Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                    startActivity(intent);
                                    finish();
                                })
                                .addOnFailureListener(e ->
                                        Toast.makeText(LoginActivity.this, "Login Failed", Toast.LENGTH_SHORT).show());
                    } else {
                        user_password.setError("Empty Fields Are Not Allowed");
                    }
                } else if (email.isEmpty()) {
                    user_name.setError("Empty Fields Are Not Allowed");
                } else {
                    user_name.setError("Please Enter a Correct Email");
                }
            }
        });
        sign_up_text.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(),SignUpActivity.class);
                startActivity(intent);
            }
        });
    }


    private void checkAutoLogin() {
        // Sprawdza, czy użytkownik jest zapisany jako zalogowany tylko, jeśli zaznaczył "Zapamiętaj mnie"
        SharedPreferences sharedPreferences = getSharedPreferences(SHARED_PREFS, MODE_PRIVATE);
        boolean isLoggedIn = sharedPreferences.getBoolean("isLoggedIn", false);
        if (isLoggedIn) {
            // Przekieruj do MainActivity, jeśli użytkownik jest zapisany jako zalogowany
            startActivity(new Intent(getApplicationContext(), MainActivity.class));
            finish();
        }

    }
    private void getCurrentLocation() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_PERMISSION_REQUEST_CODE);
            return;
        }

        fusedLocationClient.getLastLocation().addOnSuccessListener(new OnSuccessListener<Location>() {
            @Override
            public void onSuccess(Location location) {
                if (location != null) {
                    double latitude = location.getLatitude();
                    double longitude = location.getLongitude();
                    saveLocation(latitude, longitude);
                }
            }
        });
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                getCurrentLocation();  // Pobranie lokalizacji po zaakceptowaniu
            } else {
                Toast.makeText(this, "Lokalizacja wymagana do pełnej funkcjonalności", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void saveLocation(double lat, double lon) {
        SharedPreferences sharedPreferences = getSharedPreferences(SHARED_PREFS, MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putFloat("latitude", (float) lat);
        editor.putFloat("longitude", (float) lon);
        editor.apply();
    }
}


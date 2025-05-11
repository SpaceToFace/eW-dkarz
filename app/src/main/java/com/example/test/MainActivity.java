package com.example.test;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity {

    private static final int LOCATION_PERMISSION_REQUEST_CODE = 101;
    private BottomNavigationView bottomNavigationView;
    private FusedLocationProviderClient fusedLocationClient;
    private WeatherViewModel weatherViewModel;
    private Location userLocation;

    @Override
    protected void onStart() {
        super.onStart();
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser == null) {
            startActivity(new Intent(this, LoginActivity.class));
            finish(); // Jeśli użytkownik nie jest zalogowany, przekieruj do logowania
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        bottomNavigationView = findViewById(R.id.bottomNavigationView);
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        weatherViewModel = new ViewModelProvider(this).get(WeatherViewModel.class);

        bottomNavigationView.setOnItemSelectedListener(this::onNavigationItemSelected);

        // Wczytaj domyślny fragment
        if (savedInstanceState == null) {
            setFragment(new MainFragment());
        }


        // Poproś o lokalizację i pobierz pogodę
        requestLocationPermission();
    }

    private boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
        int itemId = menuItem.getItemId();
        if (itemId == R.id.atlas) {
            setFragment(new MainFragment());
        } else if (itemId == R.id.blank) {
            setFragment(new JournalFragment());
        } else if (itemId == R.id.blank3) {
            setFragment(new ProfileFragment());
        } else if (itemId == R.id.blank4) {
            setFragment(new ActivityFragment());
        }
        return true;
        }

    private void requestLocationPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    LOCATION_PERMISSION_REQUEST_CODE);
        } else {
            getCurrentLocation();
        }
    }

    private void getCurrentLocation() {
        try {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                    == PackageManager.PERMISSION_GRANTED) {

                fusedLocationClient.getLastLocation().addOnSuccessListener(location -> {
                    if (location != null) {
                        userLocation = location; 
                        fetchWeatherData(location.getLatitude(), location.getLongitude());
                    } else {
                        Toast.makeText(this, "Nie udało się uzyskać lokalizacji.", Toast.LENGTH_SHORT).show();
                        Log.e("MainActivity", "Lokalizacja jest null.");
                    }
                }).addOnFailureListener(e -> {
                    Toast.makeText(this, "Błąd lokalizacji: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    Log.e("MainActivity", "Błąd pobierania lokalizacji: " + e.getMessage());
                });

            }
        } catch (SecurityException e) {
            Toast.makeText(this, "Brak uprawnień do lokalizacji.", Toast.LENGTH_SHORT).show();
            Log.e("MainActivity", "SecurityException: " + e.getMessage());
        }
    }


    private void fetchWeatherData(double latitude, double longitude) {
        WeatherRepository repository = new WeatherRepository();
        repository.fetchWeather(latitude, longitude, new Callback<WeatherApiResponse>() {
            @Override
            public void onResponse(Call<WeatherApiResponse> call, Response<WeatherApiResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    WeatherApiResponse data = response.body();

                    String city = data.name != null ? data.name : "Nieznane miasto";
                    String temperature = (int) data.main.temp + "°C";
                    String description = data.weather.get(0).description;
                    String iconUrl = "https://openweathermap.org/img/wn/" + data.weather.get(0).icon + "@2x.png";

                    WeatherData weatherData = new WeatherData(city, temperature, description, iconUrl);
                    weatherViewModel.setWeatherData(weatherData); // Aktualizacja ViewModelu

                    Log.d("MainActivity", "Pobrano pogodę: " + city + ", " + temperature + ", " + description);
                } else {
                    Log.e("MainActivity", "Błąd odpowiedzi API: " + response.code());
                }
            }

            @Override
            public void onFailure(Call<WeatherApiResponse> call, Throwable t) {
                Log.e("MainActivity", "Błąd API: " + t.getMessage());
                Toast.makeText(MainActivity.this, "Nie udało się pobrać danych pogodowych.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void setFragment(Fragment fragment) {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.fragmentContainerView, fragment, fragment.getClass().getSimpleName());
        transaction.commit();
    }
    public Location getUserLocation() {
        return userLocation;
    }
    private void updateUserLocation(Location location) {

        this.userLocation = location;
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                getCurrentLocation(); // Jeśli uprawnienia zostały przyznane
            } else {
                Toast.makeText(this, "Uprawnienia do lokalizacji zostały odrzucone.", Toast.LENGTH_SHORT).show();
                Log.e("MainActivity", "Użytkownik odrzucił uprawnienia do lokalizacji.");
            }
        }
    }
}

package com.example.test;

import android.animation.ValueAnimator;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ActivityFragment extends Fragment {

    private TextView fishActivityTextView, weatherDetailsTextView;
    private ProgressBar progressBar;
    private WeatherRepository weatherRepository;

    public ActivityFragment() {
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_fragment, container, false);

        fishActivityTextView = view.findViewById(R.id.fishActivityTextView);
        weatherDetailsTextView = view.findViewById(R.id.weatherDetailsTextView);
        progressBar = view.findViewById(R.id.progressBar);

        weatherRepository = new WeatherRepository();

        // Pobieranie lokalizacji i pogody
        fetchWeatherForCurrentLocation();

        return view;
    }

    private void fetchWeatherForCurrentLocation() {
        progressBar.setVisibility(View.VISIBLE);

        MainActivity activity = (MainActivity) getActivity();
        if (activity != null) {
            Location userLocation = activity.getUserLocation();

            if (userLocation != null) {
                requestWeatherData(userLocation);
            } else {
                new Handler().postDelayed(() -> {
                    Location updatedLocation = activity.getUserLocation();
                    if (updatedLocation != null) {
                        requestWeatherData(updatedLocation);
                    } else {
                        Toast.makeText(getContext(), "Brak dostępu do lokalizacji", Toast.LENGTH_SHORT).show();
                        Log.e("ActivityFragment", "Lokalizacja nadal null po odczekaniu.");
                        progressBar.setVisibility(View.GONE);
                    }
                }, 2000);
            }
        }
    }

    private void requestWeatherData(Location location) {
        double latitude = location.getLatitude();
        double longitude = location.getLongitude();
        Log.d("ActivityFragment", "Pobrano lokalizację: " + latitude + ", " + longitude);

        weatherRepository.fetchWeather(latitude, longitude, new Callback<WeatherApiResponse>() {
            @Override
            public void onResponse(Call<WeatherApiResponse> call, Response<WeatherApiResponse> response) {
                progressBar.setVisibility(View.GONE);
                if (response.isSuccessful() && response.body() != null) {
                    updateUI(response.body());
                } else {
                    Toast.makeText(getContext(), "Błąd pobierania danych pogodowych", Toast.LENGTH_SHORT).show();
                    Log.e("ActivityFragment", "Niepoprawna odpowiedź API.");
                }
            }

            @Override
            public void onFailure(Call<WeatherApiResponse> call, Throwable t) {
                progressBar.setVisibility(View.GONE);
                Toast.makeText(getContext(), "Błąd połączenia z API", Toast.LENGTH_SHORT).show();
                Log.e("ActivityFragment", "Błąd pobierania pogody: " + t.getMessage());
            }
        });
    }

    private void updateUI(WeatherApiResponse weatherData) {
        if (weatherData == null) return;

        String condition = translateWeatherCondition(weatherData.weather.get(0).main);
        double temperature = weatherData.main.temp;
        double windSpeed = weatherData.wind.speed;
        double pressure = weatherData.main.pressure;

        int fishActivity = calculateFishActivity(condition, temperature, windSpeed, pressure);

        animateFishActivity(fishActivity);

        weatherDetailsTextView.setText(
                "🌦 Pogoda: " + condition + "\n" +
                        "🌡 Temperatura: " + temperature + "°C\n" +
                        "💨 Wiatr: " + windSpeed + " m/s\n" +
                        "📊 Ciśnienie: " + pressure + " hPa"
        );
    }


    private void animateFishActivity(int finalValue) {
        ValueAnimator animator = ValueAnimator.ofInt(0, finalValue);
        animator.setDuration(2000);
        animator.addUpdateListener(animation -> fishActivityTextView.setText("🎣 Aktywność ryb: " + animation.getAnimatedValue() + "%"));
        animator.start();
    }

    private int calculateFishActivity(String weatherCondition, double temperature, double windSpeed, double pressure) {
        int fishActivity = 50; // Domyślna wartość

        switch (weatherCondition) {
            case "Słonecznie":
                fishActivity += 13;
                break;
            case "Pochmurno":
                fishActivity += 24;
                break;
            case "Deszcz":
                fishActivity -= 12;
                break;
            case "Burza":
                fishActivity -= 26;
                break;
            case "Śnieg":
                fishActivity -= 12;
                break;
        }

        if (temperature >= 15 && temperature <= 25) {
            fishActivity += 15;
        } else if (temperature < 5 || temperature > 30) {
            fishActivity -= 20;
        }

        if (windSpeed > 10) {
            fishActivity -= 15;
        }

        if (pressure < 1000) {
            fishActivity -= 10;
        } else if (pressure > 1020) {
            fishActivity += 5;
        }

        return Math.max(0, Math.min(100, fishActivity));
    }


    private String translateWeatherCondition(String englishCondition) {
        switch (englishCondition) {
            case "Clear":
                return "Słonecznie";
            case "Clouds":
                return "Pochmurno";
            case "Rain":
                return "Deszcz";
            case "Thunderstorm":
                return "Burza";
            case "Snow":
                return "Śnieg";
            case "Mist":
                return "Mgła";
            case "Fog":
                return "Mglisto";
            case "Drizzle":
                return "Mżawka";
            default:
                return englishCondition;
        }
    }
}

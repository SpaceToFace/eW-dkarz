package com.example.test;

import android.util.Log;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class WeatherViewModel extends ViewModel {

    private final MutableLiveData<WeatherData> weatherLiveData = new MutableLiveData<>();
    private final WeatherRepository weatherRepository = new WeatherRepository();

    public LiveData<WeatherData> getWeatherLiveData() {
        return weatherLiveData;
    }

    public void setWeatherData(WeatherData weatherData) {
        weatherLiveData.setValue(weatherData);
    }

    public void fetchWeather(double latitude, double longitude) {
        weatherRepository.fetchWeather(latitude, longitude, new Callback<WeatherApiResponse>() {
            @Override
            public void onResponse(Call<WeatherApiResponse> call, Response<WeatherApiResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    WeatherApiResponse data = response.body();
                    String city = data.name != null ? data.name : "Nieznane miasto";
                    String temperature = (int) data.main.temp + "°C";
                    String description = data.weather.get(0).description;

                    String iconUrl = "https://openweathermap.org/img/wn/" + data.weather.get(0).icon + "@4x.png";

                    WeatherData weatherData = new WeatherData(city, temperature, description, iconUrl);
                    setWeatherData(weatherData);
                    Log.d("WeatherViewModel", "Pobrano pogodę: " + city + ", " + temperature + ", " + description);
                } else {
                    Log.e("WeatherViewModel", "Błąd odpowiedzi API: " + response.code());
                }
            }

            @Override
            public void onFailure(Call<WeatherApiResponse> call, Throwable t) {
                Log.e("WeatherViewModel", "Błąd API: " + t.getMessage());
            }
        });
    }
}

package com.example.test;

import android.util.Log;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class WeatherRepository {

    private static final String BASE_URL = "https://api.openweathermap.org/data/2.5/";
    private static final String API_KEY = "be9fcac8efb57b6518412493ba3c92d6";

    private final WeatherApiService apiService;

    public WeatherRepository() {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        apiService = retrofit.create(WeatherApiService.class);
    }

    public void fetchWeather(double latitude, double longitude, Callback<WeatherApiResponse> callback) {
        apiService.getWeather(latitude, longitude, "metric","pl", API_KEY).enqueue(new Callback<WeatherApiResponse>() {
            @Override
            public void onResponse(Call<WeatherApiResponse> call, Response<WeatherApiResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    Log.d("WeatherRepository", "Pobrano dane pogodowe: " + response.body().name);
                    callback.onResponse(call, response);
                } else {
                    Log.e("WeatherRepository", "Błąd odpowiedzi: " + response.code());
                    callback.onFailure(call, new Throwable("Nieudana odpowiedź: " + response.code()));
                }
            }
            @Override
            public void onFailure(Call<WeatherApiResponse> call, Throwable t) {
                Log.e("WeatherRepository", "Błąd pobierania pogody: " + t.getMessage());
                callback.onFailure(call, t);
            }
        });
    }
}

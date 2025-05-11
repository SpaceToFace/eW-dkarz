package com.example.test;

public class WeatherData {
    private String city;
    private String temperature;
    private String description;
    private String iconUrl;

    public WeatherData(String city, String temperature, String description, String iconUrl) {
        this.city = city;
        this.temperature = temperature;
        this.description = description;
        this.iconUrl = iconUrl;
    }

    public String getCity() { return city; }
    public String getTemperature() { return temperature; }
    public String getDescription() { return description; }
    public String getIconUrl() { return iconUrl; }
}

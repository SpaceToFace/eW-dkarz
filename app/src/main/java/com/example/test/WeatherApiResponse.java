package com.example.test;

import java.util.List;

public class WeatherApiResponse {
    public Main main;
    public Wind wind;
    public List<Weather> weather;
    public String name;

    public class Main {
        public double temp;
        public double pressure;
    }

    public class Wind {
        public double speed;
    }

    public class Weather {
        public String main;
        public String description;
        public String icon;
    }
}

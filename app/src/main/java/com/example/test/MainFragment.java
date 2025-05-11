package com.example.test;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DecodeFormat;

import java.util.ArrayList;

public class MainFragment extends Fragment implements AtlasRecyclerViewInterface {

    private RecyclerView recyclerView;
    private ArrayList<AtlasItems> atlasList;
    private AtlasAdapter atlasAdapter;
    private ImageView weatherIcon;
    private TextView weatherPlace, weatherTemp, weatherDesc,moreButton;


    private WeatherViewModel weatherViewModel;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Inicjalizacja listy atlasu
        atlasList = new ArrayList<>();
        atlasList.add(new AtlasItems("Amur Biały", "Okres ochronny: Brak", "Wymiar ochronny: 1 stycznia - 30 kwietnia", R.drawable.amurbialy));
        atlasList.add(new AtlasItems("Boleń", "Okres ochronny: Brak", "Wymiar ochronny: do 40cm", R.drawable.bolen));
        atlasList.add(new AtlasItems("Brzana", "Okres ochronny: 1 stycznia - 30 czerwca", "Wymiar ochronny: do 40cm", R.drawable.ic_launcher_background));
        atlasList.add(new AtlasItems("Certa", "Okres ochronny: 1 stycznia - 30 czerwca", "Wymiar ochronny: do 30cm", R.drawable.ic_launcher_background));
        atlasList.add(new AtlasItems("Głowacica", "Okres ochronny: 1 marca - 31 maja", "Wymiar ochronny: do 70cm", R.drawable.ic_launcher_background));
        atlasList.add(new AtlasItems("Jaź", "Okres ochronny: Brak", "Wymiar ochronny: do 25cm", R.drawable.ic_launcher_background));
        atlasList.add(new AtlasItems("Karaś", "Okres ochronny: Brak", "Wymiar ochronny: Brak", R.drawable.ic_launcher_background));
        atlasList.add(new AtlasItems("Jelec", "Okres ochronny: Brak", "Wymiar ochronny: do 15cm", R.drawable.ic_launcher_background));
        atlasList.add(new AtlasItems("Karp królewski - Lustrzeń", "Okres ochronny: do 30cm", "Wymiar ochronny: Brak", R.drawable.ic_launcher_background));
        atlasList.add(new AtlasItems("Karp pełnołuski", "Okres ochronny: Brak", "Wymiar ochronny: do 30cm", R.drawable.ic_launcher_background));
        atlasList.add(new AtlasItems("Karp", "Okres ochronny: Brak", "Wymiar ochronny: Brak", R.drawable.ic_launcher_background));
        atlasList.add(new AtlasItems("Kiełb", "Okres ochronny: Brak", "Wymiar ochronny: Brak", R.drawable.ic_launcher_background));
        atlasList.add(new AtlasItems("Kleń", "Okres ochronny: Brak", "Wymiar ochronny: do 25cm", R.drawable.ic_launcher_background));
        atlasList.add(new AtlasItems("Koza", "Okres ochronny: Brak", "Wymiar ochronny: Brak", R.drawable.ic_launcher_background));
        atlasList.add(new AtlasItems("Kiełb krótkowąsy", "zakaz połowu", "", R.drawable.ic_launcher_background));
        atlasList.add(new AtlasItems("Leszcz", "Okres ochronny: Brak", "Wymiar ochronny: Brak", R.drawable.ic_launcher_background));
        atlasList.add(new AtlasItems("Lin", "Okres ochronny: Brak", "Wymiar ochronny: do 25cm", R.drawable.ic_launcher_background));
        atlasList.add(new AtlasItems("Lipień", "Okres ochronny: 1 marca - 31 maja", "Wymiar ochronny: do 30cm", R.drawable.ic_launcher_background));
        atlasList.add(new AtlasItems("Okoń", "Okres ochronny: Brak", "Wymiar ochronny: do 17cm", R.drawable.ic_launcher_background));
        atlasList.add(new AtlasItems("Miętus", "Okres ochronny: 1 grudnia - 1 marca", "Wymiar ochronny: do 25cm", R.drawable.ic_launcher_background));
        atlasList.add(new AtlasItems("Piskorz", "zakaz połowu ", "", R.drawable.ic_launcher_background));
        atlasList.add(new AtlasItems("Płoć", "Okres ochronny: Brak", "Wymiar ochronny: do 20cm", R.drawable.ic_launcher_background));
        atlasList.add(new AtlasItems("Pstrąg tęczowy", "Okres ochronny: Brak", "Wymiar ochronny: do 40 cm (morze)", R.drawable.ic_launcher_background));
        atlasList.add(new AtlasItems("Pstrąg potokowy", "Okres ochronny: Brak", "Wymiar ochronny: Brak", R.drawable.ic_launcher_background));
        atlasList.add(new AtlasItems("Sandacz", "Okres ochronny: Brak", "Wymiar ochronny: Brak", R.drawable.ic_launcher_background));
        atlasList.add(new AtlasItems("Różanka", "zakaz połowu ", "", R.drawable.ic_launcher_background));
        atlasList.add(new AtlasItems("Sczupak", "Okres ochronny: 1 stycznia - 30 kwietnia", "Wymiar ochronny: do 45 cm", R.drawable.ic_launcher_background));
        atlasList.add(new AtlasItems("Śliz", "Okres ochronny: Brak", "Wymiar ochronny: Brak", R.drawable.ic_launcher_background));
        atlasList.add(new AtlasItems("Sum", "Okres ochronny: Brak", "Wymiar ochronny: Brak", R.drawable.ic_launcher_background));
        atlasList.add(new AtlasItems("Tołpyga", "Okres ochronny: Brak", "Wymiar ochronny: Brak", R.drawable.ic_launcher_background));
        atlasList.add(new AtlasItems("Ukleja", "Okres ochronny: Brak", "Wymiar ochronny: Brak", R.drawable.ic_launcher_background));
        atlasList.add(new AtlasItems("Świnka", "Okres ochronny: Brak", "Wymiar ochronny: Brak", R.drawable.ic_launcher_background));
        atlasList.add(new AtlasItems("Węgorz", "Okres ochronny: Brak", "Wymiar ochronny: Brak", R.drawable.ic_launcher_background));
        atlasList.add(new AtlasItems("Wzdręga", "Okres ochronny: Brak", "Wymiar ochronny: Brak", R.drawable.ic_launcher_background));

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.main_fragment, container, false);


        // Inicjalizacja widoków pogodowych
        weatherIcon = v.findViewById(R.id.weather_icon);
        weatherPlace = v.findViewById(R.id.weather_place);
        weatherTemp = v.findViewById(R.id.weather_temperature);
        weatherDesc = v.findViewById(R.id.weather_description);
        moreButton = v.findViewById(R.id.more);

        moreButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getContext(),FullAtlas.class));
            }
        });


        // Inicjalizacja RecyclerView
        recyclerView = v.findViewById(R.id.atlas_recycler_view);
        atlasAdapter = new AtlasAdapter(atlasList, getContext());
        recyclerView.setAdapter(atlasAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity(), RecyclerView.HORIZONTAL, false));

        // Pobranie ViewModel
        weatherViewModel = new ViewModelProvider(requireActivity()).get(WeatherViewModel.class);

        // Obserwacja zmian pogody
        weatherViewModel.getWeatherLiveData().observe(getViewLifecycleOwner(), weatherData -> {
            if (weatherData != null) updateWeatherUI(weatherData);
        });

        return v;
    }
    //Uaktualnienie danych pogodowych
    private void updateWeatherUI(WeatherData weatherData) {
        weatherPlace.setText(weatherData.getCity());
        weatherTemp.setText(weatherData.getTemperature());
        weatherDesc.setText(weatherData.getDescription());

        Glide.with(this)
                .load(weatherData.getIconUrl())
                .format(DecodeFormat.PREFER_ARGB_8888)
                .placeholder(R.drawable.backup)
                .error(R.drawable.backup)
                .into(weatherIcon);
    }
    @Override
    // Przejście do szczegółów ryby
    public void onClick(View v, int position) {
        startActivity(new Intent(getContext(), FishActivity.class));
    }
}

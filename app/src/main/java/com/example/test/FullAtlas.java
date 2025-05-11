package com.example.test;

import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class FullAtlas extends AppCompatActivity {

    SearchView searchView;
    RecyclerView recyclerView;
    AtlasAdapter atlasAdapter;
    ArrayList<AtlasItems> atlasList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_full_atlas);
        recyclerView = findViewById(R.id.atlas_recycler_view);
        searchView = findViewById(R.id.atlas_search);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(linearLayoutManager);
        atlasList = new ArrayList();
        AtlasItems aloza = new AtlasItems("Aloza", "Okres ochronny: Cały rok", "wymiar ochronny: Brak", R.drawable.ic_launcher_background);
        AtlasItems amur_bialy = new AtlasItems("Amur Biały", "Okres ochronny: Brak", "wymiar ochronny: Brak", R.drawable.amurbialy);
        AtlasItems babka_czarna = new AtlasItems("Babka Czarna", "Okres ochronny: Brak", "wymiar ochronny: Brak", R.drawable.ic_launcher_background);
        AtlasItems babka_piaskowa = new AtlasItems("Babka Piaskowa", "Okres ochronny: Brak", "wymiar ochronny: Brak", R.drawable.ic_launcher_background);
        AtlasItems belona = new AtlasItems("Belona", "Okres ochronny: Brak", "wymiar ochronny: Brak", R.drawable.ic_launcher_background);
        this.atlasList.add(aloza);
        this.atlasList.add(amur_bialy);
        this.atlasList.add(babka_czarna);
        this.atlasList.add(babka_piaskowa);
        this.atlasList.add(belona);
        atlasAdapter = new AtlasAdapter(atlasList,this);
        recyclerView.setAdapter(atlasAdapter);
        searchView.clearFocus();
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }
            @Override
            public boolean onQueryTextChange(String newText) {
                filteredList(newText);
                return true;
            }
        });
       }
    private void filteredList(String text) {
        ArrayList<AtlasItems> filteredList = new ArrayList<>();
        for (AtlasItems item: atlasList){
            if(item.getAtlasTitle().toLowerCase().contains(text.toLowerCase())){
                filteredList.add(item);
            }
        }
        atlasAdapter.setFilteredList(filteredList);
    }
}
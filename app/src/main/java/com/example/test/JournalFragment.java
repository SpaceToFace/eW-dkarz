package com.example.test;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class JournalFragment extends Fragment {

    // RecyclerView, adapter i lista przedmiotów (Item)
    private RecyclerView recyclerView;
    private ItemAdapter adapter;
    private List<Item> itemList;

    // SharedPreferences do zapisywania danych lokalnie
    private SharedPreferences sharedPreferences;
    private static final String PREFS_NAME = "item_prefs"; // Nazwa preferencji
    private String ITEMS_KEY; // Klucz do przechowywania danych użytkownika

    // Kody żądań do rozróżnienia wyników z aktywności (dodanie i edycja elementów)
    private final int ADD_ITEM_REQUEST = 100;
    private final int EDIT_ITEM_REQUEST = 200;

    public JournalFragment() {}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.journal_fragment, container, false);


        recyclerView = view.findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        sharedPreferences = requireContext().getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);

        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        ITEMS_KEY = "items_key_" + userId;


        itemList = loadItems();

        adapter = new ItemAdapter(itemList, item -> {

            Intent intent = new Intent(getContext(), EditItemActivity.class);
            intent.putExtra("item", item);
            startActivityForResult(intent, EDIT_ITEM_REQUEST);
        });
        recyclerView.setAdapter(adapter);


        FloatingActionButton addDataButton = view.findViewById(R.id.add_data_button);
        addDataButton.setOnClickListener(v -> {
            Intent intent = new Intent(getContext(), AddItemActivity.class);
            startActivityForResult(intent, ADD_ITEM_REQUEST);
        });

        return view;
    }

    // Metoda do wczytywania listy elementów z SharedPreferences
    private List<Item> loadItems() {
        String json = sharedPreferences.getString(ITEMS_KEY, null);
        if (json != null) {
            Type type = new TypeToken<List<Item>>() {}.getType();
            List<Item> items = new Gson().fromJson(json, type);
            if (items != null) {
                return items;
            }
        }
        return new ArrayList<>();
    }


    private void saveItems(List<Item> items) {
        String json = new Gson().toJson(items);
        sharedPreferences.edit().putString(ITEMS_KEY, json).apply();
    }

    // Obsługa wyników z aktywności (dodawanie, edycja, usuwanie)
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (data != null) {
            if (requestCode == ADD_ITEM_REQUEST && resultCode == requireActivity().RESULT_OK) {
                // Obsługa dodania nowego elementu
                Item newItem = (Item) data.getSerializableExtra("item");
                if (newItem != null) {
                    itemList.add(newItem);
                    saveItems(itemList);
                    adapter.notifyDataSetChanged();
                }
            } else if (requestCode == EDIT_ITEM_REQUEST && resultCode == requireActivity().RESULT_OK) {
                // Obsługa edycji istniejącego elementu
                Item updatedItem = (Item) data.getSerializableExtra("item");
                if (updatedItem != null) {
                    for (int i = 0; i < itemList.size(); i++) {
                        if (itemList.get(i).getId().equals(updatedItem.getId())) {
                            itemList.set(i, updatedItem);
                            break;
                        }
                    }
                    saveItems(itemList);
                    adapter.notifyDataSetChanged();
                }
            } else if (resultCode == Activity.RESULT_FIRST_USER) {
                // Obsługa usunięcia elementu
                String deletedItemId = data.getStringExtra("deletedItemId");
                if (deletedItemId != null) {
                    itemList.removeIf(item -> item.getId().equals(deletedItemId));
                    saveItems(itemList);
                    adapter.notifyDataSetChanged();
                }
            }
        }
    }

    // Zapisanie elementów przy zamykaniu fragmentu
    @Override
    public void onDestroyView() {
        super.onDestroyView();
        saveItems(itemList); //Zapis elementów przy zamknięciu widoku
    }
}

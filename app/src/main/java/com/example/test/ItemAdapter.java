package com.example.test;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.MapView;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

public class ItemAdapter extends RecyclerView.Adapter<ItemAdapter.ItemViewHolder> {

    private final List<Item> itemList;
    private final OnItemClickListener listener;

    public interface OnItemClickListener {
        void onItemClick(Item item);
    }

    public ItemAdapter(List<Item> itemList, OnItemClickListener listener) {
        this.itemList = itemList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_layout, parent, false);
        return new ItemViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ItemViewHolder holder, int position) {
        Item item = itemList.get(position);

        holder.titleTextView.setText(item.getSpecies());
        holder.weightTextView.setText(String.format(Locale.getDefault(), "%.2f kg", item.getWeight()));
        holder.lengthTextView.setText(String.format(Locale.getDefault(), "%.2f m", item.getLength()));
        holder.dateTextView.setText(new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(item.getDate()));
        holder.noteTextView.setText(item.getNote());
        holder.itemView.setOnClickListener(v -> listener.onItemClick(item));

        holder.mapView.onCreate(null);
        holder.mapView.getMapAsync(googleMap -> {
            LatLng location = new LatLng(item.getLatitude(), item.getLongitude());
            googleMap.clear();
            googleMap.addMarker(new MarkerOptions().position(location).title(item.getSpecies()));
            googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(location, 10));
        });
    }

    @Override
    public int getItemCount() {
        Log.d("ItemAdapter", "Liczba elementów: " + itemList.size());
        return itemList.size();
    }

    public static class ItemViewHolder extends RecyclerView.ViewHolder {
        TextView titleTextView, placeTextView;
        TextView weightTextView, lengthTextView, dateTextView, noteTextView;
        MapView mapView;

        public ItemViewHolder(@NonNull View itemView) {
            super(itemView);
            weightTextView = itemView.findViewById(R.id.weightTextView);
            lengthTextView = itemView.findViewById(R.id.lengthTextView);
            dateTextView = itemView.findViewById(R.id.dateTextView);
            noteTextView = itemView.findViewById(R.id.noteTextView);
            titleTextView = itemView.findViewById(R.id.titleTextView);
            placeTextView = itemView.findViewById(R.id.placeTextView);
            mapView = itemView.findViewById(R.id.mapView);

            if (mapView != null) {
                mapView.onCreate(new Bundle());
                mapView.onResume(); // WAŻNE: Bez tego mapa się nie wyświetli!
            }
        }
    }

}

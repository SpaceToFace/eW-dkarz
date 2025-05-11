package com.example.test;

import java.io.Serializable;
import java.util.Objects;
import java.util.UUID;

public class Item implements Serializable {
    private String id;
    private String species;
    private float weight;
    private float length;
    private double latitude;
    private double longitude;
    private long date;
    private String note;
    private String imagePath, waterType;  // Lokalna ścieżka do obrazu

    // Konstruktor bezargumentowy
    public Item() {}

    // Konstruktor z wszystkimi polami, w tym id
    public Item(String species, float weight, float length, double latitude, double longitude, long date, String note) {
        this.id = UUID.randomUUID().toString();  // Unikalne ID
        this.species = species;
        this.weight = weight;
        this.length = length;
        this.latitude = latitude;
        this.longitude = longitude;
        this.date = date;
        this.note = note;
    }


    // Getter i Setter dla id
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    // Gettery i settery dla innych pól
    public String getSpecies() {
        return species;
    }

    public void setSpecies(String species) {
        this.species = species;
    }

    public float getWeight() {
        return weight;
    }

    public void setWeight(float weight) {
        this.weight = weight;
    }

    public float getLength() {
        return length;
    }

    public void setLength(float length) {
        this.length = length;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public long getDate() {
        return date;
    }

    public void setDate(long date) {
        this.date = date;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }
    public String getImagePath() {
        return imagePath;
    }

    public void setImagePath(String imagePath) {
        this.imagePath = imagePath;
    }

    public void setWaterType(String waterType) {
        this.waterType = waterType;
    }

    public String getWaterType() {
        return waterType;
    }



    // Nowa metoda do zwrócenia miejsca w formacie tekstowym
    public String getPlace() {
        return "Lat: " + latitude + ", Lng: " + longitude;
    }

    // Metoda do porównania dwóch elementów (dla usuwania, edytowania)
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Item item = (Item) obj;
        return Float.compare(item.weight, weight) == 0 &&
                Float.compare(item.length, length) == 0 &&
                Double.compare(item.latitude, latitude) == 0 &&
                Double.compare(item.longitude, longitude) == 0 &&
                date == item.date &&
                species.equals(item.species) &&
                note.equals(item.note);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, species, weight, length, latitude, longitude, date, note);
    }
}
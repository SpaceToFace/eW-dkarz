package com.example.test;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.bumptech.glide.Glide;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.UUID;

public class EditItemActivity extends AppCompatActivity implements OnMapReadyCallback {

    private static final int PICK_IMAGE_REQUEST = 1;
    private static final int TAKE_PHOTO_REQUEST = 2;
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1001;

    private EditText speciesEditText, weightEditText, lengthEditText, noteEditText;
    private Button saveButton, photoButton, galleryButton, deleteButton;
    private ImageView imageView;

    private Item item;
    private String savedImagePath;
    private LatLng selectedLocation;

    private GoogleMap map;
    private Marker marker;
    private FusedLocationProviderClient fusedLocationClient;
    private Spinner waterTypeSpinner;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_item);

        // Inicjalizacja widoków
        speciesEditText = findViewById(R.id.speciesEditText);
        weightEditText = findViewById(R.id.weightEditText);
        lengthEditText = findViewById(R.id.lengthEditText);
        noteEditText = findViewById(R.id.noteEditText);
        photoButton = findViewById(R.id.photoButton);
        galleryButton = findViewById(R.id.galleryButton);
        saveButton = findViewById(R.id.saveButton1);
        deleteButton = findViewById(R.id.deleteButton);
        imageView = findViewById(R.id.imageView);
        waterTypeSpinner = findViewById(R.id.waterTypeSpinner); // Spinner


        item = (Item) getIntent().getSerializableExtra("item");

        if (item == null) {
            Toast.makeText(this, "Błąd: brak danych o elemencie!", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
                this,
                R.array.water_types, // Tablica z strings.xml
                android.R.layout.simple_spinner_item
        );
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        waterTypeSpinner.setAdapter(adapter);

        // 🔑 Ustaw Spinner na wartość z Item
        if (item.getWaterType() != null) {
            int spinnerPosition = adapter.getPosition(item.getWaterType());
            if (spinnerPosition >= 0) {
                waterTypeSpinner.setSelection(spinnerPosition);
            }
        }

        // Wypełnij pola danymi
        speciesEditText.setText(item.getSpecies());
        weightEditText.setText(String.valueOf(item.getWeight()));
        lengthEditText.setText(String.valueOf(item.getLength()));
        noteEditText.setText(item.getNote());
        selectedLocation = new LatLng(item.getLatitude(), item.getLongitude());
        savedImagePath = item.getImagePath();

        if (savedImagePath != null) {
            Glide.with(this).load(new File(savedImagePath)).into(imageView);
        }

        // Inicjalizacja klienta lokalizacji
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        // Inicjalizacja mapy
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        if (mapFragment != null) {
            mapFragment.getMapAsync(this);
        }

        // Obsługa przycisków
        galleryButton.setOnClickListener(v -> openGallery());
        photoButton.setOnClickListener(v -> openCamera());
        saveButton.setOnClickListener(v -> saveItem());
        deleteButton.setOnClickListener(v -> confirmDeleteItem());
    }

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        map = googleMap;

        // Wyświetl aktualną lokalizację elementu
        if (selectedLocation != null) {
            marker = map.addMarker(new MarkerOptions().position(selectedLocation).title("Aktualna lokalizacja"));
            map.moveCamera(CameraUpdateFactory.newLatLngZoom(selectedLocation, 12));
        }

        // Kliknięcie na mapę - zmiana lokalizacji pinezki
        map.setOnMapClickListener(latLng -> {
            selectedLocation = latLng;
            if (marker != null) marker.remove();
            marker = map.addMarker(new MarkerOptions().position(latLng).title("Nowa lokalizacja"));
            map.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 12));
        });

        // Sprawdź i włącz przycisk lokalizacji na mapie
        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            map.setMyLocationEnabled(true);
        } else {
            ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_PERMISSION_REQUEST_CODE);
        }

        // Obsługa kliknięcia przycisku lokalizacji na mapie
        map.setOnMyLocationButtonClickListener(() -> {
            getCurrentLocation();  // Ustawia pinezkę na aktualnej lokalizacji
            return true; // Zapobiega domyślnemu przesunięciu mapy, obsługa jest ręczna
        });
    }

    private void getCurrentLocation() {
        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_PERMISSION_REQUEST_CODE);
        } else if (map != null && fusedLocationClient != null) {
            fusedLocationClient.getLastLocation().addOnSuccessListener(location -> {
                if (location != null) {
                    selectedLocation = new LatLng(location.getLatitude(), location.getLongitude());
                    if (marker != null) marker.remove();
                    marker = map.addMarker(new MarkerOptions().position(selectedLocation).title("Twoja lokalizacja"));
                    map.animateCamera(CameraUpdateFactory.newLatLngZoom(selectedLocation, 15));
                    Toast.makeText(this, "Pinezka ustawiona na aktualnej lokalizacji!", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(this, "Nie udało się uzyskać lokalizacji.", Toast.LENGTH_SHORT).show();
                }
            }).addOnFailureListener(e -> Toast.makeText(this, "Błąd pobierania lokalizacji: " + e.getMessage(), Toast.LENGTH_SHORT).show());
        }
    }

    private void openGallery() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, PICK_IMAGE_REQUEST);
    }

    private void openCamera() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(intent, TAKE_PHOTO_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == Activity.RESULT_OK && data != null) {
            if (requestCode == PICK_IMAGE_REQUEST) {
                savedImagePath = saveImageLocally(data.getData());
                imageView.setImageURI(Uri.fromFile(new File(savedImagePath)));
            } else if (requestCode == TAKE_PHOTO_REQUEST && data.getExtras() != null) {
                Bitmap photo = (Bitmap) data.getExtras().get("data");
                savedImagePath = saveBitmapLocally(photo);
                imageView.setImageBitmap(photo);
            }
        }
    }

    private String saveImageLocally(Uri imageUri) {
        try {
            Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), imageUri);
            return saveBitmapLocally(bitmap);
        } catch (IOException e) {
            Toast.makeText(this, "Błąd zapisu obrazu", Toast.LENGTH_SHORT).show();
            return null;
        }
    }

    private String saveBitmapLocally(Bitmap bitmap) {
        String fileName = UUID.randomUUID().toString() + ".jpg";
        File imageFile = new File(getFilesDir(), fileName);

        try (FileOutputStream fos = new FileOutputStream(imageFile)) {
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos);
            return imageFile.getAbsolutePath();
        } catch (IOException e) {
            Toast.makeText(this, "Błąd zapisu obrazu", Toast.LENGTH_SHORT).show();
            return null;
        }
    }

    private void saveItem() {
        item.setSpecies(speciesEditText.getText().toString());
        item.setWeight(Float.parseFloat(weightEditText.getText().toString()));
        item.setLength(Float.parseFloat(lengthEditText.getText().toString()));
        item.setNote(noteEditText.getText().toString());
        item.setLatitude(selectedLocation.latitude);
        item.setLongitude(selectedLocation.longitude);
        item.setImagePath(savedImagePath);

        Toast.makeText(this, "Zaktualizowano element!", Toast.LENGTH_SHORT).show();

        Intent resultIntent = new Intent();
        resultIntent.putExtra("item", item);
        setResult(Activity.RESULT_OK, resultIntent);
        finish();
    }

    private void confirmDeleteItem() {
        new AlertDialog.Builder(this)
                .setTitle("Potwierdzenie usunięcia")
                .setMessage("Czy na pewno chcesz usunąć ten element?")
                .setPositiveButton("Usuń", (dialog, which) -> deleteItem())
                .setNegativeButton("Anuluj", null)
                .show();
    }

    private void deleteItem() {
        if (savedImagePath != null) {
            File imageFile = new File(savedImagePath);
            if (imageFile.exists() && imageFile.delete()) {
                Toast.makeText(this, "Zdjęcie usunięte.", Toast.LENGTH_SHORT).show();
            }
        }

        Intent resultIntent = new Intent();
        resultIntent.putExtra("deletedItemId", item.getId());
        setResult(Activity.RESULT_FIRST_USER, resultIntent);
        finish();
    }
}

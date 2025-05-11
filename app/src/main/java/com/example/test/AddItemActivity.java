package com.example.test;

// Importy potrzebnych klas i bibliotek
import static com.example.test.LoginActivity.LOCATION_PERMISSION_REQUEST_CODE;

import android.app.Activity;
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

public class AddItemActivity extends AppCompatActivity implements OnMapReadyCallback {

    // Stałe do identyfikacji intencji (wybór zdjęcia, zrobienie zdjęcia, uprawnienia lokalizacji)
    private static final int PICK_IMAGE_REQUEST = 1;
    private static final int TAKE_PHOTO_REQUEST = 2;
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1001;

    // Zmienne do obsługi lokalizacji i mapy
    private FusedLocationProviderClient fusedLocationClient;
    private Button currentLocationButton;

    // Widoki do wprowadzania danych
    private EditText speciesEditText, weightEditText, lengthEditText, noteEditText;
    private Button saveButton, photoButton, galleryButton;
    private ImageView imageView;
    private Spinner waterTypeSpinner;

    // Zmienne do przechowywania obrazu i lokalizacji
    private Uri selectedImageUri;
    private String savedImagePath;
    private LatLng selectedLocation;

    // Zmienne do mapy i markera
    private GoogleMap map;
    private Marker marker;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_item);

        // Inicjalizacja widoków z layoutu
        speciesEditText = findViewById(R.id.speciesEditText);
        weightEditText = findViewById(R.id.weightEditText);
        lengthEditText = findViewById(R.id.lengthEditText);
        noteEditText = findViewById(R.id.noteEditText);
        photoButton = findViewById(R.id.photoButton);
        galleryButton = findViewById(R.id.galleryButton);
        saveButton = findViewById(R.id.saveButton1);
        imageView = findViewById(R.id.imageView);
        waterTypeSpinner = findViewById(R.id.waterTypeSpinner);

        // Ustawienie adaptera dla spinnera z danymi z resources
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
                this,
                R.array.water_types,
                android.R.layout.simple_spinner_item
        );
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        waterTypeSpinner.setAdapter(adapter);

        // Inicjalizacja fragmentu mapy
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        if (mapFragment != null) {
            mapFragment.getMapAsync(this); // Asynchroniczne załadowanie mapy
        }

        // Inicjalizacja klienta lokalizacji Google
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        // Obsługa kliknięć przycisków
        galleryButton.setOnClickListener(v -> openGallery());
        photoButton.setOnClickListener(v -> openCamera());
        saveButton.setOnClickListener(v -> saveItem());
    }

    // Metoda wywoływana, gdy mapa jest gotowa do użycia
    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        map = googleMap;

        // Ustawienie domyślnej lokalizacji (Warszawa)
        LatLng defaultLocation = new LatLng(52.237049, 21.017532);
        map.moveCamera(CameraUpdateFactory.newLatLngZoom(defaultLocation, 10));

        // Sprawdzenie uprawnień lokalizacji
        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            map.setMyLocationEnabled(true);
        } else {
            ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_PERMISSION_REQUEST_CODE);
        }

        // Obsługa kliknięcia przycisku lokalizacji na mapie
        map.setOnMyLocationButtonClickListener(() -> {
            getCurrentLocation();
            return true;
        });

        // Obsługa kliknięcia na mapę (ręczne ustawienie lokalizacji)
        map.setOnMapClickListener(latLng -> {
            selectedLocation = latLng;
            if (marker != null) marker.remove();
            marker = map.addMarker(new MarkerOptions().position(latLng).title("Wybrana lokalizacja"));
            map.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 12));
        });
    }

    // Metoda do otwarcia galerii zdjęć
    private void openGallery() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, PICK_IMAGE_REQUEST);
    }

    // Metoda do otwarcia aparatu
    private void openCamera() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(intent, TAKE_PHOTO_REQUEST);
    }

    // Obsługa wyniku z galerii lub aparatu
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == Activity.RESULT_OK && data != null) {
            if (requestCode == PICK_IMAGE_REQUEST) {
                // Jeśli wybrano zdjęcie z galerii
                selectedImageUri = data.getData();
                imageView.setImageURI(selectedImageUri); // Wyświetl obraz
                savedImagePath = saveImageLocally(selectedImageUri); // Zapisz obraz lokalnie
            } else if (requestCode == TAKE_PHOTO_REQUEST && data.getExtras() != null) {
                // Jeśli zrobiono zdjęcie aparatem
                Bitmap photo = (Bitmap) data.getExtras().get("data");
                imageView.setImageBitmap(photo);
                savedImagePath = saveBitmapLocally(photo);
            }
        }
    }

    // Metoda zapisująca obraz z galerii do pamięci wewnętrznej
    private String saveImageLocally(Uri imageUri) {
        try {
            Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), imageUri);
            return saveBitmapLocally(bitmap);
        } catch (IOException e) {
            Toast.makeText(this, "Błąd zapisu obrazu", Toast.LENGTH_SHORT).show();
            return null;
        }
    }

    // Metoda zapisująca bitmapę do pliku lokalnie
    private String saveBitmapLocally(Bitmap bitmap) {
        String fileName = UUID.randomUUID().toString() + ".jpg";
        File directory = getFilesDir();
        File imageFile = new File(directory, fileName);

        try (FileOutputStream fos = new FileOutputStream(imageFile)) {
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos);
            return imageFile.getAbsolutePath();
        } catch (IOException e) {
            Toast.makeText(this, "Błąd zapisu obrazu", Toast.LENGTH_SHORT).show();
            return null;
        }
    }

    // Metoda zapisująca dane wprowadzone przez użytkownika
    private void saveItem() {
        String species = speciesEditText.getText().toString();
        String weightStr = weightEditText.getText().toString();
        String lengthStr = lengthEditText.getText().toString();
        String note = noteEditText.getText().toString();

        // Sprawdzenie, czy wymagane pola są wypełnione
        if (species.isEmpty() || weightStr.isEmpty() || lengthStr.isEmpty() || selectedLocation == null) {
            Toast.makeText(this, "Proszę wypełnić wszystkie pola i zaznaczyć lokalizację.", Toast.LENGTH_SHORT).show();
            return;
        }

        float weight = Float.parseFloat(weightStr);
        float length = Float.parseFloat(lengthStr);

        // Tworzenie nowego obiektu Item z danymi
        Item item = new Item(species, weight, length, selectedLocation.latitude, selectedLocation.longitude, System.currentTimeMillis(), note);
        item.setImagePath(savedImagePath);


        // Przekazanie danych do poprzedniej aktywności
        Intent resultIntent = new Intent();
        resultIntent.putExtra("item", item);
        setResult(Activity.RESULT_OK, resultIntent);
        finish();
    }

    // Pobranie aktualnej lokalizacji urządzenia
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
                } else {
                    Toast.makeText(this, "Nie udało się uzyskać lokalizacji.", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }
}

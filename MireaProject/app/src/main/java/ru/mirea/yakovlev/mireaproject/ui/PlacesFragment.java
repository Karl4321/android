package ru.mirea.yakovlev.mireaproject.ui;

import android.Manifest;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.preference.PreferenceManager;

import com.google.gson.Gson;
import org.osmdroid.api.IMapController;
import org.osmdroid.config.Configuration;
import org.osmdroid.events.MapEventsReceiver;
import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.MapEventsOverlay;
import org.osmdroid.views.overlay.Marker;
import org.osmdroid.views.overlay.mylocation.GpsMyLocationProvider;
import org.osmdroid.views.overlay.mylocation.MyLocationNewOverlay;
import java.util.HashSet;
import java.util.Set;
import ru.mirea.yakovlev.mireaproject.R;
import ru.mirea.yakovlev.mireaproject.databinding.DialogAddPlacesBinding;
import ru.mirea.yakovlev.mireaproject.databinding.FragmentPlacesBinding;

public class PlacesFragment extends Fragment {

    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1001;
    private static final String PREFS_NAME = "SavedPlaces";
    private static final String PLACES_KEY = "saved_places";

    private FragmentPlacesBinding binding;
    private SharedPreferences sharedPreferences;
    private MyLocationNewOverlay locationOverlay;
    private boolean isLocationEnabled = false;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Configuration.getInstance().load(requireContext(), PreferenceManager.getDefaultSharedPreferences(requireContext()));
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentPlacesBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        sharedPreferences = requireContext().getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        initializeMap();
        setupMapListeners();
        checkLocationPermissions();
        loadSavedPlaces();
    }

    private void initializeMap() {
        binding.mapView.setTileSource(TileSourceFactory.MAPNIK);
        binding.mapView.setMultiTouchControls(true);

        IMapController mapController = binding.mapView.getController();
        mapController.setZoom(12.0);
        mapController.setCenter(new GeoPoint(55.751574, 37.573856));
    }

    private void setupMapListeners() {
        binding.mapView.getOverlays().add(new MapEventsOverlay(new MapEventsReceiver() {
            @Override
            public boolean singleTapConfirmedHelper(GeoPoint p) {
                return false;
            }

            @Override
            public boolean longPressHelper(GeoPoint p) {
                showAddPlaceDialog(p);
                return true;
            }
        }));

        // Добавление предопределенных маркеров
        addMarker(new GeoPoint(55.554796, 37.708981), "Ермолино", "Лучший магазин пельменей!");
        addMarker(new GeoPoint(55.738687, 37.608279), "Пётр I", "Памятник 300-летию флота");
    }

    private void showAddPlaceDialog(GeoPoint point) {
        DialogAddPlacesBinding dialogBinding = DialogAddPlacesBinding.inflate(getLayoutInflater());

        new AlertDialog.Builder(requireContext())
                .setTitle("Добавить место")
                .setView(dialogBinding.getRoot())
                .setPositiveButton("Сохранить", (dialog, which) -> {
                    String title = dialogBinding.etTitle.getText().toString().trim();
                    String description = dialogBinding.etDescription.getText().toString().trim();

                    if (!title.isEmpty()) {
                        savePlace(point, title, description);
                        addMarker(point, title, description);
                    }
                })
                .setNegativeButton("Отмена", null)
                .show();
    }

    private void savePlace(GeoPoint point, String title, String description) {
        Set<String> savedPlaces = sharedPreferences.getStringSet(PLACES_KEY, new HashSet<>());
        Gson gson = new Gson();

        Place newPlace = new Place(
                point.getLatitude(),
                point.getLongitude(),
                title,
                description
        );

        Set<String> updatedPlaces = new HashSet<>(savedPlaces);
        updatedPlaces.add(gson.toJson(newPlace));

        sharedPreferences.edit()
                .putStringSet(PLACES_KEY, updatedPlaces)
                .apply();
    }

    private void loadSavedPlaces() {
        Set<String> savedPlaces = sharedPreferences.getStringSet(PLACES_KEY, new HashSet<>());
        Gson gson = new Gson();

        for (String json : savedPlaces) {
            Place place = gson.fromJson(json, Place.class);
            addMarker(new GeoPoint(place.lat, place.lon), place.title, place.description);
        }
    }

    private void checkLocationPermissions() {
        String[] permissions = {
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION
        };

        if (hasPermissions(permissions)) {
            enableMyLocation();
        } else {
            requestPermissions(permissions, LOCATION_PERMISSION_REQUEST_CODE);
        }
    }

    private boolean hasPermissions(String[] permissions) {
        for (String permission : permissions) {
            if (ContextCompat.checkSelfPermission(requireContext(), permission) != PackageManager.PERMISSION_GRANTED) {
                return false;
            }
        }
        return true;
    }

    private void enableMyLocation() {
        locationOverlay = new MyLocationNewOverlay(
                new GpsMyLocationProvider(requireContext()),
                binding.mapView
        );

        locationOverlay.enableMyLocation();
        binding.mapView.getOverlays().add(locationOverlay);
        isLocationEnabled = true;
    }

    private void addMarker(GeoPoint point, String title, String description) {
        Marker marker = new Marker(binding.mapView);
        marker.setPosition(point);
        marker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM);
        marker.setTitle(title);
        marker.setSnippet(description);

        marker.setOnMarkerClickListener((m, mv) -> {
            showMarkerInfo(title, description);
            return true;
        });

        binding.mapView.getOverlays().add(marker);
    }

    private void showMarkerInfo(String title, String description) {
        Toast.makeText(requireContext(), title + ": " + description, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                enableMyLocation();
            }
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        binding.mapView.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        binding.mapView.onPause();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    private static class Place {
        final double lat;
        final double lon;
        final String title;
        final String description;

        Place(double lat, double lon, String title, String description) {
            this.lat = lat;
            this.lon = lon;
            this.title = title;
            this.description = description;
        }
    }
}
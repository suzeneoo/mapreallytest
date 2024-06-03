package com.example.mapreallytest;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.InputStream;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MainActivity extends AppCompatActivity implements OnMapReadyCallback {

    private static final String TAG = "MainActivity";
    private static final int PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 1;
    private GoogleMap mMap;
    private FusedLocationProviderClient mFusedLocationProviderClient;
    private LocationCallback locationCallback;
    private LocationRequest locationRequest;
    private boolean firstLocationUpdate = true;
    private BottomSheetBehavior<View> bottomSheetBehavior;
    private TextView hospitalName, hospitalAddress, hospitalPhone, hospitalHours;
    private Map<Marker, Eyes> markerEyesMap = new HashMap<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button zoomInButton = findViewById(R.id.zoom_in_button);
        Button zoomOutButton = findViewById(R.id.zoom_out_button);
        Button btnEyeClinic = findViewById(R.id.btn_eye_clinic);
        Button btnDermatology = findViewById(R.id.btn_dermatology);
        Button btnDentist = findViewById(R.id.btn_dentist);

        zoomInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mMap != null) {
                    mMap.animateCamera(CameraUpdateFactory.zoomIn());
                }
            }
        });

        zoomOutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mMap != null) {
                    mMap.animateCamera(CameraUpdateFactory.zoomOut());
                }
            }
        });

        btnEyeClinic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadMarkersFromJson(R.raw.eyes);
            }
        });

        btnDermatology.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadMarkersFromJson(R.raw.skin);
            }
        });

        btnDentist.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadMarkersFromJson(R.raw.teeth);
            }
        });

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        if (mapFragment != null) {
            mapFragment.getMapAsync(this);
        }

        View bottomSheet = findViewById(R.id.bottom_sheet);
        bottomSheetBehavior = BottomSheetBehavior.from(bottomSheet);
        bottomSheetBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);

        hospitalName = findViewById(R.id.hospital_name);
        hospitalAddress = findViewById(R.id.hospital_address);
        hospitalPhone = findViewById(R.id.hospital_phone);
        hospitalHours = findViewById(R.id.hospital_hours);

        mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
        locationRequest = LocationRequest.create();
        locationRequest.setInterval(1000);
        locationRequest.setFastestInterval(1000);
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

        locationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                super.onLocationResult(locationResult);
                if (locationResult == null) {
                    return;
                }
                for (Location location : locationResult.getLocations()) {
                    updateLocationOnMap(location);
                }
            }
        };
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        getLocationPermission();
        updateLocationUI();

        mMap.getUiSettings().setZoomGesturesEnabled(true);
        mMap.getUiSettings().setScrollGesturesEnabled(true);
        mMap.getUiSettings().setZoomControlsEnabled(true);

        mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
                Eyes eyeClinic = markerEyesMap.get(marker);
                if (eyeClinic != null) {
                    hospitalName.setText(eyeClinic.get이름());
                    hospitalAddress.setText(eyeClinic.get도로명주소());
                    hospitalPhone.setText(eyeClinic.get일반전화());
                    hospitalHours.setText(eyeClinic.get영업시간());

                    bottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
                }
                return false;
            }
        });
    }

    private void getLocationPermission() {
        if (ContextCompat.checkSelfPermission(this.getApplicationContext(),
                Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            updateLocationUI();
        } else {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);
        }
    }

    private void updateLocationUI() {
        if (mMap == null) {
            return;
        }
        try {
            if (ContextCompat.checkSelfPermission(this.getApplicationContext(),
                    Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                mMap.setMyLocationEnabled(true);
                mMap.getUiSettings().setMyLocationButtonEnabled(true);
                getDeviceLocation();
            } else {
                mMap.setMyLocationEnabled(false);
                mMap.getUiSettings().setMyLocationButtonEnabled(false);
                getLocationPermission();
            }
        } catch (SecurityException e) {
            Log.e("Exception: %s", e.getMessage());
        }
    }

    private void getDeviceLocation() {
        try {
            if (ContextCompat.checkSelfPermission(this.getApplicationContext(),
                    Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                mFusedLocationProviderClient.requestLocationUpdates(locationRequest, locationCallback, null);
            }
        } catch (SecurityException e) {
            Log.e("Exception: %s", e.getMessage());
        }
    }

    private void updateLocationOnMap(Location location) {
        if (location != null) {
            LatLng currentLatLng = new LatLng(location.getLatitude(), location.getLongitude());
            if (firstLocationUpdate) {
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(currentLatLng, 15));
                firstLocationUpdate = false;
            }
            mMap.addMarker(new MarkerOptions()
                    .position(currentLatLng)
                    .title("현재 위치")
                    .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED)));
        } else {
            Log.d(TAG, "Current location is null. Using defaults.");
            mMap.moveCamera(CameraUpdateFactory
                    .newLatLngZoom(new LatLng(0, 0), 15));
            mMap.getUiSettings().setMyLocationButtonEnabled(false);
        }
    }

    private void loadMarkersFromJson(int jsonResourceId) {
        mMap.clear(); // 기존 마커 제거
        markerEyesMap.clear();

        try {
            InputStream inputStream = getResources().openRawResource(jsonResourceId);
            String json = new java.util.Scanner(inputStream).useDelimiter("\\A").next();

            Gson gson = new Gson();
            Type listType = new TypeToken<List<Eyes>>() {}.getType();
            List<Eyes> clinics = gson.fromJson(json, listType);

            for (Eyes clinic : clinics) {
                LatLng location = new LatLng(clinic.get위도(), clinic.get경도());
                Marker marker = mMap.addMarker(new MarkerOptions()
                        .position(location)
                        .title(clinic.get이름())
                        .snippet(clinic.get도로명주소())
                        .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED)));
                markerEyesMap.put(marker, clinic); // Marker와 병원 정보를 매핑
                Log.d(TAG, "Marker added: " + clinic.get이름() + " at " + location.toString());
            }

            if (!clinics.isEmpty()) {
                LatLng firstLocation = new LatLng(clinics.get(0).get위도(), clinics.get(0).get경도());
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(firstLocation, 12));
            }
        } catch (Exception e) {
            Log.e(TAG, "Error reading JSON file", e);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                updateLocationUI();
                getDeviceLocation();
            } else {
                Toast.makeText(this, "Permission denied", Toast.LENGTH_LONG).show();
            }
        }
    }

    public void mCurrentLocation(View view) {
        firstLocationUpdate = true;
        getDeviceLocation();
    }
}


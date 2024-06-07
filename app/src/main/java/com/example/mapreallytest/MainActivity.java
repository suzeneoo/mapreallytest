package com.example.mapreallytest;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.example.mapreallytest.Eyes;
import com.example.mapreallytest.HospitalAdapter;
import com.example.mapreallytest.R;
import com.example.mapreallytest.common.HospitalInfoActivity;
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
import java.util.ArrayList;
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
    private List<Eyes> hospitalList = new ArrayList<>();
    private HospitalAdapter listAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button zoomInButton = findViewById(R.id.zoom_in_button);
        Button zoomOutButton = findViewById(R.id.zoom_out_button);
        Button btnEyeClinic = findViewById(R.id.btn_eye_clinic);
        Button btnDermatology = findViewById(R.id.btn_dermatology);
        Button btnDentist = findViewById(R.id.btn_dentist);

        View bottomSheet = findViewById(R.id.bottom_sheet);
        bottomSheetBehavior = BottomSheetBehavior.from(bottomSheet);
        bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);

        bottomSheetBehavior.addBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {
            @Override
            public void onStateChanged(@NonNull View bottomSheet, int newState) {
                if (newState == BottomSheetBehavior.STATE_HIDDEN) {
                    bottomSheetBehavior.setPeekHeight(200);
                    bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
                }
            }

            @Override
            public void onSlide(@NonNull View bottomSheet, float slideOffset) {
                // 슬라이드될 때 처리할 내용
            }
        });

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
                toggleBottomSheet();
                loadMarkersFromJson(R.raw.eyes, BitmapDescriptorFactory.HUE_RED);
                loadHospitalNamesFromJson(R.raw.eyes);
            }
        });

        btnDermatology.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                toggleBottomSheet();
                loadMarkersFromJson(R.raw.skin, BitmapDescriptorFactory.HUE_GREEN);
                loadHospitalNamesFromJson(R.raw.skin);
            }
        });

        btnDentist.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                toggleBottomSheet();
                loadMarkersFromJson(R.raw.teeth, BitmapDescriptorFactory.HUE_ORANGE);
                loadHospitalNamesFromJson(R.raw.teeth);
            }
        });

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        if (mapFragment != null) {
            mapFragment.getMapAsync(this);
        }

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

        setupDragHandle();

        setupListView();
    }

    private void setupDragHandle() {
        View dragHandle = findViewById(R.id.drag_handle);
        dragHandle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (bottomSheetBehavior.getState() == BottomSheetBehavior.STATE_COLLAPSED) {
                    bottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
                } else {
                    bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
                }
            }
        });
    }

    private void toggleBottomSheet() {
        if (bottomSheetBehavior.getState() != BottomSheetBehavior.STATE_EXPANDED) {
            bottomSheetBehavior.setPeekHeight(200);
            bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
        } else {
            bottomSheetBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);
        }
    }

    private void setupListView() {
        ListView listView = findViewById(R.id.listView_list);
        listAdapter = new HospitalAdapter(this, new ArrayList<>());  // HospitalAdapter 사용
        listView.setAdapter(listAdapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Eyes selectedHospital = (Eyes) listAdapter.getItem(position);
                Intent intent = new Intent(MainActivity.this, HospitalInfoActivity.class);
                intent.putExtra("hospital_name", selectedHospital.get이름());
                intent.putExtra("hospital_address", selectedHospital.get도로명주소());
                intent.putExtra("hospital_phone", selectedHospital.get일반전화());
                intent.putExtra("hospital_category", selectedHospital.get카테고리());
                intent.putExtra("hospital_image_url", selectedHospital.get썸네일이미지URL());
                startActivity(intent);
            }
        });
    }

    private void loadHospitalNamesFromJson(int jsonResourceId) {
        try {
            InputStream inputStream = getResources().openRawResource(jsonResourceId);
            String json = new java.util.Scanner(inputStream).useDelimiter("\\A").next();

            Gson gson = new Gson();
            Type listType = new TypeToken<List<Eyes>>() {}.getType();
            List<Eyes> clinics = gson.fromJson(json, listType);

            hospitalList.clear(); // 병원 리스트 초기화
            hospitalList.addAll(clinics); // 병원 리스트 업데이트

            listAdapter.clear(); // 어댑터 데이터 초기화
            listAdapter.addAll(hospitalList); // 어댑터에 데이터 추가
            listAdapter.notifyDataSetChanged(); // 어댑터 갱신
        } catch (Exception e) {
            Log.e(TAG, "Error reading JSON file", e);
        }
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
                    hospitalName = findViewById(R.id.hospital_name);
                    hospitalAddress = findViewById(R.id.hospital_address);
                    hospitalPhone = findViewById(R.id.hospital_phone);
                    hospitalHours = findViewById(R.id.hospital_hours);

                    hospitalName.setText(eyeClinic.get이름());
                    hospitalName.setText(eyeClinic.get이름());
                    hospitalAddress.setText(eyeClinic.get도로명주소());
                    hospitalPhone.setText(eyeClinic.get일반전화());
                    hospitalHours.setText(eyeClinic.get카테고리());

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

    private void loadMarkersFromJson(int jsonResourceId, float color) {
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
                        .icon(BitmapDescriptorFactory.defaultMarker(color)));
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

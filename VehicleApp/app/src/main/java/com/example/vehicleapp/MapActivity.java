package com.example.vehicleapp;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnSuccessListener;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

import Services.Auto.NearbyServicesActivity;
import Services.Hotels.NearbyHotelsActivity;
import Services.Restaurants.NearbyRestaurantsActivity;

public class MapActivity extends Activity implements OnMapReadyCallback {

    private static final String TAG = "MapActivity";
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1;

    private GoogleMap mMap;
    private MapView mMapView;
    private List<GeopointItem> restaurantList = new ArrayList<>();
    private List<GeopointItem> hotelList = new ArrayList<>();
    private List<GeopointItem> carServiceList = new ArrayList<>();

    ImageView hotelicon, restauranticon, carserviceicon;

    private HotelDatabaseHelper hotelDatabaseHelper;
    private RestaurantDatabaseHelper restaurantDatabaseHelper;
    private CarServiceDatabaseHelper carServiceDatabaseHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);

        // Initializing the MapView
        mMapView = findViewById(R.id.mapView);
        mMapView.onCreate(savedInstanceState);
        mMapView.getMapAsync(this);

        // Initialize database helpers
        hotelDatabaseHelper = new HotelDatabaseHelper(this);
        restaurantDatabaseHelper = new RestaurantDatabaseHelper(this);
        carServiceDatabaseHelper = new CarServiceDatabaseHelper(this);

        hotelicon = findViewById(R.id.hotelicon);
        restauranticon = findViewById(R.id.restauranticon);
        carserviceicon = findViewById(R.id.caricon);

        hotelicon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MapActivity.this, NearbyHotelsActivity.class);
                startActivity(intent);
            }
        });
        restauranticon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MapActivity.this, NearbyRestaurantsActivity.class);
                startActivity(intent);
            }
        });
        carserviceicon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MapActivity.this, NearbyServicesActivity.class);
                startActivity(intent);
            }
        });

    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        // Request location permission
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    LOCATION_PERMISSION_REQUEST_CODE);
        } else {
            showCurrentLocation();
        }
    }

    private void showCurrentLocation() {
        Log.d(TAG, "Fetching current location...");
        FusedLocationProviderClient locationProviderClient = LocationServices.getFusedLocationProviderClient(this);
        locationProviderClient.getLastLocation()
                .addOnSuccessListener(this, new OnSuccessListener<Location>() {
                    @Override
                    public void onSuccess(Location location) {
                        if (location != null) {
                            LatLng userLocation = new LatLng(location.getLatitude(), location.getLongitude());
                            mMap.addMarker(new MarkerOptions().position(userLocation).title("My Location"));
                            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(userLocation, 15));

                            Log.d(TAG, "Current location fetched successfully. Searching nearby places...");

                            // Call POISearchTask to search nearby POIs using current location
                            if (ContextCompat.checkSelfPermission(MapActivity.this,
                                    Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                                new POISearchTask().execute(location.getLatitude(), location.getLongitude());
                            } else {
                                Log.w(TAG, "Location permission not granted, skipping POI search.");
                            }
                        } else {
                            Toast.makeText(MapActivity.this, "Failed to get location", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    // Handle user's answer on the permission request
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                showCurrentLocation();
            } else {
                // Permission denied, show an error message
                Toast.makeText(this, "Location permission denied", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private class POISearchTask extends AsyncTask<Double, Void, String> {
        @Override
        protected String doInBackground(Double... geoCoordinates) {
            try {
                URL url = new URL("https://overpass-api.de/api/interpreter");
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("POST");
                connection.setDoOutput(true);
                String requestData = "data=[out:json];(node[\"amenity\"=\"hotel\"](" +
                        (geoCoordinates[0] - 0.15) + "," + (geoCoordinates[1] - 0.15) + "," +
                        (geoCoordinates[0] + 0.15) + "," + (geoCoordinates[1] + 0.15) + ");" +
                        "node[\"amenity\"=\"restaurant\"](" +
                        (geoCoordinates[0] - 0.009) + "," + (geoCoordinates[1] - 0.009) + "," +
                        (geoCoordinates[0] + 0.009) + "," + (geoCoordinates[1] + 0.009) + ");" +
                        "node[\"amenity\"=\"car_rental\"](" +
                        (geoCoordinates[0] - 0.099)
                        + "," + (geoCoordinates[1] - 0.099) + "," +
                        (geoCoordinates[0] + 0.099) + "," + (geoCoordinates[1] + 0.099) + "););" +
                        "out;";

                Log.d(TAG, "Starting database insertions...");

                byte[] out = requestData.getBytes(StandardCharsets.UTF_8);
                connection.getOutputStream().write(out);

                BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream(), StandardCharsets.UTF_8));
                StringBuilder response = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    response.append(line.trim());
                }

                return response.toString();
            } catch (Exception e) {                e.printStackTrace();
                Log.e(TAG, "Error in POISearchTask doInBackground: " + e.getMessage());

                return null;
            }
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            Log.d(TAG, "POISearchTask onPostExecute called.");

            if (s == null) return;
            try {
                JSONObject jsonResponse = new JSONObject(s);
                JSONArray elements = jsonResponse.getJSONArray("elements");
                for (int i = 0; i < elements.length(); i++) {
                    JSONObject element = elements.getJSONObject(i);
                    double lat = element.getDouble("lat");
                    double lon = element.getDouble("lon");
                    String name = element.has("tags") ? element.getJSONObject("tags").optString("name", "Unknown") : "Unknown";
                    String amenity = element.getJSONObject("tags").optString("amenity", "");

                    GeopointItem geopoint = new GeopointItem(name, lat, lon);


                    // Insert into database helpers
                    if (amenity.equals("hotel")) {
                        hotelList.add(geopoint);
                        long hotelInsertResult = hotelDatabaseHelper.insertHotel(name, lat, lon, geopoint.getAddress());
                        if (hotelInsertResult != -1) {
                            Log.d(TAG, "Hotel inserted successfully: " + geopoint.getName());
                        } else {
                            Log.e(TAG, "Failed to insert hotel: " + geopoint.getName());
                        }
                    } else if (amenity.equals("restaurant")) {
                        restaurantList.add(geopoint);
                        long restaurantInsertResult = restaurantDatabaseHelper.insertRestaurant(name, lat, lon, geopoint.getAddress());
                        if (restaurantInsertResult != -1) {
                            Log.d(TAG, "Restaurant inserted successfully: " + geopoint.getName());
                        } else {
                            Log.e(TAG, "Failed to insert restaurant: " + geopoint.getName());
                        }
                    } else if (amenity.equals("car_rental")) {
                        carServiceList.add(geopoint);
                        long carServiceInsertResult = carServiceDatabaseHelper.insertCarService(name, lat, lon, geopoint.getAddress());
                        if (carServiceInsertResult != -1) {
                            Log.d(TAG, "Car service inserted successfully: " + geopoint.getName());
                        } else {
                            Log.e(TAG, "Failed to insert car service: " + geopoint.getName());
                        }
                    }


                    // Add a marker on the map for each POI
                    LatLng poiLocation = new LatLng(geopoint.getLatitude(), geopoint.getLongitude());
                    mMap.addMarker(new MarkerOptions().position(poiLocation).title(geopoint.getName()));
                }
            } catch (Exception e) {
                e.printStackTrace();
                Log.e(TAG, "Error in POISearchTask onPostExecute: " + e.getMessage());
            }
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        mMapView.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        mMapView.onPause();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mMapView.onDestroy();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mMapView.onLowMemory();
    }


    private String getListAsString(List<GeopointItem> itemList) {
        StringBuilder stringBuilder = new StringBuilder();
        for (GeopointItem item : itemList) {
            stringBuilder.append("Name: ").append(item.getName()).append("\n");
            stringBuilder.append("Latitude: ").append(item.getLatitude()).append("\n");
            stringBuilder.append("Longitude: ").append(item.getLongitude()).append("\n");
            stringBuilder.append("Address: ").append(item.getAddress()).append("\n\n");
        }
        return stringBuilder.toString();
    }
}




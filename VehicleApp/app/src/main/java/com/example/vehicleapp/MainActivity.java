package com.example.vehicleapp;
import Services.NearbyActivity;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.text.Layout;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import org.json.JSONObject;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;



public class MainActivity extends AppCompatActivity implements LocationListener {
    private static final int PERMISSIONS_REQUEST_LOCATION = 1;
    private static final int PERMISSIONS_REQUEST_SMS = 2;
    private ArrayList<String> breakdownReasons;

    private LocationManager locationManager;
    private ArrayList<String> emergencyContacts;
    private Location currentLocation;
    private TextView temperatureTextView, weatherConditionTextView, locationTextView;
    private ImageView menu, sos, mappic, diagnosticspic, helpPic;

    View optionslayout;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Initialize emergency contacts
        emergencyContacts = new ArrayList<>();
        emergencyContacts.add("+40743811531"); // Hardcoded for exercise, should be replaced with user input

        // Initialize breakdown reasons
        initializeBreakdownReasons();

        // Request permissions
        requestPermissions();

        // Initialize location manager
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        // Set up SOS button click listener

        sos=findViewById(R.id.sos);
        mappic=findViewById(R.id.mappic);
        diagnosticspic=findViewById(R.id.diagnosticspic);
        helpPic=findViewById(R.id.helpPic);
        sos.setOnClickListener(view -> sendSosSignal());

        // Initialize views
        temperatureTextView = findViewById(R.id.temperature_textview);
        weatherConditionTextView = findViewById(R.id.weather_condition_textview);
        locationTextView=findViewById(R.id.locationtextView);


         menu=findViewById(R.id.menu);
         optionslayout=findViewById(R.id.optionslayout);
        // Fetch weather data when activity starts
        fetchWeatherData();

        // Set up button listeners


        diagnosticspic.setOnClickListener(view -> displayRandomBreakdownReason());
        helpPic.setOnClickListener(this::onBroadcastSendBtnClicked);

        mappic.setOnClickListener(view -> openMapActivity());

        if (checkLocationPermission()) {
            // Start listening for location updates
            startLocationUpdates();

            menu.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (optionslayout.getVisibility() == View.VISIBLE) {
                        optionslayout.setVisibility(View.GONE);
                    } else {
                        optionslayout.setVisibility(View.VISIBLE);
                    }
                }
            });

        }
    }

    private void startLocationUpdates() {
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        if (locationManager != null) {
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, this);
        }
    }

    private boolean checkLocationPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // Permission is not granted, request it
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, PERMISSIONS_REQUEST_LOCATION);
            return false;
        }
        return true;
    }

    private void openMapActivity() {
        Intent intent = new Intent(this, MapActivity.class);
        startActivity(intent);
    }

    private void openNearbyServices() {
        Intent intent = new Intent(this, NearbyActivity.class);
        startActivity(intent);
    }

    private void requestPermissions() {
        // Request location permission
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, PERMISSIONS_REQUEST_LOCATION);
        }

        // Request SMS permission
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.SEND_SMS) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.SEND_SMS}, PERMISSIONS_REQUEST_SMS);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSIONS_REQUEST_LOCATION) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission granted, start location updates
                startLocationUpdates();
            } else {
                // Permission denied, handle accordingly
                Toast.makeText(this, "Location permission denied", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void fetchWeatherData() {
        if (checkPermissions()) {
            if (currentLocation != null) {
                new FetchWeatherTask().execute(Double.toString(currentLocation.getLatitude()), Double.toString(currentLocation.getLongitude()));
            }
        }
    }


    private void sendSosSignal() {
        if (checkPermissions()) {
            sendLocationUpdates();
        }
    }

    private void sendLocationUpdates() {
        if (currentLocation != null) {
            String breakdownReason = getRandomBreakdownReason();
            String message = "Current location: " + currentLocation.getLatitude() + ", " + currentLocation.getLongitude() +
                    "City: "+ locationTextView.getText().toString()+" Weather condition: " + weatherConditionTextView.getText().toString() +
                    " Breakdown Reason: " + breakdownReason;

            for (String contact : emergencyContacts) {
                sendSms(contact, message);
            }

            Toast.makeText(this, "Location update sent to emergency contacts", Toast.LENGTH_SHORT).show();
        }
    }



    private boolean checkPermissions() {
        return ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, Manifest.permission.SEND_SMS) == PackageManager.PERMISSION_GRANTED;
    }


    // Inner AsyncTask class to fetch weather data
    private class FetchWeatherTask extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... location) {
            try {
                URL url = new URL("https://api.openweathermap.org/data/2.5/weather?lat=" + location[0] + "&lon=" + location[1] +
                        "&units=metric&appid=55f91d9d3fa62a6de60cc982aca16cac");
                HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                InputStream in = urlConnection.getInputStream();
                InputStreamReader reader = new InputStreamReader(in);
                int data;
                StringBuilder response = new StringBuilder();
                while ((data = reader.read()) != -1) {
                    response.append((char) data);
                }
                return response.toString();
            } catch (Exception e) {
                Log.e("WeatherTask", "Error fetching weather data", e);
                return null;
            }
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            if (s == null || s.isEmpty()) {
                Log.e("WeatherTask", "Empty or null response");
                locationTextView.setText("-");
                weatherConditionTextView.setText("-");
                temperatureTextView.setText("-");
                return;
            }

            try {
                JSONObject jsonObject = new JSONObject(s);

                String cityName = jsonObject.optString("name", "-");
                String weatherInfo = jsonObject.getJSONArray("weather").getJSONObject(0).optString("main", "-");
                String temp = jsonObject.getJSONObject("main").optString("temp", "-");

                locationTextView.setText(cityName);
                temperatureTextView.setText(temp + "°C");
                weatherConditionTextView.setText(weatherInfo);

            } catch (Exception e) {
                Log.e("WeatherTask", "Error parsing weather data", e);
                locationTextView.setText("-");
                weatherConditionTextView.setText("-");
                temperatureTextView.setText("-");
            }
        }
    }

    private void sendSms(final String phoneNumber, final String message) {
        Timer smsSendingTimer = new Timer();
        smsSendingTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                try {
                    SmsManager smsManager = SmsManager.getDefault();
                    smsManager.sendTextMessage(phoneNumber, null, message, null, null);
                } catch (Exception e) {
                    Log.e("MainActivity", "Failed to send SMS", e);
                    Toast.makeText(MainActivity.this, "Failed to send SMS", Toast.LENGTH_SHORT).show();
                }
            }
        }, 0, 5000); // Repeat every 30 seconds
    }

    private void initializeBreakdownReasons() {
        breakdownReasons = new ArrayList<>();
        breakdownReasons.add("Mechanical Failure");
        breakdownReasons.add("Electrical Issues");
        breakdownReasons.add("Fuel System Problems");
        breakdownReasons.add("Overheating");
        breakdownReasons.add("Flat Tires");
        breakdownReasons.add("Battery Failure");
        breakdownReasons.add("Ignition System Problems");
        breakdownReasons.add("Brake System Issues");
        breakdownReasons.add("Exhaust System Failure");
        breakdownReasons.add("Transmission Problems");
        breakdownReasons.add("Engine Oil Issues");
        breakdownReasons.add("Fluid Leaks");
        breakdownReasons.add("Environmental Factors");
        breakdownReasons.add("Accidents or Collisions");
        breakdownReasons.add("Lack of Maintenance");
    }

    private String getRandomBreakdownReason() {
        if (!breakdownReasons.isEmpty()) {
            Random random = new Random();
            int index = random.nextInt(breakdownReasons.size());
            return breakdownReasons.get(index);
        }
        return "";
    }

    private void displayRandomBreakdownReason() {
        String reason = getRandomBreakdownReason();
        if (!reason.isEmpty()) {
            Toast.makeText(this, "Breakdown Reason: " + reason, Toast.LENGTH_LONG).show();
        } else {
            Toast.makeText(this, "No breakdown reasons available", Toast.LENGTH_LONG).show();
        }
    }
    @Override
    public void onLocationChanged(Location location) {
        // Update currentLocation when location changes
        currentLocation = location;
        // Call any method that requires the current location, e.g., fetchWeatherData()
        fetchWeatherData();
    }

    public void onBroadcastSendBtnClicked(View v) {
        Intent intent = new Intent();
        intent.setAction("com.vehicleapp.sender");
        intent.setFlags(Intent.FLAG_INCLUDE_STOPPED_PACKAGES);
        sendBroadcast(intent);
        Toast.makeText(this, "Broadcast sent ", Toast.LENGTH_LONG).show();
    }
}

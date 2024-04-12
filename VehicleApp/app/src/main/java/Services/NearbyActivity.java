package Services;

import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.os.AsyncTask;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.vehicleapp.GeopointItem;
import com.example.vehicleapp.R;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;


public class NearbyActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nearby_services);
        TextView all_services=findViewById(R.id.all_services);

        // Retrieve lists from intent
        List<GeopointItem> restaurantList = new ArrayList<>();
        List<GeopointItem> hotelList = new ArrayList<>();
        List<GeopointItem> carServiceList = new ArrayList<>();


        Intent intent = getIntent();
        if (intent != null) {
            ArrayList<String> restaurantNames = intent.getStringArrayListExtra("restaurantNames");
            ArrayList<String> hotelNames = intent.getStringArrayListExtra("hotelNames");
            ArrayList<String> carServiceNames = intent.getStringArrayListExtra("carServiceNames");

            // Populate your lists using the received names
            // For example:

            // Populate lists using the received names
            populateListFromNames(restaurantList, restaurantNames);
            populateListFromNames(hotelList, hotelNames);
            populateListFromNames(carServiceList, carServiceNames);
            // Similarly, populate hotelList and carServiceList using hotelNames and carServiceNames

            // Update addresses for each list
            new UpdateAddressesTask().execute(restaurantList, hotelList, carServiceList);
        }





        // Button click listeners
        Button autoServices = findViewById(R.id.buttonAutoService);

        StringBuilder allServicesText = new StringBuilder();
        allServicesText.append("Restaurants:\n").append(getListAsString(restaurantList)).append("\n\n");
        allServicesText.append("Hotels:\n").append(getListAsString(hotelList)).append("\n\n");
        allServicesText.append("Car Services:\n").append(getListAsString(carServiceList));

        // Set the concatenated text to the TextView
        all_services.setText(allServicesText.toString());
/*5
        autoServices.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Assuming NearbyServices is the fragment class you want to display
                String carList=getListAsString(carServiceList);
                FragmentManager fragmentManager = getSupportFragmentManager();
                NearbyServices nearbyServicesFragment=NearbyServices.newInstance(carList);
                fragmentManager.beginTransaction()
                        .replace(R.id.fragment_container, new NearbyServices()) // Pass an instance of the fragment class
                        .setReorderingAllowed(true)
                        .addToBackStack(null)
                        .commit();
            }
        });
        Button restaurantsbtn = findViewById(R.id.buttonRestaurant);
        restaurantsbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String resList = getListAsString(restaurantList);
                Log.i("Restaurantlist sent:", resList);
                FragmentManager fragmentManager = getSupportFragmentManager();
                NearbyRestaurantsActivity nearbyRestaurantsFragment = NearbyRestaurantsActivity.newInstance(resList);
                fragmentManager.beginTransaction()
                        .replace(R.id.fragment_container, nearbyRestaurantsFragment)
                        .setReorderingAllowed(true)
                        .addToBackStack(null)
                        .commit();
            }
        });

        Button hotelbtn = findViewById(R.id.buttonHotel);
        hotelbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String hotels=getListAsString(hotelList);
                FragmentManager fragmentManager = getSupportFragmentManager();
                NearbyHotels nearbyHotelsFragment = NearbyHotels.newInstance(hotels);
                fragmentManager.beginTransaction()
                        .replace(R.id.fragment_container, new NearbyHotels()) // Pass an instance of the fragment class for hotel services
                        .setReorderingAllowed(true)
                        .addToBackStack(null)
                        .commit();
            }
        });*/
    }


    // AsyncTask to update addresses for each list
    private class UpdateAddressesTask extends AsyncTask<List<GeopointItem>, Void, Void> {

        @Override
        protected Void doInBackground(List<GeopointItem>... lists) {
            for (List<GeopointItem> itemList : lists) {
                for (GeopointItem item : itemList) {
                    getAddressForLocation(item);
                }
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            // Optionally handle post-execution tasks here
        }
    }

    // Method to get address for a GeopointItem
    private void getAddressForLocation(GeopointItem item) {
        try {
            Geocoder geocoder = new Geocoder(NearbyActivity.this, Locale.getDefault());
            List<Address> addresses = geocoder.getFromLocation(item.getLatitude(), item.getLongitude(), 1);
            if (addresses != null && !addresses.isEmpty()) {
                StringBuilder addressBuilder = new StringBuilder();
                Address address = addresses.get(0);
                for (int i = 0; i <= address.getMaxAddressLineIndex(); i++) {
                    addressBuilder.append(address.getAddressLine(i));
                    if (i < address.getMaxAddressLineIndex()) {
                        addressBuilder.append(", ");
                    }
                }
                item.setAddress(addressBuilder.toString());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
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

    private void populateListFromNames(List<GeopointItem> targetList, ArrayList<String> names) {
        if (names != null) {
            for (String name : names) {
                // Extract latitude, longitude, and name from the string and create GeopointItem
                String[] parts = name.split("\n");
                if (parts.length >= 3) {
                    double lat = Double.parseDouble(parts[1]);
                    double lon = Double.parseDouble(parts[2]);
                    targetList.add(new GeopointItem(parts[0], lat, lon));
                }
            }
        }
    }
}

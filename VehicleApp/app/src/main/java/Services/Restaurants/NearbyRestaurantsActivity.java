package Services.Restaurants;

import android.os.Bundle;
import android.util.Log;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.vehicleapp.GeopointItem;
import com.example.vehicleapp.R;
import com.example.vehicleapp.RestaurantDatabaseHelper;

import java.util.List;

public class NearbyRestaurantsActivity extends AppCompatActivity {

    private RestaurantDatabaseHelper restaurantDatabaseHelper;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nearby_restaurants);

        LinearLayout restaurantLayout = findViewById(R.id.restaurantLayout);

        // Initialize the restaurant database helper
        restaurantDatabaseHelper = new RestaurantDatabaseHelper(this);

        // Open the database
        restaurantDatabaseHelper.open();

        // Retrieve the list of restaurants from the database
        List<GeopointItem> restaurantList = restaurantDatabaseHelper.getAllRestaurants();

        // Close the database
        restaurantDatabaseHelper.close();

        if (restaurantList != null && !restaurantList.isEmpty()) {
            for (GeopointItem restaurant : restaurantList) {
                // Create a TextView for each restaurant item and add it to the layout
                String restaurantDetails = "Name: " + restaurant.getName() + "\n"
                        + "Latitude: " + restaurant.getLatitude() + "\n"
                        + "Longitude: " + restaurant.getLongitude() + "\n"
                        + "Address: " + restaurant.getAddress() + "\n\n";

                Log.i("Restaurant received:", restaurantDetails);

                TextView textView = new TextView(this);
                textView.setText(restaurantDetails);
                restaurantLayout.addView(textView);
            }
        } else {
            Log.i("Restaurant received", "No restaurants found in the database");
        }
    }
}

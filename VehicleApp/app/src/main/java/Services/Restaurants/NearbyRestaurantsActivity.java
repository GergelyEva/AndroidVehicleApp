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

        restaurantDatabaseHelper = new RestaurantDatabaseHelper(this);

        restaurantDatabaseHelper.open();

        List<GeopointItem> restaurantList = restaurantDatabaseHelper.getAllRestaurants();

        restaurantDatabaseHelper.close();

        if (restaurantList != null && !restaurantList.isEmpty()) {
            for (GeopointItem restaurant : restaurantList) {
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

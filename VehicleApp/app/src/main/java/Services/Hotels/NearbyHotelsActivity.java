package Services.Hotels;

import android.os.Bundle;
import android.util.Log;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.vehicleapp.GeopointItem;
import com.example.vehicleapp.HotelDatabaseHelper;
import com.example.vehicleapp.R;

import java.util.List;

public class NearbyHotelsActivity extends AppCompatActivity {

    private HotelDatabaseHelper hotelDatabaseHelper;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nearby_hotels);

        LinearLayout hotelsLayout = findViewById(R.id.hotelsLayout);

        // Initialize the hotel database helper
        hotelDatabaseHelper = new HotelDatabaseHelper(this);

        hotelDatabaseHelper.open();
        // Retrieve all hotels from the database
        List<GeopointItem> hotelList = hotelDatabaseHelper.getAllHotels();

        hotelDatabaseHelper.close();
        if (hotelList != null && !hotelList.isEmpty()) {
            // Display hotel details
            for (GeopointItem hotel : hotelList) {
                // Create a TextView for each hotel item and add it to the layout
                String hotelDetails = "Name: " + hotel.getName() + "\n"
                        + "Latitude: " + hotel.getLatitude() + "\n"
                        + "Longitude: " + hotel.getLongitude() + "\n"
                        + "Address: " + hotel.getAddress() + "\n\n";

                TextView textView = new TextView(this);
                textView.setText(hotelDetails);
                hotelsLayout.addView(textView);
            }
        } else {
            // Display message when no hotels are found
            TextView textView = new TextView(this);
            textView.setText("No hotels nearby");
            hotelsLayout.addView(textView);
        }
    }
}

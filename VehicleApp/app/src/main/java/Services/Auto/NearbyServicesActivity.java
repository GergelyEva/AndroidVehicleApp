package Services.Auto;

import android.os.Bundle;
import android.util.Log;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.vehicleapp.CarServiceDatabaseHelper;
import com.example.vehicleapp.GeopointItem;
import com.example.vehicleapp.R;

import java.util.List;

public class NearbyServicesActivity extends AppCompatActivity {

    private CarServiceDatabaseHelper carServiceDatabaseHelper;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nearbyservices);

        LinearLayout serviceLayout = findViewById(R.id.carsLayout);
        carServiceDatabaseHelper = new CarServiceDatabaseHelper(this);


        carServiceDatabaseHelper.open();
        List<GeopointItem> carServices = carServiceDatabaseHelper.getAllCarServices();

        carServiceDatabaseHelper.close();
        if (carServices != null && !carServices.isEmpty()) {
            for (GeopointItem car : carServices) {
                String serviceDetails = "Name: " + car.getName() + "\n"
                        + "Latitude: " + car.getLatitude() + "\n"
                        + "Longitude: " + car.getLongitude() + "\n"
                        + "Address: " + car.getAddress() + "\n\n";

                Log.i("Carservice received:", serviceDetails);

                TextView textView = new TextView(this);
                textView.setText(serviceDetails);
                serviceLayout.addView(textView);
            }
        }
        else{
            Log.i("Carservice received", "No Carservice found in the database");

        }
    }
}

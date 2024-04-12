package com.example.vehicleappbroadcast;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

import java.util.Random;

public class bcReceive extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        // TODO: This method is called when the BroadcastReceiver is receiving
        // an Intent broadcast.
  // Log.i("BroadcastReceiver", "Broadcast message received");
    //Toast.makeText(context,"Broadcast message is received",Toast.LENGTH_SHORT).show();
    String data = generateRandomAnswer();
    Toast.makeText(context, "John: " + data, Toast.LENGTH_LONG).show();
    Log.i("BroadcastReceiver", "Broadcast message received"+data);

    }

    private String generateRandomAnswer() {
        Random random = new Random();
        int randomNumber = random.nextInt(5) + 1; // Generates a random number between 1 and 5

        String answer;

        switch (randomNumber) {
            case 1:
                answer = "Don't worry, mommy is coming!";
                break;
            case 2:
                answer = "That sounds horrible. No worries, I called the pickup truck!";
                break;
            case 3:
                answer = "Well, it could have been worse!On my way!";
                break;
            case 4:
                answer = "Expect the unexpected! Meanwhile, go to a restaurant, it will take me an our to get to you ";
                break;
            case 5:
                answer = "Keep calm and carry on! To a hotel, cause it sounds like it is serious and it will take a couple of days to fix";
                break;
            default:
                answer = "Oops, something went wrong! Don't worry, I am closeby!";
                break;
        }

        return answer;
    }

}
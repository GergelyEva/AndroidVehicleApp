package com.example.vehicleapp;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.example.vehicleapp.GeopointItem;

import java.util.ArrayList;
import java.util.List;

public class RestaurantDatabaseHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "restaurant_db";
    private static final int DATABASE_VERSION = 1;
    private static final String TABLE_NAME = "restaurants";
    private static final String COLUMN_ID = "_id";
    private static final String COLUMN_NAME = "name";
    private static final String COLUMN_LATITUDE = "latitude";
    private static final String COLUMN_LONGITUDE = "longitude";
    private static final String COLUMN_ADDRESS = "address";

    public RestaurantDatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String createTableQuery = "CREATE TABLE " + TABLE_NAME + " (" +
                COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COLUMN_NAME + " TEXT, " +
                COLUMN_LATITUDE + " REAL, " +
                COLUMN_LONGITUDE + " REAL, " +
                COLUMN_ADDRESS + " TEXT)";
        db.execSQL(createTableQuery);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Drop the table if it exists and recreate it
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(db);
    }

    public void open() throws SQLException {
        getWritableDatabase();
    }

    @Override
    public synchronized void close() {
        super.close();
    }

    // Insert a restaurant into the database
    public long insertRestaurant(String name, double latitude, double longitude, String address) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_NAME, name);
        values.put(COLUMN_LATITUDE, latitude);
        values.put(COLUMN_LONGITUDE, longitude);
        values.put(COLUMN_ADDRESS, address);
        return db.insert(TABLE_NAME, null, values);
    }

    // Retrieve all restaurants from the database
    public List<GeopointItem> getAllRestaurants() {
        List<GeopointItem> restaurantList = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_NAME, null, null, null, null, null, null);
        if (cursor != null && cursor.moveToFirst()) {
            do {
                String name = cursor.getString(cursor.getColumnIndex(COLUMN_NAME));
                double latitude = cursor.getDouble(cursor.getColumnIndex(COLUMN_LATITUDE));
                double longitude = cursor.getDouble(cursor.getColumnIndex(COLUMN_LONGITUDE));
                String address = cursor.getString(cursor.getColumnIndex(COLUMN_ADDRESS));
                GeopointItem restaurant = new GeopointItem(name, latitude, longitude, address);
                restaurantList.add(restaurant);
            } while (cursor.moveToNext());
            cursor.close();
        }
        return restaurantList;
    }
}

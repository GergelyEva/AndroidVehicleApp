package com.example.vehicleapp;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.List;

public class HotelDatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "hotel_database";
    private static final int DATABASE_VERSION = 1;

    public static final String TABLE_HOTELS = "hotels";
    public static final String COLUMN_ID = "_id";
    public static final String COLUMN_NAME = "name";
    public static final String COLUMN_LATITUDE = "latitude";
    public static final String COLUMN_LONGITUDE = "longitude";
    public static final String COLUMN_ADDRESS = "address";

    private static final String CREATE_HOTELS_TABLE = "CREATE TABLE " + TABLE_HOTELS + " (" +
            COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
            COLUMN_NAME + " TEXT, " +
            COLUMN_LATITUDE + " REAL, " +
            COLUMN_LONGITUDE + " REAL, " +
            COLUMN_ADDRESS + " TEXT);";

    public HotelDatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_HOTELS_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_HOTELS);
        onCreate(db);
    }
    // Insert a hotel into the database

    public long insertHotel(String name, double latitude, double longitude, String address) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_NAME, name);
        values.put(COLUMN_LATITUDE, latitude);
        values.put(COLUMN_LONGITUDE, longitude);
        values.put(COLUMN_ADDRESS, address);
        long result = db.insert(TABLE_HOTELS, null, values);
        db.close();
        return result;
    }

    public void open() throws SQLException {
        getWritableDatabase();
    }

    // Retrieve all hotels from the database
    public List<GeopointItem> getAllHotels() {
        List<GeopointItem> hotelList = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_HOTELS, null, null, null, null, null, null);
        if (cursor != null && cursor.moveToFirst()) {
            do {
                String name = cursor.getString(cursor.getColumnIndex(COLUMN_NAME));
                double latitude = cursor.getDouble(cursor.getColumnIndex(COLUMN_LATITUDE));
                double longitude = cursor.getDouble(cursor.getColumnIndex(COLUMN_LONGITUDE));
                String address = cursor.getString(cursor.getColumnIndex(COLUMN_ADDRESS));
                GeopointItem hotel = new GeopointItem(name, latitude, longitude, address);
                hotelList.add(hotel);
            } while (cursor.moveToNext());
            cursor.close();
        }
        return hotelList;
    }
}

package com.example.vehicleapp;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "vehicle_app.db";
    private static final int DATABASE_VERSION = 1;

    // Table names
    public static final String TABLE_HOTELS = "hotels";
    public static final String TABLE_RESTAURANTS = "restaurants";
    public static final String TABLE_CAR_SERVICES = "car_services";

    public static final String COLUMN_ID = "id";
    public static final String COLUMN_NAME = "name";
    public static final String COLUMN_LATITUDE = "latitude";
    public static final String COLUMN_LONGITUDE = "longitude";
    public static final String COLUMN_ADDRESS = "address";

    private static final String CREATE_TABLE_HOTELS = "CREATE TABLE " + TABLE_HOTELS + "(" +
            COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
            COLUMN_NAME + " TEXT," +
            COLUMN_LATITUDE + " REAL," +
            COLUMN_LONGITUDE + " REAL," +
            COLUMN_ADDRESS + " TEXT" +
            ")";

    private static final String CREATE_TABLE_RESTAURANTS = "CREATE TABLE " + TABLE_RESTAURANTS + "(" +
            COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
            COLUMN_NAME + " TEXT," +
            COLUMN_LATITUDE + " REAL," +
            COLUMN_LONGITUDE + " REAL," +
            COLUMN_ADDRESS + " TEXT" +
            ")";

    private static final String CREATE_TABLE_CAR_SERVICES = "CREATE TABLE " + TABLE_CAR_SERVICES + "(" +
            COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
            COLUMN_NAME + " TEXT," +
            COLUMN_LATITUDE + " REAL," +
            COLUMN_LONGITUDE + " REAL," +
            COLUMN_ADDRESS + " TEXT" +
            ")";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    // Create tables

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_TABLE_HOTELS);
        db.execSQL(CREATE_TABLE_RESTAURANTS);
        db.execSQL(CREATE_TABLE_CAR_SERVICES);
    }

    // Upgrade older tables if they exist
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_HOTELS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_RESTAURANTS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_CAR_SERVICES);

        onCreate(db);
    }
}

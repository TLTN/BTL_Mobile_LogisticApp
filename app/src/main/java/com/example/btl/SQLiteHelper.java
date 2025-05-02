package com.example.btl;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.List;

public class SQLiteHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "MyDatabase.db";
    private static final int DATABASE_VERSION = 2;

    private static final String CREATE_TABLE_USERS = "CREATE TABLE IF NOT EXISTS Users (" +
            "_id INTEGER PRIMARY KEY AUTOINCREMENT, " +
            "username TEXT NOT NULL UNIQUE, " +
            "password TEXT NOT NULL, " +
            "phone TEXT NOT NULL" +
            ");";

    private static final String CREATE_TABLE_USER_INFO = "CREATE TABLE IF NOT EXISTS UserInfo (" +
            "username TEXT PRIMARY KEY, " +
            "fullname TEXT, " +
            "birthdate TEXT, " +
            "FOREIGN KEY(username) REFERENCES Users(username) ON DELETE CASCADE" +
            ");";

    private static final String CREATE_TABLE_TRUCKS = "CREATE TABLE IF NOT EXISTS Trucks (" +
            "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
            "name TEXT NOT NULL, " +
            "plate TEXT NOT NULL, " +
            "status TEXT NOT NULL " +
            ");";

    private static final String CREATE_TABLE_DON_HANG = "CREATE TABLE IF NOT EXISTS DonHang (" +
            "maDon TEXT PRIMARY KEY, " +
            "trangThai TEXT NOT NULL" +
            ");";

    public SQLiteHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_TABLE_USERS);
        db.execSQL(CREATE_TABLE_USER_INFO);
        db.execSQL(CREATE_TABLE_TRUCKS);
        db.execSQL(CREATE_TABLE_DON_HANG);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if (oldVersion != newVersion) {
            db.execSQL("DROP TABLE IF EXISTS UserInfo");
            db.execSQL("DROP TABLE IF EXISTS Users");
            db.execSQL("DROP TABLE IF EXISTS Trucks");
            db.execSQL("DROP TABLE IF EXISTS DonHang");
            onCreate(db);
        }
    }

    public void insertTruck(String name, String plate, String status, String color) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("name", name);
        values.put("plate", plate);
        values.put("status", status);
        db.insert("Trucks", null, values);
    }

    public void updateTruck(int id, String name, String plate, String status, String color) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("name", name);
        values.put("plate", plate);
        values.put("status", status);
        db.update("Trucks", values, "id = ?", new String[]{String.valueOf(id)});
    }

    public void deleteTruck(int id) {
        SQLiteDatabase db = getWritableDatabase();
        db.delete("Trucks", "id = ?", new String[]{String.valueOf(id)});
    }

    public List<Truck> getAllTrucks() {
        List<Truck> list = new ArrayList<>();
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM Trucks", null);
        if (cursor != null) {
            while (cursor.moveToNext()) {
                int id = cursor.getInt(0);
                String name = cursor.getString(1);
                String plate = cursor.getString(2);
                String status = cursor.getString(3);
                list.add(new Truck(id, name, plate, status));
            }
            cursor.close();
        }
        return list;
    }

    public void insertDonHang(String maDon, String trangThai) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("maDon", maDon);
        values.put("trangThai", trangThai);
        db.insert("DonHang", null, values);
    }

    public void updateDonHang(String maDon, String trangThai) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("trangThai", trangThai);
        db.update("DonHang", values, "maDon = ?", new String[]{maDon});
    }

    public void deleteDonHang(String maDon) {
        SQLiteDatabase db = getWritableDatabase();
        db.delete("DonHang", "maDon = ?", new String[]{maDon});
    }

    public List<DonHang> getAllDonHang() {
        List<DonHang> list = new ArrayList<>();
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM DonHang", null);
        if (cursor != null) {
            while (cursor.moveToNext()) {
                String maDon = cursor.getString(0);
                String trangThai = cursor.getString(1);
                list.add(new DonHang(maDon, trangThai));
            }
            cursor.close();
        }
        return list;
    }
}

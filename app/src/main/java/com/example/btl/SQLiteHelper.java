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
    private static final int DATABASE_VERSION = 4;

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

    private static final String CREATE_TABLE_TAIXE = "CREATE TABLE IF NOT EXISTS TaiXe (" +
            "maTaiXe TEXT PRIMARY KEY, " +
            "tenTaiXe TEXT NOT NULL, " +
            "sdt TEXT NOT NULL" +
            ");";

    private static final String CREATE_TABLE_TRUCKS = "CREATE TABLE IF NOT EXISTS Trucks (" +
            "maXe TEXT PRIMARY KEY, " +
            "plate TEXT NOT NULL, " +
            "status TEXT NOT NULL, " +
            "maTaiXe TEXT, " +
            "FOREIGN KEY(maTaiXe) REFERENCES TaiXe(maTaiXe) ON DELETE SET NULL" +
            ");";

    private static final String CREATE_TABLE_DON_HANG = "CREATE TABLE IF NOT EXISTS DonHang (" +
            "maDon TEXT PRIMARY KEY" +
            ");";

    private static final String CREATE_TABLE_PHANCONG = "CREATE TABLE IF NOT EXISTS PhanCong (" +
            "maDon TEXT, " +
            "maXe TEXT, " +
            "PRIMARY KEY(maDon, maXe), " +
            "FOREIGN KEY(maDon) REFERENCES DonHang(maDon) ON DELETE CASCADE, " +
            "FOREIGN KEY(maXe) REFERENCES Trucks(maXe) ON DELETE CASCADE" +
            ");";


    public SQLiteHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_TABLE_USERS);
        db.execSQL(CREATE_TABLE_USER_INFO);
        db.execSQL(CREATE_TABLE_TAIXE);
        db.execSQL(CREATE_TABLE_TRUCKS);
        db.execSQL(CREATE_TABLE_DON_HANG);
        db.execSQL(CREATE_TABLE_PHANCONG);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if (oldVersion != newVersion) {
            db.execSQL("DROP TABLE IF EXISTS PhanCong");
            db.execSQL("DROP TABLE IF EXISTS DonHang");
            db.execSQL("DROP TABLE IF EXISTS Trucks");
            db.execSQL("DROP TABLE IF EXISTS TaiXe");
            db.execSQL("DROP TABLE IF EXISTS UserInfo");
            db.execSQL("DROP TABLE IF EXISTS Users");
            onCreate(db);
        }
    }

    public void insertTruck(String maXe, String plate, String status, String maTaiXe) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("maXe", maXe);
        values.put("plate", plate);
        values.put("status", status);
        values.put("maTaiXe", maTaiXe);
        db.insert("Trucks", null, values);
    }

    public void updateTruck(String maXe, String plate, String status, String maTaiXe) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("plate", plate);
        values.put("status", status);
        values.put("maTaiXe", maTaiXe);
        db.update("Trucks", values, "maXe = ?", new String[]{maXe});
    }

    public void deleteTruck(String maXe) {
        SQLiteDatabase db = getWritableDatabase();
        db.delete("Trucks", "maXe = ?", new String[]{maXe});
    }

    public List<Truck> getAllTrucks() {
        List<Truck> list = new ArrayList<>();
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM Trucks", null);
        if (cursor != null) {
            while (cursor.moveToNext()) {
                String maXe = cursor.getString(0);
                String plate = cursor.getString(1);
                String status = cursor.getString(2);
                String maTaiXe = cursor.getString(3);
                list.add(new Truck(maXe, plate, status, maTaiXe));
            }
            cursor.close();
        }
        return list;
    }

    public void insertDonHang(String maDon) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("maDon", maDon);
        db.insert("DonHang", null, values);
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
                list.add(new DonHang(maDon));
            }
            cursor.close();
        }
        return list;
    }

    public void insertTaiXe(String maTaiXe, String tenTaiXe, String sdt) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("maTaiXe", maTaiXe);
        values.put("tenTaiXe", tenTaiXe);
        values.put("sdt", sdt);
        db.insert("TaiXe", null, values);
    }

    public List<TaiXe> getAllTaiXe() {
        List<TaiXe> list = new ArrayList<>();
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM TaiXe", null);
        if (cursor != null) {
            while (cursor.moveToNext()) {
                String maTaiXe = cursor.getString(0);
                String tenTaiXe = cursor.getString(1);
                String sdt = cursor.getString(2);
                list.add(new TaiXe(maTaiXe, tenTaiXe, sdt));
            }
            cursor.close();
        }
        return list;
    }

    public void insertPhanCong(String maDon, String maXe) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("maDon", maDon);
        values.put("maXe", maXe);
        db.insert("PhanCong", null, values);
    }

    public void deletePhanCong(String maDon, String maXe) {
        SQLiteDatabase db = getWritableDatabase();
        db.delete("PhanCong", "maDon = ? AND maXe = ?", new String[]{maDon, maXe});
    }

    public List<PhanCong> getAllPhanCong() {
        List<PhanCong> list = new ArrayList<>();
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM PhanCong", null);
        if (cursor != null) {
            while (cursor.moveToNext()) {
                String maDon = cursor.getString(0);
                String maXe = cursor.getString(1);
                list.add(new PhanCong(maDon, maXe));
            }
            cursor.close();
        }
        return list;
    }

    public boolean isDonHangAssigned(String maDon, String maXe) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM DonHangXeTai WHERE maDon = ? AND maXe = ?",
                new String[]{maDon, maXe});
        boolean exists = cursor.getCount() > 0;
        cursor.close();
        return exists;
    }
    public void assignDonHangToXeTai(String maDon, String maXe) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("maDon", maDon);
        values.put("maXe", maXe);
        db.insert("DonHangXeTai", null, values);
    }

}
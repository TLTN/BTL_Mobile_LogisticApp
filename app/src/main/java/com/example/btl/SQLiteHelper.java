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

    public SQLiteHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }
    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE IF NOT EXISTS Users (" +
                "_id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "username TEXT NOT NULL UNIQUE, " +
                "password TEXT NOT NULL, " +
                "phone TEXT NOT NULL);");

        db.execSQL("CREATE TABLE IF NOT EXISTS UserInfo (" +
                "username TEXT PRIMARY KEY, " +
                "fullname TEXT, " +
                "birthdate TEXT, " +
                "FOREIGN KEY(username) REFERENCES Users(username) ON DELETE CASCADE);");

        db.execSQL("CREATE TABLE IF NOT EXISTS TaiXe (" +
                "maTaiXe TEXT PRIMARY KEY, " +
                "tenTaiXe TEXT NOT NULL, " +
                "sdt TEXT NOT NULL);");

        db.execSQL("CREATE TABLE IF NOT EXISTS Trucks (" +
                "maXe TEXT PRIMARY KEY, " +
                "plate TEXT NOT NULL, " +
                "status TEXT NOT NULL, " +
                "maTaiXe TEXT, " +
                "FOREIGN KEY(maTaiXe) REFERENCES TaiXe(maTaiXe) ON DELETE SET NULL);");

        db.execSQL("CREATE TABLE IF NOT EXISTS DonHang (" +
                "maDon TEXT PRIMARY KEY);");

        db.execSQL("CREATE TABLE IF NOT EXISTS PhanCong (" +
                "maDon TEXT, " +
                "maXe TEXT, " +
                "timestamp TEXT," +
                "PRIMARY KEY(maDon, maXe), " +
                "FOREIGN KEY(maDon) REFERENCES DonHang(maDon) ON DELETE CASCADE, " +
                "FOREIGN KEY(maXe) REFERENCES Trucks(maXe) ON DELETE CASCADE);");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS PhanCong");
        db.execSQL("DROP TABLE IF EXISTS DonHang");
        db.execSQL("DROP TABLE IF EXISTS Trucks");
        db.execSQL("DROP TABLE IF EXISTS TaiXe");
        db.execSQL("DROP TABLE IF EXISTS UserInfo");
        db.execSQL("DROP TABLE IF EXISTS Users");
        onCreate(db);
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


    public long insertTaiXe(String maTaiXe, String tenTaiXe, String sdt) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("maTaiXe", maTaiXe);
        values.put("tenTaiXe", tenTaiXe);
        values.put("sdt", sdt);
        db.insert("TaiXe", null, values);
        return 0;
    }

    public int updateTaiXe(String maTaiXe, String tenTaiXe, String sdtTaiXe) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("maTaiXe", maTaiXe);
        values.put("tenTaiXe", tenTaiXe);
        values.put("sdt", sdtTaiXe);
        db.update("TaiXe", values, "maTaiXe = ?", new String[]{maTaiXe});
        return 0;
    }

    public int deleteTaiXe(String maTaiXe)
    {
        SQLiteDatabase db = getWritableDatabase();
        return db.delete("TaiXe", "maTaiXe = ?", new String[]{maTaiXe});
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

    public TaiXe getTaiXeByMa(String mtx) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query("TaiXe", null, "maTaiXe = ?", new String[]{mtx}, null, null, null);
        if (cursor != null && cursor.moveToFirst()) {
            String maTaiXe = cursor.getString(0);
            String tenTaiXe = cursor.getString(1);
            String sdt = cursor.getString(2);
            cursor.close();
            return new TaiXe(maTaiXe, tenTaiXe, sdt);
        }
        if (cursor != null) cursor.close();
        return null;
    }


    public long insertPhanCong(String maDon, String maXe, String timestamp) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("maDon", maDon);
        values.put("maXe", maXe);
        values.put("timestamp", timestamp);
        return db.insert("PhanCong", null, values);
    }

    public int deletePhanCong(String maDon, String maXe, String timestamp) {
        SQLiteDatabase db = getWritableDatabase();
        return db.delete("PhanCong", "maDon = ? AND maXe = ? AND timestamp = ?", new String[]{maDon, maXe, timestamp});
    }

    public List<PhanCong> getAllPhanCong() {
        List<PhanCong> list = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM PhanCong", null);

        if (cursor != null) {
            if (cursor.moveToFirst()) {
                do {
                    String maDon = cursor.getString(0);
                    String maXe = cursor.getString(1);
                    String timestamp = cursor.getString(2);
                    list.add(new PhanCong(maDon, maXe, timestamp));
                } while (cursor.moveToNext());
            }
            cursor.close();
        }
        return list;
    }

    public List<PhanCong> get3LatestPhanCong() {
        List<PhanCong> list = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM PhanCong ORDER BY timestamp DESC LIMIT 3", null);

        if (cursor != null) {
            if (cursor.moveToFirst()) {
                do {
                    String maDon = cursor.getString(0);
                    String maXe = cursor.getString(1);
                    String timestamp = cursor.getString(2);
                    list.add(new PhanCong(maDon, maXe, timestamp));
                } while (cursor.moveToNext());
            }
            cursor.close();
        }
        return list;
    }


    public void updateTruckStatus(String selectedXeTai, String s)
    {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("status", s);
        db.update("Trucks", values, "maXe = ?", new String[]{selectedXeTai});
        db.close();
    }

    public void insertThongKe(String selectedDonHang, String selectedXeTai)
    {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("maDon", selectedDonHang);
        values.put("maXe", selectedXeTai);
        db.insert("ThongKe", null, values);
        db.close();
    }

    public int countDonHang() {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT COUNT(*) FROM DonHang", null);
        int count = 0;
        if (cursor.moveToFirst()) {
            count = cursor.getInt(0);
        }
        cursor.close();
        return count;
    }

    public int countXeTai() {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT COUNT(*) FROM Trucks", null);
        int count = 0;
        if (cursor.moveToFirst()) {
            count = cursor.getInt(0);
        }
        cursor.close();
        return count;
    }

    public int countTaiXe() {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT COUNT(*) FROM TaiXe", null);
        int count = 0;
        if (cursor.moveToFirst()) {
            count = cursor.getInt(0);
        }
        cursor.close();
        return count;
    }
}

package com.example.btl;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class SQLiteHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "MyDatabase";
    private static final int DATABASE_VERSION = 2;

    private static final String CREATE_TABLE_USERS = "CREATE TABLE Users (" +
            "_id INTEGER PRIMARY KEY AUTOINCREMENT, " +
            "username TEXT NOT NULL, " +
            "password TEXT NOT NULL, " +
            "phone TEXT NOT NULL" +
            ");";

    private static final String CREATE_TABLE_USER_INFO = "CREATE TABLE UserInfo (" +
            "username TEXT PRIMARY KEY, " +
            "fullname TEXT, " +
            "birthdate TEXT, " +
            "FOREIGN KEY(username) REFERENCES Users(username)" +
            ");";

    public SQLiteHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_TABLE_USERS);
        db.execSQL(CREATE_TABLE_USER_INFO);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS UserInfo");
        db.execSQL("DROP TABLE IF EXISTS Users");
        onCreate(db);
    }
}

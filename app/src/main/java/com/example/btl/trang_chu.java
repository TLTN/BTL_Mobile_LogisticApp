package com.example.btl;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TabHost;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewFlipper;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.cursoradapter.widget.SimpleCursorAdapter;

import com.google.firebase.Firebase;

import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class trang_chu extends AppCompatActivity
{
    TabHost tabHost;
    int tabCurrent = 0;
    Button btnSave, btnChangePassword, btnLogout;
    EditText edtCurrent, edtNew, edtConfirm;
    EditText edtFullName, edtBirthDate;
    TextView tvUser, tvName;
    String loggedInUsername = "";
    SQLiteHelper helper;
    SQLiteDatabase db;
    CardView cardThongKe, cardDonHang, cardXeTai, cardQuanLy;
    ViewFlipper viewFlipper;


    private void Init() {
        helper = new SQLiteHelper(this);
        db = helper.getWritableDatabase();
        btnSave = findViewById(R.id.btnSave);
        btnChangePassword = findViewById(R.id.btnChangePassword);
        btnLogout = findViewById(R.id.btnLogout);
        edtCurrent = findViewById(R.id.edtCurrentPassword);
        edtNew = findViewById(R.id.edtNewPassword);
        edtConfirm = findViewById(R.id.edtConfirmPassword);
        edtFullName = findViewById(R.id.edtFullName);
        edtBirthDate = findViewById(R.id.edtBirthDate);
        tvUser = findViewById(R.id.tvUser);
        tvName = findViewById(R.id.tvName);
        cardThongKe = findViewById(R.id.card1);
        cardDonHang = findViewById(R.id.card2);
        cardXeTai = findViewById(R.id.card3);
        cardQuanLy = findViewById(R.id.card4);
        viewFlipper = findViewById(R.id.viewFlipper);

        tabHost = findViewById(R.id.tabhost);
        tabHost.setup();
        TabHost.TabSpec tab1 = tabHost.newTabSpec("Tab1");
        tab1.setIndicator("Trang chủ");
        tab1.setContent(R.id.tab1);
        tabHost.addTab(tab1);
        TabHost.TabSpec tab2 = tabHost.newTabSpec("Tab2");
        tab2.setIndicator("Danh sách");
        tab2.setContent(R.id.tab2);
        tabHost.addTab(tab2);
        TabHost.TabSpec tab3 = tabHost.newTabSpec("Tab3");
        tab3.setIndicator("Cá nhân");
        tab3.setContent(R.id.tab3);
        tabHost.addTab(tab3);
        tabHost.setCurrentTab(tabCurrent);
    }

    private boolean isValidDate(String date) {
        String regex = "^(0[1-9]|[12][0-9]|3[01])/(0[1-9]|1[0-2])/(\\d{4})$";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(date);
        return matcher.matches();
    }

    private void LoadUserInfo() {
        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        if (bundle != null) {
            loggedInUsername = bundle.getString("username", "");
        }

        if (loggedInUsername == null || loggedInUsername.isEmpty()) {
            Toast.makeText(this, "Không lấy được tên đăng nhập", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        tvUser.setText(loggedInUsername);

        Cursor cursor = db.rawQuery("SELECT fullname, birthdate FROM UserInfo WHERE username=?", new String[]{loggedInUsername});
        if (cursor != null && cursor.moveToFirst()) {
            int fullNameIndex = cursor.getColumnIndex("fullname");
            int birthDateIndex = cursor.getColumnIndex("birthdate");
            if (fullNameIndex != -1 && birthDateIndex != -1) {
                String fullname = cursor.getString(fullNameIndex);
                String birthdate = cursor.getString(birthDateIndex);

                edtFullName.setText(fullname);
                edtBirthDate.setText(birthdate);
                tvName.setText(fullname);
            } else {
                Toast.makeText(this, "Không tìm thấy cột fullname hoặc birthdate", Toast.LENGTH_SHORT).show();
            }
            cursor.close();
        }
    }


    private void Event()
    {
        btnSave.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v) {
                String name = edtFullName.getText().toString().trim();
                String birth = edtBirthDate.getText().toString().trim();
                if (name.isEmpty() || birth.isEmpty()) {
                    Toast.makeText(trang_chu.this, "Vui lòng nhập họ tên và ngày sinh", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (!isValidDate(birth)) {
                    Toast.makeText(trang_chu.this, "Ngày sinh không đúng định dạng (dd/mm/yyyy)", Toast.LENGTH_SHORT).show();
                    return;
                }
                Cursor cursor = db.rawQuery("SELECT * FROM UserInfo WHERE username=?", new String[]{loggedInUsername});
                ContentValues values = new ContentValues();
                values.put("username", loggedInUsername);
                values.put("fullname", name);
                values.put("birthdate", birth);
                if (cursor.moveToFirst()) {
                    db.update("UserInfo", values, "username=?", new String[]{loggedInUsername});
                } else {
                    db.insert("UserInfo", null, values);
                }
                cursor.close();
                Toast.makeText(trang_chu.this, "Lưu thông tin thành công", Toast.LENGTH_SHORT).show();
            }
        });

        btnLogout.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                Intent intent = new Intent(trang_chu.this, trang_dang_nhap.class);
                startActivity(intent);
                finish();
            }
        });

        cardThongKe.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(trang_chu.this, thong_ke.class);
                intent.putExtra("username", loggedInUsername);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                finish();
            }
        });
        cardDonHang.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(trang_chu.this, don_hang.class);
                intent.putExtra("username", loggedInUsername);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                finish();
            }
        });
        cardXeTai.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(trang_chu.this, xe_tai.class);
                intent.putExtra("username", loggedInUsername);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                finish();
            }
        });
        cardQuanLy.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                Intent intent = new Intent(trang_chu.this, quan_ly.class);
                intent.putExtra("username", loggedInUsername);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                finish();
            }
        });
    }

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.trang_chu);
        Init();
        LoadUserInfo();
        Event();
    }
}
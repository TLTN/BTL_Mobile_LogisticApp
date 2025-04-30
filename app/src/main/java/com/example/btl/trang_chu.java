package com.example.btl;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TabHost;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class trang_chu extends AppCompatActivity {
    TabHost tabHost;
    int tabCurrent = 0;
    Button btnSave, btnChangePassword, btnLogout;
    EditText edtCurrent, edtNew, edtConfirm;
    EditText edtFullName, edtBirthDate;
    TextView tvUser, tvName;
    String loggedInUsername = "";
    SQLiteHelper helper;
    SQLiteDatabase db;

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

    private void LoadUserInfo() {
        Intent intent = getIntent();
        loggedInUsername = intent.getStringExtra("username");
        if (loggedInUsername == null || loggedInUsername.isEmpty()) {
            Toast.makeText(this, "Không lấy được tên đăng nhập", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        Cursor cursor = db.rawQuery("SELECT fullname, birthdate FROM UserInfo WHERE username=?", new String[]{loggedInUsername});
        if (cursor.moveToFirst())
        {
            int colName = cursor.getColumnIndex("fullname");
            int colBirth = cursor.getColumnIndex("birthdate");
            if (colName >= 0 && colBirth >= 0)
            {
                edtFullName.setText(cursor.getString(colName));
                edtBirthDate.setText(cursor.getString(colBirth));
            }
        }
        cursor.close();
    }

    private boolean isValidDate(String date) {
        String regex = "^(0[1-9]|[12][0-9]|3[01])/(0[1-9]|1[0-2])/(\\d{4})$";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(date);
        return matcher.matches();
    }

    private boolean isValidDateReal(String date) {
        try
        {
            String[] parts = date.split("/");
            int day = Integer.parseInt(parts[0]);
            int month = Integer.parseInt(parts[1]);
            int year = Integer.parseInt(parts[2]);
            java.util.Calendar calendar = java.util.Calendar.getInstance();
            calendar.set(year, month - 1, day);
            return day == calendar.get(java.util.Calendar.DAY_OF_MONTH);
        }
        catch (Exception e)
        {
            return false;
        }
    }

    private void Event() {
        btnChangePassword.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v) {
                if (edtCurrent.getVisibility() == View.GONE) {
                    edtCurrent.setVisibility(View.VISIBLE);
                    edtNew.setVisibility(View.VISIBLE);
                    edtConfirm.setVisibility(View.VISIBLE);
                } else {
                    edtCurrent.setText("");
                    edtNew.setText("");
                    edtConfirm.setText("");
                    edtCurrent.setVisibility(View.GONE);
                    edtNew.setVisibility(View.GONE);
                    edtConfirm.setVisibility(View.GONE);
                }
            }
        });

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

                if (!isValidDateReal(birth)) {
                    Toast.makeText(trang_chu.this, "Ngày sinh không hợp lệ", Toast.LENGTH_SHORT).show();
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

                if (edtCurrent.getVisibility() == View.VISIBLE) {
                    String current = edtCurrent.getText().toString().trim();
                    String newPass = edtNew.getText().toString().trim();
                    String confirm = edtConfirm.getText().toString().trim();
                    if (current.isEmpty() || newPass.isEmpty() || confirm.isEmpty()) {
                        Toast.makeText(trang_chu.this, "Vui lòng nhập đầy đủ thông tin mật khẩu", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    Cursor c = db.rawQuery("SELECT password FROM Users WHERE username=?", new String[]{loggedInUsername});
                    if (c.moveToFirst()) {
                        int colPass = c.getColumnIndex("password");
                        if (colPass >= 0) {
                            String oldPass = c.getString(colPass);
                            if (!oldPass.equals(current)) {
                                Toast.makeText(trang_chu.this, "Mật khẩu hiện tại không đúng", Toast.LENGTH_SHORT).show();
                                c.close();
                                return;
                            }
                            if (!newPass.equals(confirm)) {
                                Toast.makeText(trang_chu.this, "Mật khẩu mới không khớp", Toast.LENGTH_SHORT).show();
                                c.close();
                                return;
                            }
                            ContentValues passUpdate = new ContentValues();
                            passUpdate.put("password", newPass);
                            db.update("Users", passUpdate, "username=?", new String[]{loggedInUsername});
                            Toast.makeText(trang_chu.this, "Đổi mật khẩu thành công", Toast.LENGTH_SHORT).show();
                            edtCurrent.setText("");
                            edtNew.setText("");
                            edtConfirm.setText("");
                            edtCurrent.setVisibility(View.GONE);
                            edtNew.setVisibility(View.GONE);
                            edtConfirm.setVisibility(View.GONE);
                        }
                    }
                    c.close();
                } else {
                    Toast.makeText(trang_chu.this, "Lưu thông tin thành công", Toast.LENGTH_SHORT).show();
                }
            }
        });

        btnLogout.setOnClickListener(v -> {
            Intent intent = new Intent(trang_chu.this, trang_dang_nhap.class);
            startActivity(intent);
            finish();
        });

        if (tvUser != null)
            tvUser.setText(loggedInUsername);
        else
            tvUser.setText("");
        if (edtFullName != null)
            tvName.setText(edtFullName.getText().toString().trim());
        else
            tvName.setText("");
    }

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.trang_chu);
        Init();
        LoadUserInfo();
        Event();
    }
}

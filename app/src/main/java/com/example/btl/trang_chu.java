package com.example.btl;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
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

import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class trang_chu extends AppCompatActivity {
    TabHost tabHost;
    int tabCurrent = 0;
    Button btnSave, btnChangePassword, btnLogout, btnAddTruck, btnAddOrder;
    EditText edtCurrent, edtNew, edtConfirm;
    EditText edtFullName, edtBirthDate;
    TextView tvUser, tvName;
    String loggedInUsername = "";
    SQLiteHelper helper;
    SQLiteDatabase db;
    CardView cardThongKe, cardDonHang, cardXeTai, cardCaiDat;
    ViewFlipper viewFlipper;
    ListView lvXe, lvDonHang;
    ArrayAdapter<String> xeAdapter, donHangAdapter;
    ArrayList<String> listXe, listDonHang;

    private void Init() {
        helper = new SQLiteHelper(this);
        db = helper.getWritableDatabase();
        btnSave = findViewById(R.id.btnSave);
        btnChangePassword = findViewById(R.id.btnChangePassword);
        btnLogout = findViewById(R.id.btnLogout);
        btnAddTruck = findViewById(R.id.btnInsertTruck);
        btnAddOrder = findViewById(R.id.btnInsertDonHang);
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
        cardCaiDat = findViewById(R.id.card4);
        viewFlipper = findViewById(R.id.viewFlipper);
        lvXe = findViewById(R.id.lvXeTai);
        lvDonHang = findViewById(R.id.lvDonHang);

        listXe = new ArrayList<>();
        listDonHang = new ArrayList<>();

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

//        listXe.add("Xe 1 - Biển 29A12345");
//        listXe.add("Xe 2 - Biển 30B67890");
//        listXe.add("Xe 3 - Biển 31C98765");
//
//        listDonHang.add("ĐH001 - Giao Hà Nội");
//        listDonHang.add("ĐH002 - Giao Hải Phòng");
//        listDonHang.add("ĐH003 - Giao Đà Nẵng");
        xeAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, listXe);
        donHangAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, listDonHang);

        lvXe.setAdapter(xeAdapter);
        lvDonHang.setAdapter(donHangAdapter);

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
        if (cursor.moveToFirst()) {
            int colName = cursor.getColumnIndex("fullname");
            int colBirth = cursor.getColumnIndex("birthdate");
            if (colName >= 0 && colBirth >= 0) {
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
        try {
            String[] parts = date.split("/");
            int day = Integer.parseInt(parts[0]);
            int month = Integer.parseInt(parts[1]);
            int year = Integer.parseInt(parts[2]);
            java.util.Calendar calendar = java.util.Calendar.getInstance();
            calendar.set(year, month - 1, day);
            return day == calendar.get(java.util.Calendar.DAY_OF_MONTH);
        } catch (Exception e) {
            return false;
        }
    }

    private void Event() {
        btnChangePassword.setOnClickListener(v -> {
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
        });

        btnSave.setOnClickListener(v -> {
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
        });

        btnLogout.setOnClickListener(v -> {
            Intent intent = new Intent(trang_chu.this, trang_dang_nhap.class);
            startActivity(intent);
            finish();
        });

        btnAddTruck.setOnClickListener(v -> {
            AlertDialog.Builder builder = new AlertDialog.Builder(trang_chu.this);
            builder.setTitle("Thêm Xe Mới");

            final View customLayout = getLayoutInflater().inflate(R.layout.dialog_add_truck, null);
            builder.setView(customLayout);

            final EditText edtTruckName = customLayout.findViewById(R.id.editTextTruckName);
            final EditText edtTruckLicensePlate = customLayout.findViewById(R.id.editTextTruckLicensePlate);

            builder.setPositiveButton("Thêm", (dialog, which) -> {
                String truckName = edtTruckName.getText().toString().trim();
                String licensePlate = edtTruckLicensePlate.getText().toString().trim();

                if (!truckName.isEmpty() && !licensePlate.isEmpty()) {
                    ContentValues values = new ContentValues();
                    values.put("name", truckName);
                    values.put("license", licensePlate);
                    db.insert("Trucks", null, values);

                    listXe.add(truckName + " - " + licensePlate);
                    xeAdapter.notifyDataSetChanged();
                    Toast.makeText(trang_chu.this, "Thêm xe thành công", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(trang_chu.this, "Vui lòng nhập đủ thông tin xe", Toast.LENGTH_SHORT).show();
                }
            });


            builder.setNegativeButton("Hủy", (dialog, which) -> dialog.dismiss());

            builder.create().show();
        });

        btnAddOrder.setOnClickListener(v -> {
            AlertDialog.Builder builder = new AlertDialog.Builder(trang_chu.this);
            builder.setTitle("Thêm Đơn Hàng Mới");

            final View customLayout = getLayoutInflater().inflate(R.layout.dialog_add_order, null);
            builder.setView(customLayout);

            final EditText edtOrderName = customLayout.findViewById(R.id.edtOrderName);
            final EditText edtOrderDescription = customLayout.findViewById(R.id.edtOrderDescription);

            builder.setPositiveButton("Thêm", (dialog, which) -> {
                String orderName = edtOrderName.getText().toString().trim();
                String orderDescription = edtOrderDescription.getText().toString().trim();

                if (!orderName.isEmpty() && !orderDescription.isEmpty()) {
                    ContentValues values = new ContentValues();
                    values.put("name", orderName);
                    values.put("description", orderDescription);
                    db.insert("Orders", null, values);

                    listDonHang.add(orderName + " - " + orderDescription);
                    donHangAdapter.notifyDataSetChanged();
                    Toast.makeText(trang_chu.this, "Thêm đơn hàng thành công", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(trang_chu.this, "Vui lòng nhập đủ thông tin đơn hàng", Toast.LENGTH_SHORT).show();
                }
            });

            builder.setNegativeButton("Hủy", (dialog, which) -> dialog.dismiss());

            builder.create().show();
        });


        if (tvUser != null)
            tvUser.setText(loggedInUsername);
        else
            tvUser.setText("");
        if (edtFullName != null)
            tvName.setText(edtFullName.getText().toString().trim());
        else
            tvName.setText("");

        cardThongKe.setOnClickListener(v -> viewFlipper.setDisplayedChild(0));
        cardDonHang.setOnClickListener(v -> viewFlipper.setDisplayedChild(1));
        cardXeTai.setOnClickListener(v -> viewFlipper.setDisplayedChild(2));
        cardCaiDat.setOnClickListener(v -> viewFlipper.setDisplayedChild(3));
    }

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.trang_chu);
        Init();
        LoadUserInfo();
        Event();
    }
}

package com.example.btl;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TabHost;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
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
    SQLiteHelper db;
    CardView cardThongKe, cardDonHang, cardXeTai, cardQuanLy, cardTaiXe;
    LinearLayout layoutXe;
    TextView donHangDangGiao, donHangHoanTat, donHangHomNay;
    List<PhanCong> phanCongList;
    int count;

    private void Init() {
        db = new SQLiteHelper(this);

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
        cardTaiXe = findViewById(R.id.card5);
        layoutXe = findViewById(R.id.linearCardview);
        donHangDangGiao = findViewById(R.id.donhangDangGiao);
        donHangHoanTat = findViewById(R.id.donHangHoanTat);
        donHangHomNay = findViewById(R.id.donHangHomNay);

        tabHost = findViewById(R.id.tabhost);
        tabHost.setup();
        TabHost.TabSpec tab1 = tabHost.newTabSpec("Tab1");
        tab1.setIndicator("Trang chủ");
        tab1.setContent(R.id.tab1);
        tabHost.addTab(tab1);
        tabCurrent = tabHost.getCurrentTab();

        TabHost.TabSpec tab2 = tabHost.newTabSpec("Tab2");
        tab2.setIndicator("Danh sách");
        tab2.setContent(R.id.tab2);
        tabHost.addTab(tab2);
        tabCurrent = tabHost.getCurrentTab();

        TabHost.TabSpec tab3 = tabHost.newTabSpec("Tab3");
        tab3.setIndicator("Cá nhân");
        tab3.setContent(R.id.tab3);
        tabHost.addTab(tab3);
        tabCurrent = tabHost.getCurrentTab();

        tabHost.setCurrentTab(tabCurrent);
    }

    private boolean isValidDate(String date)
    {
        String regex = "^(0[1-9]|[12][0-9]|3[01])/(0[1-9]|1[0-2])/(\\d{4})$";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(date);
        return matcher.matches();
    }

    private void LoadUserInfo()
    {
        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        if (bundle != null)
        {
            loggedInUsername = bundle.getString("username", "");
            count = bundle.getInt("count", 0);
        }

        if (loggedInUsername == null || loggedInUsername.isEmpty()) {
            Toast.makeText(this, "Không lấy được tên đăng nhập", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        tvUser.setText(loggedInUsername);

        Cursor cursor = db.getReadableDatabase().rawQuery("SELECT fullname, birthdate FROM UserInfo WHERE username=?", new String[]{loggedInUsername});
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

                Cursor cursor = db.getReadableDatabase().rawQuery("SELECT * FROM UserInfo WHERE username=?", new String[]{loggedInUsername});
                ContentValues values = new ContentValues();
                values.put("username", loggedInUsername);
                values.put("fullname", name);
                values.put("birthdate", birth);
                if (cursor.moveToFirst()) {
                    db.getWritableDatabase().update("UserInfo", values, "username=?", new String[]{loggedInUsername});
                } else {
                    db.getWritableDatabase().insert("UserInfo", null, values);
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

                    Cursor c = db.getWritableDatabase().rawQuery("SELECT password FROM Users WHERE username=?", new String[]{loggedInUsername});
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
                            db.getWritableDatabase().update("Users", passUpdate, "username=?", new String[]{loggedInUsername});
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
        cardTaiXe.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                Intent intent = new Intent(trang_chu.this, tai_xe.class);
                intent.putExtra("username", loggedInUsername);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                finish();
            }
        });
    }
    private void loadXeTaiData()
    {
        List<Truck> danhSachXe = db.getAllTrucks();

        for (Truck xe : danhSachXe)
        {
            CardView cardView = new CardView(this);
            cardView.setLayoutParams(new LinearLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    dpToPx(100)
            ));
            ((LinearLayout.LayoutParams) cardView.getLayoutParams()).setMargins(0, 0, 0, dpToPx(25));
            cardView.setRadius(dpToPx(20));
            cardView.setCardElevation(dpToPx(6));

            String trangThai = xe.getStatus();
            int bgColor = R.color.button3;
            if ("Đang hoạt động".equalsIgnoreCase(trangThai)) {
                bgColor = R.color.button1;
            } else if ("Chờ điều phối".equalsIgnoreCase(trangThai)) {
                bgColor = R.color.button2;
            }
            cardView.setCardBackgroundColor(ContextCompat.getColor(this, bgColor));

            LinearLayout container = new LinearLayout(this);
            container.setOrientation(LinearLayout.HORIZONTAL);
            container.setPadding(dpToPx(10), dpToPx(10), dpToPx(10), dpToPx(10));
            container.setGravity(Gravity.CENTER_VERTICAL);

            ImageView imageView = new ImageView(this);
            LinearLayout.LayoutParams imageParams = new LinearLayout.LayoutParams(dpToPx(75), dpToPx(75));
            imageView.setLayoutParams(imageParams);
            imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
            imageView.setImageResource(R.drawable.container);
            container.addView(imageView);

            LinearLayout textContainer = new LinearLayout(this);
            textContainer.setOrientation(LinearLayout.VERTICAL);
            textContainer.setPadding(dpToPx(10), 0, 0, 0);
            LinearLayout.LayoutParams textLayoutParams = new LinearLayout.LayoutParams(
                    0, ViewGroup.LayoutParams.WRAP_CONTENT, 1
            );
            textContainer.setLayoutParams(textLayoutParams);

            TextView txtTenXe = new TextView(this);
            txtTenXe.setText("Xe Tải " + xe.getMaXe());
            txtTenXe.setTextSize(18);
            txtTenXe.setTextColor(ContextCompat.getColor(this, R.color.text_color));

            TextView txtBienSo = new TextView(this);
            txtBienSo.setText("Biển số: " + xe.getPlate());
            txtBienSo.setTextSize(14);
            txtBienSo.setTextColor(ContextCompat.getColor(this, R.color.text2_color));

            TextView txtTrangThai = new TextView(this);
            txtTrangThai.setText("Trạng thái: " + trangThai);
            txtTrangThai.setTextSize(14);
            int colorTrangThai = R.color.text3_color;
            if ("Đang hoạt động".equalsIgnoreCase(trangThai))
            {
                colorTrangThai = R.color.text3_color;
            }
            else if ("Chờ điều phối".equalsIgnoreCase(trangThai))
            {
                colorTrangThai = R.color.important1;
            }
            txtTrangThai.setTextColor(ContextCompat.getColor(this, colorTrangThai));

            textContainer.addView(txtTenXe);
            textContainer.addView(txtBienSo);
            textContainer.addView(txtTrangThai);

            container.addView(textContainer);
            cardView.addView(container);
            layoutXe.addView(cardView);
        }
    }

    private int dpToPx(int dp)
    {
        return (int) TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP, dp, getResources().getDisplayMetrics());
    }

    private void loadDonHang()
    {
        phanCongList = db.getAllPhanCong();
        String ngayHomNay = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(new Date());

        int tongDonHomNay = 0;
//        int soDonDangGiao = 0;

        for (PhanCong p : phanCongList) {
            if (p.getNgayGiao().equals(ngayHomNay))
            {
                tongDonHomNay++;
//                if (p.getMaXe() != null && !p.getMaXe().isEmpty())
//                    soDonDangGiao++;
            }
        }

        donHangHomNay.setText(String.valueOf(tongDonHomNay));
        donHangDangGiao.setText(String.valueOf(tongDonHomNay - count));
//        donHangHoanTat.setText(String.valueOf(tongDonHomNay - soDonDangGiao));
        donHangHoanTat.setText(String.valueOf(count));

    }

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.trang_chu);
        Init();
        LoadUserInfo();
        loadXeTaiData();
        loadDonHang();
        Event();
    }
}
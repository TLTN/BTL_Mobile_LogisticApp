package com.example.btl;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.HashMap;

public class thong_ke extends AppCompatActivity {

    TextView tvTongDon, tvDonDangGiao, tvDonHuy, tvDonDaGiao,
            tvTongXe, tvXeDangHoatDong, tvXeChuaCoDon,
            tvTongTaiXe, tvTaiXeChuaCoXe;
    ImageView btnBack;
    ListView lvThongKe;
    ArrayList<HashMap<String, String>> danhSachChiTiet;
    ThongKe thongKe;
    String username;
    private void Init()
    {
        tvTongDon = findViewById(R.id.tvTongDon);
        tvDonDangGiao = findViewById(R.id.tvDonDangGiao);
        tvDonHuy = findViewById(R.id.tvDonHuy);
        tvDonDaGiao = findViewById(R.id.tvDonDaGiao);
        tvTongXe = findViewById(R.id.tvTongXe);
        tvXeDangHoatDong = findViewById(R.id.tvXeDangHoatDong);
        tvXeChuaCoDon = findViewById(R.id.tvXeChuaCoDon);
        tvTongTaiXe = findViewById(R.id.tvTongTaiXe);
        tvTaiXeChuaCoXe = findViewById(R.id.tvTaiXeChuaCoXe);
        lvThongKe = findViewById(R.id.lvThongKe);
        btnBack = findViewById(R.id.btnBack);
        danhSachChiTiet = new ArrayList<>();


    }
    private void Event()
    {
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(thong_ke.this, trang_chu.class);
                intent.putExtra("username", username);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                finish();
            }
        });
    }

    private void LoadUsername()
    {
        Intent intent = getIntent();
        username = intent.getStringExtra("username");
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.thong_ke);

        Init();
        LoadUsername();
        Event();

    }
}


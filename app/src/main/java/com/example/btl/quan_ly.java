package com.example.btl;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.button.MaterialButton;

import java.util.ArrayList;
import java.util.List;

public class quan_ly extends AppCompatActivity {

    private Spinner spinnerDonHang, spinnerXeTai;
    private MaterialButton btnGan;
    private ListView lvGan;
    private TextView tvTitle;
    private ImageView btnBack;
    private SQLiteHelper dbHelper;
    private List<DonHang> donHangList;
    private List<Truck> truckList;
    private ArrayAdapter<String> donHangAdapter;
    private ArrayAdapter<String> xeTaiAdapter;
    private ArrayAdapter<String> ganListAdapter;
    private List<String> danhSachGianDonHang = new ArrayList<>();
    private String username;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.quan_ly);

        dbHelper = new SQLiteHelper(this);

        spinnerDonHang = findViewById(R.id.spinnerDonHang);
        spinnerXeTai = findViewById(R.id.spinnerXeTai);
        btnGan = findViewById(R.id.btnGan);
        btnBack = findViewById(R.id.btnBack);
        lvGan = findViewById(R.id.lvGan);
        tvTitle = findViewById(R.id.tvTitle);

        LoadUsername();
        loadDonHangData();
        loadXeTaiData();
        setupAdapters();

        btnGan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String selectedDonHang = (String) spinnerDonHang.getSelectedItem();
                String selectedXeTai = (String) spinnerXeTai.getSelectedItem();

                if (selectedDonHang != null && selectedXeTai != null) {
                    String item = "Đơn: " + selectedDonHang + " \nXe: " + selectedXeTai;
                    danhSachGianDonHang.add(item);
                    ganListAdapter.notifyDataSetChanged();
                    Toast.makeText(quan_ly.this, "Đã gán đơn hàng cho xe tải", Toast.LENGTH_SHORT).show();
                }
            }
        });

        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(quan_ly.this, trang_chu.class);
                intent.putExtra("username", username);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                finish();
            }
        });

        lvGan.setOnItemClickListener((parent, view, position, id) -> {
            String item = danhSachGianDonHang.get(position);
            showDeleteConfirmationDialog(position, item);
        });
    }

    private void LoadUsername() {
        Intent intent = getIntent();
        username = intent.getStringExtra("username");
    }

    private void loadDonHangData() {
        donHangList = dbHelper.getAllDonHang();
        List<String> donHangNames = new ArrayList<>();
        for (DonHang donHang : donHangList) {
            donHangNames.add(donHang.getMaDon());
        }
        donHangAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, donHangNames);
        donHangAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerDonHang.setAdapter(donHangAdapter);
    }

    private void loadXeTaiData() {
        truckList = dbHelper.getAllTrucks();
        List<String> truckNames = new ArrayList<>();
        for (Truck truck : truckList) {
            truckNames.add(truck.getMaXe());
        }
        xeTaiAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, truckNames);
        xeTaiAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerXeTai.setAdapter(xeTaiAdapter);
    }

    private void setupAdapters() {
        ganListAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, danhSachGianDonHang);
        lvGan.setAdapter(ganListAdapter);
    }

    private void showDeleteConfirmationDialog(final int position, final String item) {
        new AlertDialog.Builder(quan_ly.this)
                .setTitle("Xác nhận xóa")
                .setMessage("Bạn có chắc chắn muốn xóa \n" + item + "\nkhỏi danh sách?")
                .setPositiveButton("Xóa", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // Xóa phần tử khỏi danh sách
                        danhSachGianDonHang.remove(position);
                        ganListAdapter.notifyDataSetChanged();
                        Toast.makeText(quan_ly.this, "Đã xóa thành công", Toast.LENGTH_SHORT).show();
                    }
                })
                .setNegativeButton("Hủy", null)
                .show();
    }
}

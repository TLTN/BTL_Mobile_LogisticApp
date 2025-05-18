package com.example.btl;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.button.MaterialButton;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class quan_ly extends AppCompatActivity {

    private Spinner spinnerDonHang, spinnerXeTai;
    private MaterialButton btnGan;
    private ListView lvGan;
    private ImageView btnBack;
    private SQLiteHelper db;
    private List<DonHang> donHangList;
    private List<Truck> truckList;
    private List<PhanCong> phanCongList;
    private ArrayAdapter<String> donHangAdapter;
    private ArrayAdapter<String> xeTaiAdapter;
    private ArrayAdapter<String> phanCongAdapter;
    private int selectedIndex = -1;
    private String username;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.quan_ly);

        Init();
        LoadUsername();
        loadDonHangData();
        loadXeTaiData();
        Event();
        loadPhanCongList();
    }

    private void Init()
    {
        db = new SQLiteHelper(this);

        spinnerDonHang = findViewById(R.id.spinnerDonHang);
        spinnerXeTai = findViewById(R.id.spinnerXeTai);
        btnGan = findViewById(R.id.btnGan);
        btnBack = findViewById(R.id.btnBack);
        lvGan = findViewById(R.id.lvGan);
    }

    private void Event()
    {
        btnGan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String selectedDonHang = (String) spinnerDonHang.getSelectedItem();
                String selectedXeTai = (String) spinnerXeTai.getSelectedItem();

                if (selectedDonHang != null && selectedXeTai != null)
                {
                    String timestamp = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss", Locale.getDefault()).format(new Date());
                    db.insertPhanCong(selectedDonHang, selectedXeTai, timestamp);
                    db.updateTruckStatus(selectedXeTai, "Đang hoạt động");
                    loadDonHangData();
                    loadXeTaiData();
                    spinnerDonHang.setSelection(0);
                    spinnerXeTai.setSelection(0);
                    selectedIndex = -1;

                    Toast.makeText(quan_ly.this, "Đã gán đơn hàng cho xe tải", Toast.LENGTH_SHORT).show();
                    loadPhanCongList();
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

        lvGan.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                String item = (String) parent.getItemAtPosition(position);
                showDeleteConfirmationDialog(position, item);
                return true;
            }
        });

    }
    private void LoadUsername() {
        Intent intent = getIntent();
        username = intent.getStringExtra("username");
    }
    private void loadPhanCongList() {
        phanCongList = db.get3LatestPhanCong();

        phanCongAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1);

        if (phanCongList != null && !phanCongList.isEmpty()) {
            for (PhanCong phanCong : phanCongList) {
                phanCongAdapter.add("Đơn: " + phanCong.getMaDon() + "\nXe: " + phanCong.getMaXe());
            }
        } else {
            phanCongAdapter.add("Không có dữ liệu phân công");
        }

        lvGan.setAdapter(phanCongAdapter);
        selectedIndex = -1;
    }


    private void loadDonHangData() {
        donHangList = db.getAllDonHang();

        donHangList.sort((d1, d2) -> d1.getMaDon().compareToIgnoreCase(d2.getMaDon()));

        List<String> donHangNames = new ArrayList<>();
        for (DonHang donHang : donHangList) {
            donHangNames.add(donHang.getMaDon());
        }

        donHangAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, donHangNames);
        donHangAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerDonHang.setAdapter(donHangAdapter);
    }

    private void loadXeTaiData() {
        truckList = db.getAllTrucks();

        truckList.sort((t1, t2) -> t1.getMaXe().compareToIgnoreCase(t2.getMaXe()));

        List<String> truckNames = new ArrayList<>();
        for (Truck truck : truckList) {
            truckNames.add(truck.getMaXe());
        }

        xeTaiAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, truckNames);
        xeTaiAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerXeTai.setAdapter(xeTaiAdapter);
    }

    private void showDeleteConfirmationDialog(final int position, final String item) {
        new AlertDialog.Builder(quan_ly.this)
                .setTitle("Xác nhận xóa")
                .setMessage("Bạn có chắc chắn muốn xóa \n" + item + "\nkhỏi danh sách?")
                .setPositiveButton("Xóa", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        PhanCong pc = phanCongList.get(position);
                        int rowsAffected = db.deletePhanCong(pc.getMaDon(), pc.getMaXe(), pc.getTimestamp());

                        if (rowsAffected > 0) {
                            phanCongList.remove(position);
                            phanCongAdapter.remove(item);
                            phanCongAdapter.notifyDataSetChanged();
                            loadPhanCongList();
                            Toast.makeText(quan_ly.this, "Đã xóa thành công", Toast.LENGTH_SHORT).show();
                        }
                        else
                        {
                            Toast.makeText(quan_ly.this, "Không thể xóa phân công này", Toast.LENGTH_SHORT).show();
                        }
                    }
                })
                .setNegativeButton("Hủy", null)
                .show();
    }

}

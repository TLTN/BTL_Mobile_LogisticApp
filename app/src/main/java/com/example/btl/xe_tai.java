package com.example.btl;

import android.app.AlertDialog;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;
import java.util.List;

public class xe_tai extends AppCompatActivity {

    EditText edtMaXe, edtBienSo;
    Button btnInsertTruck, btnUpdateTruck, btnDeleteTruck;
    ListView lvTrucks;
    ImageView btnBack;
    SQLiteHelper db;
    List<Truck> list;
    ArrayAdapter<String> adapter;
    int selectedIndex = -1;
    String username;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.xe_tai);
        init();
        LoadUsername();
        setEvent();
        loadData();
    }

    private void init() {
        edtMaXe = findViewById(R.id.edtMaXe);
        edtBienSo = findViewById(R.id.edtBienSo);
        btnInsertTruck = findViewById(R.id.btnInsertTruck);
        btnUpdateTruck = findViewById(R.id.btnUpdateTruck);
        btnDeleteTruck = findViewById(R.id.btnDeleteTruck);
        lvTrucks = findViewById(R.id.lvTrucks);
        btnBack = findViewById(R.id.btnBack);
        db = new SQLiteHelper(this);
    }

    private void LoadUsername() {
        Intent intent = getIntent();
        username = intent.getStringExtra("username");
    }
    private void setEvent() {
        btnInsertTruck.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String maXe = edtMaXe.getText().toString().trim();
                String bienSo = edtBienSo.getText().toString().trim();
                if (maXe.isEmpty() || bienSo.isEmpty()) {
                    Toast.makeText(xe_tai.this, "Vui lòng nhập đầy đủ thông tin", Toast.LENGTH_SHORT).show();
                    return;
                }
                db.insertTruck(maXe, bienSo, "Chờ điều phối", null);
                Toast.makeText(xe_tai.this, "Đã thêm xe tải", Toast.LENGTH_SHORT).show();
                clearFields();
                loadData();
            }
        });

        btnUpdateTruck.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (selectedIndex == -1) {
                    Toast.makeText(xe_tai.this, "Vui lòng chọn xe để sửa", Toast.LENGTH_SHORT).show();
                    return;
                }
                String maXe = edtMaXe.getText().toString().trim();
                String bienSo = edtBienSo.getText().toString().trim();
                if (maXe.isEmpty() || bienSo.isEmpty()) {
                    Toast.makeText(xe_tai.this, "Vui lòng nhập đầy đủ thông tin", Toast.LENGTH_SHORT).show();
                    return;
                }
                db.updateTruck(maXe, bienSo, "Chờ điều phối", null);
                Toast.makeText(xe_tai.this, "Đã cập nhật xe tải", Toast.LENGTH_SHORT).show();
                clearFields();
                loadData();
            }
        });

        btnDeleteTruck.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (selectedIndex == -1) {
                    Toast.makeText(xe_tai.this, "Vui lòng chọn xe để xóa", Toast.LENGTH_SHORT).show();
                    return;
                }
                new AlertDialog.Builder(xe_tai.this)
                        .setTitle("Xác nhận xóa")
                        .setMessage("Bạn có chắc chắn muốn xóa xe này không?")
                        .setPositiveButton("Xóa", (dialog, which) -> {
                            db.deleteTruck(list.get(selectedIndex).getMaXe());
                            Toast.makeText(xe_tai.this, "Đã xóa xe tải", Toast.LENGTH_SHORT).show();
                            clearFields();
                            loadData();
                        })
                        .setNegativeButton("Hủy", null)
                        .show();
            }
        });

        lvTrucks.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Truck truck = list.get(position);
                edtMaXe.setText(truck.getMaXe());
                edtBienSo.setText(truck.getPlate());
                selectedIndex = position;
            }
        });

        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(xe_tai.this, trang_chu.class);
                intent.putExtra("username", username);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                finish();
            }
        });
    }

    private void loadData() {
        list = db.getAllTrucks();

        list.sort((t1, t2) -> t1.getMaXe().compareToIgnoreCase(t2.getMaXe()));

        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1);
        for (Truck t : list) {
            adapter.add(t.getMaXe() + "\n" + t.getPlate());
        }
        lvTrucks.setAdapter(adapter);
        selectedIndex = -1;
    }
    private void showToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    private void navigateBack() {
        startActivity(new Intent(xe_tai.this, trang_chu.class));
        finish();
    }

    private void clearFields() {
        edtMaXe.setText("");
        edtBienSo.setText("");
        selectedIndex = -1;
    }
}

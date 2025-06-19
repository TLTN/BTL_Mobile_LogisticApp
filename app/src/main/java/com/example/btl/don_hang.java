package com.example.btl;

import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;

public class don_hang extends AppCompatActivity {
    EditText edtOrderCode;
    Button btnInsert, btnUpdate, btnDelete;
    ListView lvDonHang;
    ImageView btnBack;
    ArrayList<String> list = new ArrayList<>();
    ArrayAdapter<String> adapter;
    SQLiteHelper dbHelper;
    int selectedIndex = -1;
    String username;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.don_hang);

        Init();
        LoadUsername();
        Event();
        loadData();
    }
    private void Init() {
        edtOrderCode = findViewById(R.id.edtOrderCode);
        btnInsert = findViewById(R.id.btnInsertDonHang);
        btnUpdate = findViewById(R.id.btnUpdateDonHang);
        btnDelete = findViewById(R.id.btnDeleteDonHang);
        lvDonHang = findViewById(R.id.lvDonHang);
        btnBack = findViewById(R.id.btnBack);

        dbHelper = new SQLiteHelper(this);
        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, list);
        lvDonHang.setAdapter(adapter);
    }

    private void Event() {
        lvDonHang.setOnItemClickListener((parent, view, position, id) -> {
            edtOrderCode.setText(list.get(position));
            selectedIndex = position;
        });

        btnInsert.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                insertDonHang();
            }
        });

        btnUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                updateDonHang();
            }
        });

        btnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                deleteDonHang();
            }
        });

        btnBack.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view) {
                navigateBack();
                Intent intent = new Intent(don_hang.this, trang_chu.class);
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

    void loadData()
    {
        list.clear();
        try (SQLiteDatabase db = dbHelper.getReadableDatabase()) {
            Cursor cursor = db.rawQuery("SELECT * FROM DonHang", null);
            while (cursor.moveToNext())
            {
                list.add(cursor.getString(0));
            }
            cursor.close();
        }
        list.sort(String::compareToIgnoreCase);
        adapter.notifyDataSetChanged();
    }

    void insertDonHang()
    {
        String maDon = edtOrderCode.getText().toString().trim();
        if (maDon.isEmpty()) {
            showToast("Vui lòng nhập mã đơn");
            return;
        }
        try (SQLiteDatabase db = dbHelper.getWritableDatabase()) {
            ContentValues values = new ContentValues();
            values.put("maDon", maDon);
            long result = db.insert("DonHang", null, values);
            if (result == -1)
            {
                showToast("Mã đơn đã tồn tại");
            }
            else
            {
                showToast("Thêm thành công");
                edtOrderCode.setText("");
                loadData();
            }
        }
    }

    void updateDonHang() {
        if (selectedIndex == -1) {
            showToast("Chọn mã đơn để sửa");
            return;
        }
        String newMaDon = edtOrderCode.getText().toString().trim();
        if (newMaDon.isEmpty()) {
            showToast("Vui lòng nhập mã đơn mới");
            return;
        }
        String oldMaDon = list.get(selectedIndex);
        try (SQLiteDatabase db = dbHelper.getWritableDatabase()) {
            ContentValues values = new ContentValues();
            values.put("maDon", newMaDon);
            int rows = db.update("DonHang", values, "maDon=?", new String[]{oldMaDon});
            if (rows > 0) {
                showToast("Cập nhật thành công");
                edtOrderCode.setText("");
                selectedIndex = -1;
                loadData();
            } else {
                showToast("Không thể cập nhật");
            }
        }
    }

    void deleteDonHang()
    {
        if (selectedIndex == -1)
        {
            showToast("Chọn mã đơn để xóa");
            return;
        }
        String maDon = list.get(selectedIndex);
        new AlertDialog.Builder(this)
                .setTitle("Xóa đơn hàng")
                .setMessage("Bạn có chắc muốn xóa mã đơn: " + maDon + "?")
                .setPositiveButton("Xóa", (dialog, which) -> {
                    try (SQLiteDatabase db = dbHelper.getWritableDatabase()) {
                        int rows = db.delete("DonHang", "maDon=?", new String[]{maDon});
                        if (rows > 0) {
                            showToast("Xóa thành công");
                            edtOrderCode.setText("");
                            selectedIndex = -1;
                            loadData();
                        } else {
                            showToast("Không thể xóa");
                        }
                    }
                })
                .setNegativeButton("Hủy", null)
                .show();
    }

    void navigateBack()
    {
        startActivity(new Intent(don_hang.this, trang_chu.class));
        finish();
    }

    void showToast(String message)
    {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }
}

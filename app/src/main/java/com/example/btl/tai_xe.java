package com.example.btl;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import java.util.List;

public class tai_xe extends AppCompatActivity {

    private EditText edtMaTaiXe, edtTenTaiXe, edtSdtTaiXe;
    private Button btnInsertTaiXe, btnUpdateTaiXe, btnDeleteTaiXe;
    private ImageView btnBack;
    private ListView lvTaiXe;
    private CardView cardListView;

    private SQLiteHelper db;

    List<TaiXe> taiXeList;
    ArrayAdapter<String> adapter;
    int selectedIndex = -1;
    String username;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.tai_xe);

        Init();
        LoadUsername();
        Event();
        loadTaiXeList();
    }

    private void Init() {
        edtMaTaiXe = findViewById(R.id.edtMaTaiXe);
        edtTenTaiXe = findViewById(R.id.edtTenTaiXe);
        edtSdtTaiXe = findViewById(R.id.edtSdtTaiXe);
        btnInsertTaiXe = findViewById(R.id.btnInsertTaiXe);
        btnUpdateTaiXe = findViewById(R.id.btnUpdateTaiXe);
        btnDeleteTaiXe = findViewById(R.id.btnDeleteTaiXe);
        btnBack = findViewById(R.id.btnBack);
        lvTaiXe = findViewById(R.id.lvTaiXe);
        cardListView = findViewById(R.id.cardListView);

        db = new SQLiteHelper(this);
    }

    private void Event() {
        btnInsertTaiXe.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                insertTaiXe();
            }
        });

        btnUpdateTaiXe.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateTaiXe();
            }
        });

        btnDeleteTaiXe.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteTaiXe();
            }
        });

        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(tai_xe.this, trang_chu.class);
                intent.putExtra("username", username);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                finish();
            }
        });

        lvTaiXe.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                selectedIndex = position;
                TaiXe selectedTaiXe = taiXeList.get(position);
                edtMaTaiXe.setText(selectedTaiXe.getMaTaiXe());
                edtTenTaiXe.setText(selectedTaiXe.getTenTaiXe());
                edtSdtTaiXe.setText(selectedTaiXe.getSdt());

                edtMaTaiXe.setEnabled(false);
            }
        });
    }

    private boolean isValidPhoneNumber(String phone) {
        return phone.matches("0\\d{9}");
    }

    private void insertTaiXe() {
        String maTaiXe = edtMaTaiXe.getText().toString().trim();
        String tenTaiXe = edtTenTaiXe.getText().toString().trim();
        String sdtTaiXe = edtSdtTaiXe.getText().toString().trim();

        if (maTaiXe.isEmpty() || tenTaiXe.isEmpty() || sdtTaiXe.isEmpty()) {
            Toast.makeText(this, "Vui lòng nhập đầy đủ thông tin", Toast.LENGTH_SHORT).show();
            return;
        }

        if (!isValidPhoneNumber(sdtTaiXe)) {
            Toast.makeText(this, "Số điện thoại phải bắt đầu bằng 0 và gồm 10 chữ số", Toast.LENGTH_SHORT).show();
            return;
        }

        if (db.getTaiXeByMa(maTaiXe) != null) {
            Toast.makeText(this, "Mã tài xế đã tồn tại", Toast.LENGTH_SHORT).show();
            return;
        }

        long result = db.insertTaiXe(maTaiXe, tenTaiXe, sdtTaiXe);
        if (result != -1) {
            Toast.makeText(this, "Thêm tài xế thành công", Toast.LENGTH_SHORT).show();
            clearForm();
            loadTaiXeList();
        } else {
            Toast.makeText(this, "Lỗi khi thêm tài xế", Toast.LENGTH_SHORT).show();
        }
    }

    private void updateTaiXe() {
        if (selectedIndex == -1) {
            Toast.makeText(this, "Vui lòng chọn tài xế cần sửa", Toast.LENGTH_SHORT).show();
            return;
        }

        String maTaiXe = edtMaTaiXe.getText().toString().trim();
        String tenTaiXe = edtTenTaiXe.getText().toString().trim();
        String sdtTaiXe = edtSdtTaiXe.getText().toString().trim();

        if (maTaiXe.isEmpty() || tenTaiXe.isEmpty() || sdtTaiXe.isEmpty()) {
            Toast.makeText(this, "Vui lòng nhập đầy đủ thông tin", Toast.LENGTH_SHORT).show();
            return;
        }

        if (!isValidPhoneNumber(sdtTaiXe)) {
            Toast.makeText(this, "Số điện thoại phải bắt đầu bằng 0 và gồm 10 chữ số", Toast.LENGTH_SHORT).show();
            return;
        }

        int result = db.updateTaiXe(maTaiXe, tenTaiXe, sdtTaiXe);
        if (result > 0) {
            Toast.makeText(this, "Cập nhật tài xế thành công", Toast.LENGTH_SHORT).show();
            clearForm();
            loadTaiXeList();
        } else {
            Toast.makeText(this, "Lỗi khi cập nhật tài xế", Toast.LENGTH_SHORT).show();
        }
    }

    private void deleteTaiXe() {
        if (selectedIndex == -1) {
            Toast.makeText(this, "Vui lòng chọn tài xế cần xóa", Toast.LENGTH_SHORT).show();
            return;
        }

        String maTaiXe = edtMaTaiXe.getText().toString().trim();

        int result = db.deleteTaiXe(maTaiXe);
        if (result > 0) {
            Toast.makeText(this, "Xóa tài xế thành công", Toast.LENGTH_SHORT).show();
            clearForm();
            loadTaiXeList();
        } else {
            Toast.makeText(this, "Lỗi khi xóa tài xế", Toast.LENGTH_SHORT).show();
        }
    }

    private void loadTaiXeList() {
        taiXeList = db.getAllTaiXe();

        taiXeList.sort((t1, t2) -> t1.getMaTaiXe().compareToIgnoreCase(t2.getMaTaiXe()));

        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1);
        for (TaiXe taiXe : taiXeList) {
            adapter.add(taiXe.getMaTaiXe() + "\n" + taiXe.getTenTaiXe() + "\n" + taiXe.getSdt());
        }
        lvTaiXe.setAdapter(adapter);
        selectedIndex = -1;

        clearForm();
    }

    private void clearForm() {
        edtMaTaiXe.setText("");
        edtTenTaiXe.setText("");
        edtSdtTaiXe.setText("");
        edtMaTaiXe.setEnabled(true);
        selectedIndex = -1;
    }

    private void LoadUsername() {
        Intent intent = getIntent();
        username = intent.getStringExtra("username");
    }
}

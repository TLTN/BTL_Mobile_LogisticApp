package com.example.btl;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;

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


    private void Init()
    {
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

    private void Event()
    {
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
    }
    private void insertTaiXe() {
        String maTaiXe = edtMaTaiXe.getText().toString();
        String tenTaiXe = edtTenTaiXe.getText().toString();
        String sdtTaiXe = edtSdtTaiXe.getText().toString();

        if (maTaiXe.isEmpty() || tenTaiXe.isEmpty() || sdtTaiXe.isEmpty()) {
            Toast.makeText(this, "Vui lòng nhập đầy đủ thông tin", Toast.LENGTH_SHORT).show();
            return;
        }

        long result = db.insertTaiXe(maTaiXe, tenTaiXe, sdtTaiXe);
        if (result != -1) {
            Toast.makeText(this, "Thêm tài xế thành công", Toast.LENGTH_SHORT).show();
            loadTaiXeList();
        } else {
            Toast.makeText(this, "Lỗi khi thêm tài xế", Toast.LENGTH_SHORT).show();
        }
    }

    private void updateTaiXe() {
        String maTaiXe = edtMaTaiXe.getText().toString();
        String tenTaiXe = edtTenTaiXe.getText().toString();
        String sdtTaiXe = edtSdtTaiXe.getText().toString();

        if (maTaiXe.isEmpty() || tenTaiXe.isEmpty() || sdtTaiXe.isEmpty()) {
            Toast.makeText(this, "Vui lòng nhập đầy đủ thông tin", Toast.LENGTH_SHORT).show();
            return;
        }

        int result = db.updateTaiXe(maTaiXe, tenTaiXe, sdtTaiXe);
        if (result > 0) {
            Toast.makeText(this, "Cập nhật tài xế thành công", Toast.LENGTH_SHORT).show();
            loadTaiXeList();
        } else {
            Toast.makeText(this, "Lỗi khi cập nhật tài xế", Toast.LENGTH_SHORT).show();
        }
    }

    private void deleteTaiXe() {
        String maTaiXe = edtMaTaiXe.getText().toString();

        if (maTaiXe.isEmpty()) {
            Toast.makeText(this, "Vui lòng nhập mã tài xế cần xóa", Toast.LENGTH_SHORT).show();
            return;
        }

        int result = db.deleteTaiXe(maTaiXe);
        if (result > 0) {
            Toast.makeText(this, "Xóa tài xế thành công", Toast.LENGTH_SHORT).show();
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
            adapter.add(taiXe.getMaTaiXe() + " - " + taiXe.getTenTaiXe() + " - " + taiXe.getSdt());
        }
        lvTaiXe.setAdapter(adapter);
        selectedIndex = -1;
    }


    private void LoadUsername()
    {
        Intent intent = getIntent();
        username = intent.getStringExtra("username");
    }
}

package com.example.btl;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class thong_ke extends AppCompatActivity {
    private SQLiteHelper dbHelper;
    private TextView tvTongDon, tvTongXe, tvTongTaiXe;
    private ImageView btnBack;
    private ListView lvThongKe;
    private ArrayAdapter<String> thongKeAdapter;
    private List<PhanCong> danhSachPhanCong;
    private Spinner spnFilter;
    private String username;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.thong_ke);

        Init();
        LoadUsername();
        SetupSpinner();
        loadThongKe("DAY");
    }

    private void Init() {
        dbHelper = new SQLiteHelper(this);
        tvTongDon = findViewById(R.id.tvTongDon);
        tvTongXe = findViewById(R.id.tvTongXe);
        tvTongTaiXe = findViewById(R.id.tvTongTaiXe);
        lvThongKe = findViewById(R.id.lvThongKe);
        btnBack = findViewById(R.id.btnBack);
        spnFilter = findViewById(R.id.spinnerTimeFilter);
    }

    private void SetupSpinner() {
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.filter_options, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spnFilter.setAdapter(adapter);
        spnFilter.setSelection(0);

        spnFilter.setOnItemSelectedListener(new android.widget.AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(android.widget.AdapterView<?> adapterView, View view, int position, long l) {
                String filterType = "DAY";
                switch (position) {
                    case 0: filterType = "DAY"; break;
                    case 1: filterType = "WEEK"; break;
                    case 2: filterType = "MONTH"; break;
                    case 3: filterType = "YEAR"; break;
                }
                loadThongKe(filterType);
            }

            @Override
            public void onNothingSelected(android.widget.AdapterView<?> adapterView) {
                loadThongKe("DAY");
            }
        });

        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                navigateBack();
                Intent intent = new Intent(thong_ke.this, trang_chu.class);
                intent.putExtra("username", username);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                finish();
            }
        });
    }

    private void LoadUsername() {
        Intent intent = getIntent();
        username = intent.getStringExtra("username");
    }

    private void loadThongKe(String filterType) {
        int tongDon = dbHelper.countDonHang();
        int tongXe = dbHelper.countXeTai();
        int tongTaiXe = dbHelper.countTaiXe();

        tvTongDon.setText("Tổng số đơn hàng: " + tongDon);
        tvTongXe.setText("Tổng số xe tải: " + tongXe);
        tvTongTaiXe.setText("Tổng số tài xế: " + tongTaiXe);

        danhSachPhanCong = dbHelper.getAllPhanCong();
        thongKeAdapter = new ArrayAdapter<>(this, R.layout.list_item_thongke, R.id.tvThongKeItem);

        List<PhanCong> danhSachLoc = filterPhanCong(danhSachPhanCong, filterType);

        if (danhSachLoc != null && !danhSachLoc.isEmpty()) {
            for (PhanCong pc : danhSachLoc) {
                thongKeAdapter.add(pc.getTimestamp() + "\nĐơn: " + pc.getMaDon() + "\nXe: " + pc.getMaXe());
            }
        } else {
            thongKeAdapter.add("Không có dữ liệu phân công");
        }

        lvThongKe.setAdapter(thongKeAdapter);
    }

    private List<PhanCong> filterPhanCong(List<PhanCong> danhSach, String type) {
        if (type.equals("ALL")) return danhSach;

        List<PhanCong> ketQua = new ArrayList<>();
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");

        Calendar cal = Calendar.getInstance();
        Date now = cal.getTime();

        for (PhanCong pc : danhSach) {
            try {
                Date date = sdf.parse(pc.getTimestamp());

                if (type.equals("DAY")) {
                    Calendar c1 = Calendar.getInstance();
                    c1.setTime(date);
                    if (isSameDay(c1, cal)) {
                        ketQua.add(pc);
                    }
                } else if (type.equals("WEEK")) {
                    long diff = now.getTime() - date.getTime();
                    if (diff <= 7L * 24 * 60 * 60 * 1000) {
                        ketQua.add(pc);
                    }
                } else if (type.equals("MONTH")) {
                    Calendar c1 = Calendar.getInstance();
                    c1.setTime(date);
                    if (c1.get(Calendar.MONTH) == cal.get(Calendar.MONTH) && c1.get(Calendar.YEAR) == cal.get(Calendar.YEAR)) {
                        ketQua.add(pc);
                    }
                } else if (type.equals("YEAR")) {
                    Calendar c1 = Calendar.getInstance();
                    c1.setTime(date);
                    if (c1.get(Calendar.YEAR) == cal.get(Calendar.YEAR)) {
                        ketQua.add(pc);
                    }
                }

            } catch (ParseException e) {
                e.printStackTrace();
            }
        }

        return ketQua;
    }

    private boolean isSameDay(Calendar cal1, Calendar cal2) {
        return cal1.get(Calendar.YEAR) == cal2.get(Calendar.YEAR)
                && cal1.get(Calendar.DAY_OF_YEAR) == cal2.get(Calendar.DAY_OF_YEAR);
    }

    void navigateBack() {
        startActivity(new Intent(thong_ke.this, trang_chu.class));
        finish();
    }
}

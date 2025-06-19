package com.example.btl;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.button.MaterialButton;
import com.hivemq.client.mqtt.MqttClient;
import com.hivemq.client.mqtt.MqttGlobalPublishFilter;
import com.hivemq.client.mqtt.datatypes.MqttQos;
import com.hivemq.client.mqtt.mqtt3.Mqtt3AsyncClient;

import java.nio.charset.StandardCharsets;
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
    final String mqtt_server = "f4204c2525e9427398a6c66a28d11336.s1.eu.hivemq.cloud";
    final int mqtt_port = 8883;
    final String mqtt_username = "TLTNChanh";
    final String mqtt_password = "TLTNChanh2106";
    final String topicPublish = "esp32/Mobile_number";
    final String myTopic = "esp32/TX_number";
    final String myTopic3 = "esp32/RX_number";
    Mqtt3AsyncClient mqttClient;
    int count = 0;

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
        connectMQTT();
    }

    private void Init() {
        db = new SQLiteHelper(this);

        spinnerDonHang = findViewById(R.id.spinnerDonHang);
        spinnerXeTai = findViewById(R.id.spinnerXeTai);
        btnGan = findViewById(R.id.btnGan);
        btnBack = findViewById(R.id.btnBack);
        lvGan = findViewById(R.id.lvGan);

        mqttClient = MqttClient.builder()
                .useMqttVersion3()
                .serverHost(mqtt_server)
                .serverPort(mqtt_port)
                .sslWithDefaultConfig()
                .identifier("AndroidClient_" + System.currentTimeMillis())
                .buildAsync();
    }

    private void Event() {
        btnGan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String selectedDonHang = (String) spinnerDonHang.getSelectedItem();
                String selectedXeTai = (String) spinnerXeTai.getSelectedItem();

                if (selectedDonHang != null && selectedXeTai != null) {
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
                    publishMessage(selectedDonHang);
                    Toast.makeText(quan_ly.this, "Đã gửi mã đơn hàng: " + selectedDonHang, Toast.LENGTH_SHORT).show();
                }
            }
        });

        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(quan_ly.this, trang_chu.class);
                intent.putExtra("username", username);
                intent.putExtra("count", count);
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
                        } else {
                            Toast.makeText(quan_ly.this, "Không thể xóa phân công này", Toast.LENGTH_SHORT).show();
                        }
                    }
                })
                .setNegativeButton("Hủy", null)
                .show();
    }

    private void connectMQTT() {
        mqttClient.connectWith()
                .simpleAuth()
                .username(mqtt_username)
                .password(mqtt_password.getBytes(StandardCharsets.UTF_8))
                .applySimpleAuth()
                .send()
                .whenComplete((connAck, throwable) -> {
                    if (throwable == null) {
                        Log.d("MQTT", "Kết nối MQTT thành công");
                        runOnUiThread(() -> Toast.makeText(quan_ly.this, "MQTT connected", Toast.LENGTH_SHORT).show());
                        subscribeToTopics();
                    } else {
                        Log.e("MQTT", "Lỗi kết nối MQTT: " + throwable.getMessage());
                        runOnUiThread(() -> Toast.makeText(quan_ly.this, "MQTT connection failed", Toast.LENGTH_SHORT).show());
                    }
                });
    }

    private void publishMessage(String msg) {
        if (mqttClient.getState().isConnected()) {
            mqttClient.publishWith()
                    .topic(myTopic)
                    .qos(MqttQos.AT_LEAST_ONCE)
                    .payload(msg.getBytes(StandardCharsets.UTF_8))
                    .send();
        } else {
            Toast.makeText(this, "MQTT chưa kết nối", Toast.LENGTH_SHORT).show();
            Log.e("MQTT", "Không thể gửi, MQTT chưa kết nối");
        }
    }

    private void subscribeToTopics() {
        mqttClient.publishes(MqttGlobalPublishFilter.ALL, publish -> {
            String topic = publish.getTopic().toString();
            String msg = new String(publish.getPayloadAsBytes(), StandardCharsets.UTF_8);
            Log.d("MQTT", "Nhận từ topic: " + topic + ", message: " + msg);

            runOnUiThread(() -> {
                if (topic.equals(myTopic)) {
                    xuLyTX(msg);
                } else if (topic.equals(myTopic3)) {
                    xuLyRX(msg);
                }
            });
        });

        mqttClient.subscribeWith().topicFilter(myTopic).qos(MqttQos.AT_LEAST_ONCE).send();
        mqttClient.subscribeWith().topicFilter(myTopic3).qos(MqttQos.AT_LEAST_ONCE).send();
    }

    private void xuLyTX(String msg) {
        String timestamp = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss", Locale.getDefault()).format(new Date());
        db.insertPhanCong(msg, "1", timestamp);
        db.updateTruckStatus("1", "Đang hoạt động");
        Toast.makeText(this, "TX: " + msg, Toast.LENGTH_SHORT).show();
    }

    private void xuLyRX(String msg) {
        db.updateTruckStatus("1", "Chờ điều phối");
        count++;
        Toast.makeText(this, "RX: " + msg, Toast.LENGTH_SHORT).show();
    }
}

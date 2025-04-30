package com.example.btl;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

public class trang_dang_ky extends AppCompatActivity {
    EditText edtUsername, edtPassword, edtConfirmPassword, edtPhone;
    Button btnRegister;
    TextView txtLogin;
    SQLiteHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.trang_dang_ky);

        dbHelper = new SQLiteHelper(this);
        dbHelper.getWritableDatabase();
        Init();
        Event();
    }

    private void Init() {
        edtUsername = findViewById(R.id.edtUsername);
        edtPassword = findViewById(R.id.edtPassword);
        edtConfirmPassword = findViewById(R.id.edtConfirmPassword);
        edtPhone = findViewById(R.id.edtPhone);
        btnRegister = findViewById(R.id.btnRegister);
        txtLogin = findViewById(R.id.txtLogin);
    }

    private boolean isUsernameExist(String username) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = null;
        int count = 0;

        try {
            String query = "SELECT COUNT(*) FROM Users WHERE username = ?";
            cursor = db.rawQuery(query, new String[]{username});
            if (cursor.moveToFirst()) {
                count = cursor.getInt(0);
            }
        } catch (Exception e) {
            Log.e("SQLite", "Lỗi kiểm tra username", e);
        } finally {
            if (cursor != null) cursor.close();
            db.close();
        }

        return count > 0;
    }

    private boolean isValidPhoneNumber(String phone) {
        return phone.matches("^0\\d{9}$");
    }

    private void registerUser(String username, String password, String phone) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        try {
            ContentValues values = new ContentValues();
            values.put("username", username);
            values.put("password", password);
            values.put("phone", phone);

            long result = db.insertOrThrow("Users", null, values);

            if (result != -1) {
                new android.app.AlertDialog.Builder(this)
                        .setTitle("Đăng ký thành công")
                        .setMessage("Bạn có muốn quay lại trang đăng nhập không?")
                        .setPositiveButton("Có", (dialog, which) -> {
                            Intent intent = new Intent(trang_dang_ky.this, trang_dang_nhap.class);
                            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                            startActivity(intent);
                            finish();
                        })
                        .setNegativeButton("Không", (dialog, which) -> {
                            Intent intent = new Intent(trang_dang_ky.this, MainActivity.class);
                            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                            startActivity(intent);
                            finish();
                        })
                        .setCancelable(false)
                        .show();
            } else {
                Toast.makeText(this, "Đăng ký không thành công", Toast.LENGTH_SHORT).show();
            }
        } catch (Exception e) {
            Toast.makeText(this, "Lỗi: " + e.getMessage(), Toast.LENGTH_LONG).show();
            Log.e("SQLite", "Lỗi khi đăng ký", e);
        } finally {
            db.close();
        }
    }


    private void Event() {
        txtLogin.setOnClickListener(view -> {
            Intent intent = new Intent(trang_dang_ky.this, trang_dang_nhap.class);
            startActivity(intent);
        });

        btnRegister.setOnClickListener(view -> {
            String username = edtUsername.getText().toString().trim();
            String password = edtPassword.getText().toString().trim();
            String confirmPassword = edtConfirmPassword.getText().toString().trim();
            String phone = edtPhone.getText().toString().trim();

            if (TextUtils.isEmpty(username)) {
                Toast.makeText(this, "Tên đăng nhập không được trống", Toast.LENGTH_SHORT).show();
                return;
            }

            if (isUsernameExist(username)) {
                Toast.makeText(this, "Tên đăng nhập đã tồn tại", Toast.LENGTH_SHORT).show();
                return;
            }

            if (password.length() < 6) {
                Toast.makeText(this, "Mật khẩu phải có ít nhất 6 ký tự", Toast.LENGTH_SHORT).show();
                return;
            }

            if (!password.equals(confirmPassword)) {
                Toast.makeText(this, "Mật khẩu và xác nhận mật khẩu không khớp", Toast.LENGTH_SHORT).show();
                return;
            }

            if (!isValidPhoneNumber(phone)) {
                Toast.makeText(this, "Số điện thoại không hợp lệ", Toast.LENGTH_SHORT).show();
                return;
            }

            registerUser(username, password, phone);
        });
    }
}

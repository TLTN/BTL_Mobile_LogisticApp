package com.example.btl;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.text.InputType;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

public class trang_dang_nhap extends AppCompatActivity {
    EditText edtUser, edtPass;
    Button btnLogin;
    TextView txtRegister;
    ImageView imgTogglePass;
    SQLiteHelper dbHelper;

    private void Init() {
        edtUser = findViewById(R.id.edtUser);
        edtPass = findViewById(R.id.edtPass);
        btnLogin = findViewById(R.id.btnLogin);
        txtRegister = findViewById(R.id.txtRegister);
        imgTogglePass = findViewById(R.id.imgTogglePass);
        dbHelper = new SQLiteHelper(this);
    }

    private void Event() {
        txtRegister.setOnClickListener(view -> {
            Intent intent = new Intent(trang_dang_nhap.this, trang_dang_ky.class);
            startActivity(intent);
        });

        imgTogglePass.setOnClickListener(new View.OnClickListener() {
            boolean isPasswordVisible = false;

            @Override
            public void onClick(View v) {
                if (isPasswordVisible) {
                    edtPass.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                    imgTogglePass.setImageResource(R.drawable.pass);
                } else {
                    edtPass.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
                    imgTogglePass.setImageResource(R.drawable.passp);
                }
                edtPass.setSelection(edtPass.getText().length());
                isPasswordVisible = !isPasswordVisible;
            }
        });

        btnLogin.setOnClickListener(view -> {
            String username = edtUser.getText().toString().trim();
            String password = edtPass.getText().toString().trim();

            if (TextUtils.isEmpty(username) || TextUtils.isEmpty(password)) {
                Toast.makeText(this, "Vui lòng nhập đầy đủ thông tin", Toast.LENGTH_SHORT).show();
                return;
            }

            if (!isUsernameExist(username)) {
                Toast.makeText(this, "Tên đăng nhập không tồn tại", Toast.LENGTH_SHORT).show();
                return;
            }

            if (isLoginValid(username, password)) {
                new AlertDialog.Builder(this)
                        .setTitle("Đăng nhập thành công")
                        .setMessage("Chuyển đến trang chính?")
                        .setPositiveButton("OK", (dialog, which) -> {
                            Intent intent = new Intent(trang_dang_nhap.this, trang_chu.class);
                            intent.putExtra("username", username);
                            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                            startActivity(intent);
                            finish();
                        })
                        .setCancelable(false)
                        .show();
            } else {
                Toast.makeText(this, "Mật khẩu không đúng", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private boolean isUsernameExist(String username) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM Users WHERE username = ?", new String[]{username});
        boolean exists = cursor.moveToFirst();
        cursor.close();
        db.close();
        return exists;
    }

    private boolean isLoginValid(String username, String password) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM Users WHERE username = ? AND password = ?", new String[]{username, password});
        boolean valid = cursor.moveToFirst();
        cursor.close();
        db.close();
        return valid;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.trang_dang_nhap);
        Init();
        Event();
    }
}

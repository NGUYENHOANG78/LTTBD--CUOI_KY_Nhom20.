package com.example.movie.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.movie.R;

public class LoginActivity extends AppCompatActivity {

    private EditText editTextEmail, editTextPassword;
    private Button btnRegister;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        editTextEmail = findViewById(R.id.editTextEmail);
        editTextPassword = findViewById(R.id.editTextPassword);
        btnRegister = findViewById(R.id.btnRegister);

        // Kiểm tra xem người dùng đã đăng nhập trước đó chưa
        SharedPreferences prefs = getSharedPreferences("user_data", MODE_PRIVATE);
        String savedEmail = prefs.getString("email", "");

        if (!savedEmail.isEmpty()) {
            // Nếu đã lưu email trong SharedPreferences, chuyển thẳng đến MainActivity
            String name = prefs.getString(savedEmail + "_name", "");
            String age = prefs.getString(savedEmail + "_age", "");
            String gender = prefs.getString(savedEmail + "_gender", "");

            // Nếu đã có thông tin người dùng thì vào MainActivity luôn
            if (!name.isEmpty() && !age.isEmpty() && !gender.isEmpty()) {
                Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                startActivity(intent);
                finish();
                return;
            }
        }

        // Nếu không có thông tin người dùng thì xử lý đăng nhập như bình thường
        btnRegister.setOnClickListener(v -> {
            String email = editTextEmail.getText().toString().trim();
            String password = editTextPassword.getText().toString().trim();

            if (!email.isEmpty() && !password.isEmpty()) {
                SharedPreferences.Editor editor = prefs.edit();
                editor.putString("email", email); // lưu email hiện tại
                editor.apply();

                // Kiểm tra xem người dùng đã có thông tin chưa
                boolean hasInfo = prefs.contains(email + "_name") &&
                        prefs.contains(email + "_age") &&
                        prefs.contains(email + "_gender");

                if (hasInfo) {
                    // Nếu có thông tin, vào MainActivity
                    Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                    startActivity(intent);
                } else {
                    // Nếu chưa có thông tin, sang RegisterInfoActivity để nhập thông tin
                    Intent intent = new Intent(LoginActivity.this, RegisterInfoActivity.class);
                    intent.putExtra("email", email);
                    startActivity(intent);
                }
                finish();
            } else {
                Toast.makeText(this, "Vui lòng nhập email và mật khẩu", Toast.LENGTH_SHORT).show();
            }
        });
    }
}

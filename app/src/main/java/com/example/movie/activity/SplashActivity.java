package com.example.movie.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.movie.R;
import com.example.movie.constant.AboutUsConfig;

public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_splash);
        TextView tvAboutUsTitle = findViewById(R.id.tv_about_us_title);
        TextView tvAboutUsSlogan = findViewById(R.id.tv_about_us_slogan);
        tvAboutUsTitle.setText(AboutUsConfig.ABOUT_US_TITLE);
        tvAboutUsSlogan.setText(AboutUsConfig.ABOUT_US_SLOGAN);

        new Handler().postDelayed(() -> {
            SharedPreferences preferences = getSharedPreferences("user_data", MODE_PRIVATE);
            String name = preferences.getString("name", null);
            String age = preferences.getString("age", null);
            String gender = preferences.getString("gender", null);
            String email = preferences.getString("email", null);

            if (name != null && age != null && gender != null && email != null) {
                // Nếu đã có dữ liệu => bỏ qua RegisterInfoActivity
                Intent intent = new Intent(SplashActivity.this, MainActivity.class);
                intent.putExtra("name", name);
                intent.putExtra("age", age);
                intent.putExtra("gender", gender);
                intent.putExtra("email", email);
                startActivity(intent);
            } else {
                // Chưa có thông tin => vào LoginActivity
                startActivity(new Intent(SplashActivity.this, LoginActivity.class));
            }

            finish();
        }, 500);
    }
}

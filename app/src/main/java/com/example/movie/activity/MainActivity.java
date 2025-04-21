package com.example.movie.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.viewpager2.widget.ViewPager2;

import com.example.movie.R;
import com.example.movie.adapter.MyViewPagerAdapter;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MainActivity extends AppCompatActivity {

    private BottomNavigationView mBottomNavigationView;
    private ViewPager2 mViewPager2;
    private TextView tvTitle;
    private ImageView userAvatar;
    private TextView userName, userEmail;
    private Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Ánh xạ view
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        tvTitle = findViewById(R.id.tv_title);
        mBottomNavigationView = findViewById(R.id.bottom_navigation);
        mViewPager2 = findViewById(R.id.viewpager_2);
        userAvatar = findViewById(R.id.user_avatar);
        userName = findViewById(R.id.user_name);
        userEmail = findViewById(R.id.user_email);

        // Lấy dữ liệu từ SharedPreferences
        SharedPreferences preferences = getSharedPreferences("user_data", MODE_PRIVATE);
        String email = preferences.getString("email", "Email người dùng");
        String name = preferences.getString(email + "_name", "Tên người dùng");

        userName.setText(name);
        userEmail.setText(email);
        userAvatar.setImageResource(R.drawable.ic_avatar_placeholder); // Avatar tĩnh

        // ViewPager setup
        MyViewPagerAdapter myViewPagerAdapter = new MyViewPagerAdapter(this);
        mViewPager2.setAdapter(myViewPagerAdapter);

        mViewPager2.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                switch (position) {
                    case 0:
                        mBottomNavigationView.setSelectedItemId(R.id.nav_home);
                        tvTitle.setText(R.string.nav_home);
                        break;
                    case 1:
                        mBottomNavigationView.setSelectedItemId(R.id.nav_favorite);
                        tvTitle.setText(R.string.nav_favorite);
                        break;
                    case 2:
                        mBottomNavigationView.setSelectedItemId(R.id.nav_history);
                        tvTitle.setText(R.string.nav_history);
                        break;
                }
            }
        });

        mBottomNavigationView.setOnItemSelectedListener(item -> {
            int id = item.getItemId();
            if (id == R.id.nav_home) {
                mViewPager2.setCurrentItem(0);
                return true;
            } else if (id == R.id.nav_favorite) {
                mViewPager2.setCurrentItem(1);
                return true;
            } else if (id == R.id.nav_history) {
                mViewPager2.setCurrentItem(2);
                return true;
            }
            return false;
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_logout) {
            // Xoá dữ liệu đăng ký khỏi SharedPreferences khi đăng xuất
            SharedPreferences preferences = getSharedPreferences("user_data", MODE_PRIVATE);
            preferences.edit().clear().apply();

            Toast.makeText(this, "Đăng xuất...", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(this, LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            return true;
        } else if (id == R.id.action_add_account) {
            // Xóa dữ liệu hiện tại trong SharedPreferences (nếu có)
            SharedPreferences preferences = getSharedPreferences("user_data", MODE_PRIVATE);
            preferences.edit().clear().apply();

            // Quay lại màn hình đăng nhập
            Intent intent = new Intent(this, LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}

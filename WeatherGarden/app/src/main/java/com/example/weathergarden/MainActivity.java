package com.example.weathergarden;

import android.os.Bundle;
import android.view.MenuItem;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MainActivity extends AppCompatActivity {

    LinearLayout main;
    BottomNavigationView bottomNavigationView;

    private GardenFragment garden;
    private WeatherFragment weather;
    private SettingsFragment setting;

    private FragmentTransaction transaction;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        init(); // 객체 정의
        SettingListener();
        getSupportFragmentManager().beginTransaction().replace(R.id.main, weather )
                .commit();
    }
    private void init() {
        main = findViewById(R.id.main);
        bottomNavigationView = findViewById(R.id.bottom);

        garden = new GardenFragment();
        weather = new WeatherFragment();
        setting = new SettingsFragment();
    }
    private void SettingListener() {
        // 선택 리스너 등록
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                transaction = getSupportFragmentManager().beginTransaction();

                switch (item.getItemId() ) {
                    case R.id.weather:
                        transaction.replace(R.id.main, weather ).commit();
                        return true;
                    case R.id.garden:
                        transaction.replace(R.id.main, garden ).commit();
                        return true;
                    case R.id.settings:
                        transaction.replace(R.id.main, setting ).commit();
                        return true;
                    case R.id.notice:
                        transaction.replace(R.id.main, new NoticeFragment() ).commit();
                        return true;
                    case R.id.store:
                        transaction.replace(R.id.main, new StoreFragment() ).commit();
                        return true;
                }
                return false;
            }
        });
    }
}
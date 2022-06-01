package com.example.weathergarden;

import android.os.Bundle;
import android.view.MenuItem;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MainActivity extends AppCompatActivity {

    LinearLayout main;
    BottomNavigationView bottomNavigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        init(); // 객체 정의
        SettingListener();
        getSupportFragmentManager().beginTransaction().replace(R.id.main, new gardenFragment() )
                .commit();
    }
    private void init() {
        main = findViewById(R.id.main);
        bottomNavigationView = findViewById(R.id.bottom);
    }
    private void SettingListener() {
        // 선택 리스너 등록
        bottomNavigationView.setOnNavigationItemSelectedListener(new TabSelectedListener());
    }

    class TabSelectedListener implements BottomNavigationView.OnNavigationItemSelectedListener{
        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId() ) {
                case R.id.weather:
                    getSupportFragmentManager().beginTransaction().replace(R.id.main, new weatherFragment() )
                            .commit();
                    return true;
            }
            switch (item.getItemId() ) {
                case R.id.garden:
                    getSupportFragmentManager().beginTransaction().replace(R.id.main, new gardenFragment() )
                            .commit();
                    return true;
            }
            switch (item.getItemId() ) {
                case R.id.settings:
                    getSupportFragmentManager().beginTransaction().replace(R.id.main, new settingsFragment() )
                            .commit();
                    return true;
            }
            switch (item.getItemId() ) {
                case R.id.notice:
                    getSupportFragmentManager().beginTransaction().replace(R.id.main, new noticeFragment() )
                            .commit();
                    return true;
            }
            switch (item.getItemId() ) {
                case R.id.store:
                    getSupportFragmentManager().beginTransaction().replace(R.id.main, new storeFragment() )
                            .commit();
                    return true;
            }
            return false;
        }
    }

}
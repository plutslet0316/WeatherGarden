package com.example.weathergarden;

import android.os.Bundle;
import android.view.MenuItem;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentTransaction;

import com.example.weathergarden.weather.WeatherInfo;
import com.example.weathergarden.weather.WeatherProc;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MainActivity extends AppCompatActivity {

    LinearLayout main;
    BottomNavigationView bottomNavigationView;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

//        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
//        FragmentTulip fragmentTulip = new FragmentTulip();
//        transaction.replace(R.id.frameLayout, fragmentTulip);
//        transaction.commit();


        init(); // 객체 정의
        SettingListener();
        getSupportFragmentManager().beginTransaction().replace(R.id.main, new weatherFragment() )
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
                case R.id.garden:
                    getSupportFragmentManager().beginTransaction().replace(R.id.main, new gardenTestFragment() )
                            .commit();
                    return true;
                case R.id.settings:
                    getSupportFragmentManager().beginTransaction().replace(R.id.main, new settingsFragment() )
                            .commit();
                    return true;
                case R.id.book:
                    getSupportFragmentManager().beginTransaction().replace(R.id.main, new bookFragment() )
                            .commit();
                    return true;

            }
            return false;
        }
    }

}
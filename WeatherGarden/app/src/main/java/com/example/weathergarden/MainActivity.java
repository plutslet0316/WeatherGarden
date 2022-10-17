package com.example.weathergarden;

import android.os.Bundle;
import android.view.MenuItem;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;

import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MainActivity extends AppCompatActivity {

    LinearLayout main;
    BottomNavigationView bottomNavigationView;

    private GardenFragment garden;
    private WeatherFragment weather;
    private SettingsFragment setting;
    private BookFragment book;

    private FragmentManager fragmentManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // FragmentTransaction transaction =
        // getSupportFragmentManager().beginTransaction();
        // FragmentTulip fragmentTulip = new FragmentTulip();
        // transaction.replace(R.id.frameLayout, fragmentTulip);
        // transaction.commit();

        init(); // 객체 정의
        SettingListener();
        fragmentManager.beginTransaction().show(weather).commitAllowingStateLoss();
    }

    private void init() {
        main = findViewById(R.id.main);
        bottomNavigationView = findViewById(R.id.bottom);
        fragmentManager = getSupportFragmentManager();

        weather = new WeatherFragment();
        garden = new GardenFragment();
        setting = new SettingsFragment();
        book = new BookFragment();

        fragmentManager.beginTransaction().add(R.id.main, weather).commit();
        // transaction.hide(weather).commit();

        fragmentManager.beginTransaction().add(R.id.main, garden).commit();
        // transaction.hide(garden).commit();

        fragmentManager.beginTransaction().add(R.id.main, setting).commit();
        // transaction.hide(setting).commit();

        fragmentManager.beginTransaction().add(R.id.main, book).commit();
        // transaction.hide(notice).commit();

    }

    private void allHideFragment() {
        if (weather != null)
            fragmentManager.beginTransaction().hide(weather).commit();
        if (garden != null)
            fragmentManager.beginTransaction().hide(garden).commit();
        if (setting != null)
            fragmentManager.beginTransaction().hide(setting).commit();
        if (book != null)
            fragmentManager.beginTransaction().hide(book).commit();
    }

    private void SettingListener() {
        // 선택 리스너 등록
        bottomNavigationView
                .setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                        allHideFragment();
                        switch (item.getItemId()) {
                            case R.id.weather:
                                fragmentManager.beginTransaction().show(weather).commit();
                                return true;
                            case R.id.garden:
                                fragmentManager.beginTransaction().show(garden).commit();
                                return true;
                            case R.id.settings:
                                fragmentManager.beginTransaction().show(setting).commit();
                                return true;
                            case R.id.book:
                                fragmentManager.beginTransaction().show(book).commit();
                                return true;
                        }
                        return false;
                    }
                });
    }
}
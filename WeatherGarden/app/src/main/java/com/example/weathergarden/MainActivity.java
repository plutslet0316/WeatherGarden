package com.example.weathergarden;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.unity3d.player.UnityFragment;
import com.unity3d.player.UnityPlayerActivity;

public class MainActivity extends AppCompatActivity {

    LinearLayout main;
    BottomNavigationView bottomNavigationView;

    private GardenFragment garden;
    private WeatherFragment weather;
    private SettingsFragment setting;
    private NoticeFragment notice;
    private StoreFragment store;

    private FragmentManager fragmentManager;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

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
        notice = new NoticeFragment();
        store = new StoreFragment();

        fragmentManager.beginTransaction().add(R.id.main, weather).commit();
//        transaction.hide(weather).commit();

        fragmentManager.beginTransaction().add(R.id.main, garden).commit();
//        transaction.hide(garden).commit();

        fragmentManager.beginTransaction().add(R.id.main, setting).commit();
//        transaction.hide(setting).commit();

        fragmentManager.beginTransaction().add(R.id.main, notice).commit();
//        transaction.hide(notice).commit();

        fragmentManager.beginTransaction().add(R.id.main, store).commit();
//        transaction.hide(store).commit();

    }

    private void allHideFragment(){
        if (weather != null) fragmentManager.beginTransaction().hide(weather).commit();
        if (garden != null) fragmentManager.beginTransaction().hide(garden).commit();
        if (setting != null) fragmentManager.beginTransaction().hide(setting).commit();
        if (notice != null) fragmentManager.beginTransaction().hide(notice).commit();
        if (store != null) fragmentManager.beginTransaction().hide(store).commit();
    }

    private void SettingListener() {
        // 선택 리스너 등록
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                allHideFragment();
                switch (item.getItemId() ) {
                    case R.id.weather:
                        fragmentManager.beginTransaction().show(weather).commit();
                        return true;
                    case R.id.garden:
                        fragmentManager.beginTransaction().show(garden).commit();
                        return true;
                    case R.id.settings:
                        fragmentManager.beginTransaction().show(setting).commit();
                        return true;
                    case R.id.notice:
                        fragmentManager.beginTransaction().show(notice).commit();
                        return true;
                    case R.id.store:
                        fragmentManager.beginTransaction().show(store).commit();
                        return true;
                }
                return false;
            }
        });
    }
}
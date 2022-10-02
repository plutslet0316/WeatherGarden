package com.example.weathergarden;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewTreeObserver;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.splashscreen.SplashScreen;

import com.example.weathergarden.weather.GPS;
import com.example.weathergarden.weather.WeatherProc;
import com.unity3d.player.UnityPlayerActivity;


public class SplashTestActivity extends Activity {
    GPS gps;
    boolean isAllow;
    SplashScreen splashScreen;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_test);

        isAllow = false;
        // 이건 스플레시 화면이 뜨고 나서 좌표값을 가져오고,
        // 이후 모든 처리가 끝나면 다음 화면으로 넘어가는 부분입니다.
        gps = new GPS(this);
        WeatherProc weatherProc = new WeatherProc(this);

        OnCheckPermission();

        final View content = findViewById(android.R.id.content);
        content.getViewTreeObserver().addOnPreDrawListener(
                new ViewTreeObserver.OnPreDrawListener() {
                    @Override
                    public boolean onPreDraw() {
                        // Check if the initial data is ready.
                        if (gps.isGetLocation) {
                            Log.d("SplashActivity", "위치 가져옴");
                            weatherProc.getWeather();

                            Intent i = new Intent(SplashTestActivity.this, MainActivity.class);
                            startActivity(i);
                            finish();
                            content.getViewTreeObserver().removeOnPreDrawListener(this);
                            return true;
                        } else if (isAllow) {
                            gps.findLocation();
                            isAllow = false;
                            return false;
                        } else {
                            // The content is not ready; suspend.
                            return false;
                        }
                    }
                });
    }

    @Override
    public int checkPermission(String permission, int pid, int uid) {
        return super.checkPermission(permission, pid, uid);
    }

    public void OnCheckPermission() {
        if (ActivityCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED ||
                ActivityCompat.checkSelfPermission(this,
                        Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            String[] permissionList = new String[] {
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION };


            ActivityCompat.requestPermissions((Activity) this,
                    permissionList,
                    1);

        } else {
            isAllow = true;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
            @NonNull int[] grantResults) {

        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        isAllow = true;
    }
}

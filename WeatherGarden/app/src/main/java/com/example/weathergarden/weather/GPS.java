package com.example.weathergarden.weather;

import android.Manifest;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.ViewModel;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.CancellationTokenSource;
import com.google.android.gms.tasks.OnCanceledListener;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.gson.Gson;

public class GPS extends ViewModel {
    Context context;

    SharedPreferences preferences;
    SharedPreferences.Editor editor;

    FusedLocationProviderClient fusedLocationClient;

    public boolean isGetLocation;

    public GPS(Context context) {
        this.context = context;

        preferences = context.getSharedPreferences("player_data", Context.MODE_PRIVATE);
        editor = preferences.edit();

        isGetLocation = false;
    }

    private void setGridXY(Double x, Double y) {
        try {
            Double RE = 6371.00877; // 지구 반경(km)
            Double GRID = 5.0; // 격자 간격(km)
            Double SLAT1 = 30.0; // 투영 위도1(degree)
            Double SLAT2 = 60.0; // 투영 위도2(degree)
            Double OLON = 126.0; // 기준점 경도(degree)
            Double OLAT = 38.0; // 기준점 위도(degree)
            int XO = 43; // 기준점 X좌표(GRID)
            int YO = 136; // 기준점 Y좌표(GRID)
            Double DEGRAD = Math.PI / 180.0;
            Double re = RE / GRID;
            Double slat1 = SLAT1 * DEGRAD;
            Double slat2 = SLAT2 * DEGRAD;
            Double olon = OLON * DEGRAD;
            Double olat = OLAT * DEGRAD;

            Double sn = Math.tan(Math.PI * 0.25 + slat2 * 0.5) / Math.tan(Math.PI * 0.25 + slat1 * 0.5);
            sn = Math.log(Math.cos(slat1) / Math.cos(slat2)) / Math.log(sn);
            Double sf = Math.tan(Math.PI * 0.25 + slat1 * 0.5);
            sf = Math.pow(sf, sn) * Math.cos(slat1) / sn;
            Double ro = Math.tan(Math.PI * 0.25 + olat * 0.5);
            ro = re * sf / Math.pow(ro, sn);

            Double ra = Math.tan(Math.PI * 0.25 + (x) * DEGRAD * 0.5);
            ra = re * sf / Math.pow(ra, sn);
            Double theta = y * DEGRAD - olon;
            if (theta > Math.PI)
                theta -= 2.0 * Math.PI;
            if (theta < -Math.PI)
                theta += 2.0 * Math.PI;
            theta *= sn;

            int nx = (int) (ra * Math.sin(theta) + XO + 0.5);
            int ny = (int) (ro - ra * Math.cos(theta) + YO + 0.5);

            Gson gson = new Gson();
            LocationData locationData = new LocationData(x, y, nx, ny);
            String locationString = gson.toJson(locationData);
            Log.d("Gps", locationString);

            editor.putString("location_data", locationString);
            editor.apply();
        } finally {
            isGetLocation = true;
            Log.d("Gps", "좌표값 변환 완료");
        }
    }

    public void findLocation() {
        Log.d("Gps", "좌표값 가져오기 시작");

        try {

            int hasFineLocationPermission = ContextCompat.checkSelfPermission(context,
                    Manifest.permission.ACCESS_FINE_LOCATION);
            int hasCoarseLocationPermission = ContextCompat.checkSelfPermission(context,
                    Manifest.permission.ACCESS_COARSE_LOCATION);

            if (hasFineLocationPermission == PackageManager.PERMISSION_GRANTED &&
                    hasCoarseLocationPermission == PackageManager.PERMISSION_GRANTED) {

                ;
            }

            fusedLocationClient = LocationServices.getFusedLocationProviderClient(context);

            CancellationTokenSource cts = new CancellationTokenSource();

            fusedLocationClient.getCurrentLocation(LocationRequest.PRIORITY_HIGH_ACCURACY, cts.getToken())
                    .addOnSuccessListener(new OnSuccessListener<Location>() {
                        @Override
                        public void onSuccess(Location location) {
                            // Log.d("Gps", (location != null) + "");
                            isGetLocation = true;
                            if (location != null) {
                                setGridXY(location.getLatitude(), location.getLongitude());
                            }
                        }
                    })
                    .addOnCanceledListener(new OnCanceledListener() {
                        @Override
                        public void onCanceled() {
                            Log.d("Gps", "취소됨");
                            setGridXY(37.5666805, 126.9784147);
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Log.d("Gps", "실패: " + e.getMessage());
                            setGridXY(37.5666805, 126.9784147);
                        }
                    })
                    .addOnCompleteListener(new OnCompleteListener<Location>() {
                        @Override
                        public void onComplete(@NonNull Task<Location> task) {
                        }
                    });

            /*
             * fusedLocationClient.getLastLocation()
             * .addOnSuccessListener( new OnSuccessListener<Location>() {
             * 
             * @Override
             * public void onSuccess(Location location) {
             * Log.d("Gps", (location != null) + "");
             * if (location != null) {
             * getGridXY(location.getLatitude(), location.getLongitude());
             * }
             * }
             * })
             * .addOnCanceledListener(new OnCanceledListener() {
             * 
             * @Override
             * public void onCanceled() {
             * Log.d("Gps", "취소됨");
             * }
             * })
             * .addOnFailureListener(new OnFailureListener() {
             * 
             * @Override
             * public void onFailure(@NonNull Exception e) {
             * Log.d("Gps", "실패: " + e.getMessage());
             * }
             * });
             */
        } catch (Exception e) {
            Log.d("Gps", e.getMessage());
        }
    }

}

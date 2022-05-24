package com.example.weathergarden.weather;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.Activity;
import android.app.Service;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import java.util.HashMap;

public class GPS implements LocationListener {
    Location location;
    int nCurrentPermission = 0;
    static final int PERMISSIONS_REQUEST = 0x0000001;
    Context context;
    LocationManager lm;

    public GPS(Context context) {
        this.context = context;
    }


    public HashMap<String, Integer> getGridXY(Double x, Double y) {
        Log.d("Gps", "좌표값 시작 완료");
        
        Double RE = 6371.00877;     // 지구 반경(km)
        Double GRID = 5.0;          // 격자 간격(km)
        Double SLAT1 = 30.0;        // 투영 위도1(degree)
        Double SLAT2 = 60.0;        // 투영 위도2(degree)
        Double OLON = 126.0;        // 기준점 경도(degree)
        Double OLAT = 38.0;         // 기준점 위도(degree)
        int XO = 43;             // 기준점 X좌표(GRID)
        int YO = 136;            // 기준점 Y좌표(GRID)
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
        if (theta > Math.PI) theta -= 2.0 * Math.PI;
        if (theta < -Math.PI) theta += 2.0 * Math.PI;
        theta *= sn;

        int nx = (int) (ra * Math.sin(theta) + XO + 0.5);
        int ny = (int) (ro - ra * Math.cos(theta) + YO + 0.5);

        HashMap<String, Integer> gridXY = new HashMap<String, Integer>() {{
            put("nx", nx);
            put("ny", ny);
        }};

        Log.d("Gps", "좌표값 변환 완료");
        
        return gridXY;
    }

    public HashMap<String, Integer> getLocation() {
        lm = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        Log.d("Gps", "좌표값 가져오기 시작");
        HashMap<String, Integer> gridXY = null;

        boolean isGPSEnabled = lm.isProviderEnabled(LocationManager.GPS_PROVIDER);
        boolean isNetworkEnabled = lm.isProviderEnabled(LocationManager.NETWORK_PROVIDER);

        Log.d("Gps", isGPSEnabled + " " + isNetworkEnabled);
        try {
            OnCheckPermission();

            int hasFineLocationPermission = ContextCompat.checkSelfPermission(context,
                    Manifest.permission.ACCESS_FINE_LOCATION);
            int hasCoarseLocationPermission = ContextCompat.checkSelfPermission(context,
                    Manifest.permission.ACCESS_COARSE_LOCATION);


            if (hasFineLocationPermission == PackageManager.PERMISSION_GRANTED &&
                    hasCoarseLocationPermission == PackageManager.PERMISSION_GRANTED) {

                ;
            } else
                return null;

            String locationProvider = LocationManager.NETWORK_PROVIDER;
            lm.requestLocationUpdates(locationProvider, 0, 0, this);

            location = lm.getLastKnownLocation(locationProvider);
            Log.d("Gps", (location == null) + "");
            Log.d("Gps", "좌표값 가져옴" + location.getLatitude() + " " + location.getLongitude());

            gridXY = getGridXY(location.getLatitude(), location.getLongitude());

            Log.d("Gps", "좌표값 가져오기 완료");
        } catch (Exception e){
            Log.d("Gps", e.getMessage());
        }
        
        return gridXY;
    }
    public void OnCheckPermission() {
        Toast.makeText(context, "테스트", Toast.LENGTH_SHORT).show();

        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED ||
                ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            Toast.makeText(context, "테스트1", Toast.LENGTH_SHORT).show();
//            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_FINE_LOCATION))
//            {
            Toast.makeText(context, "앱 실행을 위해서는 권한을 설정해야 합니다", Toast.LENGTH_SHORT).show();

            String[] a = new String[]{
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION};
            Toast.makeText(context, a[0], Toast.LENGTH_SHORT).show();

            ActivityCompat.requestPermissions((Activity) context,
                    a,
                    PERMISSIONS_REQUEST);
            //}
        }
    }


    @Override
    public void onLocationChanged(@NonNull Location location) {

    }
}

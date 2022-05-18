package com.example.weathergarden;

import java.util.List;
import java.util.Locale;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Criteria;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import androidx.core.app.ActivityCompat;

public class MainActivity extends Activity implements LocationListener {

    TextView tv1 = null;
    TextView tv2 = null;

    //위치정보 객체
    LocationManager lm = null;
    //위치정보 장치 이름
    String provider = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


/**위치정보 객체를 생성한다.*/
        lm = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

/** 현재 사용가능한 위치 정보 장치 검색*/
//위치정보 하드웨어 목록
        Criteria c = new Criteria();
//최적의 하드웨어 이름을 리턴받는다.
        provider = lm.getBestProvider(c, true);

// 최적의 값이 없거나, 해당 장치가 사용가능한 상태가 아니라면,
//모든 장치 리스트에서 사용가능한 항목 얻기
        if (provider == null || !lm.isProviderEnabled(provider)) {
// 모든 장치 목록
            List<String> list = lm.getAllProviders();

            for (int i = 0; i < list.size(); i++) {
//장치 이름 하나 얻기
                String temp = list.get(i);

//사용 가능 여부 검사
                if (lm.isProviderEnabled(temp)) {
                    provider = temp;
                    break;
                }
            }
        }// (end if)위치정보 검색 끝

/**마지막으로  조회했던 위치 얻기*/
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        Location location = lm.getLastKnownLocation(provider);

        if (location == null) {
            Toast.makeText(this, "사용가능한 위치 정보 제공자가 없습니다.", Toast.LENGTH_SHORT).show();
        } else {
//최종 위치에서 부터 이어서 GPS 시작...
            onLocationChanged(location);

        }
    }

    /** 이 화면이 불릴 때, 일시정지 해제 처리*/
    @Override
    public void onResume() {
//Activity LifrCycle 관련 메서드는 무조건 상위 메서드 호출 필요
        super.onResume();

//위치정보 객체에 이벤트 연결
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        lm.requestLocationUpdates(provider, 500, 1, this);
    }
    /** 다른 화면으로 넘어갈 때, 일시정지 처리*/
    @Override
    public void onPause(){
//Activity LifrCycle 관련 메서드는 무조건 상위 메서드 호출 필요
        super.onPause();

//위치정보 객체에 이벤트 해제
        lm.removeUpdates(this);
    }

    /** 위치가 변했을 경우 호출된다.*/
    @Override
    public void onLocationChanged(Location location) {
// 위도, 경도
        double lat = location.getLatitude();
        double lng = location.getLongitude();
    }
    @Override
    public void onProviderDisabled(String provider) {
// TODO Auto-generated method stub

    }
    @Override
    public void onProviderEnabled(String provider) {
// TODO Auto-generated method stub

    }
    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {
// TODO Auto-generated method stub

    }
}

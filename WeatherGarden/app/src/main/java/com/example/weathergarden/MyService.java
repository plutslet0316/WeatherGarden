package com.example.weathergarden;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Address;
import android.location.Geocoder;
import android.os.AsyncTask;
import android.os.Build;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

import androidx.core.app.NotificationCompat;

import com.example.weathergarden.weather.LocationData;
import com.example.weathergarden.weather.WeatherInfo;
import com.example.weathergarden.weather.WeatherProc;
import com.google.gson.Gson;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

// 서비스 구현 부분
// 서비스 안에서 반복작업을 할 수 있는 스레드 구현

public class MyService extends Service {
    BackgroundTask task;

    int value = 0;

    public MyService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        // bindService() 메소드를 통해 서비스를 바인딩할 때 호출
        // 구현 시 클라이언트가 이 서비스와 커뮤니케이션할 때 사용할 수 있는 인터페이스인 IBinder 객체 반환, or null 반환
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");

    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        // 반복작업 부분
        // mainactivity에서 startService() 메소드를 통해 서비스를 시작할 때 callback 호출
        task = new BackgroundTask();
        task.execute();

        initializeNotification(); // 포그라운드 생성
        return START_NOT_STICKY; // 서비스가 죽어도 시스템에서 재생성 x

//        return super.onStartCommand(intent, flags, startId); //기존 코드
    }

    public String formatDate() {
        long mNow = System.currentTimeMillis(); // 현재 시간 가져옴
        Date mReDate = new Date(mNow); // Date 형식 적용
        SimpleDateFormat mFormat = new SimpleDateFormat("MM/dd"); // 시간을 원하는 포맷으로 변경
        String formatDate = mFormat.format(mReDate);

        return formatDate;
    }

    // 포그라운드 서비스
    public void initializeNotification() {
        // 날씨 사용
        WeatherProc weatherProc =new WeatherProc(this);

        WeatherInfo weatherInfo = weatherProc.getWeatherInfo();

        //타임 스탬프 setWhen() 사용하면 된다

        // 버전 오레오 이상일 경우
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, "1");
        // 알림창 아이콘 사이즈 24x24
        builder.setSmallIcon(R.drawable.cloud_weather_icon);

        NotificationCompat.BigTextStyle style = new NotificationCompat.BigTextStyle();
        // !! 81번줄 현재 위치 들어갈 부분입니다!!
        Gson gson = new Gson();
        Geocoder geocoder = new Geocoder(this, Locale.KOREAN);
        List<Address> addresses = null;

        // 위치 가져오기
        SharedPreferences preferences = this.getSharedPreferences("player_data", Context.MODE_PRIVATE);
        LocationData locationData = gson.fromJson(preferences.getString("location_data", ""), LocationData.class);

        // 위치 주소로 변환하기
        try {
            addresses = geocoder.getFromLocation(
                    Double.parseDouble(locationData.x),
                    Double.parseDouble(locationData.y),
                    7);
        } catch (IOException ioException) {
            //네트워크 문제
            Toast.makeText(this, "지오코더 서비스 사용불가", Toast.LENGTH_LONG).show();
        } catch (IllegalArgumentException illegalArgumentException) {
            Toast.makeText(this, "잘못된 GPS 좌표", Toast.LENGTH_LONG).show();
        } catch (Exception e){
            Log.d("weatherFragment", e.getMessage());
        }

        style.setBigContentTitle((addresses.get(1).getThoroughfare() != null ? addresses.get(1).getThoroughfare(): (addresses.get(1).getSubLocality() != null ? addresses.get(1).getSubLocality():(addresses.get(1).getLocality() != null ? addresses.get(1).getLocality() : addresses.get(1).getAdminArea()))) + " " + weatherInfo.temp + " ℃");
        style.bigText("강수량: " + weatherInfo.rainAmount + "  습도: " + weatherInfo.hum + "%"); // 내용 표시
        style.setSummaryText(formatDate()); // 패키지 이름 옆에 조금 큰 텍스트뷰
        // 알림으로 표시 (홈화면-메뉴-앱 세부정보에서 정보 표시)

        // !! 88번줄이 현재 위치 들어가는 부분입니다!!
        builder.setContentTitle("날씨정원"); // 제목
        builder.setContentText((addresses.get(1).getThoroughfare() != null ? addresses.get(1).getThoroughfare(): (addresses.get(1).getSubLocality() != null ? addresses.get(1).getSubLocality():(addresses.get(1).getLocality() != null ? addresses.get(1).getLocality() : addresses.get(1).getAdminArea()))) + " " + weatherInfo.temp + " ℃"); // 본문 텍스트

        builder.setOngoing(true);
        builder.setStyle(style);  // 알림 더 길게 설정, 스타일 템플릿 추가하여 확장 가능한 알림
        builder.setWhen(0);
        builder.setShowWhen(false);

        Intent notificationIntent = new Intent(this, MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, notificationIntent, PendingIntent.FLAG_IMMUTABLE);
        builder.setContentIntent(pendingIntent);

        NotificationManager manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            manager.createNotificationChannel(new NotificationChannel("1", "포그라운드 서비스", NotificationManager.IMPORTANCE_NONE));
        }
        Notification notification = builder.build(); // 알림창 아이콘 2
        startForeground(1, notification); // 알림창 실행
    }

    class BackgroundTask extends AsyncTask<Integer, String, Integer> {

        String result = "";

        @Override
        protected Integer doInBackground(Integer... integers) {

            while (isCancelled() == false) {
                try {
                    println(value + "번째 실행 중");
                    Thread.sleep(1000);
                    value++;
                } catch (InterruptedException e) {
                }
            }
            return value;
        }

        // 상태 확인
        @Override
        protected void onProgressUpdate(String... values) {
            println("onProgressUpdate()업데이트");
        }

        @Override
        protected void onPostExecute(Integer integer) {
            println("onPostExecute()");
            value = 0;
        }

        @Override
        protected void onCancelled() {
            value = 0; // 정지로 초기화
        }
    }

    // 서비스 종료
    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d("MyService", "onDestory");

        task.cancel(true);
    }

    public void println(String message) {
        Log.d("MyService", message);
    }


}

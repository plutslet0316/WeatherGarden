package com.example.weathergarden;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.widget.RemoteViews;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

import com.example.weathergarden.weather.WeatherInfo;
import com.example.weathergarden.weather.WeatherProc;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Implementation of App Widget functionality.
 */

public class NewAppWidget extends AppWidgetProvider {

    private Fragment activity;

    private static final String ACTION_BUTTON1 = "com.js.example.WeatherGarden.BUTTON1";

    void updateAppWidget(Context context, AppWidgetManager appWidgetManager,
                         int appWidgetId) {
        // Construct the RemoteViews object
        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.new_app_widget);

        // 시작되면서 동적으로 타이틀 넣고 스타일 설정하기
        CharSequence widgetText = context.getString(R.string.appwidget_text);

        // 위젯 누르면 앱으로 이동
        Intent intent = context.getPackageManager().getLaunchIntentForPackage("com.example.weathergarden");
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        //intent.addCategory(Intent.CATEGORY_LAUNCHER);
        //intent.setComponent(new ComponentName(context, MainActivity.class));
        PendingIntent pi = PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_MUTABLE);
        views.setOnClickPendingIntent(R.id.location_tv, PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_MUTABLE));
        views.setOnClickPendingIntent(R.id.temp, PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_MUTABLE));
        views.setOnClickPendingIntent(R.id.updatetime, PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_MUTABLE));

        //버튼1 클릭 : 클릭 성공 메세지 출력!
        Intent intent1 = new Intent(ACTION_BUTTON1);
        PendingIntent pendingIntent1 = PendingIntent.getBroadcast(context, 0, intent1, PendingIntent.FLAG_IMMUTABLE);
        views.setOnClickPendingIntent(R.id.button1, pendingIntent1);

        //Instruct the widget manager to update the widget
        appWidgetManager.updateAppWidget(appWidgetId, views);
    }
    //앱 위젯은 하나의 BroadcastReceiver, onReceive() 가 종료되는 순간에 소멸되어 오랫동안 뷰를 간직할 수 없다
    @Override
    public void onReceive(Context context, Intent intent) {
        super.onReceive(context, intent);

        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.new_app_widget);
        AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
        ComponentName thisAppWidget = new ComponentName(context.getPackageName(), NewAppWidget.class.getName());
        int[] appWidgets = appWidgetManager.getAppWidgetIds(thisAppWidget);

        final String action = intent.getAction();
        if (action.equals(ACTION_BUTTON1)) {
            //your code here
            Toast.makeText(context, "업데이트 완료", Toast.LENGTH_SHORT).show();
            onUpdate(context, appWidgetManager, appWidgets);

        }
    }


    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        // There may be multiple widgets active, so update all of them
        // 날씨 적용
        WeatherProc weatherProc =new WeatherProc(context);
        weatherProc.getWeather();

        WeatherInfo weatherInfo = weatherProc.getWeatherInfo();

        for (int appWidgetId : appWidgetIds) {
            updateAppWidget(context, appWidgetManager, appWidgetId);

            RemoteViews remoteViews = new RemoteViews(context.getPackageName(), R.layout.new_app_widget);

            remoteViews.setTextViewText(R.id.updatetime, "업데이트 " + formatDate());
            // !!현재 위치 지역명 들어가는 곳입니다.!!
            remoteViews.setTextViewText(R.id.location_tv, "금광동 ");
            //remoteViews.setTextViewText(R.id.temp, String.format("℃"));
            remoteViews.setTextViewText(R.id.temp, String.format(weatherInfo.temp + "º"));

            //버튼1 클릭 : 클릭 성공 메세지 출력!
            remoteViews.setOnClickPendingIntent(R.id.button1, getPendingSelfIntent(context, ACTION_BUTTON1, PendingIntent.FLAG_IMMUTABLE));

            appWidgetManager.updateAppWidget(appWidgetId, remoteViews);
        }
    }


    @Override
    public void onEnabled(Context context) {
        // Enter relevant functionality for when the first widget is created
    }

    @Override
    public void onDisabled(Context context) {
        // Enter relevant functionality for when the last widget is disabled
    }

    protected PendingIntent getPendingSelfIntent(Context context, String action, int flag) {
        Intent intent = new Intent(context, getClass());
        intent.setAction(action);

        return PendingIntent.getBroadcast(context, 0, intent, PendingIntent.FLAG_IMMUTABLE);
    }

    public String formatDate() {
        long mNow = System.currentTimeMillis(); // 현재 시간 가져옴
        Date mReDate = new Date(mNow); // Date 형식 적용
        SimpleDateFormat mFormat = new SimpleDateFormat("MM/dd HH:mm"); // 시간을 원하는 포맷으로 변경
        String formatDate = mFormat.format(mReDate);

        return formatDate;
    }


}

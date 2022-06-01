package com.example.weathergarden;

import android.app.Activity;
import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;

import android.content.SharedPreferences;
import android.widget.RemoteViews;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

import com.example.weathergarden.weather.WeatherInfo;
import com.example.weathergarden.weather.WeatherProc;
import com.google.gson.Gson;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Implementation of App Widget functionality.
 */

public class NewAppWidget extends AppWidgetProvider {

    private Fragment activity;

    private static final String ACTION_BUTTON1 = "com.js.example.noti.BUTTON1";

    void updateAppWidget(Context context, AppWidgetManager appWidgetManager,
                         int appWidgetId) {
        // Construct the RemoteViews object
        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.new_app_widget);

        // 시작되면서 동적으로 타이틀 넣고 스타일 설정하기
        //CharSequence widgetText = context.getString(R.string.appwidget_text);

        //버튼1 클릭 : 클릭 성공 메세지 출력!
        Intent intent1 = new Intent(ACTION_BUTTON1);
        PendingIntent pendingIntent1 = PendingIntent.getBroadcast(context, 0, intent1, PendingIntent.FLAG_CANCEL_CURRENT);
        views.setOnClickPendingIntent(R.id.button1, pendingIntent1);

        //Instruct the widget manager to update the widget
        appWidgetManager.updateAppWidget(appWidgetId, views);
    }

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
        for (int appWidgetId : appWidgetIds) {
            updateAppWidget(context, appWidgetManager, appWidgetId);

            RemoteViews remoteViews = new RemoteViews(context.getPackageName(), R.layout.new_app_widget);

            remoteViews.setTextViewText(R.id.updatetime, "업데이트 " + formatDate());

            // 날씨 적용
            //WeatherProc weatherProc =new WeatherProc(this);
            //weatherProc.getWeather();

            //WeatherInfo weatherInfo = weatherProc.getWeatherInfo();

            remoteViews.setTextViewText(R.id.temp, String.format("℃"));

            //remoteViews.setTextViewText(R.id.temp, String.format("기온: %dº", WeatherInfo.temp));


            //버튼1 클릭 : 클릭 성공 메세지 출력!
            remoteViews.setOnClickPendingIntent(R.id.button1, getPendingSelfIntent(context, ACTION_BUTTON1, PendingIntent.FLAG_CANCEL_CURRENT));

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

        return PendingIntent.getBroadcast(context, 0, intent, flag);
    }

    public String formatDate() {
        long mNow = System.currentTimeMillis(); // 현재 시간 가져옴
        Date mReDate = new Date(mNow); // Date 형식 적용
        SimpleDateFormat mFormat = new SimpleDateFormat("MM/dd HH:mm"); // 시간을 원하는 포맷으로 변경
        String formatDate = mFormat.format(mReDate);

        return formatDate;
    }


}

package com.example.weathergarden;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.weathergarden.plantdb.DBManager;

public class SunflowerBook extends Activity {
    DBManager dbManager;
    SQLiteDatabase sqlDB2;

    ImageButton tulip2, sunflower2;
    ImageView circle2, rectangle2;
    TextView title3, diffi2, flow2, origin2, light2, temp2, water2, soil2, earth2, coun2;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.book_sunflower);

        tulip2 = (ImageButton) findViewById(R.id.tulip2);
        sunflower2 = (ImageButton) findViewById(R.id.sunflower2);
        circle2 = (ImageView) findViewById(R.id.circle2);
        rectangle2 = (ImageView) findViewById(R.id.rectangle2);

        title3 = (TextView) findViewById(R.id.title3);

        diffi2 = (TextView) findViewById(R.id.diffi2);
        flow2 = (TextView) findViewById(R.id.flow2);
        origin2 = (TextView) findViewById(R.id.origin2);
        light2 = (TextView) findViewById(R.id.light2);
        temp2 = (TextView) findViewById(R.id.temp2);
        water2 = (TextView) findViewById(R.id.water2);
        soil2 = (TextView) findViewById(R.id.soil2);
        earth2 = (TextView) findViewById(R.id.earth2);
        coun2 = (TextView) findViewById(R.id.coun2);


        dbManager = new DBManager(this);

        tulip2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(SunflowerBook.this, TulipBook.class);
                startActivity(intent);
                finish();
            }
        });

        dbManager.createDataBase();
        dbManager.openDataBase();
        dbManager.close();
        sqlDB2 = dbManager.getReadableDatabase();
        Cursor cursor;
        cursor = sqlDB2.rawQuery("SELECT * FROM plants ORDER BY ROWID LIMIT 1;", null);

    /*
    데이터 전체 출력
    SELECT * FROM plants;
    마지막 데이터만 출력
    SELECT * FROM plants ORDER BY ROWID DESC LIMIT 1;   // 튤립
    첫 행만 출력
    SELECT * FROM plants ORDER BY ROWID LIMIT 1;    // 해바라기
     */

        String strDiff2 = "\n" + "난이도 : ";
        String strFlower2 = "개화시기 :  " ;
        String strOrigin2 = "\n" + "유래 : " ;
        String strLight2 = "햇빛 :  ";
        String strTemp2 = "온도 : ";
        String strWater2 = "물주기 : " + "\r\n";
        String strSoil2 = "흙 :  " + "\r\n";
        String strEarth2 = "비료 및 분갈이 : ";
        String strCoun2 = "원산지 : ";

        if (cursor.moveToNext()) {
            strDiff2 += cursor.getString(3);
            strFlower2 += cursor.getString(4);
            strOrigin2 += cursor.getString(5) + "\r\n";
            strLight2 += cursor.getString(6);
            strTemp2 += cursor.getString(7) + "\r\n";
            strWater2 += cursor.getString(8) + "\r\n";
            strSoil2 += cursor.getString(9) + "\r\n";
            strEarth2 += cursor.getString(10) + "\r\n";
            strCoun2 += cursor.getString(11) + "\r\n";
        }

        diffi2.setText(strDiff2);
        flow2.setText(strFlower2);
        origin2.setText(strOrigin2);
        light2.setText(strLight2);
        temp2.setText(strTemp2);
        water2.setText(strWater2);
        soil2.setText(strSoil2);
        earth2.setText(strEarth2);
        coun2.setText(strCoun2);

        cursor.close();
        sqlDB2.close();
    }
}

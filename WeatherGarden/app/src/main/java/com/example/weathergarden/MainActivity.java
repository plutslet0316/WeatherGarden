package com.example.weathergarden;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.weathergarden.SQLite.DataBaseHelper;

public class MainActivity extends AppCompatActivity {

    float val = 0;

    Button btnSelect;
    TextView plant_book, title, content;
    //  "dbWeaGar.db", // DB 파일 이름

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setTitle("식물도감 DB");

        getVal();

        btnSelect = (Button) findViewById(R.id.btnSelect);
        plant_book = (TextView) findViewById(R.id.plant_book);
        title = (TextView) findViewById(R.id.title);
        content = (TextView) findViewById(R.id.content);
    }

    public void getVal() {

        DataBaseHelper dbHelper = new DataBaseHelper(this);
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        Cursor cursor = db.rawQuery("SELECT * FROM plants where plant_code = 1", null);
        if (cursor.moveToNext())
        {
            val = cursor.getFloat(3);
        }

        cursor.close();
        dbHelper.close();
    }
}

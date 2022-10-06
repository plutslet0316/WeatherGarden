package com.example.weathergarden;


import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;


public class MainActivity extends AppCompatActivity {

    myDBHelper myHelper;
    EditText edtName, edtNumber;
    TextView tvDiff, tvFlower, tvOrigin, edtNameResult, edtNumberResult;
    ImageView ivFlower;
    Button btnInit, btnInsert, btnSelect;
    SQLiteDatabase sqlDB;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setTitle("식물 DB");

        edtName = (EditText) findViewById(R.id.edtName);
        edtNumber = (EditText) findViewById(R.id.edtNumber);

        ivFlower = (ImageView) findViewById(R.id.ivFlower);

        edtNameResult = (TextView) findViewById(R.id.edtNameResult);
        edtNumberResult = (TextView) findViewById(R.id.edtNumberResult);
        tvDiff = (TextView) findViewById(R.id.tvDiff);
        tvFlower = (TextView) findViewById(R.id.tvFlower);
        tvOrigin = (TextView) findViewById(R.id.tvOrigin);

        btnInit = (Button) findViewById(R.id.btnInit);
        btnInsert = (Button) findViewById(R.id.btnInsert);
        btnSelect = (Button) findViewById(R.id.btnSelect);

        myHelper = new myDBHelper(this);
        btnInit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sqlDB = myHelper.getWritableDatabase();
                myHelper.onUpgrade(sqlDB, 1, 2);
                sqlDB.close();
            }
        });

        btnInsert.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sqlDB = myHelper.getWritableDatabase();
                sqlDB.execSQL("INSERT INTO plants VALUES ( '"
                        + edtName.getText().toString() + "', "
                        + edtNumber.getText().toString() + ");");
                sqlDB.close();
                Toast.makeText(getApplicationContext(), "입력됨", Toast.LENGTH_SHORT).show();
            }
        });


        btnSelect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sqlDB = myHelper.getReadableDatabase();
                Cursor cursor;
                cursor = sqlDB.rawQuery("SELECT * FROM plants;", null);

            /*
            데이터 전체 출력
            SELECT * FROM plants;
            마지막 데이터만 출력
            SELECT * FROM plants ORDER BY ROWID DESC LIMIT 1;
            첫 행만 출력
            SELECT * FROM plants ORDER BY ROWID LIMIT 1;
             */

                String strNames = "식물 코드" + "\r\n" + "----------------------" + "\r\n";
                String strNumbers = "번호" + "\r\n" + "----------------------" + "\r\n";
                String strDiff = "난이도" + "\r\n" + "----------------------" + "\r\n";
                String strFlower = "개화시기" + "\r\n" + "----------------------" + "\r\n";
                String strOrigin = "유래" + "\r\n" + "----------------------" + "\r\n";

                while (cursor.moveToNext()) {
                    strNames += cursor.getString(0) + "\r\n";
                    strNumbers += cursor.getString(1) + "\r\n";
                    strDiff += cursor.getString(3) + "\r\n";
                    strFlower += cursor.getString(4) + "\r\n";
                    strOrigin += cursor.getString(5) + "\r\n";
                }

                edtNameResult.setText(strNames);
                edtNumberResult.setText(strNumbers);
                tvDiff.setText(strDiff);
                tvFlower.setText(strFlower);
                tvOrigin.setText(strOrigin);

                ivFlower.setImageResource(R.drawable.tulips);

                cursor.close();
                sqlDB.close();
            }
        });
    }

    public class myDBHelper extends SQLiteOpenHelper {
        public myDBHelper(Context context) {
            super(context, "plantDB", null, 1);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            db.execSQL("CREATE TABLE plants (PLANT_CODE CHAR(20) PRIMARY KEY, PLANT_NAME TEXT, PLANT_IMG BLOB, PLANT_DIFF TEXT, PLANT_FLOWER TEXT, PLANT_ORI TEXT, PLANT_SUN TEXT, PLANT_TEMP TEXT, PLANT_WATER TEXT, PLANT_SOIL TEXT, PLANT_FERSUB TEXT, PLANT_COUORI TEXT);");
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            db.execSQL("DROP TABLE IF EXISTS plants");
            onCreate(db);
        }
    }
}
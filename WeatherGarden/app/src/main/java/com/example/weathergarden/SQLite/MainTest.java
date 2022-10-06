//package com.example.weathergarden.SQLite;
//
//import android.database.Cursor;
//import android.database.sqlite.SQLiteDatabase;
//import android.database.sqlite.SQLiteException;
//import android.database.sqlite.SQLiteOpenHelper;
//import android.os.Bundle;
//import android.view.View;
//import android.widget.Button;
//import android.widget.TextView;
//import android.widget.Toast;
//
//import androidx.appcompat.app.AppCompatActivity;
//
//import com.example.weathergarden.R;
//
//public class MainTest extends AppCompatActivity {
//
//    float val = 0;
//
//    Button btnSelect;
//    TextView plant_book, title, content;
//    //  "dbWeaGar.db", // DB 파일 이름
//
//    SQLiteOpenHelper database = new DataBaseHelper(this);
//    SQLiteDatabase db = database.getReadableDatabase();
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_main);
//        setTitle("식물도감 DB");
//
//        getVal();
//
//        btnSelect = (Button) findViewById(R.id.btnSelect);
//        plant_book = (TextView) findViewById(R.id.plant_book);
//        title = (TextView) findViewById(R.id.title);
//        content = (TextView) findViewById(R.id.content);
//
//        btnSelect.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Toast.makeText(getApplicationContext(), "버튼 클릭 성공", Toast.LENGTH_SHORT).show();
//                title.setText("이 식물의 이름은 해바라기!");
//                try{
////                    db.query("plants", new String[]{"plant_name", "plant_difficulty"});
//                    db.rawQuery("SELECT * FROM plants where plant_code = 1", null);
//                } catch (SQLiteException ex) {
//                    title.setText("이 식물의 이름은 해바라기!");
//                }
//            }
//        });
//
//    }
//
//    public void getVal() {
//
//        DataBaseHelper dbHelper = new DataBaseHelper(this);
//        SQLiteDatabase db = dbHelper.getReadableDatabase();
//
//        Cursor cursor = db.rawQuery("SELECT * FROM plants where plant_code = 1", null);
//        if (cursor.moveToNext())
//        {
//            val = cursor.getFloat(3);
//        }
//
//        cursor.close();
//        dbHelper.close();
//    }
//}

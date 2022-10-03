//package com.example.weathergarden.SQLite;
//
//import android.content.Context;
//import android.database.sqlite.SQLiteDatabase;
//
//import androidx.annotation.Nullable;
//
//// db 생성, open 및 버전, update 대한 내용을 편리하게 도와주는 클래스
//
//public class SQLiteHelper extends android.database.sqlite.SQLiteOpenHelper{
//
//    // 나중에 수정할 때 대비하여 final 선언
//    public final String TABLE_NAME = "plants";
//    public final String PLANT_CODE = "plants_code";
//    public final String PLANT_NAME = "plants_name";
//    public final String PLANT_IMG = "plants_image";
//    public final String PLANT_DIFF = "plants_difficulty";
//    public final String PLANT_FLOWER = "plants_flowering";
//    public final String PLANT_ORI = "plants_origin";
//    public final String PLANT_SUN = "plants_sunlight";
//    public final String PLANT_TEMP = "plants_temperature";
//    public final String PLANT_WATER = "plants_watering";
//    public final String PLANT_SOIL = "plants_soil";
//    public final String PLANT_FERSUB = "plants_fer_sub";
//    public final String PLANT_COUORI = "plants_country_origin";
//
//    public SQLiteHelper(@Nullable Context context, @Nullable String name, @Nullable SQLiteDatabase.CursorFactory factory, int version) {
//        super(context, name, factory, version);
//    }
//
//    @Override
//    public void onCreate(SQLiteDatabase sqLiteDatabase) {
//    // 테이블 생성하는 쿼리
//    // if not exists 만약 존재하지 않으면 생성
//        String create_query = "create table if not exists " + TABLE_NAME + "("
//                + PLANT_CODE + " text primary key, "
//                + PLANT_NAME + " text ,"
//                + PLANT_IMG + " text , "  // not null 쓰지 않는 경우
//                + PLANT_DIFF + " text , "
//                + PLANT_FLOWER + " text , "
//                + PLANT_ORI + " text , "
//                + PLANT_SUN + " text , "
//                + PLANT_TEMP + " text , "
//                + PLANT_WATER + " text , "
//                + PLANT_SOIL + " text , "
//                + PLANT_FERSUB + " text , "
//                + PLANT_COUORI + " text);";
//
//        // 위 create query로 table을 생성
//        sqLiteDatabase.execSQL(create_query);
//    }
//
//    @Override
//    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
//        // SQLite에 대해 설정한 버전을 올렸을 때
//
//        // 기존 테이블 drop 해준 후
//        String drop_query = "drop table " + TABLE_NAME + ";";
//        sqLiteDatabase.execSQL(drop_query);
//
//        // onCreate를 호출해서 Table 다시 생성
//        onCreate(sqLiteDatabase);
//    }
//}

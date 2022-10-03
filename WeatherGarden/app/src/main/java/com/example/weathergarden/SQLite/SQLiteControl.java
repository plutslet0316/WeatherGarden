//package com.example.weathergarden.SQLite;
//
//import android.annotation.SuppressLint;
//import android.content.ContentValues;
//import android.database.Cursor;
//import android.database.sqlite.SQLiteDatabase;
//import android.util.Log;
//
//public class SQLiteControl {
//
//    SQLiteHelper helper;
//    SQLiteDatabase sqlite;
//
//    // 생성자
//    public SQLiteControl(SQLiteHelper _helper) {
//        this.helper = _helper;
//    }
//
//    // DB insert
//    public void insert(String _code, String _name, String _img, String _diff,
//                       String _flower, String _ori, String _sun, String _temp,
//                       String _water, String _soil, String _fersub, String _couori) {
//        sqlite = helper.getWritableDatabase();
//        ContentValues values = new ContentValues();
//
//        values.put(helper.PLANT_CODE, _code);
//        values.put(helper.PLANT_NAME, _name);
//        values.put(helper.PLANT_IMG, _img);
//        values.put(helper.PLANT_DIFF, _diff);
//
//        values.put(helper.PLANT_FLOWER, _flower);
//        values.put(helper.PLANT_ORI, _ori);
//        values.put(helper.PLANT_SUN, _sun);
//        values.put(helper.PLANT_TEMP, _temp);
//
//        values.put(helper.PLANT_WATER, _water);
//        values.put(helper.PLANT_SOIL, _soil);
//        values.put(helper.PLANT_FERSUB, _fersub);
//        values.put(helper.PLANT_COUORI, _couori);
//
//        sqlite.insert(helper.TABLE_NAME, null, values);
//    }
//
//    // DB Select
//    @SuppressLint("Range")
//    public String[] select() {
//        sqlite = helper.getReadableDatabase();
//        // 커서 사용
//        Cursor c = sqlite.query(helper.TABLE_NAME, null, null, null, null, null, null);
//
//        // 칼럼 정보를 배열에 넣기
//        String[] columnName = {helper.PLANT_CODE, helper.PLANT_NAME, helper.PLANT_IMG, helper.PLANT_DIFF,
//                               helper.PLANT_FLOWER, helper.PLANT_ORI, helper.PLANT_SUN, helper.PLANT_TEMP,
//                               helper.PLANT_WATER, helper.PLANT_SOIL, helper.PLANT_FERSUB, helper.PLANT_COUORI };
//
//        // 칼럼 정보와 길이가 같은 배열을 생성 후
//        String[] returnValue = new String[columnName.length];
//
//        // 생성한 배열에 데이터를 받아줍니다.
//        while(c.moveToNext()) {
//            for(int i=0; i<returnValue.length; i++) {
//                returnValue[i] = c.getString(c.getColumnIndex(columnName[i]));
//                Log.e("DB Select : ",i + " - " + returnValue[i]);
//            }
//        }
//        // 커서를 사용 후 닫아주기
//        c.close();
//        return returnValue;
//    }
//
//    // DB Update
//    public void update(String _key, String _value, String _code) {
//        sqlite = helper.getWritableDatabase();
//
//        ContentValues value = new ContentValues();
//        value.put(_key, _value);
//        // 기본키가 code 여서 사용
//        sqlite.update(helper.TABLE_NAME, value, "code=?", new String[]{_code});
//    }
//
//    // DB Delete
//    public void delete (String _code) {
//        sqlite = helper.getWritableDatabase();
//        sqlite.delete(helper.TABLE_NAME, "code=?",  new String[]{_code});
//    }
//
//    // SQLite Close
//    public void db_close() {
//        sqlite.close();
//        helper.close();
//    }
//}

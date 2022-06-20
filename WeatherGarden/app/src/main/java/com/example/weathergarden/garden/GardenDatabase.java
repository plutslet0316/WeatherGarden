package com.example.weathergarden.garden;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.migration.Migration;
import androidx.sqlite.db.SupportSQLiteDatabase;

/*
    데이터를 부를 때
    GardenDatabase db = null; 로 전역 변수로 먼저 선언한 다음

    쓰레드 안에서
    db= GardenDatabase.getInstance(getApplicationContext());
    로 데이터베이스를 생성해서 가져온다.

    이후
    GardenDao dao = new GardenDao(); 를 만들고
    dao 로 데이터를 처리하면 된다.
*/
@Database(entities = {PlantInfo.class, GroundInfo.class}, version = 1)
public abstract class GardenDatabase extends RoomDatabase {
    public abstract GardenDao gardenDao();

    // DB 생성하는 부분
    static private GardenDatabase instance = null;

    static public GardenDatabase getInstance(Context context) {
        if (instance == null) {
            synchronized(GardenDatabase.class){
                instance = Room.databaseBuilder(
                        context.getApplicationContext(),
                        GardenDatabase.class, "garden-database")    // class 로 정의된 DB를 name 에 기입한 이름으로 생성한다,
                        .createFromAsset("database/plants_data.db")       // Asset 폴더 안에서 기입한 파일을 찾아 초기 DB로 생성한다. - 미리 채우기
                        .build();
            }
        }
        
        // 위에서 가져온 DB 본체를 넘겨준다.
        return instance;
    }
}

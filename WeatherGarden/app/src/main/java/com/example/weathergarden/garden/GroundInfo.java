package com.example.weathergarden.garden;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Ignore;

// 땅 테이블
// plant_code 가 외래키로 지정되어있다.
@Entity(
    tableName = "ground",
    primaryKeys = {"ground_no"},
    foreignKeys = { @ForeignKey(
        entity = PlantInfo.class,
        parentColumns = "plant_code",
        childColumns = "plant_code"
    )})
public class GroundInfo {
    @ColumnInfo(name = "ground_no") @NonNull
    public int groundNo;
    @ColumnInfo(name = "plant_code") @NonNull
    public String plantCode;
    @ColumnInfo(name = "water") @NonNull
    public int water;
    @ColumnInfo(name = "nutri") @NonNull
    public int nutri;
    @ColumnInfo(name = "wither") @NonNull
    public int wither;
    @ColumnInfo(name = "grow_point") @NonNull
    public int growPoint;
    @ColumnInfo(name = "grow_level") @NonNull
    public int growLevel;

    // @Ignore 로 DB 테이블에서 무시되고 메소드로만 사용된다.
    // 값을 넣으면 객체 안에 대입시키는 Setter 다.
    @Ignore
    public void SetGroundInfo (int gNo, String pCode, int wa, int n, int wh, int gPoint, int gLevel){
        groundNo = gNo;
        plantCode = pCode;
        water = wa;
        nutri = n;
        wither = wh;
        growPoint = gPoint;
        growLevel = gLevel;
    }
}

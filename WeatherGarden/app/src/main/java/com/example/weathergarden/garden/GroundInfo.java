package com.example.weathergarden.garden;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Ignore;

import java.io.Serializable;

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
public class GroundInfo implements Serializable {
    @ColumnInfo(name = "ground_no") @NonNull
    public int groundNo;
    @ColumnInfo(name = "plant_code") @NonNull
    public String plantCode;
    @ColumnInfo(name = "water") @NonNull
    public float water;
    @ColumnInfo(name = "nutrient") @NonNull
    public int nutrient;
    @ColumnInfo(name = "wither") @NonNull
    public int wither;
    @ColumnInfo(name = "grow_point") @NonNull
    public double growPoint;
    @ColumnInfo(name = "grow_level") @NonNull
    public int growLevel;
    @ColumnInfo(name = "year") @NonNull
    public int year;
    // @Ignore 로 DB 테이블에서 무시되고 메소드로만 사용된다.
    // 값을 넣으면 객체 안에 대입시키는 Setter 다.
    @Ignore
    public void setGroundInfo (int groundNo, String plantCode, int water, int nutrient, int wither, float growPoint, int growLevel, int year){
        this.groundNo = groundNo;
        this.plantCode = plantCode;
        this.water = water;
        this.nutrient = nutrient;
        this.wither = wither;
        this.growPoint = growPoint;
        this.growLevel = growLevel;
        this.year = year;
    }
}

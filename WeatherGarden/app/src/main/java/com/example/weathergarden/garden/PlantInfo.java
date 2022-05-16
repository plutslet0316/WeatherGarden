package com.example.weathergarden.garden;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.room.ColumnInfo;
import androidx.room.Entity;

import java.io.Serializable;

// 식물 정보
@Entity(tableName = "plant", primaryKeys = "plant_code")
public class PlantInfo implements Serializable {
    @ColumnInfo(name = "plant_code") @NonNull
    public String plantCode;
    @ColumnInfo(name = "name") @NonNull
    public String name;
    @ColumnInfo(name = "water_limit") @NonNull
    public int waterLimit;
    @ColumnInfo(name = "nutrient_limit") @NonNull
    public int nutrientLimit;
    @ColumnInfo(name = "wither_limit") @NonNull
    public int witherLimit;
    @ColumnInfo(name = "grow_require") @NonNull
    public int growRequire;
    @ColumnInfo(name = "grow_limit") @NonNull
    public int growLimit;
    @ColumnInfo(name = "point") @NonNull
    public int point;
    @ColumnInfo(name = "img_link") @NonNull
    public String img;
}

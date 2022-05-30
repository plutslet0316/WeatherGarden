package com.example.weathergarden.garden;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.room.ColumnInfo;
import androidx.room.Entity;

import java.io.Serializable;

// 식물 정보
@Entity(tableName = "plant", primaryKeys = "plant_code")
public class PlantInfo implements Serializable {

    @ColumnInfo(name = "plant_code") @NonNull               public String plantCode;
    @ColumnInfo(name = "name") @NonNull                     public String name;
    @ColumnInfo(name = "temperature_require") @NonNull      public int temperatureRequire;
    @ColumnInfo(name = "temperature_min") @NonNull          public int temperatureMin;
    @ColumnInfo(name = "temperature_max") @NonNull          public int temperatureMax;
    @ColumnInfo(name = "water_require") @NonNull            public int waterRequire;
    @ColumnInfo(name = "water_min") @NonNull                public int waterMin;
    @ColumnInfo(name = "water_max") @NonNull                public int waterMax;
    @ColumnInfo(name = "water_consume") @NonNull            public int waterConsume;
    @ColumnInfo(name = "nutrient_require") @NonNull         public int nutrientRequire;
    @ColumnInfo(name = "nutrient_min") @NonNull             public int nutrientMin;
    @ColumnInfo(name = "nutrient_max") @NonNull             public int nutrientMax;
    @ColumnInfo(name = "nutrient_consume") @NonNull         public int nutrientConsume;
    @ColumnInfo(name = "wither_limit") @NonNull             public int witherLimit;
    @ColumnInfo(name = "seed_require") @NonNull             public int seedRequire;
    @ColumnInfo(name = "stem_require") @NonNull             public int stemRequire;
    @ColumnInfo(name = "flower_require") @NonNull           public int flowerRequire;
    @ColumnInfo(name = "grow_limit") @NonNull               public int growLimit;
    @ColumnInfo(name = "year_limit") @NonNull               public int yearLimit;
    @ColumnInfo(name = "point") @NonNull                    public int point;
    @ColumnInfo(name = "img_link") @NonNull                 public String img;
}

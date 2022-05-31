package com.example.weathergarden.garden;

import androidx.room.Embedded;
import androidx.room.Relation;

// plant_code 외래키를 이용해 땅과 식물이 합쳐진다.
public class GardenInfo {
    @Embedded
    public GroundInfo groundInfo;
    @Relation(
            parentColumn = "plant_code",
            entityColumn = "plant_code"
    )
    public PlantInfo plantInfo;
}

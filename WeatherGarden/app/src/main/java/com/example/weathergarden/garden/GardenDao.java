package com.example.weathergarden.garden;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Transaction;
import androidx.room.Update;

import java.util.List;


// 데이터 입출력
@Dao
public interface GardenDao {

    // 땅과 식물이 합쳐서 반환된다.
    @Transaction
    @Query("SELECT * FROM ground")
    public List<GardenInfo> readGardenInfoList();

    // 땅번호로 땅 데이터를 찾아 반환한다.
    @Query("SELECT * FROM ground WHERE ground_no = :groundNo")
    public GroundInfo readGroundWithGroundNo(int groundNo);

    @Query("SELECT ground_no FROM ground")
    public List<Integer> readAllGroundNo();

    // 식물코드로 식물 데이터를 찾아 반환한다.
    @Query("SELECT * FROM plant WHERE plant_code = :plantCode")
    public PlantInfo readPlantWithPlantCode(String plantCode);

    // 식물 리스트가 반환된다.
    @Query("SELECT * FROM plant")
    public List<PlantInfo> readPlantsList();

    // 땅 리스트를 반환된다.
    @Query("SELECT * FROM ground")
    public List<GroundInfo> readGroundList();

    // 땅 데이터 추가 - 이미 해당 기본키가 있으면 덮어쓰기
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    public void insertGroundInfo(GroundInfo... groundInfo);

    // 식물 데이터 추가 - 이미 해당 기본키가 있으면 덮어쓰기
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    public void insertPlantInfo(PlantInfo... plantInfo);

    // 땅 데이터 업데이트 위에 Insert 만 쓰게 되면 필요 없을 듯하다.
    @Update
    public void updateGroundInfo(GroundInfo groundInfo);

    // 땅번호를 기준으로 데이터를 삭제한다.
    @Query("DELETE FROM ground WHERE ground_no = :groundNo")
    public void deleteGroundWithGroundNo(int groundNo);

}

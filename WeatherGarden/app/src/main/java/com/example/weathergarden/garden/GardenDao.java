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
    public List<GardenInfo> GardenInfoList();

    // 식물 리스트만 반환된다.
    @Query("SELECT * FROM plant")
    public List<PlantInfo> PlantsList();

    // 땅만 반환된다.
    @Query("SELECT * FROM ground")
    public List<GroundInfo> GroundList();

    // 데이터 추가 - 이미 해당 기본키가 있으면 덮어쓰기
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    public void InsertGroundInfo(GroundInfo... groundInfo);

    @Update
    public void UpdateGroundInfo(GroundInfo groundInfo);

    // 땅번호를 기준으로 데이터를 삭제한다.
    @Query("DELETE FROM ground WHERE ground_no = :groundNo")
    public void DeleteGroundByGroundNO(int groundNo);

}

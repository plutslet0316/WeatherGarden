package com.example.weathergarden.garden;

import static android.content.Context.MODE_PRIVATE;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Build;
import android.util.Log;

import androidx.annotation.RequiresApi;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

public class GrowProc {
    GardenDao dao;
    Context context;

    // 생성자
    public GrowProc() {}
    public GrowProc(Context context) {
        this.context = context;
    }

    // dao와 같이 생성
    public GrowProc withDao(GardenDao gardenDao) {
        dao = gardenDao;
        return this;
    }

    public class CarePlant {
        GroundInfo groundInfo;
        PlantInfo plantInfo;

        public CarePlant() {}

        public CarePlant withGroundNo(int groundNo) {
            groundInfo = dao.readGroundWithGroundNo(groundNo);
            plantInfo = dao.readPlantWithPlantCode(groundInfo.plantCode);
            return this;
        }

        // 식물 성장
        private void Growing(int value) {
            groundInfo.growPoint += value;

            // 요구치 가져오고, 0부터 시작하는 레벨에 1 곱해서 요구치 가져옴
            int growRequire = plantInfo.growRequire * (groundInfo.growLevel + 1);

            // 현재 레벨과 성장 요구치를 넘는지 확인해서
            boolean checkLevel = groundInfo.growLevel < plantInfo.growLimit;
            boolean checkRequire = groundInfo.growPoint >= growRequire;

            // 현재 포인트를 요구치만큼 지우고, 레벨 증가
            if (checkLevel && checkRequire) {
                groundInfo.growPoint -= growRequire;
                groundInfo.growLevel++;
            }

            // 데이터 업데이트
            dao.updateGroundInfo(groundInfo);
        }

        // 식물 심기
        public void planting(int groundNo, String plantCode) {
            GroundInfo groundInfo = new GroundInfo();
            groundInfo.setGroundInfo(groundNo, plantCode, 0, 0, 0, 0, 0);
            dao.insertGroundInfo(groundInfo);
        }

        // 식물 뽑기
        public void removePlant(int groundNo) {
            dao.deleteGroundWithGroundNo(groundNo);
        }


        // 정원 정보 가져오기? 필요 없을지도 모른다. 보류
        /*
            물
            물 줄 때 최대치 * 1.2 이상 물 줄 수 없도록
            만약 미니게임 방식으로 바꾼다면 조금 더 고민해야할 듯
            0을 반환하면 줄 수 없는 상태
            1을 반환하면 성공적으로 준 것
        */
        public int addWater(int value) {
            // 최대치의 1.2가 넘는지 확인
            int limit = (int) (plantInfo.waterLimit * 1.2);
            boolean checkLimit = groundInfo.water < limit;

            if (checkLimit)
                groundInfo.water += value;
            else
                return 0;

            if (groundInfo.water > limit) groundInfo.water = limit;

            dao.updateGroundInfo(groundInfo);
            return 1;
        }

        /*
            영양
            물 주기와 같은 방식으로 할 지
            아니면 그냥 최대를 넘지 않는 방식으로 할 지
            고민해봐야한다.
        */
        public int addNutrient(int value) {
            int limit = (int) (plantInfo.nutrientLimit * 1.2);

            boolean checkLimit = groundInfo.nutrient < limit;

            if (checkLimit)
                groundInfo.nutrient += value;
            else
                return 0;

            if (groundInfo.nutrient > limit) groundInfo.nutrient = limit;

            dao.updateGroundInfo(groundInfo);
            return 1;
        }

        // 식물 시듦
        private void Withering(int value) {
            groundInfo.wither += value;
            
            // 0보다 낮아지면 0으로 고정
            if (groundInfo.wither < 0) groundInfo.wither = 0;
            
            // 제한보다 높하지만 제한으로 고정
            if (groundInfo.wither > plantInfo.witherLimit) groundInfo.wither = plantInfo.witherLimit;

            dao.updateGroundInfo(groundInfo);
        }


        // 물 소모
        public void consumeWater(int value) {
            groundInfo.water -= value;
        }
        // 양분 소모
        public void consumeNutrient(int value) {
            groundInfo.nutrient -= value;
        }

        // 물 확인
        public boolean checkWater() {
            return groundInfo.water > 0;
        }
        // 영양 확인
        public boolean checkNutrient() {
            return groundInfo.nutrient > 0;
        }
        // 시듦 상태 확인
        public boolean checkWither() {
            return groundInfo.wither > 0;
        }

    }

    private int checkGrowTime() {
        // 설정 파일 열기
        SharedPreferences preferences = context.getSharedPreferences("player_data", MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();

        // 현재 시간을 형식대로 문자열로 가져온다.
        TimeZone time = TimeZone.getTimeZone("Asia/Seoul");
        Date now = new Date(System.currentTimeMillis());
        DateFormat df = new SimpleDateFormat("yyyyMMddHHmmss");

        df.setTimeZone(time);
        String nowDate = df.format(now);

        // 마지막 성장 시간을 문자열로 가져온다.
        String lastDate = preferences.getString("last_date", "");

        // 만일 가져온 값이 없으면(첫 접속이면) 현재 시간으로 설정한다.
        if (lastDate == "") {
            editor.putString("last_date", nowDate);
            editor.apply();     // 이 부분은 필수다.
            lastDate = preferences.getString("last_date", "");
        }

        // 시간 형식 지정
        DateTimeFormatter format = DateTimeFormatter.ofPattern("yyyyMMddHHmmss");

        // 시간 형식 대로 문자열을 시간으로 바꾼다.
        LocalDateTime nowDateTime = LocalDateTime.parse(nowDate, format);
        LocalDateTime lastDateTime = LocalDateTime.parse(lastDate, format);

        // 두 시간의 차이를 가져온다.
        int differ = (int) ChronoUnit.HOURS.between(lastDateTime, nowDateTime);
        Log.d("Grow", "Time Differ : " + differ + " " + nowDate + " " + lastDate);

        if(differ < 0){
            editor.putString("last_date", nowDate);
            editor.apply();

            return 0;
        }
        // 시간이 한시간 차이가 나게 되면 1을 리턴
        if (differ >= 1) {
            // 현재 시간으로 설정한다.
            editor.putString("last_date", nowDate);
            editor.apply();

            return differ;
        }

        // 아니면 0 리턴
        return 0;
    }

    // 식물 성장 시작
    public int startGrowing(Context context) {
        // 시간차이를 가져온다.
        int differ = checkGrowTime();
        // Log.d("test", String.valueOf(differ));

        // 차이가 없거나 적으면 0을 리턴 / 성장하지 않는다.
        if (differ <= 0) return 0;

        // 리스트에서 하나씩 확인해서 각각 상황에 맞게 성장시킨다.
        List<Integer> groundNoList = dao.readAllGroundNo();

        // 리스트만큼 반복한다.
        for (int groundNo : groundNoList) {
            CarePlant carePlant = new CarePlant().withGroundNo(groundNo);

            // 시간차이만큼 반복
            for (int i = 0; i < differ; i++) {
                // 과습, 과영양, 시듦

                // 각각 물, 영양, 시듦 상태를 확인해서 boolean 형식으로 가져온다.
                boolean isHasWater = carePlant.checkWater();
                boolean isHasNutrient = carePlant.checkNutrient();
                boolean isHasWither = carePlant.checkWither();

                // 물이 없으면 시듦수치가 증가하고 아래 무시 반복을 계속한다.
                if (isHasWater == false) {
                    carePlant.Withering(2);
                    continue;
                }

                // 물 소모
                carePlant.consumeWater(1);

                // 성장,시듦감소 수치를 지정한다.
                int value = 2;

                // 영양이 있으면 영양을 소모하고 수치를 증가시킨다.
                if (isHasNutrient) {
                    carePlant.consumeNutrient(1);
                    value = 5;
                }

                // 시듦상태를 확인하고 맞다면 감소 아니라면 성장한다.
                if (isHasWither) {
                    carePlant.Withering(-value); // 시듦수치가 감소한다.
                } else {
                    carePlant.Growing(value); // 식물이 성장한다.
                }
            }
        }

        // 모든 식물이 성장을 마치면 1 리턴
        return 1;
    }
}

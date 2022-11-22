package com.example.weathergarden.garden;

import static android.content.Context.MODE_PRIVATE;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Build;
import android.util.Log;

import androidx.annotation.RequiresApi;

import com.example.weathergarden.weather.WeatherInfo;
import com.example.weathergarden.weather.WeatherProc;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;
import java.util.concurrent.TimeUnit;

public class GrowProc {
    GardenDao dao;
    Context context;
    ShowDao showDao;

    // 생성자
    public GrowProc(Context context) {
        this.context = context;
        showDao = new ShowDao(context);
    }

    // dao와 같이 생성
    public GrowProc withDao(GardenDao gardenDao) {
        dao = gardenDao;
        return this;
    }

    public class CarePlant {
        GroundInfo groundInfo;
        PlantInfo plantInfo;

        public CarePlant() {
        }

        public CarePlant withGroundNo(int groundNo) {
            groundInfo = dao.readGroundWithGroundNo(groundNo);
            plantInfo = dao.readPlantWithPlantCode(groundInfo.plantCode);
            return this;
        }

        // 식물 성장 메서드 / 매개로 받은 만큼 성장시킨다.
        private void Growing(float value) {
            // 설계상 하루을 기준으로 성장하도록 만들었기 때문에
            // 시간 단위로 성장시키기 위해서 24로 나눠준다.
            groundInfo.growPoint += (value / 24);

            // 요구치 가져오기
            int growRequire = 0;
            switch (groundInfo.growLevel) {
                case 3:
                    growRequire = plantInfo.growLimit;      // 성장 제한
                    break;

                // 요구치가 가중된다.
                case 2:
                    growRequire += plantInfo.flowerRequire; // 꽃
                case 1:
                    growRequire += plantInfo.stemRequire;   // 성장기
                case 0:
                    growRequire += plantInfo.seedRequire;   // 씨앗
                    break;
            }

            // 성장 요구치를 넘는지 확인해서
            boolean checkRequire = groundInfo.growPoint >= growRequire;

            // 레벨 증가
            if (checkRequire) {
                groundInfo.growLevel++;
            }
            if (groundInfo.growLevel >= 4) {
                groundInfo.growLevel = 4;
                Withering(100);
            }

            // 데이터 업데이트
            dao.updateGroundInfo(groundInfo);
        }

        // 식물 심기
        public void planting(int groundNo, String plantCode) {
            GroundInfo groundInfo = new GroundInfo();
            groundInfo.setGroundInfo(groundNo, plantCode, 0, 0, 0, 0, 0, 0);
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
        public void addWater(int value) {
            groundInfo.water += value;
            dao.updateGroundInfo(groundInfo);
        }

        /*
            영양
            물 주기와 같은 방식으로 할 지
            아니면 그냥 최대를 넘지 않는 방식으로 할 지
            고민해봐야한다.
        */
        public void addNutrient(int value) {
            groundInfo.nutrient += value;
            dao.updateGroundInfo(groundInfo);
        }

        // 식물 시듦
        private void Withering(float value) {
            groundInfo.wither += value;

            // 0보다 낮아지면 0으로 고정
            if (groundInfo.wither < 0) groundInfo.wither = 0;

            // 제한보다 높아지만 제한으로 고정
            if (groundInfo.wither > plantInfo.witherLimit)
                groundInfo.wither = plantInfo.witherLimit;

            dao.updateGroundInfo(groundInfo);
        }


        // 물 소모
        public void consumeWater() {
            ShowInfo showInfo = showDao.getShowInfo();

            // 습도에 따라 물 소모량 조절
            groundInfo.water -= plantInfo.waterConsume - (plantInfo.waterConsume * ((Float.valueOf(showInfo.hum) * 0.01f) - 0.2f));

            if (groundInfo.water <= 0) {
                groundInfo.water = 0;
            }

            dao.updateGroundInfo(groundInfo);
        }

        // 양분 소모
        public void consumeNutrient() {
            groundInfo.nutrient -= plantInfo.nutrientConsume;

            if (groundInfo.nutrient <= 0) {
                groundInfo.nutrient = 0;
            }

            dao.updateGroundInfo(groundInfo);
        }

        // 시듦 확인
        public boolean checkWither() {
            return plantInfo.witherLimit <= groundInfo.wither;
        }

        // 조건 확인
        private float check(String type) {
            float result = 1;
            float var = 0;
            int require = 0;
            int min = 0;
            int max = 0;

            ShowInfo showInfo = showDao.getShowInfo();
            switch (type) {
                case "Temperature":
                    try {
                        var = Float.valueOf(showInfo.temp);
                        require = plantInfo.temperatureRequire;
                        min = plantInfo.temperatureMin;
                        max = plantInfo.temperatureMax;
                    } catch (Exception e) {
                        Log.d("GrowProc", e.getMessage());
                        return 1;
                    }
                    break;
                case "Water":
                    var = groundInfo.water;
                    require = plantInfo.waterRequire;
                    min = plantInfo.waterMin;
                    max = plantInfo.waterMax;
                    break;
                case "Nutrient":
                    var = groundInfo.nutrient;
                    require = plantInfo.nutrientRequire;
                    min = plantInfo.nutrientMin;
                    max = plantInfo.nutrientMax;
                    break;
            }

            float minRange = require - ((require - min) / 2);
            float maxRange = require + ((max - require) / 2);
            float rawMin = (minRange / var) / (minRange / require);
            float rawMax = (require / maxRange) / (var / maxRange);


            // 이탈
            if (max <= var) {
                result = (max / var) * 0.1f;
                Withering(1);
                //Log.d("GrowProc", "max: "+result);
                return result;
            } else if (var <= min) {
                result = (var / min) * 0.1f;
                Withering(1);
                //Log.d("GrowProc", "min: "+result);
                return result;
            }

            Withering(-result);

            // 범위 외
            if (var <= minRange) {
                result = (var / minRange);
                //Log.d("GrowProc", "minRange: "+result);
                return result;
            } else if (maxRange <= var) {
                result = (maxRange / var);
                //Log.d("GrowProc", "maxRange: "+result);
                return result;
            } else

                // 범위 내
                if (var <= maxRange) {
                    result = 0.9f + ((rawMin * rawMax) * 0.2f);
                    //Log.d("GrowProc", "range: "+result);
                    return result;
                } else if (minRange <= var) {
                    result = 0.9f + (0.2f / (rawMin * rawMax));
                    //Log.d("GrowProc", "range: "+result);
                    return result;
                } else

                    return result;
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

        // 두 시간의 차이를 가져온다.
        TimeUnit timeUnit = TimeUnit.HOURS;
        int differ = 0;
        try {
            long diffInMillies = now.getTime() - df.parse(lastDate).getTime();
            differ = (int) timeUnit.convert(diffInMillies, TimeUnit.MILLISECONDS);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        Log.d("Grow", "Time Differ : " + differ + " " + nowDate + " " + lastDate);

        if (differ < 0) {
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

    // 접속 이후 시간이 흐른 만큼 식물이 성장하는 메서드
    public int startGrowing(Context context) {
        // 시간차이를 가져온다.
        int differ = checkGrowTime();

        // 차이가 없거나 적으면 0을 리턴 / 성장하지 않는다.
        if (differ <= 0) return 0;

        // 땅이 여래 개를 상정하고 만들었기 때문에 리스트로 정보를 가져옴
        List<Integer> groundList = dao.readAllGroundNo();

        // 리스트만큼 반복한다.
        for (int ground : groundList) {
            // 성장을 관리하는 메서드를 땅 번호로 불러옴
            CarePlant carePlant = new CarePlant().withGroundNo(ground);

            // 시간차이만큼 반복
            for (int i = 0; i < differ; i++) {
                // 시듦 상태 체크
                boolean isWither = carePlant.checkWither();

                // 각각 물, 영양, 시듦 수치를 확인해서 float 형식으로 가져온다.
                float temperature = carePlant.check("Temperature");
                float water = carePlant.check("Water");
                float nutrient = carePlant.check("Nutrient");

                // 모든 수치를 곱해 하나의 값으로 만듦
                float value = temperature * water * nutrient;

                // 식물 성장
                carePlant.Growing(value);

                // 수분과 영양 소모
                carePlant.consumeWater();
                carePlant.consumeNutrient();
            }
        }

        // 모든 식물이 성장을 마치면 1 리턴
        return 1;
    }

    public int startGrowing(Context context, int time) {
        // 성장 시간이 없거나 적으면 0을 리턴 / 성장하지 않는다.
        if (time <= 0) return 0;

        // 땅이 여래 개를 상정하고 만들었기 때문에 리스트로 정보를 가져옴
        List<Integer> groundList = dao.readAllGroundNo();

        // 리스트만큼 반복한다.
        for (int ground : groundList) {
            // 성장을 관리하는 메서드를 땅 번호로 불러옴
            CarePlant carePlant = new CarePlant().withGroundNo(ground);

            // 시간차이만큼 반복
            for (int i = 0; i < time; i++) {
                // 각각 물, 영양, 시듦 수치를 확인해서 float 형식으로 가져온다.
                float temperature = carePlant.check("Temperature");
                float water = carePlant.check("Water");
                float nutrient = carePlant.check("Nutrient");

                // 모든 수치를 곱해 하나의 값으로 만듦
                float value = temperature * water * nutrient;

                // 식물 성장
                carePlant.Growing(value);

                // 수분과 영양 소모
                carePlant.consumeWater();
                carePlant.consumeNutrient();
            }
        }

        // 모든 식물이 성장을 마치면 1 리턴
        return 1;
    }
}
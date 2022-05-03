package com.example.weathergarden.garden;

import android.util.Log;

import java.util.List;

public class GrowProc {
    GardenDao dao;

    // 생성자
    public GrowProc(GardenDao gardenDao) {
        dao = gardenDao;
    }

    public class CarePlant {
        GroundInfo groundInfo;
        PlantInfo plantInfo;

        public CarePlant(int groundNo) {
            groundInfo = dao.readGroundWithGroundNo(groundNo);
            plantInfo = dao.readPlantWithPlantCode(groundInfo.plantCode);
        }

        // 식물 심기
        public void planting(int gNo, String pCode) {
            GroundInfo groundInfo = new GroundInfo();
            groundInfo.setGroundInfo(gNo, pCode, 0, 0, 0, 0, 0);
            dao.insertGroundInfo(groundInfo);
        }

        // 식물 뽑기
        public void remove(int groundNo) {
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

        public void subtractWater(int value) {
            groundInfo.water -= value;
        }

        public boolean checkWater() {
            return groundInfo.water > 0;
        }

        /*
            영양
            물 주기와 같은 방식으로 할 지
            아니면 그냥 최대를 넘지 않는 방식으로 할 지
            고민해봐야한다.
            이름을 어떻게 지을까?
            Nutrient: 영양
            Fertilizer: 비료
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

        public void subtractNutrient(int value) {
            groundInfo.nutrient -= value;
        }

        public boolean checkNutrient() {
            return groundInfo.nutrient > 0;
        }

        // 식물 시듦
        private void Withering(int value) {
            groundInfo.wither += value;
            if (groundInfo.wither < 0) groundInfo.wither = 0;
            dao.updateGroundInfo(groundInfo);
        }

        // 시듦 상태 확인
        public boolean checkWither() {
            return groundInfo.wither > 0;
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

            // 업데이트
            dao.updateGroundInfo(groundInfo);
        }
    }

    // 식물 성장 시작
    public void startGrowing() {
        // 리스트에서 하나씩 확인해서 각각 상황에 맞게 성장시킨다.
        List<Integer> groundNoList = dao.readAllGroundNo();
        for (int groundNo : groundNoList) {
            CarePlant carePlant = new CarePlant(groundNo);

            // 각각 물, 영양, 시듦 상태를 확인해서 boolean 형식으로 가져온다.
            boolean isHasWater = carePlant.checkWater();
            boolean isHasNutrient = carePlant.checkNutrient();
            boolean isHasWither = carePlant.checkWither();

            // 물 -> 영양 -> 시듦 순서로 확인한다.
            if (isHasWater) {
                carePlant.subtractWater(5);

                if (isHasNutrient) {
                    carePlant.subtractNutrient(5);

                    if (isHasWither) {
                        carePlant.Withering(-10); // 시듦수치가 많이 감소한다.
                    } else {
                        carePlant.Growing(10); // 식물이 많이 성장한다.
                    }
                } else {
                    if (isHasWither) {
                        carePlant.Withering(-5); // 시듦수치가 조금 감소한다.
                    } else {
                        carePlant.Growing(5); // 식물이 조금 성장한다.
                    }
                }
            } else {
                carePlant.Withering(5); // 시듦수치가 증가한다.
            }
        }
    }
}

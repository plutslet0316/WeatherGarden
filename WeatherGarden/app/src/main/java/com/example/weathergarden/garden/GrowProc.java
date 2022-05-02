package com.example.weathergarden.garden;

import java.util.List;

public class GrowProc {
    GardenDao dao;

    // 생성자
    public GrowProc(GardenDao gardenDao) {
        dao = gardenDao;
    }

    // 식물 심기
    public void PlantingPlant(int gNo, String pCode){
        GroundInfo groundInfo = new GroundInfo();
        groundInfo.SetGroundInfo(gNo, pCode,0,0,0,0,0);
        dao.InsertGroundInfo(groundInfo);
    }

    // 식물 뽑기
    public void RemovePlant(int groundNo){
        dao.DeleteGroundWithGroundNO(groundNo);
    }

    // 식물 성장 시작
    public void GrowStartAll(List<GardenInfo> gList){
        // 리스트에서 하나씩 확인해서 각각 상황에 맞게 성장시킨다.
        for (GardenInfo g : gList) {
            GrowManager gm = new GrowManager(g);

            boolean isHasWater = (float) g.groundInfo.water / g.plantInfo.waterLimit>= 30;
            boolean isHasNutri = (float) g.groundInfo.nutri/ g.plantInfo.nutriLimit> 0;
            boolean isHasWither = (float) g.groundInfo.wither>= 30;

            // 물 -> 영양 -> 시듦 순서로 확인한다.
            if (isHasWater) {
                if (isHasNutri) {
                    if (isHasWither) {
                        gm.Withering(-10); // 시듦수치가 많이 감소한다.
                    } else {
                        gm.Growing(10); // 식물이 많이 성장한다.
                    }
                } else {
                    if (isHasWither) {
                        gm.Withering(-5); // 시듦수치가 조금 감소한다.
                    } else {
                        gm.Growing(5); // 식물이 조금 성장한다.
                    }
                }
            } else {
                gm.Withering(5); // 시듦수치가 증가한다.
            }
        }
    }

    // 성장 관련된 메서드를 모아둔 것
    public class GrowManager {
        GroundInfo g;
        PlantInfo p;

        GrowManager(GardenInfo gardenInfo) {
            g = gardenInfo.groundInfo;
            p = gardenInfo.plantInfo;
        }

        // 정원 정보 가져오기? 필요 없을지도 모른다. 보류


        /*
            물
            물 줄 때 최대치 * 1.2 이상 물 줄 수 없도록
            만약 미니게임 방식으로 바꾼다면 조금 더 고민해야할 듯
            0을 반환하면 줄 수 없는 상태
            1을 반환하면 성공적으로 준 것
        */
        public int Water(int value) {
            // 최대치의 1.2가 넘는지 확인
            boolean checkLimit = g.water < p.waterLimit * 1.2;

            if (checkLimit)
                g.water += value;
            else
                return 0;

            dao.UpdateGroundInfo(g);
            return 1;
        }

        /*
            영양
            물 주기와 같은 방식으로 할 지
            아니면 그냥 최대를 넘지 않는 방식으로 할 지
            고민해봐야한다.
        */
        public int Nutrient(int value) {
            boolean checkLimit = g.nutri < p.nutriLimit * 1.2;

            if (checkLimit)
                g.nutri += value;
            else
                return 0;

            g.nutri += value;

            dao.UpdateGroundInfo(g);
            return 1;
        }

        // 식물 시듦
        private void Withering(int value) {
            g.wither += value;
            dao.UpdateGroundInfo(g);
        }

        // 식물 성장
        private void Growing(int value) {
            g.growPoint += value;

            // 요구치 가져오고, 0부터 시작하는 레벨에 1 곱해서 요구치 가져옴
            int growRequire = p.growRequire * (g.growLevel + 1);

            // 현재 레벨과 성장 요구치를 넘는지 확인해서
            boolean checkLevel = g.growLevel < p.growLimit;
            boolean checkRequire = growRequire <= g.growPoint;

            // 현재 포인트를 요구치만큼 지우고, 레벨 증가
            if (checkLevel && checkRequire) {
                g.growPoint -= growRequire;
                g.growLevel++;
            }

            // 업데이트
            dao.UpdateGroundInfo(g);
        }
    }
}

package com.example.weathergarden;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.fragment.app.Fragment;

import com.example.weathergarden.garden.GardenDao;
import com.example.weathergarden.garden.GardenDatabase;
import com.example.weathergarden.garden.GardenInfo;
import com.example.weathergarden.garden.GroundInfo;
import com.example.weathergarden.garden.GrowProc;
import com.example.weathergarden.garden.PlantInfo;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link gardenFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class gardenFragment extends Fragment implements View.OnClickListener{
    View view = null;
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;


    public gardenFragment() {
        // Required empty public constructor
    }
    Button g1, g2, g3, refresh, grow;
    TextView infoText;
    EditText differText;

    GardenDatabase gardenDatabase;
    GardenDao gardenDao;
    GrowProc growProc;

    ArrayList<PlantInfo> plantInfoList;
    List<GardenInfo> gardenList;

    boolean check = true;

    // 액티비티 인텐트 핸들러
    ActivityResultLauncher<Intent> mStartForResult = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == 10) {
                    try {
                        Intent intentGet = result.getData();

                        // 받은 식물 심은 정보가 있다면 insert
                        Thread thread = new Thread() {
                            @Override
                            public void run() {
                                super.run();
                                GroundInfo groundInfoGet = (GroundInfo) intentGet.getSerializableExtra("ground_info");

                                if (groundInfoGet != null)
                                    gardenDao.insertGroundInfo(groundInfoGet);
                            }
                        };
                        thread.start();
                        thread.join();

                        // 기다렸다가 정원 갱신
                        showGarden();
                    } catch (Exception e) {
                        Log.d("test", e.getMessage());
                    } finally {
                        return;
                    }
                }
            });

    // 정원 정보 가져와서 가공한 후 넘겨준다.
    void showGarden() {
        // 정원 정보 가져오기
        // 항상 스레드를 써야한다.
        Thread thread = new Thread() {
            @Override
            public void run() {
                super.run();
                gardenList = gardenDao.readGardenInfoList();

            }
        };
        thread.start();

        try {
            thread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        // 정보 표시.
        showInfo();
    }

    // 받은 정보 가공해서 표시하기
    void showInfo() {

        for (GardenInfo gardenInfo : gardenList) {
            GroundInfo groundInfo = gardenInfo.groundInfo;
            PlantInfo plantInfo = gardenInfo.plantInfo;
            String info;

            int plant = getResources().getIdentifier("plant" + groundInfo.groundNo, "id", getContext().getPackageName());
            int plant_img = getResources().getIdentifier("plant" + groundInfo.groundNo+"_img", "id", getContext().getPackageName());


            info = "이름: " + plantInfo.name + "\n\n";

            info += "성장\n";
            info += "  - 단계:  " + groundInfo.growLevel + "\n";
            info += "  - 수치:  " + groundInfo.growPoint + "\n\n";

            info += "상태\n";
            info += "  - 수분:  " + groundInfo.water + "\n";
            info += "  - 영양:  " + groundInfo.nutrient + "\n";
            info += "  - 시듦:  " + groundInfo.wither;

            TextView plantText =  view.findViewById(plant);
            ImageView plantImg =  view.findViewById(plant_img);

            plantText.setText("");
            plantText.setText(info);

            InputStream img = null;
            try {
                img = getResources().getAssets().open(plantInfo.img+"/" + (groundInfo.growLevel) + ".png");
            } catch (IOException e) {
                e.printStackTrace();
            }
            Drawable drawable = Drawable.createFromStream(img, null);

            plantImg.setImageDrawable(drawable);
        }
    }

    boolean checkGround(int groundNo) {
        Intent intent = new Intent(getContext(), PopupPlantSelectTest.class);
        intent.putExtra("plant_info", plantInfoList);
        check = true;

        Thread thread = new Thread() {
            @Override
            public void run() {
                super.run();
                if (gardenDao.readGroundWithGroundNo(groundNo) == null) {
                    intent.putExtra("ground_no", groundNo);
                    mStartForResult.launch(intent);
                    check = false;
                }
            }
        };
        thread.start();

        try {
            thread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        return check;
    }

    void growing()
    {
        Thread thread = new Thread() {
            @Override
            public void run() {
                super.run();
                GrowProc gp = new GrowProc(getContext()).withDao(gardenDao);
                if(gp.startGrowing(getContext()) == 1){
                    infoText.setText("식물을 성장시킵니다.");
                }else {
                    infoText.setText("식물이 성장하기엔 시간이 이릅니다.");
                }
            }
        };
        thread.start();

        try {
            thread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    void growing(int differ)
    {
        Thread thread = new Thread() {
            @Override
            public void run() {
                super.run();
                GrowProc gp = new GrowProc(getContext()).withDao(gardenDao);
                if(gp.startGrowing(getContext(), differ) == 1){
                    infoText.setText("식물을 성장시킵니다.");
                }else {
                    infoText.setText("식물이 성장하기엔 시간이 이릅니다.");
                }
            }
        };
        thread.start();

        try {
            thread.join();
            showGarden();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment gardenFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static gardenFragment newInstance(String param1, String param2) {
        gardenFragment fragment = new gardenFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_garden, container, false);

        infoText = view.findViewById(R.id.info);
        differText = view.findViewById(R.id.time_differ);

        g1 = view.findViewById(R.id.ground1);
        g2 = view.findViewById(R.id.ground2);
        g3 = view.findViewById(R.id.ground3);

        refresh = view.findViewById(R.id.refresh);
        grow = view.findViewById(R.id.growup);

        g1.setOnClickListener(this);
        g2.setOnClickListener(this);
        g3.setOnClickListener(this);
        refresh.setOnClickListener(this);
        grow.setOnClickListener(this);
        // DB 연동
        Thread t = new Thread() {
            @Override
            public void run() {
                super.run();
                try {
                    gardenDatabase = GardenDatabase.getInstance(getContext());
                    gardenDao = gardenDatabase.gardenDao();
                    growProc = new GrowProc().withDao(gardenDao);
                    growing();

                    plantInfoList = (ArrayList<PlantInfo>) gardenDao.readPlantsList();
                } catch (Exception e) {
                    Log.d("test", e.getMessage());
                }
            }
        };
        t.start();
        // 스레드 기다리고 끝나면 정원 보이기
        try {
            t.join();
            showGarden();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return view;
    }

    @Override
    public void onClick(View view) {
        int index = 0;
        try {
            infoText.setText("");

            switch (view.getId()) {
                case R.id.ground1:
                    index = 1;
                    break;
                case R.id.ground2:
                    index = 2;
                    break;
                case R.id.ground3:
                    index = 3;
                    break;
                case R.id.refresh:
                    showGarden();
                    break;
                case R.id.growup:
                    growing(Integer.valueOf(differText.getText().toString()));
                    break;
            }
        } finally {
            if (index == 0) return;

            if (checkGround(index)) {
                PopupCarePlant popupCarePlant = new PopupCarePlant(getActivity(), view, growProc, index);
                popupCarePlant.displayPopupWindow();
            }
        }

    }
}
package com.example.weathergarden;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.fragment.app.Fragment;

import com.example.weathergarden.garden.GardenDao;
import com.example.weathergarden.garden.GardenDatabase;
import com.example.weathergarden.garden.GroundInfo;
import com.example.weathergarden.garden.GrowProc;
import com.example.weathergarden.garden.PlantInfo;
import com.example.weathergarden.garden.ShowDao;
import com.example.weathergarden.garden.ShowGarden;
import com.example.weathergarden.garden.ShowInfo;
import com.google.gson.Gson;
import com.unity3d.player.UnityFragment;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link GardenFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class GardenFragment extends Fragment implements View.OnClickListener {
    View view = null, frame = null, dialogView =null;
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    GestureDetector detector;

    Button waterButton, fertiButton, tempButton, humButton, plantButton, growButton;
    AlertDialog.Builder dig;

    int glowUp;
    Timer timer;
    TimerTask timerTask;

    GardenDatabase gardenDatabase;
    GardenDao gardenDao;
    GrowProc growProc;
    GrowProc.CarePlant carePlant;

    ShowDao showDao;
    ShowGarden showGarden;
    PopupCarePlant popupCarePlant;

    ArrayList<PlantInfo> plantInfoList;
    UnityFragment unityFragment;
    Gson gson;

    int index;

    boolean check = true;

    public GardenFragment() {
        // Required empty public constructor
    }

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
                                assert intentGet != null;
                                GroundInfo groundInfoGet = (GroundInfo) intentGet.getSerializableExtra("ground_info");

                                if (groundInfoGet != null)
                                    gardenDao.insertGroundInfo(groundInfoGet);
                            }
                        };
                        thread.start();
                        thread.join();
                        plantButton.setText("뽑기");

                        // 기다렸다가 정원 갱신
//                        showGarden.show();
                        unityFragment.SendMessage("GameManager", "setPlantInfo", gson.toJson(showGarden.getData()));

                    } catch (Exception e) {
                        Log.d("test", e.getMessage());
                    } finally {
                        return;
                    }
                }
                else if (result.getResultCode() == 20) {
                    try {
                        Intent intentGet = result.getData();

                        // Delete
                        Thread thread = new Thread() {
                            @Override
                            public void run() {
                                super.run();
                                if(intentGet.getIntExtra("delete", 0) == 1) {
                                    popupCarePlant.removePlant();
                                }
                            }
                        };
                        thread.start();
                        thread.join();

                        // 기다렸다가 정원 갱신
//                        showGarden.show();
                        unityFragment.SendMessage("GameManager", "setPlantInfo", gson.toJson(showGarden.getData()));

                    } catch (Exception e) {
                        Log.d("test", e.getMessage());
                    } finally {
                        return;
                    }
                }
            });

    void plantGround(int groundNo){
        Intent intent = new Intent(view.getContext(), PopupPlantSelect.class);
        intent.putExtra("plant_info", plantInfoList);
        intent.putExtra("ground_no", groundNo);
        mStartForResult.launch(intent);
    }

    // 물/영양주기 메서드
    void addValue(String type){
        // 스레드 생성
        Thread thread = new Thread(){
            @Override
            public void run() {
                super.run();
                carePlant = growProc.new CarePlant().withGroundNo(1); // 화분 관리하는 클래스 생성
                if(type.equals("water"))
                    carePlant.addWater(500);    // 수분 500만큼 추가
                else if(type.equals("nutri"))
                    carePlant.addNutrient(500); // 영양 500만큼 추가

            }
        };
        thread.start(); // 스레드 시작

        try {
            thread.join(); // 스레드 종료까지 기다림
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    boolean checkGround(int groundNo) {
        check = true;
        Thread thread = new Thread() {
            @Override
            public void run() {
                super.run();
                if (gardenDao.readGroundWithGroundNo(groundNo) == null) {
                    check = false;
                }
            }
        };
        thread.start();

        try {
            thread.join();

            if(check)   plantButton.setText("뽑기");
            else        plantButton.setText("심기");

        } catch (InterruptedException interruptedException) {
            interruptedException.printStackTrace();
        }
        return check;
    }



    void setGlowTime(int time){
        glowUp = time;
    }
    void growing(int time)
    {
        Thread thread = new Thread() {
            @Override
            public void run() {
                super.run();
                GrowProc gp = new GrowProc(view.getContext()).withDao(gardenDao);
                if(gp.startGrowing(getContext(), time) == 1){
                    // 성장한 시간만큼 시간 추가
                    ShowInfo showInfo = showDao.getShowInfo();
                    showInfo.time += time;
                    showDao.setShowInfo(showInfo);
//                    infoText.setText("식물이 " + differ + "시간씩 성장합니다.");
                }else {
//                    infoText.setText("식물이 성장하지 않습니다.");
                }
            }
        };
        thread.start();

        try {
            thread.join();
//            showGarden.show();
            unityFragment.SendMessage("GameManager", "setPlantInfo", gson.toJson(showGarden.getData()));

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
    public static GardenFragment newInstance(String param1, String param2) {
        GardenFragment fragment = new GardenFragment();
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

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_garden, container, false);
        frame = view.findViewById(R.id.unity_frame);
        showDao = new ShowDao(view.getContext());
        gson = new Gson();

//    Button waterButton, fertiButton, tempButton, humiButton, plantButton, growButton;
        waterButton = view.findViewById(R.id.watering_button);
        fertiButton = view.findViewById(R.id.fertilizer_button);
        tempButton = view.findViewById(R.id.temperature_button);
        humButton = view.findViewById(R.id.humidity_button);
        plantButton = view.findViewById(R.id.planting_button);
        growButton = view.findViewById(R.id.growing_button);

        glowUp = 0;

        timer = new Timer();
        timerTask = getTimerTask();

        unityFragment = new UnityFragment();

        getChildFragmentManager().beginTransaction().add(R.id.unity_frame, unityFragment).commitAllowingStateLoss();

        plantButton.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                index = 1;
                detector.onTouchEvent(motionEvent);
                return false;
            }
        });

        waterButton.setOnClickListener(this);
        fertiButton.setOnClickListener(this);
        tempButton.setOnClickListener(this);
        humButton.setOnClickListener(this);
        growButton.setOnClickListener(this);

        detector = new GestureDetector(view.getContext(), new GestureDetector.OnGestureListener() {

            @Override
            public boolean onDown(MotionEvent motionEvent) {
                return false;
            }

            //화면이 눌렸다 떼어지는 경우
            @Override
            public void onShowPress(MotionEvent e) {

            }

            //화면이 한 손가락으로 눌렸다 떼어지는 경우
            @Override
            public boolean onSingleTapUp(MotionEvent e) {
                if (index == 0) return false;

                if (checkGround(index)) {
                    Thread thread = new Thread(){
                        @Override
                        public void run() {
                            super.run();
                            carePlant = growProc.new CarePlant().withGroundNo(index);
                            carePlant.removePlant(index);
                        }
                    };
                    thread.start();

                    try {
                        thread.join();

                        // 식물 뽑기시
                        // 식물 기른 시간 초기화
                        ShowInfo showInfo = showDao.getShowInfo();
                        showInfo.time = 0;
                        showDao.setShowInfo(showInfo);

                        // 성장 멈추고 텍스트 심기로 변경
                        glowUp = 0;
                        plantButton.setText("심기");

                        unityFragment.SendMessage("GameManager", "setPlantInfo", gson.toJson(showGarden.getData()));
                    } catch (InterruptedException interruptedException) {
                        interruptedException.printStackTrace();
                    }
                }
                else {
                    plantGround(index);
                }

                return true;
            }

            //화면이 눌린채 일정한 속도와 방향으로 움직였다 떼어지는 경우
            @Override
            public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
                return false;
            }

            //화면을 손가락으로 오랫동안 눌렀을 경우
            @Override
            public void onLongPress(MotionEvent e) {
            }

            //화면이 눌린채 손가락이 가속해서 움직였다 떼어지는 경우
            @Override
            public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
                return true;
            }
        });

        // DB 연동
        Thread t = new Thread() {
            @Override
            public void run() {
                super.run();
                try {
                    gardenDatabase = GardenDatabase.getInstance(getContext());
                    gardenDao = gardenDatabase.gardenDao();
                    growProc = new GrowProc(view.getContext()).withDao(gardenDao);

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
            showGarden = new ShowGarden(view, (Activity) view.getContext(), gardenDao);

            checkGround(1);


            // 유니티 로딩 이후 실행되도록 1초 지연 실행
            new Handler().postDelayed(() ->
                    unityFragment.SendMessage("GameManager", "setPlantInfo", gson.toJson(showGarden.getData()))
                    , 1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return view;
    }

    private TimerTask getTimerTask() {
        return new TimerTask() {
            @Override
            public void run() {
                growing(glowUp);
            }
        };
    }

    private void newDialog(String type) {
        dig = new AlertDialog.Builder(view.getContext());
        dialogView = View.inflate(view.getContext(), R.layout.dialog_input, null);

        ShowInfo showInfo = showDao.getShowInfo();
        TextView min = dialogView.findViewById(R.id.dialog_min);
        TextView max = dialogView.findViewById(R.id.dialog_max);
        TextView cur = dialogView.findViewById(R.id.dialog_current);
        SeekBar bar = dialogView.findViewById(R.id.seekBar);
        DialogInterface.OnClickListener listener = null;

        switch (type) {
            case "temp":
                dig.setTitle("온도 조절");
                min.setText("0℃");
                max.setText("45℃");
                bar.setProgress(Integer.valueOf(showInfo.temp));

                bar.setMax(45);

                listener = (dialogInterface, i) -> {
                    showInfo.temp = bar.getProgress()+"";
                    showDao.setShowInfo(showInfo);
                    unityFragment.SendMessage("GameManager", "setPlantInfo", gson.toJson(showGarden.getData()));
                };
                break;
            case "hum":
                dig.setTitle("습도 조절");
                min.setText("20%");
                max.setText("100%");
                bar.setProgress(Integer.valueOf(showInfo.hum));

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    bar.setMin(20);
                }
                bar.setMax(100);

                listener = (dialogInterface, i) -> {
                    showInfo.hum = bar.getProgress()+"";
                    showDao.setShowInfo(showInfo);
                    unityFragment.SendMessage("GameManager", "setPlantInfo", gson.toJson(showGarden.getData()));
                };
                break;
        }

        cur.setText(String.format("%d", bar.getProgress()));
        bar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                cur.setText(String.format("%d", seekBar.getProgress()));
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                cur.setText(String.format("%d", seekBar.getProgress()));
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                cur.setText(String.format("%d", seekBar.getProgress()));
            }
        });

        dig.setView(dialogView);
        dig.setPositiveButton("확인", listener);
        dig.setNegativeButton("취소", null);
    }

    @Override
    public void onClick(View view) {

        switch (view.getId()) {

            case R.id.watering_button:
                // 물 주는 부분
                if(checkGround(1)) addValue("water");
                else Toast.makeText(view.getContext(), "아직 식물을 심지 않았습니다.", Toast.LENGTH_SHORT).show();
                unityFragment.SendMessage("GameManager", "addWater", "");
                break;
            case R.id.fertilizer_button:
                // 비료 주는 부분
                if(checkGround(1)) addValue("nutri");
                else Toast.makeText(view.getContext(), "아직 식물을 심지 않았습니다.", Toast.LENGTH_SHORT).show();
                unityFragment.SendMessage("GameManager", "addNutrient", "");
                break;
            case R.id.temperature_button:
                // 온도 변경하는 부분
                newDialog("temp");
                dig.show();
                break;
            case R.id.humidity_button:
                // 습도 변경하는 부분
                newDialog("hum");
                dig.show();
                break;
            case R.id.growing_button:
                if(checkGround(1)) {
                    if(glowUp == 0) glowUp = 36;
                    else glowUp = 0;
                }
                else {
                    glowUp = 0;
                    Toast.makeText(view.getContext(), "아직 식물을 심지 않았습니다.", Toast.LENGTH_SHORT).show();
                }
                break;
        }

        unityFragment.SendMessage("GameManager", "setPlantInfo", gson.toJson(showGarden.getData()));

        if (glowUp == 0)
            timerTask.cancel();
        else {
            timerTask.cancel();
            timerTask = getTimerTask();
            timer.schedule(timerTask, 0, 1000);
        }
    }
}
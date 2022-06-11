package com.example.weathergarden;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.HorizontalScrollView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.fragment.app.Fragment;

import com.example.weathergarden.garden.GardenDao;
import com.example.weathergarden.garden.GardenDatabase;
import com.example.weathergarden.garden.GardenInfo;
import com.example.weathergarden.garden.GroundInfo;
import com.example.weathergarden.garden.GrowProc;
import com.example.weathergarden.garden.PlantInfo;
import com.example.weathergarden.garden.ShowGarden;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link gardenFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class gardenFragment extends Fragment {
    View view = null, frame;
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    GestureDetector detector;

    Button g1, g2, g3;
    EditText differText;

    GardenDatabase gardenDatabase;
    GardenDao gardenDao;
    GrowProc growProc;

    ShowGarden showGarden;

    ArrayList<PlantInfo> plantInfoList;

    int index;


    boolean check = true;

    public gardenFragment() {
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

                        // 기다렸다가 정원 갱신
                        showGarden.show();
                    } catch (Exception e) {
                        Log.d("test", e.getMessage());
                    } finally {
                        return;
                    }
                }
            });


    boolean checkGround(int groundNo) {
        Intent intent = new Intent(view.getContext(), PopupPlantSelectTest.class);
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
                GrowProc gp = new GrowProc(view.getContext()).withDao(gardenDao);
                if(gp.startGrowing(getContext()) == 1) {
                    Toast.makeText(view.getContext(), "식물이 성장합니다.", Toast.LENGTH_SHORT).show();
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

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_garden, container, false);
        frame = view.findViewById(R.id.garden_frame);

        differText = view.findViewById(R.id.time_differ);

        g1 = view.findViewById(R.id.ground1);
        g2 = view.findViewById(R.id.ground2);
        g3 = view.findViewById(R.id.ground3);

        g1.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                index = 1;
                detector.onTouchEvent(motionEvent);
                return false;
            }
        });
        g2.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                index = 2;
                detector.onTouchEvent(motionEvent);
                return false;
            }
        });
        g3.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                index = 3;
                detector.onTouchEvent(motionEvent);
                return false;
            }
        });
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
                //Log.d("Popup", e.getX() + " " + e.getY() + " " + index + " " + frame.getScrollX());
                if (index == 0) return false;

                int x = (int) e.getX() + (g1.getWidth() * (index-1)) - frame.getScrollX();
                int y = (int) e.getY();

                if (checkGround(index)) {
                    PopupCarePlant popupCarePlant = new PopupCarePlant((Activity) view.getContext(), view, growProc, gardenDao, index, x, y);
                    popupCarePlant.displayPopupWindow();
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
            showGarden = new ShowGarden(view, (Activity) view.getContext(), gardenDao);
            showGarden.show();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return view;
    }
}
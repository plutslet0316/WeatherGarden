package com.example.weathergarden;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link SettingsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class SettingsFragment extends Fragment implements View.OnClickListener {
    //private Switch not_swi;
    private TextView loc_set, not_tit;
    private ImageView set_icon;
    private Button on, off;
    private View not_box;

    Intent serviceIntent;

    View view;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_settings, container, false);

        loc_set = view.findViewById(R.id.location_set);
        not_tit = view.findViewById(R.id.noti_title);
        //not_swi_sma = view.findViewById(R.id.noti_switch_small);

        //not_swi = view.findViewById(R.id.noti_switch);

        set_icon = view.findViewById(R.id.setting_icon);

        not_box = view.findViewById(R.id.noti_box);

        on = (Button) view.findViewById(R.id.noti_on);
        off = (Button) view.findViewById(R.id.noti_off);

        on.setOnClickListener(this);
        off.setOnClickListener(this);

        return view;
    }

    // TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public SettingsFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment settingsFragment.
     */
// TODO: Rename and change types and number of parameters
    public static SettingsFragment newInstance(String param1, String param2) {
        SettingsFragment fragment = new SettingsFragment();
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
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.noti_on:
                startService();
                Log.d("a", "on");
                break;
            case R.id.noti_off:
                stopService();
                Log.d("a", "off");
                break;
        }
    }

    public void startService() {
        Activity activity = (Activity) view.getContext();
        serviceIntent = new Intent(activity, MyService.class);
        activity.startService(serviceIntent);
    }

    public void stopService() {
        Activity activity = (Activity) view.getContext();
        serviceIntent = new Intent(activity, MyService.class);
        activity.stopService(serviceIntent);

    }


}

//package com.example.weathergarden;
//
//import android.os.Bundle;
//import android.view.LayoutInflater;
//import android.view.View;
//import android.view.ViewGroup;
//import android.widget.Button;
//import android.widget.TextView;
//
//import androidx.annotation.NonNull;
//import androidx.annotation.Nullable;
//import androidx.fragment.app.Fragment;
//import androidx.fragment.app.FragmentTransaction;
//
//public class FragmentSunflower extends Fragment {
//
//    private View view;
//    private TextView title;
//    private Button button2;
//    private String result;
//
//    @Nullable
//    @Override
//    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
//        view = inflater.inflate(R.layout.fragment_sunflower, container, false);
//
//        title = view.findViewById(R.id.title);
//        // button2 = view.findViewById(R.id.button2);
//
//        if(getArguments() != null )
//        {
//            result = getArguments().getString("fromFrag1");
//            //sunflower.setText(result);
//        }
//
//        button2.setOnClickListener((view) -> { // 해바라기 프래그먼트로
//            Bundle bundle = new Bundle();   //무언가를 담을 준비를 할 수 있는 보따리
//            bundle.putString("fromFrag2", "해바라기 프래그먼트");
//            FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
//           // FragmentTulip fragmentTulip = new FragmentTulip();
//           // fragmentTulip.setArguments(bundle);
//           // transaction.replace(R.id.frameLayout, fragmentTulip);
//            transaction.commit();
//        });
//        return view;
//    }
//
//    public static class Fragment1 extends Fragment {
//
//        private View view;
//        private TextView tulip;
//        private Button button;
//        private String result;
//
//        @Nullable
//        @Override
//        public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
//            view = inflater.inflate(R.layout.fragment_tulip, container, false);
//
//            tulip = view.findViewById(R.id.tulip);
//            button = view.findViewById(R.id.button);
//
//            if(getArguments() != null )
//            {
//                result = getArguments().getString("fromFrag2");
//                tulip.setText(result);
//            }
//
//            button.setOnClickListener(new View.OnClickListener() { // 해바라기 프래그먼트로
//                @Override
//                public void onClick(View view) {
//                    Bundle bundle = new Bundle();   //무언가를 담을 준비를 할 수 있는 보따리
//                    bundle.putString("fromFrag1", "튤립 프래그먼트");
//                    FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
//                    FragmentSunflower fragmentSunflower = new FragmentSunflower();
//                    fragmentSunflower.setArguments(bundle);
//                    transaction.replace(R.id.frameLayout, fragmentSunflower);
//                    transaction.commit();
//
//                }
//            });
//            return view;
//        }
//    }
//}
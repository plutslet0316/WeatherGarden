package com.example.weathergarden;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;




public class bookFragment extends Fragment {
    private View view;
    private ImageButton tulip, sunflower;
    private ImageView circle, rectangle;
    private TextView title;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_book, container, false);

        tulip = (ImageButton) view.findViewById(R.id.tulip);
        sunflower = (ImageButton) view.findViewById(R.id.sunflower);
        circle = view.findViewById(R.id.circle);
        rectangle = view.findViewById(R.id.rectangle);
        title = view.findViewById(R.id.title);

        tulip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), TulipBook.class);
                startActivity(intent);
            }
        });

        sunflower.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), SunflowerBook.class);
                startActivity(intent);
            }
        });

        return view;


    }

}
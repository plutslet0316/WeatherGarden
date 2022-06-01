package com.example.weathergarden;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;

import androidx.annotation.Nullable;

public class weather_gps extends Activity {
    ImageButton add, back;
    ImageView gps;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.weather_gps);

        add = (ImageButton) findViewById(R.id.add);
        back = (ImageButton) findViewById(R.id.back);
        gps = (ImageView) findViewById(R.id.gps_icon);


        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(weather_gps.this, weather_add.class);
                startActivity(intent);
            }
        });

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

    }
}

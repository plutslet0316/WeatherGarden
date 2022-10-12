package com.example.weathergarden;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;

public class TulipBook extends Activity {
    ImageButton tulip1, sunflower1;
    ImageView circle1, rectangle1;
    TextView title2;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.book_tulip);

        tulip1 = (ImageButton) findViewById(R.id.tulip1);
        sunflower1 = (ImageButton) findViewById(R.id.sunflower1);
        circle1 = (ImageView) findViewById(R.id.circle1);
        rectangle1 = (ImageView) findViewById(R.id.rectangle1);
        title2 = (TextView) findViewById(R.id.title2);

        sunflower1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(TulipBook.this, SunflowerBook.class);
                startActivity(intent);
            }
        });
    }
}

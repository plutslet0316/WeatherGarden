package com.example.weathergarden;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;

import androidx.annotation.Nullable;

public class SunflowerBook extends Activity {
    ImageButton tulip2, sunflower2;
    ImageView circle2, rectangle2;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.book_sunflower);

        tulip2 = (ImageButton) findViewById(R.id.tulip2);
        sunflower2 = (ImageButton) findViewById(R.id.sunflower2);
        circle2 = (ImageView) findViewById(R.id.circle2);
        rectangle2 = (ImageView) findViewById(R.id.rectangle2);

        tulip2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(SunflowerBook.this, TulipBook.class);
                startActivity(intent);
            }
        });
    }

}

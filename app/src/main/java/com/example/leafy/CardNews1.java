package com.example.leafy;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.view.View;


import com.smarteist.autoimageslider.IndicatorView.animation.type.IndicatorAnimationType;
import com.smarteist.autoimageslider.SliderAnimations;
import com.smarteist.autoimageslider.SliderView;

public class CardNews1 extends Activity {

    ImageView back_btn;

    SliderView sliderView;
    int[] images = {R.drawable.cardnews01_1,
            R.drawable.cardnews01_2,
            R.drawable.cardnews01_3,
            R.drawable.cardnews01_4,
            R.drawable.cardnews01_5};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cardnews_01);

        sliderView = findViewById(R.id.slider1);

        SliderAdapter sliderAdapter = new SliderAdapter(images, 1);
        sliderView.setSliderAdapter(sliderAdapter);
        sliderView.setIndicatorAnimation(IndicatorAnimationType.WORM);
        sliderView.setSliderTransformAnimation(SliderAnimations.DEPTHTRANSFORMATION);
        sliderView.startAutoCycle();


        back_btn  = (ImageView) findViewById(R.id.imageView2);
        back_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    Intent intent = new Intent(getApplicationContext(), CardNewsActivity.class);
                    startActivity(intent);
                }catch(Exception e){
                    Log.v("testerr",e.getMessage());
                }
            }
        });
    }
}

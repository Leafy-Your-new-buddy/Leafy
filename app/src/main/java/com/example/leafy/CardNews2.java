package com.example.leafy;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import com.smarteist.autoimageslider.IndicatorView.animation.type.IndicatorAnimationType;
import com.smarteist.autoimageslider.SliderAnimations;
import com.smarteist.autoimageslider.SliderView;

public class CardNews2 extends Activity {

    ImageView back_btn;

    SliderView sliderView;
    int[] images = {R.drawable.cardnews02_1,
            R.drawable.cardnews02_2,
            R.drawable.cardnews02_3,
            R.drawable.cardnews02_4,
            R.drawable.cardnews02_5,
            R.drawable.cardnews02_6,
            R.drawable.cardnews02_7,
            R.drawable.cardnews02_8,
            R.drawable.cardnews02_9,
            R.drawable.cardnews02_10,
            R.drawable.cardnews02_11,
            R.drawable.cardnews02_12};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cardnews_01);

        sliderView = findViewById(R.id.slider2);

        SliderAdapter sliderAdapter = new SliderAdapter(images, 2);
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

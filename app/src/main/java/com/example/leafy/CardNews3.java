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
import android.app.Activity;

public class CardNews3 extends Activity {

    ImageView back_btn;

    SliderView sliderView;
    int[] images = {R.drawable.cardnews03_1,
            R.drawable.cardnews03_2,
            R.drawable.cardnews03_3};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cardnews_03); //

        sliderView = findViewById(R.id.slider3); //

        SliderAdapter sliderAdapter = new SliderAdapter(images, 3);
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

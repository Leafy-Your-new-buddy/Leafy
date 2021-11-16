package com.example.leafy;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.smarteist.autoimageslider.SliderViewAdapter;

public class SliderAdapter extends SliderViewAdapter<SliderAdapter.Holder> {

    int[] images;
    int flag;

    public SliderAdapter(int[] images, int flag){
        this.images = images;
        this.flag = flag;
    }

    @Override
    public Holder onCreateViewHolder(ViewGroup parent) {
        View view = null;
        if(flag ==1) {
            view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.slider1_item,parent,false);

        }
        else if(flag ==2){
            view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.slider2_item,parent,false);
        }
        else if(flag ==3){
            view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.slider3_item,parent,false);
        }

        return new Holder(view);


    }

    @Override
    public void onBindViewHolder(Holder viewHolder, int position) {
        viewHolder.imageView.setImageResource(images[position]);
    }

    @Override
    public int getCount() {
        return images.length;
    }

    public class Holder extends SliderViewAdapter.ViewHolder{
        ImageView imageView;
        public Holder(View itemView){
            super(itemView);
            if(flag==1)
                imageView = itemView.findViewById(R.id.slider1_iv);

            else if(flag==2)
                imageView = itemView.findViewById(R.id.slider2_iv);
            else if(flag==3)
                imageView = itemView.findViewById(R.id.slider3_iv);
        }
    }
}

package com.example.datingcourse;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.viewpager.widget.PagerAdapter;

public class ImagePagerAdapter2 extends PagerAdapter {

    private Context context;
    private int[] imageIds;
    private String[] titles;
    private String[] overviews;
    private String[] times;
    private String[] prices;

    public ImagePagerAdapter2(Context context, int[] imageIds, String[] titles, String[] overviews, String[] times, String[] prices) {
        this.context = context;
        this.imageIds = imageIds;
        this.titles = titles;
        this.overviews = overviews;
        this.times = times;
        this.prices = prices;
    }

    @Override
    public int getCount() {
        return imageIds.length;
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
        return view == object;
    }

    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, int position) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.item_image, container, false);

        ImageView imageView = view.findViewById(R.id.imageView);
        imageView.setImageResource(imageIds[position]);
        TextView titleTextView = view.findViewById(R.id.title);
        titleTextView.setText(titles[position]);
        TextView overviewTextView = view.findViewById(R.id.overview);
        overviewTextView.setText(overviews[position]);
        TextView timeTextView = view.findViewById(R.id.time);
        timeTextView.setText(times[position]);
        TextView priceTextView = view.findViewById(R.id.price);
        priceTextView.setText(prices[position]);


        container.addView(view);
        return view;
    }

    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        container.removeView((View) object);
    }
}
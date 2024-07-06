package com.example.datingcourse;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import net.daum.mf.map.api.MapPoint;
import net.daum.mf.map.api.MapView;

import java.util.ArrayList;

public class CourseAdapter extends RecyclerView.Adapter<CourseAdapter.HorizontalViewHolder> implements MapView.CurrentLocationEventListener, MapView.MapViewEventListener {
    private ArrayList<Photo> dataList;
    private CourseAdapter.OnItemClickListener itemClickListener;
    public Context context;

    public CourseAdapter(Context context, ArrayList<Photo> dataList) {
        this.context = context;
        this.dataList = dataList;
    }

    public class HorizontalViewHolder extends RecyclerView.ViewHolder {
        ImageView course_imgView_item;
        TextView txt_course_name;
        TextView txt_course_address;
        Button course_finding_btns;
        public HorizontalViewHolder(View itemView) {
            super(itemView);

            course_imgView_item = itemView.findViewById(R.id.course_imgView_item);
            txt_course_name = itemView.findViewById(R.id.txt_course_name);
            txt_course_address = itemView.findViewById(R.id.txt_course_address);
            course_finding_btns = itemView.findViewById(R.id.course_finding_btns);
        }
    }

    // 아이템 뷰를 위한 뷰홀더 객체를 생성하여 리턴
    @NonNull
    @Override
    public HorizontalViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater
                .from(parent.getContext())
                .inflate(R.layout.item_course, null);

        return new CourseAdapter.HorizontalViewHolder(v);
    }

    // position에 해당하는 데이터를 뷰홀더의 아이템뷰에 표시
    @Override
    public void onBindViewHolder(@NonNull HorizontalViewHolder holder, int position) {
        Photo item = dataList.get(position);

        Glide.with(context)
                .load(item.getImgUrl())
                .error(R.drawable.noimage)
                .fallback(R.drawable.noimage)
                .placeholder(R.drawable.loading)
                .into(holder.course_imgView_item);

        holder.txt_course_name.setText(item.getTitleName());
        holder.txt_course_address.setText(item.getAddressName());
        holder.course_finding_btns.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openKakaoMapForNavigation(item.getX(), item.getY());
            }
        });

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(itemClickListener != null){
                    itemClickListener.onClick(v, position);
                }
            }
        });
    }

    private void openKakaoMapForNavigation(Double x, Double y) {
        Intent intent;
        String kakaoUri = "kakaomap://route?sp=&ep=" + y + "," + x + "&by=PUBLICTRANSIT"; // 버스 길찾기
        // 카카오맵 앱이 설치되어 있는지 확인
        try {
            context.getPackageManager().getPackageInfo(context.getPackageName(), 0);
            intent = new Intent(Intent.ACTION_VIEW, Uri.parse(kakaoUri));
        } catch (PackageManager.NameNotFoundException e) {
            // 카카오맵 앱이 설치되어 있지 않은 경우, Play Store로 연결
            intent = new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=net.daum.android.map"));
        }

        context.startActivity(intent);
    }


    public interface OnItemClickListener {
        void onClick(View v, int position);
    }

    @Override
    public int getItemCount() {
        return dataList.size();
    }

    public void setItemClickListener(CourseAdapter.OnItemClickListener onItemClickListener) {
        this.itemClickListener = onItemClickListener;
    }

    @Override
    public void onCurrentLocationUpdate(MapView mapView, MapPoint mapPoint, float v) {

    }

    @Override
    public void onCurrentLocationDeviceHeadingUpdate(MapView mapView, float v) {

    }

    @Override
    public void onCurrentLocationUpdateFailed(MapView mapView) {

    }

    @Override
    public void onCurrentLocationUpdateCancelled(MapView mapView) {

    }

    @Override
    public void onMapViewInitialized(MapView mapView) {

    }

    @Override
    public void onMapViewCenterPointMoved(MapView mapView, MapPoint mapPoint) {

    }

    @Override
    public void onMapViewZoomLevelChanged(MapView mapView, int i) {

    }

    @Override
    public void onMapViewSingleTapped(MapView mapView, MapPoint mapPoint) {

    }

    @Override
    public void onMapViewDoubleTapped(MapView mapView, MapPoint mapPoint) {

    }

    @Override
    public void onMapViewLongPressed(MapView mapView, MapPoint mapPoint) {

    }

    @Override
    public void onMapViewDragStarted(MapView mapView, MapPoint mapPoint) {

    }

    @Override
    public void onMapViewDragEnded(MapView mapView, MapPoint mapPoint) {

    }

    @Override
    public void onMapViewMoveFinished(MapView mapView, MapPoint mapPoint) {

    }
}

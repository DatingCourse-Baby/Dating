package com.example.datingcourse;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import net.daum.mf.map.api.CameraUpdateFactory;
import net.daum.mf.map.api.MapPOIItem;
import net.daum.mf.map.api.MapPoint;
import net.daum.mf.map.api.MapPointBounds;
import net.daum.mf.map.api.MapPolyline;
import net.daum.mf.map.api.MapView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class CourseListActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private CourseAdapter courseAdapter;
    private ArrayList<Photo> photos;
    MapView mapView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_course_list); // 여기서는 여러분의 레이아웃 XML 이름으로 교체해주세요.

        // 카카오 맵 초기화
        mapView = new MapView(this);
        RelativeLayout mapViewContainer = findViewById(R.id.course_mapView);
        mapViewContainer.addView(mapView);

        ArrayList<Photo> photosList = (ArrayList<Photo>) getIntent().getSerializableExtra("photosList");
        if (photosList != null) {
            mapView.removeAllPOIItems();
            // 모든 사진 위치에 마커 추가
            for (Photo photo : photosList) {
                addMarkerToMap(mapView, photo.getX(), photo.getY(), photo.getTitleName());
            }
            // 모든 사진 위치를 연결하는 폴리라인 추가
            addPolylineToMap(mapView, photosList);

            // RecyclerView 설정
            recyclerView = findViewById(R.id.course_list);

            // CourseAdapter 초기화
            courseAdapter = new CourseAdapter(this, photosList);

            recyclerView.setAdapter(courseAdapter);
            LinearLayoutManager horizontalLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
            recyclerView.setLayoutManager(horizontalLayoutManager);

            // RecyclerView 아이템 클릭 리스너 설정
            courseAdapter.setItemClickListener(new CourseAdapter.OnItemClickListener() {
                @Override
                public void onClick(View v, int position) {
                    Photo clickedPhoto = photosList.get(position);
                    Intent intent = new Intent(CourseListActivity.this, CourseInfo.class);
                    intent.putExtra("photosList", photosList);
                    intent.putExtra("imgUrl", clickedPhoto.getImgUrl());
                    intent.putExtra("titleName", clickedPhoto.getTitleName());
                    intent.putExtra("addressName", clickedPhoto.getAddressName());
                    intent.putExtra("x", clickedPhoto.getX());
                    intent.putExtra("y", clickedPhoto.getY());
                    intent.putExtra("tel", clickedPhoto.getTel());
                    startActivity(intent);
                }
            });
        }
    }

    private void addMarkerToMap(MapView mapView, double x, double y, String name) {
        MapPoint mapPoint = MapPoint.mapPointWithGeoCoord(y, x);
        net.daum.mf.map.api.MapPOIItem marker = new net.daum.mf.map.api.MapPOIItem();
        marker.setItemName(name);
        marker.setTag(0);
        marker.setMapPoint(mapPoint);
        marker.setMarkerType(MapPOIItem.MarkerType.CustomImage); // 기본으로 제공하는 BluePin 마커 모양.
        marker.setCustomImageResourceId(R.drawable.custom_marker1);
        marker.setSelectedMarkerType(MapPOIItem.MarkerType.CustomImage); // 마커를 클릭했을때, 기본으로 제공하는 RedPin 마커 모양.

        marker.setCustomSelectedImageResourceId(R.drawable.custom_marker2);
        mapView.addPOIItem(marker);
    }

    private void addPolylineToMap(MapView mapView, List<Photo> photos) {
        MapPolyline polyline = new MapPolyline();
        polyline.setTag(1000);
        for (Photo photo : photos) {
            polyline.addPoint(MapPoint.mapPointWithGeoCoord(photo.getY(), photo.getX()));
        }
        mapView.addPolyline(polyline);

        MapPointBounds mapPointBounds = new MapPointBounds(polyline.getMapPoints());
        int padding = 100; // px
        mapView.moveCamera(CameraUpdateFactory.newMapPointBounds(mapPointBounds, padding));
    }

    @Override
    public void onBackPressed() {
        mapView.removeAllPOIItems();
        // 기존 액티비티 스택을 사용하여 이전 화면(CourseListActivity)으로 돌아감
        super.onBackPressed();
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (mapView != null) {
            mapView.setCurrentLocationTrackingMode(MapView.CurrentLocationTrackingMode.TrackingModeOff);
            mapView.setShowCurrentLocationMarker(false);
            if (mapView.getParent() != null) {
                ((ViewGroup) mapView.getParent()).removeView(mapView);
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (mapView != null) {
//            if (mapView.getParent() == null) {
//                RelativeLayout mapViewContainer = findViewById(R.id.course_mapView);
//                mapViewContainer.addView(mapView);
//            }
            //mapView.setCurrentLocationTrackingMode(MapView.CurrentLocationTrackingMode.TrackingModeOnWithoutHeadingWithoutMapMoving);
            mapView.setShowCurrentLocationMarker(false);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mapView != null) {
            mapView.setCurrentLocationTrackingMode(MapView.CurrentLocationTrackingMode.TrackingModeOff);
            mapView.setShowCurrentLocationMarker(false);
        }
    }

}
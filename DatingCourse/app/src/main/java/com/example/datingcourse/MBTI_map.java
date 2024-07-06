package com.example.datingcourse;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import net.daum.mf.map.api.CameraUpdateFactory;
import net.daum.mf.map.api.MapPOIItem;
import net.daum.mf.map.api.MapPoint;
import net.daum.mf.map.api.MapPointBounds;
import net.daum.mf.map.api.MapPolyline;
import net.daum.mf.map.api.MapView;

import java.util.ArrayList;
import java.util.List;

public class MBTI_map extends AppCompatActivity implements MapView.CurrentLocationEventListener, MapView.MapViewEventListener{


    private MapView mapView;
    private ViewGroup mapViewContainer;

    private ImagePagerAdapter2 adapter;
    private ViewPager viewPager;
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mbti_map);

        mapView = new MapView(this);
        mapViewContainer = (ViewGroup) findViewById(R.id.map_view);
        mapViewContainer.addView(mapView);
        mapView.setMapViewEventListener(this);
        //mapView.setCurrentLocationTrackingMode(MapView.CurrentLocationTrackingMode.TrackingModeOnWithoutHeading);

        Intent intent = getIntent();
        String predict_time = intent.getStringExtra("time");
        String predict_price = intent.getStringExtra("price");
        ArrayList<String> titles = intent.getStringArrayListExtra("titles");
        ArrayList<Integer> images = intent.getIntegerArrayListExtra("images");
        ArrayList<String> X = intent.getStringArrayListExtra("X");
        ArrayList<String> Y = intent.getStringArrayListExtra("Y");
        ArrayList<String> overview = intent.getStringArrayListExtra("overview");
        ArrayList<String> times = intent.getStringArrayListExtra("times");
        ArrayList<String> prices = intent.getStringArrayListExtra("prices");

        MapSetting(titles,images,X,Y,overview,times,prices);

        TextView pred_time = findViewById(R.id.pred_time);
        TextView pred_price = findViewById(R.id.pred_price);

        pred_time.setText(predict_time);
        pred_price.setText(predict_price);


        int[] imageIds = new int[images.size()];
        for (int i = 0; i < images.size(); i++) {
            imageIds[i] = images.get(i);
        }

        viewPager = findViewById(R.id.viewPager);
        adapter = new ImagePagerAdapter2(this,imageIds,titles.toArray(new String[0]), overview.toArray(new String[0]), times.toArray(new String[0]), prices.toArray(new String[0]));
        viewPager.setAdapter(adapter);
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                // 페이지 스크롤 중에 아무 작업 필요 없음
            }

            @Override
            public void onPageSelected(int position) {
                // 페이지가 선택되었을 때 해당 페이지에 맞는 마커를 선택
                mapView.selectPOIItem(mapView.findPOIItemByTag(position), true);
            }

            @Override
            public void onPageScrollStateChanged(int state) {
                // 페이지 스크롤 상태 변경 시 아무 작업 필요 없음
            }
        });

    }


    public void MapSetting(List<String> titles,List<Integer> images,List<String> Xs,List<String> Ys,List<String> overviews,List<String> times,List<String> prices){
        MapPOIItem marker = new MapPOIItem();
        double latitude = Double.parseDouble(Ys.get(0));
        double longitude = Double.parseDouble(Xs.get(0));
        MapPoint mapPoint = MapPoint.mapPointWithGeoCoord(latitude, longitude);
        marker.setItemName(titles.get(0));
        marker.setTag(0);
        marker.setMapPoint(mapPoint);
        marker.setMarkerType(MapPOIItem.MarkerType.CustomImage);
        marker.setCustomImageResourceId(R.drawable.custom_marker1);
        marker.setSelectedMarkerType(MapPOIItem.MarkerType.CustomImage);
        marker.setCustomSelectedImageResourceId(R.drawable.custom_marker2);
        mapView.addPOIItem(marker);

        MapPOIItem marker2 = new MapPOIItem();
        double latitude2 = Double.parseDouble(Ys.get(1));
        double longitude2 = Double.parseDouble(Xs.get(1));
        MapPoint mapPoint2 = MapPoint.mapPointWithGeoCoord(latitude2, longitude2);
        marker2.setItemName(titles.get(1));
        marker2.setTag(1);
        marker2.setMapPoint(mapPoint2);
        marker2.setMarkerType(MapPOIItem.MarkerType.CustomImage);
        marker2.setCustomImageResourceId(R.drawable.custom_marker1);
        marker2.setSelectedMarkerType(MapPOIItem.MarkerType.CustomImage);
        marker2.setCustomSelectedImageResourceId(R.drawable.custom_marker2);
        mapView.addPOIItem(marker2);

        MapPOIItem marker3 = new MapPOIItem();
        double latitude3 = Double.parseDouble(Ys.get(2));
        double longitude3= Double.parseDouble(Xs.get(2));
        MapPoint mapPoint3 = MapPoint.mapPointWithGeoCoord(latitude3, longitude3);
        marker3.setItemName(titles.get(2));
        marker3.setTag(2);
        marker3.setMapPoint(mapPoint3);
        marker3.setMarkerType(MapPOIItem.MarkerType.CustomImage);
        marker3.setCustomImageResourceId(R.drawable.custom_marker1);
        marker3.setSelectedMarkerType(MapPOIItem.MarkerType.CustomImage);
        marker3.setCustomSelectedImageResourceId(R.drawable.custom_marker2);
        mapView.addPOIItem(marker3);


        MapPolyline polyline = new MapPolyline();
        polyline.setTag(1000);
        polyline.setLineColor(Color.parseColor("#FF5D7D"));

        polyline.addPoint(MapPoint.mapPointWithGeoCoord(latitude, longitude));
        polyline.addPoint(MapPoint.mapPointWithGeoCoord(latitude2, longitude2));
        polyline.addPoint(MapPoint.mapPointWithGeoCoord(latitude3, longitude3));

        mapView.addPolyline(polyline);

        // 지도뷰의 중심좌표와 줌레벨을 Polyline이 모두 나오도록 조정.
        MapPointBounds mapPointBounds = new MapPointBounds(polyline.getMapPoints());
        int padding = 100; // px
        mapView.moveCamera(CameraUpdateFactory.newMapPointBounds(mapPointBounds, padding));

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

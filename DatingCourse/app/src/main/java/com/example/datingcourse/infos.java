package com.example.datingcourse;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;

import net.daum.mf.map.api.MapPOIItem;
import net.daum.mf.map.api.MapPoint;
import net.daum.mf.map.api.MapPolyline;
import net.daum.mf.map.api.MapView;

public class infos extends AppCompatActivity implements MapView.CurrentLocationEventListener, MapView.MapViewEventListener  {
    private MapView mapView;

    private MapPolyline polyline;

    private ViewGroup mapViewContainer;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_infos);

        // 데이터 받아오기
        Intent intent = getIntent();
        String imgUrl = intent.getStringExtra("imgUrl");
        String titleName = intent.getStringExtra("titleName");
        String addressName = intent.getStringExtra("addressName");
        Double x = intent.getDoubleExtra("x", 0.0); // 기본값 0.0
        Double y = intent.getDoubleExtra("y", 0.0); // 기본값 0.0
        String tel = intent.getStringExtra("tel");

        // 값이 null이거나 빈 문자열인 경우 "정보 없음"으로 설정
        imgUrl = (imgUrl == null || imgUrl.isEmpty()) ? null : imgUrl;
        titleName = (titleName == null || titleName.isEmpty()) ? "정보 없음" : titleName;
        addressName = (addressName == null || addressName.isEmpty()) ? "정보 없음" : addressName;
        tel = (tel == null || tel.isEmpty()) ? "정보 없음" : tel;

        getSupportActionBar().setTitle(titleName);

        TextView representTitle = findViewById(R.id.represent_titles);
        representTitle.setText(titleName);

        TextView representContent = findViewById(R.id.represent_contents);
        representContent.setText(addressName);

        TextView representPhoneNum = findViewById(R.id.represent_phone_nums);
        representPhoneNum.setText(tel);

        ImageView representImage = findViewById(R.id.represents_image);
        Glide.with(this)
                .load(imgUrl)
                .error(R.drawable.noimage)
                .fallback(R.drawable.noimage)
                .placeholder(R.drawable.loading)
                .into(representImage);

        mapView = new MapView(this);
        final ScrollView scrollView = findViewById(R.id.scroll_view_infos);
        mapViewContainer = (ViewGroup) findViewById(R.id.infos_mapView);
        mapViewContainer.addView(mapView);
        mapView.setMapViewEventListener(this);
        // mapView.setCurrentLocationTrackingMode(MapView.CurrentLocationTrackingMode.TrackingModeOnWithoutHeading);

        // 지도에 polyline 추가
        polyline = new MapPolyline();
        polyline.setTag(1000);
        polyline.setLineColor(Color.argb(128, 255, 51, 0));

        MapPoint mapPoint = MapPoint.mapPointWithGeoCoord(y, x);
        mapView.setMapCenterPointAndZoomLevel(mapPoint, 1, true);

        // 지도에 마커 추가
        MapPOIItem point = new MapPOIItem();
        point.setItemName(titleName);
        point.setMapPoint(MapPoint.mapPointWithGeoCoord(y, x));
        point.setMarkerType(MapPOIItem.MarkerType.CustomImage); // 기본으로 제공하는 BluePin 마커 모양.
        point.setCustomImageResourceId(R.drawable.custom_marker1);
        point.setSelectedMarkerType(MapPOIItem.MarkerType.CustomImage); // 마커를 클릭했을때, 기본으로 제공하는 RedPin 마커 모양.

        point.setCustomSelectedImageResourceId(R.drawable.custom_marker2);
        mapView.addPOIItem(point);

        // Polyline에 좌표 추가
        polyline.addPoint(mapPoint);
        mapView.addPolyline(polyline);  // Polyline 지도에 올리기.

        Button btnSearch = findViewById(R.id.finding_btn);
        btnSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openKakaoMapForNavigation(x, y);
            }
        });

        Button btnCourse = findViewById(R.id.course_btns);
        btnCourse.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mapView.removeAllPOIItems();
                // 데이터 받아오기
                Intent intent = getIntent();
                String imgUrl = intent.getStringExtra("imgUrl");
                String titleName = intent.getStringExtra("titleName");
                String addressName = intent.getStringExtra("addressName");
                Double x = intent.getDoubleExtra("x", 0.0); // 기본값 0.0
                Double y = intent.getDoubleExtra("y", 0.0); // 기본값 0.0
                String tel = intent.getStringExtra("tel");

                // 값이 null이거나 빈 문자열인 경우 "정보 없음"으로 설정
                imgUrl = (imgUrl == null || imgUrl.isEmpty()) ? "정보 없음" : imgUrl;
                titleName = (titleName == null || titleName.isEmpty()) ? "정보 없음" : titleName;
                addressName = (addressName == null || addressName.isEmpty()) ? "정보 없음" : addressName;
                x = (x == null || x == 0.0) ? 0.0 : x;
                y = (y == null || y == 0.0) ? 0.0 : y;
                tel = (tel == null || tel.isEmpty()) ? "정보 없음" : tel;

                // CourseMakingActivity로 데이터 전송
                Intent courseIntent = new Intent(infos.this, CourseMakingActivity.class);
                courseIntent.putExtra("imgUrl", imgUrl);
                courseIntent.putExtra("titleName", titleName);
                courseIntent.putExtra("addressName", addressName);
                courseIntent.putExtra("x", x);
                courseIntent.putExtra("y", y);
                courseIntent.putExtra("tel", tel);
                startActivity(courseIntent);
            }
        });
    }

    @Override
    protected void onPause() {
        super.onPause();
        mapView.setCurrentLocationTrackingMode(MapView.CurrentLocationTrackingMode.TrackingModeOff);
        mapView.setShowCurrentLocationMarker(false);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (mapView.getParent() == null) {
            mapViewContainer.addView(mapView);
        }
        mapView.setShowCurrentLocationMarker(true);
        mapView.onResume();
    }


    private void openKakaoMapForNavigation(Double x, Double y) {
        String kakaoUri = "kakaomap://route?sp=&ep=" + y + "," + x + "&by=PUBLICTRANSIT"; // 버스 길찾기

        // 카카오맵 앱이 설치되어 있는지 확인
        PackageManager pm = getPackageManager();
        Intent intent;
        try {
            pm.getPackageInfo(getPackageName(), 0);
            intent = new Intent(Intent.ACTION_VIEW, Uri.parse(kakaoUri));
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
            // 카카오맵 앱이 설치되어 있지 않은 경우, 웹 브라우저를 통해 카카오맵 웹 길찾기 페이지로 연결
            intent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://map.kakao.com/?eX=" + x + "&eY=" + y));
        }

        startActivity(intent);
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
    package com.example.datingcourse;

    import android.content.Intent;
    import android.content.pm.PackageManager;
    import android.graphics.Color;
    import android.net.Uri;
    import android.os.Bundle;
    import android.view.MotionEvent;
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

    public class info extends AppCompatActivity implements MapView.CurrentLocationEventListener, MapView.MapViewEventListener  {
        private static final String BASE_URL = "https://dapi.kakao.com/";
        private static final String API_KEY = "KakaoAK 4b857970dbbfec9a77078e89f8b363cc"; // REST API 키

        private MapView mapView;

        private MapPolyline polyline;

        private ViewGroup mapViewContainer;

        public static InformationAPI.DataListener dataListener;

        private String keyword = ""; // 검색 키워드
        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_info);

            mapView = new MapView(this);

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
            imgUrl = (imgUrl == null || imgUrl.isEmpty()) ? null : imgUrl;
            titleName = (titleName == null || titleName.isEmpty()) ? "정보 없음" : titleName;
            addressName = (addressName == null || addressName.isEmpty()) ? "정보 없음" : addressName;
            tel = (tel == null || tel.isEmpty()) ? "정보 없음" : tel;

            getSupportActionBar().setTitle(titleName);

            TextView representTitle = findViewById(R.id.represent_title);
            representTitle.setText(titleName);

            TextView representContent = findViewById(R.id.represent_content);
            representContent.setText(addressName);

            TextView representPhoneNum = findViewById(R.id.represent_phone_num);
            representPhoneNum.setText(tel);

            ImageView representImage = findViewById(R.id.represent_image);
            Glide.with(this)
                    .load(imgUrl)
                    .error(R.drawable.noimage)
                    .fallback(R.drawable.noimage)
                    .placeholder(R.drawable.loading)
                    .into(representImage);

            final ScrollView scrollView = findViewById(R.id.scroll_view);
            mapViewContainer = (ViewGroup) findViewById(R.id.info_mapView);
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

            mapView.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    switch (event.getAction()) {
                        case MotionEvent.ACTION_MOVE:
                            scrollView.requestDisallowInterceptTouchEvent(true);
                            break;
                        case MotionEvent.ACTION_UP:
                        case MotionEvent.ACTION_CANCEL:
                            scrollView.requestDisallowInterceptTouchEvent(false);
                            break;
                    }
                    return mapView.onTouchEvent(event);
                }
            });

            Button btnSearch = findViewById(R.id.finding_btn);
            btnSearch.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    openKakaoMapForNavigation(x, y);
                }
            });

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
            Intent intent;
            String kakaoUri = "kakaomap://route?sp=&ep=" + y + "," + x + "&by=PUBLICTRANSIT"; // 버스 길찾기
            // 카카오맵 앱이 설치되어 있는지 확인
            try {
                getPackageManager().getPackageInfo(getPackageName(), 0);
                intent = new Intent(Intent.ACTION_VIEW, Uri.parse(kakaoUri));
            } catch (PackageManager.NameNotFoundException e) {
                // 카카오맵 앱이 설치되어 있지 않은 경우, Play Store로 연결
                intent = new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=net.daum.android.map"));
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
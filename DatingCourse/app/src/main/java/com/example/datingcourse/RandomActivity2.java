package com.example.datingcourse;


import android.Manifest;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.AssetManager;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.bumptech.glide.Glide;

import net.daum.mf.map.api.CameraUpdateFactory;
import net.daum.mf.map.api.MapPOIItem;
import net.daum.mf.map.api.MapPoint;
import net.daum.mf.map.api.MapPointBounds;
import net.daum.mf.map.api.MapPolyline;
import net.daum.mf.map.api.MapView;

import java.util.ArrayList;
import java.util.List;


public class RandomActivity2 extends AppCompatActivity implements MapView.CurrentLocationEventListener, MapView.MapViewEventListener,API.DataListener{
    private MapView mapView;
    private ViewGroup mapViewContainer;
    private MapPolyline polyline;
    public static API.DataListener dataListener;
    private List<String> imageUrls = new ArrayList<>();
    private ImagePagerAdapter adapter;
    private ViewPager viewPager;

    private String[] columns;
    private String[] columns2;
    private String[] columns3;

    private ImageButton imageView1;
    private ImageButton imageView2;


    private ImageButton imageView3;
    private int contentTypeId1; //API 콘텐츠 종류 코드 (음식점, 숙박 등등)
    private int contentTypeId2;
    private int contentTypeId3;

    private int sigunguCode; //API 시,군,구 코드
    private int count=0;  //API호출 시 증가

    private String result="";
    private String result2="";
    private String result3="";
    private String overview1="";
    private String overview2="";
    private String overview3="";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_random2);

        // 권한ID를 가져옵니다
        int permission = ContextCompat.checkSelfPermission(this,
                Manifest.permission.INTERNET);

        int permission2 = ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION);

        int permission3 = ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_COARSE_LOCATION);

        // 권한이 열려있는지 확인
        if (permission == PackageManager.PERMISSION_DENIED || permission2 == PackageManager.PERMISSION_DENIED || permission3 == PackageManager.PERMISSION_DENIED) {
            // 마쉬멜로우 이상버전부터 권한을 물어본다
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                // 권한 체크(READ_PHONE_STATE의 requestCode를 1000으로 세팅
                requestPermissions(
                        new String[]{Manifest.permission.INTERNET, Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION},
                        1000);
            }
            return;
        }

        //지도를 띄우자
        // java code
        mapView = new MapView(this);
        mapViewContainer = (ViewGroup) findViewById(R.id.map_view);
        mapViewContainer.addView(mapView);

        mapView.setMapViewEventListener(this);
        //mapView.setCurrentLocationTrackingMode(MapView.CurrentLocationTrackingMode.TrackingModeOnWithoutHeading);



        // Excel 파일 읽어오기
        AssetManager assetManager = getAssets();


        Intent intent = getIntent();
        String list = intent.getStringExtra("list");
        String textView1Value = intent.getStringExtra("textView1");
        String textView2Value = intent.getStringExtra("textView2");
        String textView3Value = intent.getStringExtra("textView3");

        if(list.equals("강남구")) {
            sigunguCode=1;
            if (textView1Value.equals("먹기")) contentTypeId1 = 39;
            else if (textView1Value.equals("숙박")) contentTypeId1 = 32;
            else if (textView1Value.equals("관광지")) contentTypeId1 = 12;
            else if (textView1Value.equals("놀기")) contentTypeId1 = 28;
            else if (textView1Value.equals("문화시설")) contentTypeId1 = 14;
            else if (textView1Value.equals("쇼핑")) contentTypeId1 = 38;
            if (textView2Value.equals("먹기")) contentTypeId2 = 39;
            else if (textView2Value.equals("숙박")) contentTypeId2 = 32;
            else if (textView2Value.equals("관광지")) contentTypeId2 = 12;
            else if (textView2Value.equals("놀기")) contentTypeId2 = 28;
            else if (textView2Value.equals("문화시설")) contentTypeId2 = 14;
            else if (textView2Value.equals("쇼핑")) contentTypeId2 = 38;
            if (textView3Value.equals("먹기")) contentTypeId3 = 39;
            else if (textView3Value.equals("숙박")) contentTypeId3 = 32;
            else if (textView3Value.equals("관광지")) contentTypeId3 = 12;
            else if (textView3Value.equals("놀기")) contentTypeId3 = 28;
            else if (textView3Value.equals("문화시설")) contentTypeId3 = 14;
            else if (textView3Value.equals("쇼핑")) contentTypeId3 = 38;
        }
        else if (list.equals("강동구")){
            sigunguCode=2;
            if (textView1Value.equals("먹기")) contentTypeId1 = 39;
            else if (textView1Value.equals("숙박")) contentTypeId1 = 32;
            else if (textView1Value.equals("관광지")) contentTypeId1 = 12;
            else if (textView1Value.equals("놀기")) contentTypeId1 = 28;
            else if (textView1Value.equals("문화시설")) contentTypeId1 = 14;
            else if (textView1Value.equals("쇼핑")) contentTypeId1 = 38;
            if (textView2Value.equals("먹기")) contentTypeId2 = 39;
            else if (textView2Value.equals("숙박")) contentTypeId2 = 32;
            else if (textView2Value.equals("관광지")) contentTypeId2 = 12;
            else if (textView2Value.equals("놀기")) contentTypeId2 = 28;
            else if (textView2Value.equals("문화시설")) contentTypeId2 = 14;
            else if (textView2Value.equals("쇼핑")) contentTypeId2 = 38;
            if (textView3Value.equals("먹기")) contentTypeId3 = 39;
            else if (textView3Value.equals("숙박")) contentTypeId3 = 32;
            else if (textView3Value.equals("관광지")) contentTypeId3 = 12;
            else if (textView3Value.equals("놀기")) contentTypeId3 = 28;
            else if (textView3Value.equals("문화시설")) contentTypeId3 = 14;
            else if (textView3Value.equals("쇼핑")) contentTypeId3 = 38;
        }
        else if (list.equals("강북구")){
            sigunguCode=3;
            if (textView1Value.equals("먹기")) contentTypeId1 = 39;
            else if (textView1Value.equals("숙박")) contentTypeId1 = 32;
            else if (textView1Value.equals("관광지")) contentTypeId1 = 12;
            else if (textView1Value.equals("놀기")) contentTypeId1 = 28;
            else if (textView1Value.equals("문화시설")) contentTypeId1 = 14;
            else if (textView1Value.equals("쇼핑")) contentTypeId1 = 38;
            if (textView2Value.equals("먹기")) contentTypeId2 = 39;
            else if (textView2Value.equals("숙박")) contentTypeId2 = 32;
            else if (textView2Value.equals("관광지")) contentTypeId2 = 12;
            else if (textView2Value.equals("놀기")) contentTypeId2 = 28;
            else if (textView2Value.equals("문화시설")) contentTypeId2 = 14;
            else if (textView2Value.equals("쇼핑")) contentTypeId2 = 38;
            if (textView3Value.equals("먹기")) contentTypeId3 = 39;
            else if (textView3Value.equals("숙박")) contentTypeId3 = 32;
            else if (textView3Value.equals("관광지")) contentTypeId3 = 12;
            else if (textView3Value.equals("놀기")) contentTypeId3 = 28;
            else if (textView3Value.equals("문화시설")) contentTypeId3 = 14;
            else if (textView3Value.equals("쇼핑")) contentTypeId3 = 38;
        }
        else if (list.equals("강서구")){
            sigunguCode=4;
            if (textView1Value.equals("먹기")) contentTypeId1 = 39;
            else if (textView1Value.equals("숙박")) contentTypeId1 = 32;
            else if (textView1Value.equals("관광지")) contentTypeId1 = 12;
            else if (textView1Value.equals("놀기")) contentTypeId1 = 28;
            else if (textView1Value.equals("문화시설")) contentTypeId1 = 14;
            else if (textView1Value.equals("쇼핑")) contentTypeId1 = 38;
            if (textView2Value.equals("먹기")) contentTypeId2 = 39;
            else if (textView2Value.equals("숙박")) contentTypeId2 = 32;
            else if (textView2Value.equals("관광지")) contentTypeId2 = 12;
            else if (textView2Value.equals("놀기")) contentTypeId2 = 28;
            else if (textView2Value.equals("문화시설")) contentTypeId2 = 14;
            else if (textView2Value.equals("쇼핑")) contentTypeId2 = 38;
            if (textView3Value.equals("먹기")) contentTypeId3 = 39;
            else if (textView3Value.equals("숙박")) contentTypeId3 = 32;
            else if (textView3Value.equals("관광지")) contentTypeId3 = 12;
            else if (textView3Value.equals("놀기")) contentTypeId3 = 28;
            else if (textView3Value.equals("문화시설")) contentTypeId3 = 14;
            else if (textView3Value.equals("쇼핑")) contentTypeId3 = 38;
        }





        if (list != null && !list.isEmpty()) {
            if (list.equals("강남구") || list.equals("강북구") || list.equals("강동구") || list.equals("강서구")) {
                if (textView1Value.equals("먹기")) {
                    result = ExcelReader.readDataFromExcel(assetManager, "food.xls", list);
                } else if (textView1Value.equals("숙박")) {
                    result = ExcelReader.readDataFromExcel(assetManager, "sleep.xls", list);
                } else if (textView1Value.equals("관광지")) {
                    result = ExcelReader.readDataFromExcel(assetManager, "tour.xls", list);
                } else if (textView1Value.equals("놀기")) {
                    result = ExcelReader.readDataFromExcel(assetManager, "playing.xls", list);
                } else if (textView1Value.equals("문화시설")) {
                    result = ExcelReader.readDataFromExcel(assetManager, "culture.xls", list);
                } else if (textView1Value.equals("쇼핑")) {
                    result = ExcelReader.readDataFromExcel(assetManager, "shopping.xls", list);
                }

                if (textView2Value.equals("먹기")) {
                    result2 = ExcelReader.readDataFromExcel(assetManager, "food.xls", list);
                } else if (textView2Value.equals("숙박")) {
                    result2 = ExcelReader.readDataFromExcel(assetManager, "sleep.xls", list);
                } else if (textView2Value.equals("관광지")) {
                    result2 = ExcelReader.readDataFromExcel(assetManager, "tour.xls", list);
                } else if (textView2Value.equals("놀기")) {
                    result2 = ExcelReader.readDataFromExcel(assetManager, "playing.xls", list);
                } else if (textView2Value.equals("문화시설")) {
                    result2 = ExcelReader.readDataFromExcel(assetManager, "culture.xls", list);
                } else if (textView2Value.equals("쇼핑")) {
                    result2 = ExcelReader.readDataFromExcel(assetManager, "shopping.xls", list);
                }

                if (textView3Value.equals("먹기")) {
                    result3 = ExcelReader.readDataFromExcel(assetManager, "food.xls", list);
                } else if (textView3Value.equals("숙박")) {
                    result3 = ExcelReader.readDataFromExcel(assetManager, "sleep.xls", list);
                } else if (textView3Value.equals("관광지")) {
                    result3 = ExcelReader.readDataFromExcel(assetManager, "tour.xls", list);
                } else if (textView3Value.equals("놀기")) {
                    result3 = ExcelReader.readDataFromExcel(assetManager, "playing.xls", list);
                } else if (textView3Value.equals("문화시설")) {
                    result3 = ExcelReader.readDataFromExcel(assetManager, "culture.xls", list);
                } else if (textView3Value.equals("쇼핑")) {
                    result3 = ExcelReader.readDataFromExcel(assetManager, "shopping.xls", list);
                }
            }


            //텍스트 뷰에 출력
            // Button btn1 = findViewById(R.id.text_view1);
            columns = result.split("  ");
            overview1 = columns[0]+ "\n\n" +columns[1]+ "\n\n" + columns[4] + "\n\n" + columns[5] + "\n\n" + columns[6];
            result = columns[0] + "\n" + columns[1];
            // btn1.setText(result);
            MapPOIItem marker = new MapPOIItem();
            double latitude = Double.parseDouble(columns[2]);
            double longitude = Double.parseDouble(columns[3]);
            MapPoint mapPoint = MapPoint.mapPointWithGeoCoord(latitude, longitude);
            marker.setItemName(columns[0]);
            marker.setTag(1);
            marker.setMapPoint(mapPoint);
            marker.setMarkerType(MapPOIItem.MarkerType.CustomImage); // 기본으로 제공하는 BluePin 마커 모양.
            marker.setCustomImageResourceId(R.drawable.custom_marker1);
            marker.setSelectedMarkerType(MapPOIItem.MarkerType.CustomImage); // 마커를 클릭했을때, 기본으로 제공하는 RedPin 마커 모양.

            marker.setCustomSelectedImageResourceId(R.drawable.custom_marker2);
            mapView.addPOIItem(marker);

            // Button btn2 = findViewById(R.id.text_view2);
            columns2 = result2.split("  ");
            overview2 = columns2[0]+ "\n\n" +columns2[1]+ "\n\n" + columns2[4] + "\n\n" + columns2[5] + "\n\n" + columns2[6];

            result2 = columns2[0] + "\n" + columns2[1];
            // btn2.setText(result2);
            MapPOIItem marker2 = new MapPOIItem();
            double latitude2 = Double.parseDouble(columns2[2]);
            double longitude2 = Double.parseDouble(columns2[3]);
            MapPoint mapPoint2 = MapPoint.mapPointWithGeoCoord(latitude2, longitude2);
            marker2.setItemName(columns2[0]);
            marker2.setTag(1);
            marker2.setMapPoint(mapPoint2);
            marker2.setMarkerType(MapPOIItem.MarkerType.CustomImage); // 기본으로 제공하는 BluePin 마커 모양.
            marker2.setCustomImageResourceId(R.drawable.custom_marker1);
            marker2.setSelectedMarkerType(MapPOIItem.MarkerType.CustomImage); // 마커를 클릭했을때, 기본으로 제공하는 RedPin 마커 모양.

            marker2.setCustomSelectedImageResourceId(R.drawable.custom_marker2);
            mapView.addPOIItem(marker2);

            // Button btn3 = findViewById(R.id.text_view3);
            columns3 = result3.split("  ");
            overview3 = columns3[0]+ "\n\n" +columns3[1]+ "\n\n" + columns3[4] + "\n\n" + columns3[5] + "\n\n" + columns3[6];
            result3 = columns3[0] + "\n" + columns3[1];
            // btn3.setText(result3);
            MapPOIItem marker3 = new MapPOIItem();
            double latitude3 = Double.parseDouble(columns3[2]);
            double longitude3 = Double.parseDouble(columns3[3]);
            MapPoint mapPoint3 = MapPoint.mapPointWithGeoCoord(latitude3, longitude3);
            marker3.setItemName(columns3[0]);
            marker3.setTag(1);
            marker3.setMapPoint(mapPoint3);
            marker3.setMarkerType(MapPOIItem.MarkerType.CustomImage); // 기본으로 제공하는 BluePin 마커 모양.
            marker3.setCustomImageResourceId(R.drawable.custom_marker1);
            marker3.setSelectedMarkerType(MapPOIItem.MarkerType.CustomImage); // 마커를 클릭했을때, 기본으로 제공하는 RedPin 마커 모양.
            marker3.setCustomSelectedImageResourceId(R.drawable.custom_marker2);

            mapView.addPOIItem(marker3);

            MapPolyline polyline = new MapPolyline();
            polyline.setTag(1000);

            polyline.addPoint(MapPoint.mapPointWithGeoCoord(latitude, longitude));
            polyline.addPoint(MapPoint.mapPointWithGeoCoord(latitude2, longitude2));
            polyline.addPoint(MapPoint.mapPointWithGeoCoord(latitude3, longitude3));

            mapView.addPolyline(polyline);

            // 지도뷰의 중심좌표와 줌레벨을 Polyline이 모두 나오도록 조정.
            MapPointBounds mapPointBounds = new MapPointBounds(polyline.getMapPoints());
            int padding = 100; // px
            mapView.moveCamera(CameraUpdateFactory.newMapPointBounds(mapPointBounds, padding));

//                imageView1 = findViewById(R.id.imageView1);
//                imageView1.setOnClickListener(new View.OnClickListener() {
//                    public void onClick(View v){
//                        showAdditionalInfo(columns);
//                    }
//                });
//                imageView2 = findViewById(R.id.imageView2);
//                imageView2.setOnClickListener(new View.OnClickListener() {
//                    public void onClick(View v){
//                        showAdditionalInfo(columns2);
//                    }
//                 });
//                imageView3 = findViewById(R.id.imageView3);
//                imageView3.setOnClickListener(new View.OnClickListener() {
//                     public void onClick(View v){
//                        showAdditionalInfo(columns3);
//                    }
//                });
//                btn1.setOnClickListener(new View.OnClickListener(){
//                    public void onClick(View view){
//                        showAdditionalInfo(columns);
//                    }
//                });
//                btn2.setOnClickListener(new View.OnClickListener(){
//                    public void onClick(View view){
//                        showAdditionalInfo(columns2);
//                    }
//                });
//                btn3.setOnClickListener(new View.OnClickListener(){
//                    public void onClick(View view){
//                        showAdditionalInfo(columns3);
//                    }
//                });


            dataListener = this;
            API api = new API(contentTypeId1,sigunguCode);
            api.new NetworkTask().execute();
            API api2 = new API(contentTypeId2,sigunguCode);
            api2.new NetworkTask().execute();
            API api3 = new API(contentTypeId3,sigunguCode);
            api3.new NetworkTask().execute();

            viewPager = findViewById(R.id.viewPager);
            adapter = new ImagePagerAdapter();
            viewPager.setAdapter(adapter);

        }
//            else {
//                // list가 null이거나 비어있을 경우 처리
//                TextView textView1 = findViewById(R.id.text_view1);
//                textView1.setText("선택된 지역이 없습니다.");
//            }


    }

    @Override
    public void onDataReceived(List<String> titles, List<String> images) {
//            // 레이아웃에서 ViewPager2 찾기
//            ViewPager2 viewPager = findViewById(R.id.viewPager);
//            // 이미지 슬라이딩을 위한 이미지 URL 목록 생성
//            List<String> imageUrls = new ArrayList<>();
        if(count==0) {
            for (int i = 0; i < titles.size(); i++) {
                if (columns[0].equals(titles.get(i))) {
                    String imageUrl = images.get(i);
                    imageUrls.add(imageUrl);
                    adapter.updateData(imageUrls);
                    //imageView1 = findViewById(R.id.imageView1);
//                          if (imageUrl != null && !imageUrl.isEmpty()) {
//
//                              Glide.with(this).load(imageUrl).error(R.drawable.noimage).fallback(R.drawable.noimage).placeholder(R.drawable.loading).centerCrop().into(imageView1);
//                          }
                }
            }
        }
        else if (count==1) {
            for (int i = 0; i < titles.size(); i++) {
                if (columns2[0].equals(titles.get(i))) {
                    String imageUrl = images.get(i);
                    imageUrls.add(imageUrl);
                    adapter.updateData(imageUrls);

                    // imageView2 = findViewById(R.id.imageView2);
//                          if (imageUrl != null && !imageUrl.isEmpty()) {
//                              Glide.with(this).load(imageUrl).error(R.drawable.noimage).fallback(R.drawable.noimage).placeholder(R.drawable.loading).centerCrop().into(imageView2);
//                          }
                }
            }
        }
        else if (count==2) {
            for (int i = 0; i < titles.size(); i++) {
                if (columns3[0].equals(titles.get(i))) {
                    String imageUrl = images.get(i);
                    imageUrls.add(imageUrl);
                    adapter.updateData(imageUrls);

                    // imageView3 = findViewById(R.id.imageView3);
//                          if (imageUrl != null && !imageUrl.isEmpty()) {
//                              Glide.with(this).load(imageUrl).error(R.drawable.noimage).fallback(R.drawable.noimage).placeholder(R.drawable.loading).centerCrop().into(imageView3);
//                          }
                }
            }
        }
        count++;
    }
    private class ImagePagerAdapter extends PagerAdapter {

        @Override
        public int getCount() {
            return imageUrls.size();
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            View view = LayoutInflater.from(RandomActivity2.this).inflate(R.layout.item_image_pager, container, false);
            ImageView imageView = view.findViewById(R.id.imageView);
            Button textView = view.findViewById(R.id.textView);

            Glide.with(RandomActivity2.this)
                    .load(imageUrls.get(position))
                    .error(R.drawable.noimage)
                    .fallback(R.drawable.noimage)
                    .placeholder(R.drawable.loading)
                    .into(imageView);

//                           btn3.setOnClickListener(new View.OnClickListener(){
//                    public void onClick(View view){
//                        showAdditionalInfo(columns3);
//                    }
//                });

            String imageText2[] = {result,result2,result3};
            String overview[] = {overview1,overview2,overview3};
            if (position < imageText2.length) {
                textView.setText(imageText2[position]);

                textView.setOnClickListener(new View.OnClickListener(){
                    public void onClick(View view){
                        showAdditionalInfo(overview[position]);
                    }
                });

            }
            // 이미지 아래에 표시될 텍스트 설정
//            String imageText = "Image " + (position + 1);
//            textView.setText(imageText);

            container.addView(view);
            return view;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView((View) object);
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == object;
        }
        public void updateData(List<String> newImageUrls) {
            notifyDataSetChanged();
        }
    }


    // 부가 설명 창을 보여주는 함수
    private void showAdditionalInfo(String columns) {
        // 부가 설명을 포함한 문자열 생성
        //String additionalInfo =columns[1] + "\n\n" + columns[4]+"\n\n" + columns[5]+"\n\n" + columns[6];

        // AlertDialog를 이용하여 부가 설명 창 띄우기
        AlertDialog.Builder builder = new AlertDialog.Builder(RandomActivity2.this);
        builder.setTitle("더보기");
        builder.setMessage(columns);
        builder.setPositiveButton("확인", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss(); // 창 닫기
            }
        });
        AlertDialog dialog = builder.create();
        dialog.show();
    }
    // 권한 체크 이후로직
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grandResults) {
        // READ_PHONE_STATE의 권한 체크 결과를 불러온다
        super.onRequestPermissionsResult(requestCode, permissions, grandResults);
        if (requestCode == 1000) {
            boolean check_result = true;

            // 모든 퍼미션을 허용했는지 체크
            for (int result : grandResults) {
                if (result != PackageManager.PERMISSION_GRANTED) {
                    check_result = false;
                    break;
                }
            }

            // 권한 체크에 동의를 하지 않으면 안드로이드 종료
            if (check_result == false) {
                finish();
            }
        }
    }
    @Override
    public void onBackPressed() {
        // 기존 액티비티 스택을 사용하여 이전 화면(CourseListActivity)으로 돌아감
        mapView.removeAllPOIItems();
        super.onBackPressed();
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

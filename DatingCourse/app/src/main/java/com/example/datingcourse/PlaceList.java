package com.example.datingcourse;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class PlaceList extends AppCompatActivity implements InformationAPI.DataListener {

    private int pageNumber = 1; // 검색 페이지 번호
    private String keyword = ""; // 검색 키워드
    private ArrayList<Cafe> cafes = new ArrayList<>(); // 리사이클러 뷰 아이템
    private CafeAdapter cafeAdapter = new CafeAdapter(this, cafes); // 리사이클러 뷰 어댑터

    public static InformationAPI.DataListener dataListener;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Intent intent = getIntent();
        int contentTypeId = intent.getIntExtra("contentTypeId", 0);
        int sigunguCode = intent.getIntExtra("sigunguCode", 0);
        String cat1 = intent.getStringExtra("cat1");
        String cat2 = intent.getStringExtra("cat2");
        String cat3 = intent.getStringExtra("cat3");
        String selectedRegion = intent.getStringExtra("selectedRegion");

        dataListener = this;
        InformationAPI api = new InformationAPI(contentTypeId, sigunguCode, cat1, cat2, cat3);
        api.new NetworkTask().execute();

        setContentView(R.layout.activity_place_list);

        getSupportActionBar().setTitle("정보");

        TextView guChoose = findViewById(R.id.place_gu_choose);
        guChoose.setText(selectedRegion);  // TextView의 텍스트를 업데이트
        guChoose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(PlaceList.this, GuChoose.class);
                intent.putExtra("contentTypeId", contentTypeId);
                intent.putExtra("sigunguCode", sigunguCode);
                intent.putExtra("cat1", cat1);
                intent.putExtra("cat2", cat2);
                intent.putExtra("cat3", cat3);
                startActivity(intent); // 결과를 기대하는 인텐트 시작
            }
        });

        RecyclerView rvList = findViewById(R.id.place_rv_list);
        rvList.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        rvList.setAdapter(cafeAdapter);

        // 리스트 아이템 클릭 시 해당 위치로 이동
        cafeAdapter.setItemClickListener(new CafeAdapter.OnItemClickListener() {
            @Override
            public void onClick(View v, int position) {
                Intent intent = new Intent(getApplicationContext(), info.class);
                intent.putExtra("imgUrl", cafes.get(position).getImgName());
                intent.putExtra("titleName", cafes.get(position).getMainText());
                intent.putExtra("addressName", cafes.get(position).getSubText());
                intent.putExtra("x", Double.parseDouble(cafes.get(position).getX()));
                intent.putExtra("y", Double.parseDouble(cafes.get(position).getY()));
                intent.putExtra("tel", cafes.get(position).getTel());
                startActivity(intent);
            }
        });
    }

    @Override
    public void onBackPressed() {
        // MainActivity로 이동
        Intent intent = new Intent(PlaceList.this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);  // 현재 액티비티 위에 있는 모든 액티비티를 종료
        startActivity(intent);
    }

    @Override
    public void onDataReceived(List<String> titles, List<String> images, List<String> address, List<String> x, List<String> y, List<String> tel) {
        for (int i = 0; i < titles.size(); i++) {
            String imageUrl = images.get(i);
            String title = titles.get(i);
            String setAddress = address.get(i);
            String setX = x.get(i);
            String setY = y.get(i);
            String setTel = tel.get(i);

            Cafe cafe = new Cafe(imageUrl, title, setAddress, setX, setY, setTel);
            cafes.add(cafe);
        }
        cafeAdapter.notifyDataSetChanged();
    }
}
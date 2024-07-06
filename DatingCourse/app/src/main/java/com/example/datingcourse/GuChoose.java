package com.example.datingcourse;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import androidx.appcompat.app.AppCompatActivity;

public class GuChoose extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gu_choose);

        ListView regionListView = findViewById(R.id.region_listview);
        String[] regions = getResources().getStringArray(R.array.seoul_regions);
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, regions);
        regionListView.setAdapter(adapter);

        regionListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String selectedRegion = (String) parent.getItemAtPosition(position);
                int sigunguCode = 0;

                switch (selectedRegion) {
                    case "강남구":
                        sigunguCode=1;
                        break;

                    case "강동구":
                        sigunguCode=2;
                        break;

                    case "강북구":
                        sigunguCode=3;
                        break;

                    case "강서구":
                        sigunguCode=4;
                        break;
                }


                // 선택된 구의 정보를 MainActivity로 전달하며 MainActivity를 시작합니다.
                Intent intent = new Intent(GuChoose.this, PlaceList.class);
                // 이전 PlaceList의 상태 정보를 가져옵니다.
                Intent previousIntent = getIntent();
                intent.putExtra("contentTypeId", previousIntent.getIntExtra("contentTypeId", 0));
                intent.putExtra("sigunguCode", sigunguCode);
                intent.putExtra("cat1", previousIntent.getStringExtra("cat1"));
                intent.putExtra("cat2", previousIntent.getStringExtra("cat2"));
                intent.putExtra("cat3", previousIntent.getStringExtra("cat3"));
                intent.putExtra("selectedRegion", "서울특별시 " + selectedRegion);
                startActivity(intent);
                finish();  // GuChoose 액티비티를 종료합니다.
            }
        });
    }
}

package com.example.datingcourse;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    private long backPressedTime = 0;    // 뒤로가기 버튼을 누른 시간

    // bottom navigationbar 구현하기 위함
    private BottomNavigationView bottomNavigationView;
    private FragmentManager fm;
    private FragmentTransaction ft;
    private FragHome fh;
    private FragCommunity fc;
    private FragLocation fl;
    private FragMyPage fp;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_frag_main);

        // bottom navigationbar 구현
        bottomNavigationView = findViewById(R.id.nav_view);
        bottomNavigationView.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                int itemId = menuItem.getItemId();
                if (itemId == R.id.action_Home) {
                    setFrag(0);
                } else if (itemId == R.id.action_Community) {
                    setFrag(1);
                } else if (itemId == R.id.action_Location) {
                    setFrag(2);
                } else if (itemId == R.id.action_MyPage) {
                    setFrag(3);
                }
                return true;

            }

        });

        fh = new FragHome();
        fc = new FragCommunity();
        fl = new FragLocation();
        fp = new FragMyPage();

        setFrag(0); //첫 프래그먼트 화면을 무엇으로 지정해줄 것인지 선택

    }

    // 프래그먼트 교체가 일어나는 실행문
    private void setFrag(int n) {
        fm = getSupportFragmentManager();
        ft = fm.beginTransaction();
        switch(n){
            case 0 :
                ft.replace(R.id.main_frame, fh);
                ft.commit();
                break;
            case 1 :
                ft.replace(R.id.main_frame, fc);
                ft.commit();
                break;
            case 2 :
                ft.replace(R.id.main_frame, fl);
                ft.commit();
                break;
            case 3 :
                ft.replace(R.id.main_frame, fp);
                ft.commit();
                break;
        }
    }


    @Override
    public void onBackPressed() {
        // 현재 시간을 가져온다.
        long tempTime = System.currentTimeMillis();
        // 마지막으로 뒤로가기 버튼을 누른 시간과 현재 시간을 비교한다.
        long intervalTime = tempTime - backPressedTime;

        // 뒤로가기 버튼을 한 번 눌렀을 때
        if (0 <= intervalTime && intervalTime <= 2000) {
            // 뒤로가기 버튼을 연속으로 두 번 눌렀을 때, 앱 종료
            super.onBackPressed();
            finishAffinity();  // 액티비티 스택을 모두 종료
            System.runFinalization();
            System.exit(0);
        } else {
            // 마지막으로 뒤로가기 버튼을 누른 시간을 현재 시간으로 갱신
            backPressedTime = tempTime;
            // 앱을 종료하려면 한 번 더 누르라는 토스트 메시지 표시
            showToast("앱을 종료하려면 한 번 더 누르세요.");
        }
    }

    // 토스트 메시지를 출력하는 메소드
    public void showToast(String message) {
        Toast.makeText(MainActivity.this, message, Toast.LENGTH_SHORT).show();
    }
}
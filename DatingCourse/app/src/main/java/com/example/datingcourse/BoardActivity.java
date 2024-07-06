package com.example.datingcourse;

import android.content.Intent;
import android.graphics.Rect;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;

public class BoardActivity extends AppCompatActivity {
    private ArrayList<ArrayList<Photo>> allBoardList = new ArrayList();
    private ArrayList<HashMap<String,Object>> allPhotoList = new ArrayList<>();

    private DatabaseReference mDatabaseRef;
    private FirebaseAuth mFirebaseAuth;
    private FirebaseUser mFirebaseUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_board);

        this.initializeData();

        // 코스 만들기 페이지로 이동
        ImageButton write1 = (ImageButton) findViewById(R.id.write1);
        write1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), CourseMakingActivity.class);
                startActivity(intent);
            }
        });
    }

    public void initializeData()
    {
        allBoardList.clear();

        mDatabaseRef = FirebaseDatabase.getInstance().getReference("FirebaseRegister").child("UserCourse");
        mDatabaseRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                ArrayList<HashMap<String, Object>> tempList = new ArrayList<>();

                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    ArrayList<HashMap<String, Object>> childTempList = new ArrayList<>();
                    ArrayList<Photo> BoardList = new ArrayList<>();
                    for (DataSnapshot courseSnapshot : dataSnapshot.getChildren()) {
                        // dataSnapshot을 통해 필드의 값을 가져옵니다.
                        String addressName = courseSnapshot.child("addressName").getValue(String.class);
                        String imgUrl = courseSnapshot.child("imgUrl").getValue(String.class);
                        String tel = courseSnapshot.child("tel").getValue(String.class);
                        String titleName = courseSnapshot.child("titleName").getValue(String.class);
                        Double x = (courseSnapshot.child("x").getValue(Double.class));
                        Double y = (courseSnapshot.child("y").getValue(Double.class));
                        String userUid = courseSnapshot.child("userUid").getValue(String.class);
                        // 가져온 값을 이용하여 Points 객체를 생성하고, boardList에 추가합니다.
                        Photo photo = new Photo(imgUrl, titleName, addressName, x, y, tel,userUid);
                        BoardList.add(new Photo(imgUrl, titleName, addressName, x, y, tel,userUid));

                        HashMap<String, Object> photoMap = new HashMap<>();
                        photoMap.put("imageUrl", photo.getImgUrl());
                        photoMap.put("titleName", photo.getTitleName());
                        photoMap.put("address", photo.getAddressName());
                        photoMap.put("x", photo.getX());
                        photoMap.put("y", photo.getY());
                        photoMap.put("tel", photo.getTel());
                        photoMap.put("userUid",photo.getUserUid());

                        // 최근값을 리스트의 앞에 추가
                        childTempList.add(photoMap);
                    }
                    allBoardList.add(BoardList);
                    HashMap<String, Object> userMap = new HashMap<>();
                    userMap.put("dataSnapshotKey", dataSnapshot.getKey());
                    userMap.put("childList", childTempList);

                    tempList.add(userMap);
                }

                allPhotoList.clear();
                // 리스트 뒤집어서 모든 값 대체
                for (int i = tempList.size() - 1; i >= 0; i--) {
                    allPhotoList.add(tempList.get(i));
                }

                // 데이터 로딩이 완료되면 RecyclerView를 업데이트합니다.
                RecyclerView view = findViewById(R.id.recyclerView);

                int spacing = 50; // 50px
                view.addItemDecoration(new BoardActivity.ItemOffsetDecoration(spacing));

                BoardAdapter verticalAdapter = new BoardAdapter(BoardActivity.this, allBoardList, "");
                view.setHasFixedSize(true);
                view.setLayoutManager(new LinearLayoutManager(BoardActivity.this, LinearLayoutManager.VERTICAL, false));
                view.setAdapter(verticalAdapter);
                verticalAdapter.notifyDataSetChanged();

                int userIndex = 1;
                for (HashMap<String, Object> userMap : allPhotoList) {
                    Log.d("TAG", "----------------------------------------");
                    Log.d("TAG", "userAccount 자식 노드 순번: " + userIndex);
                    Log.d("TAG", "userAccount 자식 노드 키: " + userMap.get("dataSnapshotKey"));

                    // 자식 노드의 자식노드 출력
                    ArrayList<HashMap<String, Object>> childList = (ArrayList<HashMap<String, Object>>) userMap.get("childList");
                    for (int j = 0; j < childList.size(); j++) {
                        HashMap<String, Object> childMap = childList.get(j);
                        int childIndex = j + 1;
                        Log.d("TAG", "userAccount의 자식 노드의 자식 노드 :" + childIndex + ": ");
                        Log.d("TAG", "이미지 URL: " + childMap.get("imgUrl"));
                        Log.d("TAG", "장소 이름: " + childMap.get("titleName"));
                        Log.d("TAG", "주소: " + childMap.get("address"));
                        Log.d("TAG", "x좌표 : " + childMap.get("x"));
                        Log.d("TAG", "y좌표 : " + childMap.get("y"));
                        Log.d("TAG", "전화번호: " + childMap.get("tel"));
                        Log.d("TAG", "Uid: " + childMap.get("userUid"));
                    }

                    userIndex++;
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.w("TAG", "Failed to read value.", error.toException());
            }
        });
        //DB에서 값 가져오기
    }
    @Override
    public void onBackPressed() {
        // 기존 액티비티 스택을 사용하여 이전 화면(CourseListActivity)으로 돌아감
        Intent intents = new Intent(this, MainActivity.class);
        startActivity(intents);
        super.onBackPressed();
    }

    public class ItemOffsetDecoration extends RecyclerView.ItemDecoration {

        private int mItemOffset;

        public ItemOffsetDecoration(int itemOffset) {
            mItemOffset = itemOffset;
        }

        @Override
        public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
            super.getItemOffsets(outRect, view, parent, state);
            outRect.set(mItemOffset, mItemOffset, mItemOffset, mItemOffset);
        }
    }
}


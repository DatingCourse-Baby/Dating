package com.example.datingcourse;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

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

public class MyCourse extends AppCompatActivity {
    private ArrayList<ArrayList<Photo>> allBoardList = new ArrayList();
    private ArrayList<HashMap<String,Object>> allPhotoList = new ArrayList<>();

    private DatabaseReference mDatabaseRef;
    private FirebaseAuth mFirebaseAuth;
    private FirebaseUser mFirebaseUser;


    private TextView userNick;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_course);
        userNick = (TextView) findViewById(R.id.user_nick);

        FirebaseAuth mFirebaseAuth = FirebaseAuth.getInstance();
        DatabaseReference mDatabaseRef = FirebaseDatabase.getInstance().getReference("FirebaseRegister");

        fetchSingleValueFromUserRef(mFirebaseAuth,mDatabaseRef);

        MyCourse();
    }

    public void MyCourse()
    {
        allBoardList.clear();
        mFirebaseAuth = FirebaseAuth.getInstance();
        mFirebaseUser = mFirebaseAuth.getCurrentUser();
        String currentUid = mFirebaseUser.getUid();


        mDatabaseRef = FirebaseDatabase.getInstance().getReference("FirebaseRegister").child("UserCourse");
        mDatabaseRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                ArrayList<HashMap<String, Object>> tempList = new ArrayList<>();

                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    String targetParentKey = null;

                    // 현재 사용자의 Uid와 같은 userUid를 찾고, 부모 노드의 키를 저장합니다.
                    for (DataSnapshot courseSnapshot : dataSnapshot.getChildren()) {
                        String userUid = courseSnapshot.child("userUid").getValue(String.class);
                        if (userUid != null && userUid.equals(currentUid)) {
                            targetParentKey = dataSnapshot.getKey();
                            break;
                        }
                    }

                    // 현재 사용자의 Uid와 같은 userUid를 가진 부모 노드의 자식 노드 데이터를 가져옵니다.
                    if (targetParentKey != null && targetParentKey.equals(dataSnapshot.getKey())) {
                        ArrayList<HashMap<String, Object>> childTempList = new ArrayList<>();
                        ArrayList<Photo> BoardList = new ArrayList<>();
                        for (DataSnapshot courseSnapshot : dataSnapshot.getChildren()) {
                            String addressName = courseSnapshot.child("addressName").getValue(String.class);
                            String imgUrl = courseSnapshot.child("imgUrl").getValue(String.class);
                            String tel = courseSnapshot.child("tel").getValue(String.class);
                            String titleName = courseSnapshot.child("titleName").getValue(String.class);
                            Double x = courseSnapshot.child("x").getValue(Double.class);
                            Double y = courseSnapshot.child("y").getValue(Double.class);
                            String userUid = courseSnapshot.child("userUid").getValue(String.class);
                            String courseId = courseSnapshot.child("courseId").getValue(String.class);
                            Log.d("courseId 1 " ,"courseId 1 " + courseId);
                            // 가져온 값을 이용하여 Points 객체를 생성하고, boardList에 추가합니다.
                            Photo photo = new Photo(imgUrl, titleName, addressName, x, y, tel, userUid);
                            BoardList.add(new Photo(courseId,imgUrl, titleName, addressName, x, y, tel, userUid));
                            photo.setCourseId(courseId);

                            HashMap<String, Object> photoMap = new HashMap<>();
                            photoMap.put("imageUrl", photo.getImgUrl());
                            photoMap.put("titleName", photo.getTitleName());
                            photoMap.put("address", photo.getAddressName());
                            photoMap.put("x", photo.getX());
                            photoMap.put("y", photo.getY());
                            photoMap.put("tel", photo.getTel());
                            photoMap.put("userUid", photo.getUserUid());
                            photoMap.put("courseId",courseId);
                            // 최근값을 리스트의 앞에 추가
                            childTempList.add(photoMap);
                        }
                        allBoardList.add(BoardList);
                        HashMap<String, Object> userMap = new HashMap<>();
                        userMap.put("dataSnapshotKey", dataSnapshot.getKey());
                        userMap.put("childList", childTempList);

                        tempList.add(userMap);
                    }
                }

                allPhotoList.clear();
                // 리스트 뒤집어서 모든 값 대체
                for (int i = tempList.size() - 1; i >= 0; i--) {
                    allPhotoList.add(tempList.get(i));
                }

                // 데이터 로딩이 완료되면 RecyclerView를 업데이트합니다.
                RecyclerView view = findViewById(R.id.recyclerView);
                MyCourseAdapter verticalAdapter = new MyCourseAdapter(MyCourse.this, allBoardList);
                view.setHasFixedSize(true);
                view.setLayoutManager(new LinearLayoutManager(MyCourse.this, LinearLayoutManager.VERTICAL, false));
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
                        Log.d("TAG","코스 리스트 이름" + childMap.get("courseId"));
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
    public void fetchSingleValueFromUserRef(FirebaseAuth mFirebaseAuth, DatabaseReference mDatabaseRef){
        FirebaseUser currentUser = mFirebaseAuth.getCurrentUser();
        if(currentUser != null){
            String uid = currentUser.getUid();

            DatabaseReference userRef = mDatabaseRef.child("UserAccount").child(uid);
            DatabaseReference specificValueRef = userRef.child("NickName");

            specificValueRef.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                userNick.setText(dataSnapshot.getValue(String.class));
                            }
                        });
                    } else {
                        Log.w("TAG", "해당하는 닉네임 없음");
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    Log.w("TAG", "데이터 불러오는 과정에서 오류 발생");
                }
            });
        }
    }
}
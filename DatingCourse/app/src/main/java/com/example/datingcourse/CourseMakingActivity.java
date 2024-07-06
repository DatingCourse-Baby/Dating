// 코스 만들기 클래스
package com.example.datingcourse;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.database.annotations.Nullable;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

public class CourseMakingActivity extends AppCompatActivity implements InformationAPI.DataListener{

    private MyApp myApp;
    private ArrayList<Photo> photos;
    private PhotoAdapter photoAdapter;
    private String nickName;
    private ArrayList<HashMap<String, Object>> photosList = new ArrayList<>(); // 여러 장소 정보를 저장할 ArrayList
    private FirebaseAuth mFirebaseAuth;
    private FirebaseUser mFirebaseUser;
    private TextView et_nickName;
    public static InformationAPI.DataListener dataListener;
    private int count=0;  //API호출 시 증가
    private DatabaseReference mLoadDatabaseRef;
    private java.util.Random random = new Random();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_course_making);

        // MyApp 객체 가져오기
        myApp = (MyApp) getApplicationContext();
        photos = myApp.getPhotos();

        getSupportActionBar().setTitle("코스 만들기");

        if (photos == null) {
            photos = new ArrayList<>(); // Initialize photos if it's null
            myApp.setPhotos(photos);
        }
        mFirebaseAuth = FirebaseAuth.getInstance();
        mFirebaseUser = mFirebaseAuth.getCurrentUser();

        et_nickName = findViewById(R.id.nickNameMaking);
        fetchSingleValueFromUserRef(mFirebaseAuth, mLoadDatabaseRef);

        //파이어베이스 인증 및 데이터베이스 초기화등
//        mFirebaseAuth = FirebaseAuth.getInstance();
//        mDatabaseRef = FirebaseDatabase.getInstance().getReference("FirebaseRegister");

        Intent intent = getIntent();
        String imgUrl = intent.getStringExtra("imgUrl");
        String titleName = intent.getStringExtra("titleName");
        String addressName = intent.getStringExtra("addressName");
        Double x = intent.getDoubleExtra("x", 0.0); // 기본값 0.0
        Double y = intent.getDoubleExtra("y", 0.0); // 기본값 0.0
        String tel = intent.getStringExtra("tel");
        String userUid = mFirebaseUser.getUid().toString();

        // 값이 null이거나 빈 문자열인 경우 "정보 없음"으로 설정
        imgUrl = (imgUrl == null || imgUrl.isEmpty()) ? null : imgUrl;
        titleName = (titleName == null || titleName.isEmpty()) ? "정보 없음" : titleName;
        addressName = (addressName == null || addressName.isEmpty()) ? "정보 없음" : addressName;
        tel = (tel == null || tel.isEmpty()) ? "정보 없음" : tel;

        // 받아온 정보를 Photo 객체로 생성
        if (imgUrl != null && titleName != null && addressName != null && x != null && y != null && tel != null) {
            // 값들이 유효하면 Photo 객체 생성
            Photo photo = new Photo(imgUrl, titleName, addressName, x, y, tel,userUid);
            boolean isPhotoExist = false;
            for (Photo p : photos) {
                if (p.getImgUrl().equals(imgUrl) && p.getTitleName().equals(titleName)) {
                    isPhotoExist = true;
                    break;
                }
            }

            if (!isPhotoExist) {
                // 받아온 정보를 Photo 객체로 생성 및 추가
                photos.add(photo);
            }

            // 정보를 HashMap에 저장
            HashMap<String, Object> photoMap = new HashMap<>();
            photoMap.put("imageUrl", photo.getImgUrl());
            photoMap.put("placeName", photo.getTitleName());
            photoMap.put("address", photo.getAddressName());
            photoMap.put("x", photo.getX());
            photoMap.put("y", photo.getY());
            photoMap.put("tel", photo.getTel());
            photoMap.put("userUid",photo.getUserUid());

            // HashMap 객체를 ArrayList에 추가
            photosList.add(photoMap);
            myApp.setPhotos(photos);
            // 리사이클러 뷰
            RecyclerView recyclerView = findViewById(R.id.recyclerViewImg);

            if(myApp.getPhotos().isEmpty()) {
                recyclerView.setVisibility(View.GONE);
            } else {
                recyclerView.setVisibility(View.VISIBLE);
            }
            Log.d("Course_img", imgUrl);
            // 리사이클러 뷰 어댑터 초기화
            photoAdapter = new PhotoAdapter(this, myApp.getPhotos());
            recyclerView.setAdapter(photoAdapter);
            photoAdapter.notifyDataSetChanged();
            LinearLayoutManager horizontalLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
            recyclerView.setLayoutManager(horizontalLayoutManager);
        }

        // 코스 생성 버튼
        Button create_btn = findViewById(R.id.course_create_btns);
        create_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /*String nickStr = et_nickName.getText().toString();
                loadPhotosFromGlobal(nickStr);*/
                loadPhotosFromGlobal();
                if (myApp != null) {
                    myApp.setPhotos(new ArrayList<Photo>());
                }

                Intent intent = new Intent(getApplicationContext(), BoardActivity.class);   // RecommendActivity
                startActivity(intent);
            }
        });

        Button search_address = (Button) findViewById(R.id.search_address);
        search_address.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("TAG","버튼 눌림");
                Intent intent1 = new Intent(getApplicationContext(), MapChoiceActivity.class);
                startActivity(intent1);
            }
        });

        dataListener = this;
        int randomIndex = random.nextInt(4)+1;
        InformationAPI api = new InformationAPI(39,1,"","","");
        api.new NetworkTask().execute();
        InformationAPI api2 = new InformationAPI(38,1,"","","");
        api2.new NetworkTask().execute();
        InformationAPI api3 = new InformationAPI(28,1,"","","");
        api3.new NetworkTask().execute();

        ImageButton btn_again = findViewById(R.id.btn_again);
        btn_again.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                count=0;
                int randomIndex = random.nextInt(4)+1;
                InformationAPI api = new InformationAPI(39,1,"","","");
                api.new NetworkTask().execute();
                InformationAPI api2 = new InformationAPI(38,1,"","","");
                api2.new NetworkTask().execute();
                InformationAPI api3 = new InformationAPI(28,1,"","","");
                api3.new NetworkTask().execute();
            }
        });
    }

    @Override
    public void onDataReceived(List<String> titles, List<String> images, List<String> address, List<String> x, List<String> y, List<String> tel) {
        count++;
        if(count==1) {
            if(titles.size() > 0){
                ImageView place1 = findViewById(R.id.place1);
                TextView textPlace1 = findViewById(R.id.textPlace1);
                int randomIndex = random.nextInt(titles.size());
                String imageUrl = images.get(randomIndex);
                if (!isFinishing()) {
                    // Activity가 종료 중이 아닐 때만 이미지를 로드
                    Glide.with(this).load(imageUrl).error(R.drawable.noimage).fallback(R.drawable.noimage).placeholder(R.drawable.loading).centerCrop().into(place1);
                }
                textPlace1.setText(titles.get(randomIndex));
                place1.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(CourseMakingActivity.this, infos.class);
                        intent.putExtra("imgUrl", imageUrl);
                        intent.putExtra("titleName", titles.get(randomIndex));
                        intent.putExtra("addressName", address.get(randomIndex));
                        intent.putExtra("x", Double.parseDouble(x.get(randomIndex)));
                        intent.putExtra("y", Double.parseDouble(y.get(randomIndex)));
                        intent.putExtra("tel", tel.get(randomIndex));
                        startActivity(intent);
                    }
                });
            }else {
                int randomIndex = random.nextInt(4)+1;
                InformationAPI api = new InformationAPI(39,randomIndex,"","","");
                api.new NetworkTask().execute();
                InformationAPI api2 = new InformationAPI(38,randomIndex,"","","");
                api2.new NetworkTask().execute();
                InformationAPI api3 = new InformationAPI(28,randomIndex,"","","");
                api3.new NetworkTask().execute();
            }

        }
        else if (count==2) {
            if(titles.size() > 0){
                ImageView place2 = findViewById(R.id.place2);
                TextView textPlace2 = findViewById(R.id.textPlace2);
                int randomIndex = random.nextInt(titles.size());
                String imageUrl = images.get(randomIndex);
                if (!isFinishing()) {
                    // Activity가 종료 중이 아닐 때만 이미지를 로드
                    Glide.with(this).load(imageUrl).error(R.drawable.noimage).fallback(R.drawable.noimage).placeholder(R.drawable.loading).centerCrop().into(place2);
                }
                textPlace2.setText(titles.get(randomIndex));
                place2.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(CourseMakingActivity.this, infos.class);
                        intent.putExtra("imgUrl", imageUrl);
                        intent.putExtra("titleName", titles.get(randomIndex));
                        intent.putExtra("addressName", address.get(randomIndex));
                        intent.putExtra("x", Double.parseDouble(x.get(randomIndex)));
                        intent.putExtra("y", Double.parseDouble(y.get(randomIndex)));
                        intent.putExtra("tel", tel.get(randomIndex));
                        startActivity(intent);
                    }
                });
            }else {
                int randomIndex = random.nextInt(4)+1;
                InformationAPI api = new InformationAPI(39,randomIndex,"","","");
                api.new NetworkTask().execute();
                InformationAPI api2 = new InformationAPI(38,randomIndex,"","","");
                api2.new NetworkTask().execute();
                InformationAPI api3 = new InformationAPI(28,randomIndex,"","","");
                api3.new NetworkTask().execute();
            }

        }
        else if (count==3) {
            if(titles.size() > 0){
                ImageView place3 = findViewById(R.id.place3);
                TextView textPlace3 = findViewById(R.id.textPlace3);
                int randomIndex = random.nextInt(titles.size());
                String imageUrl = images.get(randomIndex);
                if (!isFinishing()) {
                    // Activity가 종료 중이 아닐 때만 이미지를 로드
                    Glide.with(this).load(imageUrl).error(R.drawable.noimage).fallback(R.drawable.noimage).placeholder(R.drawable.loading).centerCrop().into(place3);
                }
                textPlace3.setText(titles.get(randomIndex));
                place3.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(CourseMakingActivity.this, infos.class);
                        intent.putExtra("imgUrl", imageUrl);
                        intent.putExtra("titleName", titles.get(randomIndex));
                        intent.putExtra("addressName", address.get(randomIndex));
                        intent.putExtra("x", Double.parseDouble(x.get(randomIndex)));
                        intent.putExtra("y", Double.parseDouble(y.get(randomIndex)));
                        intent.putExtra("tel", tel.get(randomIndex));
                        startActivity(intent);
                    }
                });
            }else {
                int randomIndex = random.nextInt(4)+1;
                InformationAPI api = new InformationAPI(39,randomIndex,"","","");
                api.new NetworkTask().execute();
                InformationAPI api2 = new InformationAPI(38,randomIndex,"","","");
                api2.new NetworkTask().execute();
                InformationAPI api3 = new InformationAPI(28,randomIndex,"","","");
                api3.new NetworkTask().execute();
            }

        }

    }

    @Override
    public void onBackPressed() {
        // MyApp 객체의 photos 데이터 초기화
        if (myApp != null) {
            myApp.setPhotos(new ArrayList<Photo>());
        }

        // 기존 액티비티 스택을 사용하여 이전 화면(CourseListActivity)으로 돌아감
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        super.onBackPressed();
    }

    private void loadPhotosFromGlobal() {   // String nickStr
        mLoadDatabaseRef = FirebaseDatabase.getInstance().getReference("FirebaseRegister"); //getReference안에 " " 앱이름, 프로젝트 이름

        // 새로운 코스 ID 생성
        String courseId = mLoadDatabaseRef.child("UserCourse").push().getKey();

        for (int j =0; j<myApp.getPhotos().size();j++){
            Photo photo = myApp.getPhotos().get(j);

            HashMap<String,Object> saveCourse = new HashMap<String,Object>();

            saveCourse.put("imgUrl",photo.getImgUrl());
            saveCourse.put("titleName",photo.getTitleName());
            saveCourse.put("addressName",photo.getAddressName());
            saveCourse.put("x",photo.getX());
            saveCourse.put("y",photo.getY());
            saveCourse.put("tel",photo.getTel());
            saveCourse.put("userUid",photo.getUserUid());
            saveCourse.put("courseId",courseId);

            photo.setCourseId(courseId);

            Log.d("CourseMakingActivity", "Photo Title: " + photo.getTitleName());


            String photoId = mLoadDatabaseRef.child(courseId).push().getKey();

            // 값을 새 노드에 저장합니다.
            mLoadDatabaseRef.child("UserCourse")
                    .child(courseId)
                    .child(photoId)
                    .setValue(saveCourse, new DatabaseReference.CompletionListener() {
                        @Override
                        public void onComplete(@Nullable DatabaseError error, @NonNull DatabaseReference ref) {
                            if (error == null) {
                                // 데이터 저장 성공. BoardActivity로 이동
                                Intent intent = new Intent(CourseMakingActivity.this, BoardActivity.class);
                                startActivity(intent);
                            } else {
                                // 데이터 저장 실패. 오류 메시지를 출력
                                Log.e("TAG", "Data could not be saved. " + error.getMessage());
                            }
                        }
                    });

        }
    }

    public void fetchSingleValueFromUserRef(FirebaseAuth mFirebaseAuth, DatabaseReference mDatabaseRef){
        if(mDatabaseRef == null) {
            mDatabaseRef = FirebaseDatabase.getInstance().getReference("FirebaseRegister");
        }

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
                                et_nickName.setText(dataSnapshot.getValue(String.class));
                                nickName = dataSnapshot.getValue(String.class);
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

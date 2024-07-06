package com.example.datingcourse;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.List;

public class PointItem extends AppCompatActivity {

    private TextView point_my;
    private RecyclerView recyclerView;
    private ProductAdapter adapter;

    private List<Product> productList;

    private FirebaseAuth mFirebaseAuth;
    private DatabaseReference mDatabaseRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_point_item);

        point_my = findViewById(R.id.point_my);

        mFirebaseAuth = FirebaseAuth.getInstance();
        mDatabaseRef = FirebaseDatabase.getInstance().getReference("FirebaseRegister");

        fetchSingleValueFromUserRef(mFirebaseAuth,mDatabaseRef);

        recyclerView = findViewById(R.id.recycler_view);
        GridLayoutManager gridLayoutManager = new GridLayoutManager(this, 2);
        recyclerView.setLayoutManager(gridLayoutManager);

        productList = new ArrayList<>();

        productList.add(new Product("스타벅스","아이스 카페 아메리카노 T", 4950, R.drawable.item_1));
        productList.add(new Product("맘스터치", "싸이버거 단품",5000, R.drawable.item_2));
        productList.add(new Product("BHC","뿌링클+치즈볼+콜라1.25L", 27500, R.drawable.item_3));
        productList.add(new Product("죠스떡볶이","죠스떡볶이",4950, R.drawable.item_4));
        productList.add(new Product("던킨","도너츠 6개팩", 11000, R.drawable.item_5));
        productList.add(new Product("맥도날드","불고기 버거 세트", 4950, R.drawable.item_6));
        productList.add(new Product("설빙","애플망고치즈설빙", 15000, R.drawable.item_7));
        productList.add(new Product("교촌치킨","허니콤보+웨지감자+콜라1.25L", 13000, R.drawable.item_8));
        productList.add(new Product("GS25","모바일 상품권 5천원권", 5500, R.drawable.item_9));
        productList.add(new Product("CU","모바일 상품권 5천원권", 5500, R.drawable.item_10));

        adapter = new ProductAdapter(productList);

        recyclerView.setAdapter(adapter);
        adapter.setOnItemClickListener(new ProductAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                Product clickedProduct = productList.get(position);
                AlertDialog.Builder builder = new AlertDialog.Builder(PointItem.this);
                builder.setTitle("제품 구매 확인");
                builder.setMessage("제품명: "+clickedProduct.getName2()+"\n가격: " + clickedProduct.getPrice() + " 포인트\n구매하시겠습니까?");

                // "확인" 버튼 클릭 시 동작 설정
                builder.setPositiveButton("확인", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // 사용자의 포인트
                        int userPoint = Integer.parseInt(point_my.getText().toString());
                        // 클릭한 제품의 포인트
                        int productPrice = clickedProduct.getPrice();

                        // 사용자의 포인트가 제품의 가격보다 크거나 같은 경우
                        if (userPoint >= productPrice) {
                            userPoint = userPoint - productPrice;

                            // DB에 새로운 포인트 값을 업데이트
                            String uid = mFirebaseAuth.getCurrentUser().getUid();
                            DatabaseReference userRef = mDatabaseRef.child("UserAccount").child(uid);
                            DatabaseReference pointValueRef = userRef.child("point");

                            pointValueRef.setValue(userPoint);

                            // Firebase Storage 참조 가져오기
                            StorageReference storageRef = FirebaseStorage.getInstance().getReference();
                            StorageReference imageRef = storageRef.child("coupons/" + clickedProduct.getName1() + ".jpg");

                            // 이미지 다운로드 URL 가져오기
                            imageRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
                                    String imageUrl = uri.toString();

                                    // DB에 URL 저장
                                    DatabaseReference couponRef = mDatabaseRef.child("UserCoupon").child(uid);
                                    couponRef.push().setValue(imageUrl);
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    // Handle any errors
                                    Log.e("Image Download Error", e.getMessage());
                                }
                            });

                            Toast.makeText(PointItem.this, "제품을 구매했습니다.", Toast.LENGTH_SHORT).show();

                            final DatabaseReference adminCouponRef = mDatabaseRef.child("AdminCoupon").child(clickedProduct.getName1());

                            adminCouponRef.addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                    int currentCount = 0;
                                    int currentPoint = 0;

                                    // 기존 값이 있으면 불러온다
                                    if (dataSnapshot.hasChild("count")) {
                                        currentCount = dataSnapshot.child("count").getValue(Integer.class);
                                    }
                                    if (dataSnapshot.hasChild("point")) {
                                        currentPoint = dataSnapshot.child("point").getValue(Integer.class);
                                    }

                                    // 새로운 값을 설정한다
                                    adminCouponRef.child("count").setValue(currentCount + 1);
                                    adminCouponRef.child("point").setValue(currentPoint + clickedProduct.getPrice());
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError databaseError) {
                                    // Handle error
                                }
                            });
                        } else {
                            // 포인트가 부족한 경우
                            Toast.makeText(PointItem.this, "포인트가 부족합니다.", Toast.LENGTH_SHORT).show();
                        }

                        dialog.dismiss(); // 다이얼로그 닫기
                    }
                });

                // "취소" 버튼 클릭 시 동작 설정
                builder.setNegativeButton("취소", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // 여기에 취소 버튼을 클릭했을 때 수행할 동작 추가 (예: 다이얼로그 닫기)
                        dialog.dismiss();
                    }
                });

                // 다이얼로그 표시
                AlertDialog dialog = builder.create();
                dialog.show();
            }
        });
    }

    public void fetchSingleValueFromUserRef(FirebaseAuth mFirebaseAuth, DatabaseReference mDatabaseRef){
        FirebaseUser currentUser = mFirebaseAuth.getCurrentUser();
        if(currentUser != null){
            String uid = currentUser.getUid();

            DatabaseReference userRef = mDatabaseRef.child("UserAccount").child(uid);
            DatabaseReference pointValueRef = userRef.child("point");

            pointValueRef.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()) {
                        PointItem.this.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                point_my.setText(String.valueOf(dataSnapshot.getValue(Integer.class)));
                            }
                        });
                    } else {
                        Log.w("TAG", "해당하는 포인트 없음");
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

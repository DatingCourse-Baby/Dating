package com.example.datingcourse;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import kr.co.bootpay.Bootpay;
import kr.co.bootpay.BootpayAnalytics;
import kr.co.bootpay.enums.Method;
import kr.co.bootpay.enums.PG;
import kr.co.bootpay.enums.UX;
import kr.co.bootpay.listener.CancelListener;
import kr.co.bootpay.listener.CloseListener;
import kr.co.bootpay.listener.ConfirmListener;
import kr.co.bootpay.listener.DoneListener;
import kr.co.bootpay.listener.ReadyListener;
import kr.co.bootpay.model.BootExtra;
import kr.co.bootpay.model.BootUser;

public class PayActivity extends AppCompatActivity {

    Button button;
    private int stuck = 10;
    private FirebaseAuth mFirebaseAuth;
    private DatabaseReference mDatabaseRef;
    boolean memberShip;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pay);

        //파이어베이스 인증 및 데이터베이스 초기화등
        mFirebaseAuth = FirebaseAuth.getInstance();
        mDatabaseRef = FirebaseDatabase.getInstance().getReference("FirebaseRegister");

        fetchSingleValueFromUserRef(mFirebaseAuth,mDatabaseRef);
    }

    //realtimeDatabase에서 특정 값 하나만 가져오기(별명)
    public void fetchSingleValueFromUserRef(FirebaseAuth mFirebaseAuth, DatabaseReference mDatabaseRef){
        FirebaseUser currentUser = mFirebaseAuth.getCurrentUser(); //현재 로그인한 사용자
        if(currentUser != null){
            String uid = currentUser.getUid();

            DatabaseReference userRef = mDatabaseRef.child("UserAccount").child(uid);
            DatabaseReference memberShipValueRef = userRef.child("memberShip");
            memberShipValueRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    //데이터 스냅샷이 한 번 호출되어 값을 가져옴
                    if(snapshot.exists()){
                        memberShip = snapshot.getValue(Boolean.class);
                        Log.d("firebase_premium", "" + memberShip);
                        showPaymentDialog();
                    } else {
                        Log.w("TAG", "해당하는 멤버쉽 없음");
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    //에러 발생시 호출됨
                    Log.w("TAG", "데이터 불러오는 과정에서 오류 발생");
                }
            });

        }

    }
    public void showPaymentDialog() {
        // 초기설정 - 해당 프로젝트(안드로이드)의 application id 값을 설정합니다. 결제와 통계를 위해 꼭 필요합니다.
        // 앱에서 확인하지 말고 꼭 웹 사이트에서 확인하자. 앱의 application id 갖다 쓰면 안됨!!!
        BootpayAnalytics.init(this, "64f20fe7e57a7e001cbb125e");

        button = findViewById(R.id.test);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                BootUser bootUser = new BootUser().setPhone("010-9431-3882"); // !! 자신의 핸드폰 번호로 바꾸기
                BootExtra bootExtra = new BootExtra().setQuotas(new int[] {0, 2, 3});

                Bootpay.init(getFragmentManager())
                        .setApplicationId("64f20fe7e57a7e001cbb125e") // 해당 프로젝트(안드로이드)의 application id 값(위의 값 복붙)
                        .setPG(PG.INICIS) // 결제할 PG 사
                        .setMethod(Method.CARD) // 결제수단
                        .setContext(PayActivity.this)
                        .setBootUser(bootUser)
                        .setBootExtra(bootExtra)
                        .setUX(UX.PG_DIALOG)
//                .setUserPhone("010-1234-5678") // 구매자 전화번호
                        .setName("프리미엄 요금제") // 결제할 상품명
                        .setOrderId("1234") // 결제 고유번호 (expire_month)
                        .setPrice(4900) // 결제할 금액
                        .addItem("마우's 스", 1, "ITEM_CODE_MOUSE", 100) // 주문정보에 담길 상품정보, 통계를 위해 사용
                        .addItem("키보드", 1, "ITEM_CODE_KEYBOARD", 200, "패션", "여성상의", "블라우스") // 주문정보에 담길 상품정보, 통계를 위해 사용
                        .onConfirm(new ConfirmListener() { // 결제가 진행되기 바로 직전 호출되는 함수로, 주로 재고처리 등의 로직이 수행
                            @Override
                            public void onConfirm(@Nullable String message) {

                                if (0 < stuck) Bootpay.confirm(message); // 재고가 있을 경우.
                                else Bootpay.removePaymentWindow(); // 재고가 없어 중간에 결제창을 닫고 싶을 경우
                                Log.d("confirm", message);
                            }
                        })
                        .onDone(new DoneListener() { // 결제완료시 호출, 아이템 지급 등 데이터 동기화 로직을 수행합니다
                            @Override
                            public void onDone(@Nullable String message) {
                                Log.d("done", message);
                                // 결제가 완료되면 Firebase Realtime Database의 premium 값을 true로 설정
                                updatePremiumStatusInFirebase(true, 1500);
                            }
                        })
                        .onReady(new ReadyListener() { // 가상계좌 입금 계좌번호가 발급되면 호출되는 함수입니다.
                            @Override
                            public void onReady(@Nullable String message) {
                                Log.d("ready", message);
                            }
                        })
                        .onCancel(new CancelListener() { // 결제 취소시 호출
                            @Override
                            public void onCancel(@Nullable String message) {

                                Log.d("cancel", message);
                            }
                        })
                        .onClose(
                                new CloseListener() { //결제창이 닫힐때 실행되는 부분
                                    @Override
                                    public void onClose(String message) {
                                        Log.d("close", "close");
                                    }
                                })
                        .request();
            }
        });
    }

    public void updatePremiumStatusInFirebase(boolean newStatus, int newPoint) {
        FirebaseUser currentUser = mFirebaseAuth.getCurrentUser();

        if(currentUser != null){
            String uid = currentUser.getUid();

            DatabaseReference userRef = mDatabaseRef.child("UserAccount").child(uid);
            DatabaseReference memberShipRef = userRef.child("memberShip");
            DatabaseReference pointRef = userRef.child("point");

            memberShipRef.setValue(newStatus).addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {
                    Toast.makeText(PayActivity.this, "프리미엄 멤버쉽 결제가 완료되었습니다.", Toast.LENGTH_SHORT).show();
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(PayActivity.this, "결제가 실패되었습니다.", Toast.LENGTH_SHORT).show();
                }
            });

            pointRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()) {
                        int currentPoint = dataSnapshot.getValue(Integer.class);
                        int newPoint = currentPoint + 1500; // 원래 포인트에 1500을 더함

                        // 새로운 포인트 값을 DB에 업데이트
                        pointRef.setValue(newPoint).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                // 업데이트 성공
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                // 업데이트 실패
                            }
                        });
                    } else {
                        // 데이터 없음
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    // 에러 발생
                }
            });
        }
        // 업데이트가 성공적으로 이루어진 후에는 결과를 설정하고 액티비티를 종료합니다.
        Intent resultIntent = new Intent();
        setResult(Activity.RESULT_OK, resultIntent);
        finish();
    }
}
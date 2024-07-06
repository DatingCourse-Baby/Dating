package com.example.datingcourse;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
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
import com.google.firebase.storage.StorageException;
import com.google.firebase.storage.StorageReference;

import java.io.File;
import java.util.Arrays;
import java.util.List;

public class FragMyPage extends Fragment {
    private Button btn_logOut,editInfo, btn_my_Write;
    private TextView nick, point;
    private ImageView member_yes;
    private ImageView profile;

    private String uid;

    private View view;
    private ProgressDialog progressDialog;
    private FirebaseAuth mFirebaseAuth;
    private DatabaseReference mDatabaseRef;

    Boolean memberShip;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.activity_frag_my_page, container, false);

        mFirebaseAuth = FirebaseAuth.getInstance();
        mDatabaseRef = FirebaseDatabase.getInstance().getReference("FirebaseRegister");
        uid = mFirebaseAuth.getCurrentUser().getUid(); // 로그인한 사용자의 UID 가져오기

        fetchSingleValueFromUserRef(mFirebaseAuth,mDatabaseRef);

        btn_my_Write = view.findViewById(R.id.myWrite);
        btn_my_Write.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), MyPostActivity
                        .class);
                startActivity(intent);
            }
        });

        btn_logOut = view.findViewById(R.id.logOut);
        btn_logOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mFirebaseAuth.signOut();
                Intent intent = new Intent(getActivity(), LoginActivity.class);
                startActivity(intent);
            }
        });

        Button btn_myCourse = view.findViewById(R.id.myCourse);
        btn_myCourse.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), MyCourse.class);
                startActivity(intent);
            }
        });

        Button btn_store = view.findViewById(R.id.btn_store);
        btn_store.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), PointItem.class);
                startActivity(intent);
            }
        });

        Button btn_coupon = view.findViewById(R.id.btn_coupon);
        btn_coupon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), MyCouponActivity.class);
                startActivity(intent);
            }
        });

        nick = view.findViewById(R.id.nick);
        member_yes = view.findViewById(R.id.member_yes);
        point = view.findViewById(R.id.point);
        fetchSingleValueFromUserRef(mFirebaseAuth, mDatabaseRef);

        editInfo = view.findViewById(R.id.myInfo);
        editInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), EditMyInfoActivity.class);
                startActivity(intent);
            }
        });

        profile = view.findViewById(R.id.profile);
//        getFireBaseProfileImage();
        progressDialog = new ProgressDialog(getActivity());
        progressDialog.setTitle("Uploading Images...");
        progressDialog.show();
        downloadImg();
        return view;
    }
    public void fetchSingleValueFromUserRef(FirebaseAuth mFirebaseAuth, DatabaseReference mDatabaseRef){
        FirebaseUser currentUser = mFirebaseAuth.getCurrentUser();
        if(currentUser != null){
            String uid = currentUser.getUid();

            DatabaseReference userRef = mDatabaseRef.child("UserAccount").child(uid);
            DatabaseReference specificValueRef = userRef.child("NickName");
            DatabaseReference memberShipValueRef = userRef.child("memberShip");
            DatabaseReference pointValueRef = userRef.child("point");

            specificValueRef.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()) {
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                nick.setText(dataSnapshot.getValue(String.class));
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

            memberShipValueRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    //데이터 스냅샷이 한 번 호출되어 값을 가져옴
                    if(snapshot.exists()){
                        memberShip = snapshot.getValue(Boolean.class);
                        Log.d("firebase_premium", "" + memberShip);

                        if (!memberShip) {
                            member_yes.setVisibility(View.GONE);
                        } else {
                            member_yes.setVisibility(View.VISIBLE);
                        }

                        Button btn_membership = view.findViewById(R.id.btn_membership);
                        btn_membership.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                if (!memberShip) {
                                    Intent intent = new Intent(getActivity(), PayActivity.class);
                                    startActivity(intent);
                                } else {
                                    Toast.makeText(getActivity(), "이미 프리미엄 회원입니다.", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
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

            pointValueRef.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()) {
                        if(getActivity() != null) {
                            getActivity().runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    // Your code here
                                }
                            });
                        }
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

    // TODO: 2021-03-16 존나 중요!! 파이어베이스 이미지 (시작할때 프로필 가져오기)
    /**프로필 이미지 (파이어베이스 스토리지에서 가져오기) */
//    private void getFireBaseProfileImage() {
//        //우선 디렉토리 파일 하나만든다.
//        File file = getActivity().getExternalFilesDir(Environment.DIRECTORY_PICTURES + "/profile_img"); //이미지를 저장할 수 있는 디렉토리
//        //구분할 수 있게 /toolbar_images폴더에 넣어준다.
//        //이 파일안에 저 디렉토리가 있는지 확인
//        if (!file.isDirectory()) { //디렉토리가 없으면,
//            file.mkdir(); //디렉토리를 만든다.
//        }
//        downloadImg(); //이미지 다운로드해서 가져오기 메서드
//    }
    private void downloadImg() {
        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference storageRef = storage.getReference();
        String imgName = "profile" + uid;

        List<String> extensions = Arrays.asList(".jpg", ".jpeg", ".png");

        StorageReference defaultImageRef = storageRef.child("profile_img/default_profile_image.png"); // 기본 이미지 파일 경로

        loadProfileImage(storageRef, imgName, extensions, 0, defaultImageRef);
    }

    private void loadProfileImage(StorageReference storageRef, String imgName, List<String> extensions, int index, StorageReference defaultImageRef) {
        if (index < extensions.size()) {
            StorageReference profileImageRef = storageRef.child("profile_img/" + imgName + extensions.get(index));
            profileImageRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                @Override
                public void onSuccess(Uri uri) {
                    Glide.with(FragMyPage.this)
                            .load(uri)
                            .circleCrop()
                            .diskCacheStrategy(DiskCacheStrategy.NONE)
                            .skipMemoryCache(true).into(profile);
                    if(progressDialog.isShowing()) {
                        progressDialog.dismiss();
                    }
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    if (e instanceof StorageException) {
                        StorageException storageException = (StorageException) e;
                        if (storageException.getErrorCode() == StorageException.ERROR_OBJECT_NOT_FOUND) {
                            // 파일이 존재하지 않을 때 다음 확장자로 시도
                            loadProfileImage(storageRef, imgName, extensions, index + 1, defaultImageRef);
                        } else {
                            e.printStackTrace();
                        }
                    } else {
                        e.printStackTrace();
                    }
                }
            });
        } else {
            // 모든 확장자를 시도했음에도 프로필 이미지가 존재하지 않을 경우 기본 이미지를 로드
            defaultImageRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                @Override
                public void onSuccess(Uri uri) {
                    Glide.with(FragMyPage.this)
                            .load(uri)
                            .circleCrop().into(profile);
                    if(progressDialog.isShowing()) {
                        progressDialog.dismiss();
                    }
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception exception) {
                    // 기본 이미지 로드에 실패한 경우 로그를 출력하거나 에러 처리를 할 수 있습니다.
                }
            });
        }
    }

}
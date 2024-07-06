package com.example.datingcourse;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageException;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.util.Arrays;
import java.util.List;

public class EditMyInfoActivity extends AppCompatActivity {

    private TextView nick, withdrawal;

    //프로필 사진 변경 버튼
    private Button changeProfile,saveChange,doubleCheckNick;

    //프로필 사진 띄워주기
    private ImageView profile;

    private String uid;
    private String filename;

    private Uri uri;
    private TextInputEditText changeNick;

    private DatabaseReference nicknameFindRef;
    private boolean checkNickComplete;

    private static final int GALLERY_REQUEST_CODE = 1000;

    private boolean isImageChanged = false;
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editinfo);

        uid = FirebaseAuth.getInstance().getCurrentUser().getUid(); // 로그인한 사용자의 UID 가져오기
        filename = uid + ".jpg"; // 파일명을 사용자의 UID로 설정

        //닉네임 가져오기
        FirebaseAuth mFirebaseAuth = FirebaseAuth.getInstance();
        DatabaseReference mDatabaseRef = FirebaseDatabase.getInstance().getReference("FirebaseRegister");
        nick = (TextView) findViewById(R.id.nick);
        fetchSingleValueFromUserRef(mFirebaseAuth,mDatabaseRef);

        changeNick = findViewById(R.id.changeNick);
        changeNick.addTextChangedListener(textWatcher);

        doubleCheckNick = findViewById(R.id.check_nickname);
        doubleCheckNick.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TextInputEditText checkNick = findViewById(R.id.changeNick);
                String checkStr = checkNick.getText().toString().trim();
                if(TextUtils.isEmpty(checkStr)) {
                    //중복확인 버튼 눌렀는데 그 칸이 비어있다면
                    doubleCheckNick.setEnabled(false);
                }
                else {
                    nicknameFindRef = FirebaseDatabase.getInstance().getReference("FirebaseRegister").child("UserAccount");

                    //nicknameFindRef.orderByChild( )로 특정 속성에 따라 요소를 정렬 그리고 userNick과 equlTo( ) 같은게 있는지 탐색
                    Query query = nicknameFindRef.orderByChild("NickName").equalTo(checkStr);

                    query.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            boolean isDuplicate = false;

                            if (snapshot.exists()) {
                                isDuplicate = true;
                            }

                            if (isDuplicate) {
                                //닉네임 중복 O
                                checkNickComplete = false;
                                Toast.makeText(EditMyInfoActivity.this, checkStr + "은(는) 이미 사용중인 닉네임입니다.", Toast.LENGTH_SHORT).show();
                                Log.d("TAG", checkStr + "은(는) 이미 사용중인 닉네임입니다.");
                            } else {
                                //닉네임 중복 X
                                doubleCheckNick.setEnabled(false);
                                checkNickComplete = true;
                                Toast.makeText(EditMyInfoActivity.this, checkStr + "은(는) 사용 가능한 닉네임입니다.", Toast.LENGTH_SHORT).show();
                                Log.d("TAG", checkStr + "은(는) 사용 가능한 닉네임입니다.");
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {
                            Log.w("TAG", "중복확인창 닫힘 데이터베이스 에러", error.toException());
                        }
                    });
                }

            }
        });
        Button btn_go_main=findViewById(R.id.go_main);
        btn_go_main.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // MainActivity로 이동하는 Intent 생성
                Intent intent = new Intent(EditMyInfoActivity.this, MainActivity.class);
                startActivity(intent);
            }
        });
        //회원탈퇴
        withdrawal = (TextView) findViewById(R.id.remove_Info);
        withdrawal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mFirebaseAuth.getCurrentUser().delete();
                Toast.makeText(EditMyInfoActivity.this,"회원 탈퇴가 완료되었습니다.",Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(EditMyInfoActivity.this,LoginActivity.class);
                startActivity(intent);
            }
        });
        //프로필 이미지뷰에 현재 DB에서 가져온 사진 업로드
        profile = findViewById(R.id.profile);
        progressDialog = new ProgressDialog(EditMyInfoActivity.this);
        progressDialog.setTitle("이미지 업로드 중...");
        progressDialog.show();
        downloadImg();

        //프로필 사진 변경 버튼
        changeProfile = findViewById(R.id.changePicture);
        changeProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pickImageFromGallery();
            }
        });


        saveChange = findViewById(R.id.save_MyInfo);
        saveChange.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isImageChanged) {
                    progressDialog = new ProgressDialog(EditMyInfoActivity.this);
                    progressDialog.setTitle("Uploading Images...");
                    progressDialog.show();
                    createProfile_Photo_and_Delete(uri);
                }
                else if(checkNickComplete){

                    // 사용자가 입력한 닉네임 가져오기
                    String newNickname = changeNick.getText().toString().trim();

                    // 닉네임 업데이트
                    FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
                    if (currentUser != null) {
                        String uid = currentUser.getUid();
                        DatabaseReference userRef = FirebaseDatabase.getInstance().getReference("FirebaseRegister").child("UserAccount").child(uid);
                        userRef.child("NickName").setValue(newNickname).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                // 닉네임 변경 성공
                                Toast.makeText(EditMyInfoActivity.this, "닉네임이 성공적으로 변경되었습니다.", Toast.LENGTH_SHORT).show();
                                // 업데이트된 정보를 UI에 반영
                                nick.setText(newNickname);
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                // 닉네임 변경 실패
                                Toast.makeText(EditMyInfoActivity.this, "닉네임 변경에 실패하였습니다. 다시 시도해주세요.", Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                } else {
                    // 닉네임 중복 확인을 하지 않은 경우
                    Toast.makeText(EditMyInfoActivity.this, "닉네임 중복 확인을 해주세요.", Toast.LENGTH_SHORT).show();
                }
            }

        });
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
        }
    }


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
                    Glide.with(EditMyInfoActivity.this)
                            .load(uri)
                            .diskCacheStrategy(DiskCacheStrategy.NONE)
                            .circleCrop()
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
                    Glide.with(EditMyInfoActivity.this).load(uri).into(profile);
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

    private void createProfile_Photo_and_Delete(Uri uri) {
        //storage
        FirebaseStorage storage = FirebaseStorage.getInstance(); //스토리지 인스턴스를 만들고,
        StorageReference storageRef = storage.getReference();//스토리지를 참조한다
        //파일명을 만들자.
        String filename = "profile" + uid; //ex) profile1 로그인하는 사람에 따라 그에 식별값에 맞는 프로필 사진 가져오기
        List<String> extensions = Arrays.asList(".jpg", ".jpeg", ".png"); // 지원되는 모든 확장자를 여기에 추가하세요.

        Uri file = uri;
        Log.d("유알", String.valueOf(file));

        // 파일 확장자 추출
        String fileExtension = MimeTypeMap.getFileExtensionFromUrl(uri.toString()).toLowerCase();
        if (!extensions.contains("." + fileExtension)) {
            fileExtension = "jpg"; // 확장자가 지원되지 않는 경우 기본 확장자를 사용합니다.
        }
        String newExtension = "." + fileExtension;

        //여기서 원하는 이름 넣어준다. (filename 넣어주기)
        StorageReference newProfileImgRef = storageRef.child("profile_img/" + filename + newExtension);
        UploadTask uploadTask = newProfileImgRef.putFile(file);

        // 기존 파일 삭제
        for (String extension : extensions) {
            StorageReference desertRef = storageRef.child("profile_img/" + filename + extension); //삭제할 프로필이미지 명
            // Delete the file
            desertRef.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {
                }
            }).addOnFailureListener(
                    new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception exception) {
                }
            });
        }

        // TODO: 2021-03-17 새로운 프로필 이미지 저장
        // Register observers to listen for when the download is done or if it fails
        uploadTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
            }
        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                Toast.makeText(EditMyInfoActivity.this, "프로필 이미지가 변경되었습니다.", Toast.LENGTH_SHORT).show();
                if(progressDialog.isShowing()) {
                    progressDialog.dismiss();
                }
            }
        });
    }

    // TODO: 2021-03-16 존나 중요!! 파이어베이스 이미지 (시작할때 프로필 가져오기)
    /**프로필 이미지 (파이어베이스 스토리지에서 가져오기) */
//    private void getFireBaseProfileImage() {
//        //우선 디렉토리 파일 하나만든다.
//        File file = this.getExternalFilesDir(Environment.DIRECTORY_PICTURES + "/profile_img"); //이미지를 저장할 수 있는 디렉토리
//        //구분할 수 있게 /toolbar_images폴더에 넣어준다.
//        //이 파일안에 저 디렉토리가 있는지 확인
//        if (!file.isDirectory()) { //디렉토리가 없으면,
//            file.mkdir(); //디렉토리를 만든다.
//        }
//        downloadImg(); //이미지 다운로드해서 가져오기 메서드
//    }

    private void pickImageFromGallery() {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        startActivityForResult(intent, GALLERY_REQUEST_CODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == GALLERY_REQUEST_CODE && resultCode == RESULT_OK && data != null) {
            uri = data.getData();

            // imageView에 선택한 이미지 표시
            //profile.setImageURI(uri);
            Glide.with(EditMyInfoActivity.this)
                    .load(uri).diskCacheStrategy(DiskCacheStrategy.NONE)
                    .circleCrop()
                    .skipMemoryCache(true).into(profile);
            if(progressDialog.isShowing()) {
                progressDialog.dismiss();
            }
            // 이미지가 변경되었음을 표시
            isImageChanged = true;
        }
    }
    private final TextWatcher textWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            checkNickComplete = false;
            //텍스트가 변경되면 버튼 활성화
            if(s.length()>=3 && s.length() <= 16){
                doubleCheckNick.setEnabled(true);
            }else{
                doubleCheckNick.setEnabled(false);
            }
        }

        @Override
        public void afterTextChanged(Editable s) {

        }
    };
}
package com.example.datingcourse;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ClipData;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class PostMaking extends AppCompatActivity {
    private int point;
    private String uid;
    private String filename;
    private String nickName;
    private String documentId;
    ArrayList<Uri> mArrayUri = new ArrayList<>();
    // 사용자 앨범에서 사진 띄워주기
    private ImageView album;
    private FirebaseFirestore db;
    private TextView et_nickName;
    private EditText et_title;
    private EditText et_comment;
    private FirebaseAuth mFirebaseAuth;
    private DatabaseReference mDatabaseRef;
    private ProgressDialog progressDialog;
    private DocumentReference postDocumentReference;
    private Button deleteImageBtn; // 이미지 삭제 버튼
    private int currentImagePosition = 0; // 현재 보여지는 이미지의 인덱스
    private final int PICK_IMAGE_MULTIPLE = 1;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_making);

        Intent intent = getIntent();
        point = intent.getIntExtra("point", point);
        Log.d("postMaking", "" + point);

        uid = FirebaseAuth.getInstance().getCurrentUser().getUid(); // 로그인한 사용자의 UID 가져오기
        filename = uid + ".jpg"; // 파일명을 사용자의 UID로 설정

        //파이어베이스 인증 및 데이터베이스 초기화등
        mFirebaseAuth = FirebaseAuth.getInstance();
        mDatabaseRef = FirebaseDatabase.getInstance().getReference("FirebaseRegister");

        et_nickName = findViewById(R.id.postNickname);
        //fetchSingleValueFromUserRef 메소드 이용해서 realtimeDatabase에 있는 현재 사용자의 닉네임을 recycleActivity의 필드 nickName에 넣어줌
        fetchSingleValueFromUserRef(mFirebaseAuth,mDatabaseRef);

        //입력한 댓글 가져오기
        et_title = findViewById(R.id.postTitle);
        et_comment = findViewById(R.id.postContent);
        db = FirebaseFirestore.getInstance();

        Button btnChooseImages = findViewById(R.id.btn_choose_images);
        btnChooseImages.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openGallery();
            }
        });

        ViewPager2 viewPager2 = findViewById(R.id.post_img);
        viewPager2.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                currentImagePosition = position; // 현재 보여지는 이미지의 인덱스를 업데이트
            }
        });

        deleteImageBtn = findViewById(R.id.btn_delete_image);
        deleteImageBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteImage();
            }
        });

        Button saveBtn = findViewById(R.id.save_btn);
        saveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                point = point - 100;

                // DB에 새로운 포인트 값을 업데이트
                String uid = mFirebaseAuth.getCurrentUser().getUid();
                DatabaseReference userRef = mDatabaseRef.child("UserAccount").child(uid);
                DatabaseReference pointValueRef = userRef.child("point");

                pointValueRef.setValue(point);

                // 게시물 내용
                String titleText = et_title.getText().toString();
                String commentText = et_comment.getText().toString();

                if (titleText.isEmpty() || commentText.isEmpty()) {
                    Toast.makeText(PostMaking.this, "Please fill out all fields!", Toast.LENGTH_SHORT).show();
                    return;
                }
                String nickStr = et_nickName.getText().toString();

                createPostFromGlobal(titleText, commentText, nickStr);

            }
        });
    }

    private void deleteImage() {
        if (mArrayUri != null && mArrayUri.size() > 0) {
            // 현재 보여지는 이미지의 Uri를 가져옵니다
            Uri imageToDelete = mArrayUri.get(currentImagePosition);

            // 리스트에서 이미지 Uri를 삭제합니다
            mArrayUri.remove(currentImagePosition);

            // ViewPager2를 업데이트합니다
            ViewPager2 viewPager2 = findViewById(R.id.post_img);
            ViewPagerAdapter viewPagerAdapter = (ViewPagerAdapter) viewPager2.getAdapter();
            viewPagerAdapter.notifyDataSetChanged();

            // 삭제 후 현재 위치를 재조정합니다
            if (currentImagePosition > 0) {
                currentImagePosition--;
            }
        }
    }

    public void createPostFromGlobal(String titleText, String commentText, String nickName){
        com.google.firebase.Timestamp whenTimestamp = com.google.firebase.Timestamp.now();

        Map<String,Object> postData = new HashMap<>();
        postData.put("when",whenTimestamp);
        postData.put("title", titleText);
        postData.put("context",commentText);
        postData.put("NickName", nickName);
        postData.put("userId",mFirebaseAuth.getCurrentUser().getUid()); //현재 사용자의 uid를 Authentication에서 가져와서 commentData에 넣어준다.
        db.collection("posts")
                .add(postData)
                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
                        // Successfully saved comment
                        Log.d("createComment", "Comment added with ID: " + documentReference.getId());
                        et_comment.setText(""); // Clear the EditText after successful submission

                        Toast.makeText(PostMaking.this, "Comment Added Successfully!", Toast.LENGTH_SHORT).show();
                        postDocumentReference = documentReference;
                        uploadImagesToFirebase();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w("createComment", "Error adding comment", e);
                        Toast.makeText(PostMaking.this, "Failed to Add Comment!", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    //realtimeDatabase에서 특정 값 하나만 가져오기(별명)
    public void fetchSingleValueFromUserRef(FirebaseAuth mFirebaseAuth, DatabaseReference mDatabaseRef){
        FirebaseUser currentUser = mFirebaseAuth.getCurrentUser(); //현재 로그인한 사용자
        if(currentUser != null){
            String uid = currentUser.getUid();

            DatabaseReference userRef = mDatabaseRef.child("UserAccount").child(uid);
            DatabaseReference specificValueRef = userRef.child("NickName");
            specificValueRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    //데이터 스냅샷이 한 번 호출되어 값을 가져옴
                    if(snapshot.exists()){
                        //값을 String으로 캐스팅하고 사용
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                et_nickName.setText(snapshot.getValue(String.class));
                                nickName = snapshot.getValue(String.class);
                            }
                        });
                    } else {
                        Log.w("TAG", "해당하는 닉네임 없음");
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
    private void openGallery() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
        intent.setType("image/*");
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_MULTIPLE);
    }

    private void uploadImagesToFirebase() {
        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference storageRef = storage.getReference();

        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Uploading Images...");
        progressDialog.show();

        String postId = postDocumentReference.getId();  // 게시물 고유 ID를 받음
        documentId = postId;

        for (int i = 0; i < mArrayUri.size(); i++) {
            Uri individualImage = mArrayUri.get(i);

            final StorageReference individualFileRef = storageRef.child("post_images/" + postId + System.currentTimeMillis() + "_" + i + ".jpg");

            individualFileRef.putFile(individualImage).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    Toast.makeText(PostMaking.this, "Images Uploaded Successfully!", Toast.LENGTH_SHORT).show();
                    individualFileRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            String downloadUrl = uri.toString();

                            if (postDocumentReference != null) {
                                String postId = postDocumentReference.getId();
                                db.collection("posts").document(postId)
                                        .update("imageUrls", FieldValue.arrayUnion(downloadUrl));
                            }

                            if (progressDialog.isShowing() && !isFinishing()) {
                                progressDialog.dismiss();
                            }
                        }
                    });
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    if(progressDialog.isShowing()) {
                        progressDialog.dismiss();
                    }
                    Toast.makeText(PostMaking.this, "Failed to Upload Images!", Toast.LENGTH_SHORT).show();
                }
            });
        }
        // 업데이트가 성공적으로 이루어진 후에는 결과를 설정하고 액티비티를 종료합니다.
        Intent resultIntent = new Intent();
        resultIntent.putExtra("documentId", documentId);
        setResult(Activity.RESULT_OK, resultIntent);
        finish();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_MULTIPLE && resultCode == RESULT_OK && data != null) {
            // If a single image is selected
            if (data.getData() != null) {
                mArrayUri.add(data.getData());
            }
            // If multiple images are selected
            else if (data.getClipData() != null) {
                ClipData mClipData = data.getClipData();
                for (int i = 0; i < mClipData.getItemCount(); i++) {
                    ClipData.Item item = mClipData.getItemAt(i);
                    Uri uri = item.getUri();
                    mArrayUri.add(uri);
                }
            }

            // Update the ViewPager2 after images are selected
            ViewPager2 viewPager2 = findViewById(R.id.post_img);
            ViewPagerAdapter viewPagerAdapter = new ViewPagerAdapter(this, mArrayUri);
            viewPager2.setAdapter(viewPagerAdapter);
        }
    }

    public class ViewPagerAdapter extends RecyclerView.Adapter<ViewPagerAdapter.ViewHolder> {

        private ArrayList<Uri> mArrayUri;
        private Context context;

        public ViewPagerAdapter(Context context, ArrayList<Uri> mArrayUri) {
            this.context = context;
            this.mArrayUri = mArrayUri;
        }

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_viewpager, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
            Glide.with(context)
                    .load(mArrayUri.get(position))
                    .into(holder.imageView);
        }

        @Override
        public int getItemCount() {
            return mArrayUri.size();
        }

        public class ViewHolder extends RecyclerView.ViewHolder {
            ImageView imageView;

            public ViewHolder(@NonNull View itemView) {
                super(itemView);
                imageView = itemView.findViewById(R.id.image);
            }
        }
    }
}
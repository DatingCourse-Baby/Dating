package com.example.datingcourse;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager2.widget.ViewPager2;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;

public class PostEditActivity extends AppCompatActivity {

    TextView userNick, postTitle, postContent;
    ViewPager2 post_image;
    ImageView picture, recycle;
    private FirebaseFirestore db;
    private Button saveBtn;
    private String documentId, title, context, nickName;
    private Button deleteImageBtn; // 이미지 삭제 버튼
    private int currentImagePosition = 0; // 현재 보여지는 이미지의 인덱스
    private List<String> image;
    private final int PICK_IMAGE_MULTIPLE = 1;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_making); // 여기서 activity_post_edit은 레이아웃 파일 이름입니다.

        userNick = findViewById(R.id.postNickname);
        postTitle = findViewById(R.id.postTitle);
        postContent = findViewById(R.id.postContent);
        saveBtn = findViewById(R.id.save_btn);
        deleteImageBtn = findViewById(R.id.btn_delete_image);
        post_image = findViewById(R.id.post_img);

        db = FirebaseFirestore.getInstance();

        // Intent에서 데이터 가져오기
        Intent intent = getIntent();
        documentId = intent.getStringExtra("documentId");
        title = intent.getStringExtra("title");
        context = intent.getStringExtra("context");
        nickName = intent.getStringExtra("nickName");
        image = (ArrayList<String>) getIntent().getSerializableExtra("postImg");


        userNick.setText(nickName);
        postTitle.setText(title);
        postContent.setText(context);
        PostAdapter.ImageAdapter imageAdapter = new PostAdapter.ImageAdapter(PostEditActivity.this, image);

        // ViewPager2에 ImageAdapter 연결
        post_image.setAdapter(imageAdapter);
        post_image.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                currentImagePosition = position; // 현재 보여지는 이미지의 인덱스를 업데이트
            }
        });

        Button btnChooseImages = findViewById(R.id.btn_choose_images);
        btnChooseImages.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openGallery();
            }
        });

        deleteImageBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteImage();
            }
        });

        saveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updatePost();
            }
        });
    }
    private void deleteImage() {
        // 이미지 리스트에서 선택한 이미지를 삭제합니다. (Firestore에서는 아직 삭제하지 않음)
        if (image != null && image.size() > 0) {
            image.remove(currentImagePosition); // 현재 보여지는 이미지를 삭제
            post_image.getAdapter().notifyDataSetChanged(); // ViewPager2 업데이트
            Toast.makeText(PostEditActivity.this, "이미지가 삭제 되었습니다.", Toast.LENGTH_SHORT).show();
        }
    }

    private void openGallery() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
        intent.setType("image/*");
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_MULTIPLE);
    }

    private void updatePost() {
        String newTitle = postTitle.getText().toString();
        String newContent = postContent.getText().toString();

        db.collection("posts").document(documentId)
                .update(
                        "title", newTitle,
                        "context", newContent,
                        "imageUrls", image
                )
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(PostEditActivity.this ,"게시판 수정에 실패하였습니다.",Toast.LENGTH_SHORT).show();
                    }
                });

        // 업데이트가 성공적으로 이루어진 후에는 결과를 설정하고 액티비티를 종료합니다.
        Intent resultIntent = new Intent();
        resultIntent.putExtra("documentId", documentId);
        setResult(Activity.RESULT_OK, resultIntent);
        finish();
    }
}
package com.example.datingcourse;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.datepicker.MaterialDatePicker;
import com.google.android.material.datepicker.MaterialPickerOnPositiveButtonClickListener;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;

public class MyPostActivity extends AppCompatActivity implements OnLikeButtonClickListener {

    private String nickName;
    private MyPostAdapter myPostAdapter;
    private ArrayList<Post> mCommentsItems; //코멘트 리스트 객체 생성
    private FirebaseFirestore db;
    private EditText et_comment;
    private FirebaseAuth mFirebaseAuth;
    private DatabaseReference mDatabaseRef;
    private ProgressDialog progressDialog;

    private ArrayList<String> documentIds; // 문서 ID들을 저장할 ArrayList 추가

    private View view;

    private RecyclerView mRecyclerView;
    private boolean checkNickComplete;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_post);

        //파이어베이스 인증 및 데이터베이스 초기화등
        mFirebaseAuth = FirebaseAuth.getInstance();
        mDatabaseRef = FirebaseDatabase.getInstance().getReference("FirebaseRegister");

        fetchSingleValueFromUserRef(mFirebaseAuth,mDatabaseRef);

        if (this != null) {
            progressDialog = new ProgressDialog(this);
        }
        progressDialog.setCancelable(false);
        progressDialog.setMessage("Fetching Data... ");
        progressDialog.show();

        // RecyclerView 초기화 코드
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManagerWrapper(this, LinearLayoutManager.VERTICAL, false);
        mRecyclerView = findViewById(R.id.MyrecyclerView);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(mLayoutManager);

        db = FirebaseFirestore.getInstance();

        //새로운 Comments 클래스 타입의 arraylist만들어서
        mCommentsItems = new ArrayList<Post>(); // ArrayList 초기화
        Log.d("TAG", "activity1 Comments list: " + mCommentsItems.toString());

        documentIds = new ArrayList<String>(); // ArrayList 초기화

        loadComments();

        FirebaseUser currentUser = mFirebaseAuth.getCurrentUser();
        String currentUserId = null;
        if (currentUser != null) {
            currentUserId = currentUser.getUid();
        }

        Log.d("TAG", "activity2 Comments list: " + mCommentsItems.toString());
        myPostAdapter = new MyPostAdapter(this, mCommentsItems, currentUserId, documentIds, this); // documentIds를 어댑터로 전달

        //여기 recycerView에 어탭더 적용시켜줌
        mRecyclerView.setAdapter(myPostAdapter)  ;
        myPostAdapter.setOnBtnClickListener(new OnMyPostActionListener() {

            @Override
            public void onMyPostEditClick(Post item) {
                Log.d("TAG","수정 버튼 눌림");
                //선택한 댓글의 문서 ID 가져오기
                String editDocumentId = item.getDocumentId();

                Intent intent = new Intent(MyPostActivity.this, PostEditActivity.class);
                intent.putExtra("documentId", item.getDocumentId());
                intent.putExtra("title", item.getTitle());
                intent.putExtra("context", item.getContext());
                intent.putExtra("nickName", item.getNickName());
                intent.putStringArrayListExtra("postImg", (ArrayList<String>) item.getImageUrls());

                // 수정 화면에서 돌아올 때 결과를 받기 위한 Request Code
                int REQUEST_EDIT_POST = 100;
                startActivityForResult(intent, REQUEST_EDIT_POST);
            }

            @Override
            public void onMyPostDeleteClick(Post item, int position) {
                Log.d("TAG", "Deleting doc ID: " + item.getDocumentId() + ", Position: " + position);
                String commentUserId = item.getUserId();
                String documentId = item.getDocumentId();

                if (FirebaseAuth.getInstance().getCurrentUser().getUid().equals(commentUserId)) {
                    if(position >= 0 && position < mCommentsItems.size()) {
                        onDeleteConfirmed(documentId, position);
                    }
                }else {
                    Toast.makeText(MyPostActivity.this, "지정한 위치에 해당하는 댓글이 없습니다.", Toast.LENGTH_SHORT).show();
                }
            }
        });

        EventChangeListener(); //스냅샷 이벤트 변경 리스너 설정
    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        int REQUEST_EDIT_POST = 100;

        if (requestCode == REQUEST_EDIT_POST) {
            if (resultCode == Activity.RESULT_OK) {
                loadComments();
            }
        }
    }

    private void onDeleteConfirmed(String documentId, int position) {
        if (position >= 0 && position < mCommentsItems.size() && position < documentIds.size()) {
            db.collection("posts").document(documentId)
                    .delete()
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            myPostAdapter.notifyItemRemoved(position);
                            myPostAdapter.notifyItemRangeChanged(position, mCommentsItems.size());
                            loadComments();
                            Toast.makeText(MyPostActivity.this, "댓글이 삭제되었습니다.", Toast.LENGTH_SHORT).show();
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(MyPostActivity.this, "삭제에 실패하였습니다. 다시 시도해주세요.", Toast.LENGTH_SHORT).show();
                        }
                    });
        } else {
            Toast.makeText(MyPostActivity.this, "지정한 위치에 해당하는 댓글이 없습니다.", Toast.LENGTH_SHORT).show();
        }
    }
    private void EventChangeListener() {

        db.collection("posts").orderBy("when", com.google.firebase.firestore.Query.Direction.DESCENDING)
                .addSnapshotListener(new EventListener<QuerySnapshot>() {

                    //새로운 데이터가 생성
                    //데이터 변경할 때
                    //데이터 삭제할 때
                    //처음 실행할 때 firebase에서 데이터 가져올 때 실행
                    @Override
                    public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {

                        if(error != null){

                            if(progressDialog.isShowing()){
                                progressDialog.dismiss();
                            }
                            Log.e("FireStore error",error.getMessage());

                            return;
                        }

                        mCommentsItems.clear();

                        // 스냅샷에서 새로운 데이터를 로드합니다.
                        for (DocumentSnapshot doc : value.getDocuments()) {
                            mCommentsItems.add(doc.toObject(Post.class));
                        }

                        myPostAdapter.notifyDataSetChanged();
                        if (progressDialog.isShowing()) {
                            progressDialog.dismiss();
                        }
                    }
                });

    }

    @Override
    public void onLikeButtonClick(int position, String documentId, String userId, boolean isLiked) {
        DocumentReference postRef = db.collection("posts").document(documentId);

        if (isLiked) {
            postRef.update("likeUserList", FieldValue.arrayRemove(userId)).addOnCompleteListener(task -> {
                myPostAdapter.updateLikeButton(position, true);
                // 데이터 새로고침
                loadComments();
            });
        } else {
            postRef.update("likeUserList", FieldValue.arrayUnion(userId)).addOnCompleteListener(task -> {
                myPostAdapter.updateLikeButton(position, false);
                // 데이터 새로고침
                loadComments();
            });
        }
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
                        nickName = snapshot.getValue(String.class);

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

    private void loadComments() {
        db.collection("posts")
                .orderBy("when", Query.Direction.DESCENDING)
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                        if (error != null) {
                            Log.w("TAG", "Listen failed.", error);
                            progressDialog.dismiss();
                            return;
                        }

                        ArrayList<Post> newPosts = new ArrayList<>();
                        ArrayList<String> newDocumentIds = new ArrayList<>();

                        for (DocumentChange dc : value.getDocumentChanges()) {
                            if (dc.getType() == DocumentChange.Type.ADDED) {
                                Post postItem = dc.getDocument().toObject(Post.class);
                                String documentId = dc.getDocument().getId();
                                postItem.setDocumentId(documentId);
                                newDocumentIds.add(documentId);

                                newPosts.add(postItem);
//                                mRecyclerView.scrollToPosition(newPosts.size() - 1);
                            }
                        }
                        documentIds.clear();
                        documentIds.addAll(newDocumentIds);

                        mCommentsItems.clear();
                        mCommentsItems.addAll(newPosts);

                        myPostAdapter.setCommentList(mCommentsItems);
                        myPostAdapter.setDocumentId(documentIds);
                        myPostAdapter.notifyDataSetChanged();

                        mRecyclerView.scrollToPosition(0);

                        if (progressDialog.isShowing()) {
                            progressDialog.dismiss();
                        }
                    }
                });
    }


}
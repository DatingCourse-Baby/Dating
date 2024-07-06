package com.example.datingcourse;

//필요한 클래스들을 import하기
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
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
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class RecycleActivity extends AppCompatActivity {
    private String nickName;
    private Button btn_write; //댓글 작성 버튼
    private RecyclerView mRecyclerView; //RecyclerView
    private CommentsAdapter mRecyclerAdapter; //어댑터 클래스
    private ArrayList<Comments> mCommentsItems; //코멘트 리스트 객체 생성
    private FirebaseFirestore db;
    private EditText et_comment;
    private FirebaseAuth mFirebaseAuth;
    private DatabaseReference mDatabaseRef;
    private ProgressDialog progressDialog;

    private ArrayList<String> documentIds; // 문서 ID들을 저장할 ArrayList 추가

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recycle);


        //파이어베이스 인증 및 데이터베이스 초기화등
        mFirebaseAuth = FirebaseAuth.getInstance();
        mDatabaseRef = FirebaseDatabase.getInstance().getReference("FirebaseRegister");

        //fetchSingleValueFromUserRef 메소드 이용해서 realtimeDatabase에 있는 현재 사용자의 닉네임을 recycleActivity의 필드 nickName에 넣어줌
        fetchSingleValueFromUserRef(mFirebaseAuth,mDatabaseRef);

        //입력한 댓글 가져오기
        et_comment = findViewById(R.id.et_comment);
        Intent intent = getIntent();
        String postId = intent.getStringExtra("documentId");

        //댓글 쓰기 버튼 눌렀을 때 이벤트
        btn_write = findViewById(R.id.btn_write);
        btn_write.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //댓글 내용
                String commentText = et_comment.getText().toString();
                //text에 쓰여진 댓글과 아까 가져온 닉네임을 매개변수로 전송
                createComment(commentText,nickName,postId);
                loadComments(postId);
            }
        });

        progressDialog = new ProgressDialog(this);
        progressDialog.setCancelable(false);
        progressDialog.setMessage("로딩중... ");
        progressDialog.show();

        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManagerWrapper(RecycleActivity.this, LinearLayoutManager.VERTICAL, false);
        mRecyclerView = findViewById(R.id.rv_RecycleView);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(mLayoutManager);

        db = FirebaseFirestore.getInstance();

        //새로운 Comments 클래스 타입의 arraylist만들어서
        mCommentsItems = new ArrayList<Comments>();
        Log.d("TAG", "activity1 Comments list: " + mCommentsItems.toString());

        //adapter 생성자에 매개변수로 전달해줌
        documentIds = new ArrayList<String>(); // ArrayList 초기화
        loadComments(postId);
        Log.d("TAG", "activity2 Comments list: " + mCommentsItems.toString());
        mRecyclerAdapter = new CommentsAdapter(RecycleActivity.this, mCommentsItems, mFirebaseAuth.getCurrentUser().getUid(), documentIds); // documentIds를 어댑터로 전달

        //여기 recycerView에 어탭더 적용시켜줌
        mRecyclerView.setAdapter(mRecyclerAdapter)  ;
        mRecyclerAdapter.setOnBtnClickListener(new OnCommentActionListener() {

            @Override
            public void onEditClick(Comments item) {
                Log.d("TAG","수정 버튼 눌림");
                //선택한 댓글의 문서 ID 가져오기
                String editDocumentId = item.getDocumentId();

                // 다이얼 로그 생성
                AlertDialog.Builder builder = new AlertDialog.Builder(RecycleActivity.this);
                //레이아웃 인플레이터를 사용하여 앱이 실행할 때 레이아웃 파일을 뷰 객체로 변환함
                View view = getLayoutInflater().inflate(R.layout.item_edit_comment,null);
                //item_edit_comment.xml 에 있는 editTextComment 버튼 인식
                EditText editTextComment = view.findViewById(R.id.editTextComment);
                //builder.setView(view)를 통해 레이아웃을 설정
                builder.setView(view);

                //posts밑에있는 문서 id에 있는 context 필드의 값을 가져와서
                //editText에 설정
                db.collection("posts").document(postId).collection("Comments").document(editDocumentId)
                        .get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                            @Override
                            public void onSuccess(DocumentSnapshot documentSnapshot) {
                                String editCommentText = documentSnapshot.getString("context");
                                editTextComment.setText(editCommentText);
                            }
                        });
                //AlertDialog 세팅 및 버튼 리스너 등록
                builder.setTitle("댓글 수정")
                        .setPositiveButton("수정", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                //입력한 댓글 텍스트 가져오기
                                String updateCommentText = editTextComment.getText().toString();

                                db.collection("posts").document(postId).collection("Comments").document(editDocumentId)
                                        .update("context",updateCommentText)
                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void unused) {
                                                mRecyclerAdapter.notifyDataSetChanged();
                                                loadComments(postId);
                                                Toast.makeText(RecycleActivity.this,"댓글이 수정되었습니다.",Toast.LENGTH_SHORT).show();
                                            }
                                        }).addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {
                                                Toast.makeText(RecycleActivity.this,"댓글 수정에 실패하였습니다.",Toast.LENGTH_SHORT).show();
                                            }
                                        });
                            }
                        }).setNegativeButton("취소", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                            }
                        });
                AlertDialog alertDialog = builder.create();
                alertDialog.show();
            }

            @Override
            public void onDeleteClick(Comments item, int position) {
                Log.d("TAG", "Deleting doc ID: " + item.getDocumentId() + ", Position: " + position);
                String commentUserId = item.getUserId();
                String documentId = item.getDocumentId();

                if (FirebaseAuth.getInstance().getCurrentUser().getUid().equals(commentUserId)) {
                    if(position >= 0 && position < mCommentsItems.size()) {
                        onDeleteConfirmed(documentId, position,postId);
                    }
                }else {
                    Toast.makeText(RecycleActivity.this, "지정한 위치에 해당하는 댓글이 없습니다.", Toast.LENGTH_SHORT).show();
                }
            }
        });

        EventChangeListener(postId); //스냅샷 이벤트 변경 리스너 설정



    }
    private void onDeleteConfirmed(String documentId, int position,String postId) {
        if (position >= 0 && position < mCommentsItems.size() && position < documentIds.size()) {
            db.collection("posts").document(postId).collection("Comments").document(documentId)
                    .delete()
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            mRecyclerAdapter.notifyItemRemoved(position);
                            mRecyclerAdapter.notifyItemRangeChanged(position, mCommentsItems.size());
                            loadComments(postId);
                            Toast.makeText(RecycleActivity.this, "댓글이 삭제되었습니다.", Toast.LENGTH_SHORT).show();
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(RecycleActivity.this, "삭제에 실패하였습니다. 다시 시도해주세요.", Toast.LENGTH_SHORT).show();
                        }
                    });
        } else {
            Toast.makeText(RecycleActivity.this, "지정한 위치에 해당하는 댓글이 없습니다.", Toast.LENGTH_SHORT).show();
        }
    }
    private void EventChangeListener(String postId) {

        db.collection("posts").document(postId).collection("Comments").orderBy("when", Query.Direction.ASCENDING)
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
                            mCommentsItems.add(doc.toObject(Comments.class));
                        }

                        mRecyclerAdapter.notifyDataSetChanged();
                        if (progressDialog.isShowing()) {
                            progressDialog.dismiss();
                        }
                    }
                });

    }
    public void createComment(String commentText,String nickName,String postId){


        com.google.firebase.Timestamp whenTimestamp = com.google.firebase.Timestamp.now();

        Map<String,Object> commentData = new HashMap<>();
        commentData.put("userNick",nickName);
        commentData.put("when",whenTimestamp);
        commentData.put("context",commentText);
        commentData.put("userId",mFirebaseAuth.getCurrentUser().getUid()); //현재 사용자의 uid를 Authentication에서 가져와서 commentData에 넣어준다.
        db.collection("posts").document(postId).collection("Comments")
                .add(commentData)
                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
                        // Successfully saved comment
                        Log.d("createComment", "Comment added with ID: " + documentReference.getId());
                        et_comment.setText(""); // Clear the EditText after successful submission

                        Toast.makeText(RecycleActivity.this, "Comment Added Successfully!", Toast.LENGTH_SHORT).show();

                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w("createComment", "Error adding comment", e);
                        Toast.makeText(RecycleActivity.this, "Failed to Add Comment!", Toast.LENGTH_SHORT).show();
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


    public void loadComments(String postId) {
        db.collection("posts")
                .document(postId).collection("Comments").orderBy("when", Query.Direction.ASCENDING)
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                        if (error != null) {
                            Log.w("TAG", "Listen failed.", error);
                            progressDialog.dismiss();
                            return;
                        }

                        ArrayList<Comments> newComments = new ArrayList<>();
                        ArrayList<String> newDocumentIds = new ArrayList<>();

                        for (DocumentChange dc : value.getDocumentChanges()) {
                            if (dc.getType() == DocumentChange.Type.ADDED) {
                                Comments commentItem = dc.getDocument().toObject(Comments.class);
                                String documentId = dc.getDocument().getId();
                                commentItem.setDocumentId(documentId);
                                newDocumentIds.add(documentId);

                                newComments.add(commentItem);
                                mRecyclerView.scrollToPosition(newComments.size() - 1);
                            }
                        }
                        documentIds.clear();
                        documentIds.addAll(newDocumentIds);

                        mCommentsItems.clear();
                        mCommentsItems.addAll(newComments);

                        mRecyclerAdapter.setCommentList(mCommentsItems);
                        mRecyclerAdapter.setDocumentId(documentIds);
                        mRecyclerAdapter.notifyDataSetChanged();

                        if (progressDialog.isShowing()) {
                            progressDialog.dismiss();
                        }
                    }
                });
    }



}
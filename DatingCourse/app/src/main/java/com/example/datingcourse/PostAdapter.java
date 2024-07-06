package com.example.datingcourse;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.PopupMenu;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.adapter.FragmentStateAdapter;
import androidx.viewpager2.widget.ViewPager2;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageException;
import com.google.firebase.storage.StorageReference;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.atomic.AtomicBoolean;

//리사이클러 뷰 어댑터 생성
public class PostAdapter extends RecyclerView.Adapter<PostAdapter.ViewHolder> {

    private ArrayList<Post> mCommentList;
    private Context context;
    private String currentUserId;
    private ArrayList<String> documentId;

    private PostAdapter.OnItemClickListener itemClickListener;

    private OnPostActionListener mOnPostActionListener;

    private OnLikeButtonClickListener onLikeButtonClickListener;

    //어댑터 생성자
    public PostAdapter(Context context, ArrayList<Post> mCommentList, String currentUserId, ArrayList<String> documentId, OnLikeButtonClickListener onLikeButtonClickListener) {
        this.mCommentList = mCommentList;
        this.context = context;
        this.currentUserId = currentUserId;
        this.documentId = documentId;
        this.onLikeButtonClickListener = onLikeButtonClickListener;
    }
    public void setOnBtnClickListener(OnPostActionListener clickListener){

        mOnPostActionListener = clickListener;
    }
    public void setCommentList(ArrayList<Post> mCommentList) {
        this.mCommentList = mCommentList;
        notifyDataSetChanged();
    }

    public void setDocumentId(ArrayList<String> documentId) {
        this.documentId = documentId;
        notifyDataSetChanged();
    }
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        //뷰 홀더 생성
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_post, parent, false);
        return new ViewHolder(view, mOnPostActionListener);
    }

    //데이터 바인딩
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Post comments = mCommentList.get(position);
        comments.setDocumentId(documentId.get(position));
        // 게시물 생성
        holder.onBind(comments);
        holder.itemView.setVisibility(View.VISIBLE);
        holder.itemView.setLayoutParams(new RecyclerView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
    }

    //아이템 개수 반환
    @Override
    public int getItemCount() {
        return mCommentList.size();
    }

    public interface OnItemClickListener {
        void onClick(View v, int position, Post post);
    }

    public void setItemClickListener(PostAdapter.OnItemClickListener onItemClickListener) {
        this.itemClickListener = onItemClickListener;
    }

    //새로고침
    public void updateLikeButton(int position, boolean isLiked) {
        if (position >= 0 && position < mCommentList.size()) {
            Post post = mCommentList.get(position);

            if (isLiked) {
                post.getLikeUserList().remove(currentUserId);
            } else {
                post.getLikeUserList().add(currentUserId);
            }

            post.setCurrentUserLikes(!isLiked);
            notifyItemChanged(position);
        }
    }

    // 어댑터에서 createViewHolder할때
    //뷰 홀더 이너클래스
    class ViewHolder extends RecyclerView.ViewHolder {
        TextView userNick, postTitle, postContent, postDate, postLikeSize, postRecycleSize;
        ViewPager2 post_image;
        ImageView picture, recycle;
        ImageButton btn_more;
        ImageButton like; //좋아요 이미지 버튼
        FirebaseFirestore db;

        OnPostActionListener mOnPostActionListener;

        public ViewHolder(@NonNull View itemView,OnPostActionListener onPostActionListener) {
            super(itemView);

            userNick = (TextView) itemView.findViewById(R.id.postNickname);
            postTitle = (TextView) itemView.findViewById(R.id.postTitle);
            postContent = (TextView) itemView.findViewById(R.id.postContent);
            postDate = (TextView) itemView.findViewById(R.id.postDate);
            postLikeSize = (TextView) itemView.findViewById(R.id.post_like_size);
            postRecycleSize = (TextView) itemView.findViewById(R.id.post_recycle_size);

            btn_more = (ImageButton) itemView.findViewById(R.id.btn_more);
            //좋아요 이미지 버튼 할당
            like = (ImageButton) itemView.findViewById(R.id.post_like);

            picture = (ImageView) itemView.findViewById(R.id.postprofile);
            recycle = (ImageView) itemView.findViewById(R.id.post_recycle);

            post_image = (ViewPager2) itemView.findViewById(R.id.post_image);

            db = FirebaseFirestore.getInstance();

            mOnPostActionListener = onPostActionListener;

            //like 버튼 눌렀을 때 이벤트 처리
            like.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //현재 클릭된 position 가져오기
                    int position = getAdapterPosition();

                    if (position != RecyclerView.NO_POSITION) {
                        //현재 위치에 있는 Post의 정보 가져오기
                        Post item = mCommentList.get(position);
                        boolean isLiked = item.isCurrentUserLikes();
                        onLikeButtonClickListener.onLikeButtonClick(position, item.getDocumentId(), currentUserId, isLiked);

                        // 좋아요 버튼의 이미지 업데이트
                        if (!isLiked) {
                            like.setImageResource(R.drawable.like_after);
                        } else {
                            like.setImageResource(R.drawable.like_before);
                        }

                        // 좋아요 개수의 텍스트를 업데이트합니다.
                        postLikeSize.setText((item.getLikeUserList().size() + (!isLiked ? 1 : -1)) + "개");
                    }
                }
            });
        }

        //데이터 바인딩 메소드
        //어댑터 클래스에서 데이터를 표시하기 위한 뷰와 실제 데이터 연결
        void onBind(Post item) {
            if (currentUserId != null && currentUserId.equals(item.getUserId())) {
                Log.d("TAG", "Deleting doc ID: " + item.getDocumentId() + "=" + currentUserId + ", Position: " + getAdapterPosition());
                btn_more.setVisibility(View.VISIBLE);
                btn_more.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        // 이미지 버튼을 클릭하면 PopupMenu 생성 및 보이기
                        showPopupMenu(v, item, PostAdapter.ViewHolder.this);
                    }
                });
            }else{
                btn_more.setVisibility(View.GONE);
            }

            FirebaseFirestore db = FirebaseFirestore.getInstance();
            db.collection("posts").document(item.getDocumentId()).collection("Comments").get()
                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            if (task.isSuccessful()) {
                                int count = task.getResult().size();
                                postRecycleSize.setText(String.valueOf(count) + "개");
                            } else {
                                Log.d("TAG", "Error getting comments count: ", task.getException());
                                postRecycleSize.setText("0개");
                            }
                        }
                    });
            // 좋아요 개수 초기 설정
            postLikeSize.setText(item.getLikeUserList().size() + "개");
            checkCurrentLike(item, like);

            userNick.setText(item.getNickName());
            postTitle.setText(item.getTitle());
            postContent.setText(item.getContext());

            // Timestamp를 문자열로 변환
            com.google.firebase.Timestamp whenTimestamp = item.getWhen();
            if (whenTimestamp != null) {
                Date whenDate = whenTimestamp.toDate();
                String whenString = new SimpleDateFormat("yyyy.MM.dd HH:mm", Locale.getDefault()).format(whenDate);
                postDate.setText(whenString); // 변환한 문자열로 TextView 업데이트
            } else {
                postDate.setText(""); // when 값이 없는 경우, 비워 둡니다.
            }

            recycle.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(context, RecycleActivity.class);
                    intent.putExtra("documentId", item.getDocumentId());
                    intent.putExtra("title", item.getTitle());
                    context.startActivity(intent);
                }
            });

            downloadImg(item);
            // 이미지 URL 리스트를 가져와 ImageAdapter에 연결
            List<String> imageUrls = item.getImageUrls();
            ImageAdapter imageAdapter = new ImageAdapter(context, imageUrls);

            // ViewPager2에 ImageAdapter 연결
            post_image.setAdapter(imageAdapter);
        }

        private void downloadImg(Post item) {
            FirebaseStorage storage = FirebaseStorage.getInstance();
            StorageReference storageRef = storage.getReference();
            String imgName = "profile" + item.getUserId();

            List<String> extensions = Arrays.asList(".jpg", ".jpeg", ".png");

            StorageReference defaultImageRef = storageRef.child("profile_img/default_profile_image.png"); // 기본 이미지 파일 경로

            loadProfileImage(storageRef, imgName, extensions, 0, defaultImageRef, item);
        }

        private void loadProfileImage(StorageReference storageRef, String imgName, List<String> extensions, int index, StorageReference defaultImageRef, Post item) {
            if (index < extensions.size()) {
                StorageReference profileImageRef = storageRef.child("profile_img/" + imgName + extensions.get(index));
                profileImageRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        Glide.with(context)
                                .load(uri)
                                .error(R.drawable.noimage)
                                .fallback(R.drawable.noimage)
                                .placeholder(R.drawable.noimage)
                                .into(picture);
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        if (e instanceof StorageException) {
                            StorageException storageException = (StorageException) e;
                            if (storageException.getErrorCode() == StorageException.ERROR_OBJECT_NOT_FOUND) {
                                // 파일이 존재하지 않을 때 다음 확장자로 시도
                                loadProfileImage(storageRef, imgName, extensions, index + 1, defaultImageRef, item);
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
                        Glide.with(context)
                                .load(uri)
                                .error(R.drawable.noimage)
                                .fallback(R.drawable.noimage)
                                .placeholder(R.drawable.noimage)
                                .into(picture);
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

    //바
    public void checkCurrentLike(Post post, ImageButton likeButton) {
        ArrayList<String> likeUserList = (ArrayList<String>) post.getLikeUserList();
        AtomicBoolean currentUserLikes = new AtomicBoolean(false);
        likeUserList.forEach(userId -> {
            if (userId.equals(currentUserId)) {
                currentUserLikes.set(true);
            }
        });
        post.setCurrentUserLikes(currentUserLikes.get());

        if (currentUserLikes.get()) {
            likeButton.setImageResource(R.drawable.like_after);
        } else {
            likeButton.setImageResource(R.drawable.like_before);
        }
    }

    private void showPopupMenu(View view, Post items, ViewHolder viewHolder) {
        PopupMenu popupMenu = new PopupMenu(context, view);
        popupMenu.inflate(R.menu.drop); // 메뉴 리소스 파일을 팝업 메뉴에 연결

        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                // 메뉴 아이템 클릭 시 처리할 로직 구현
                switch (item.getItemId()) {
                    case R.id.edit_post:
                        mOnPostActionListener.onPostEditClick(items);
                        return true;
                    case R.id.delete_post:
                        mOnPostActionListener.onPostDeleteClick(items, viewHolder.getAdapterPosition());
                        return true;
                    // 다른 메뉴 아이템들에 대한 처리도 추가 가능
                    default:
                        return false;
                }
            }
        });

        popupMenu.show(); // PopupMenu 보이기
    }

    public static class ImageAdapter extends RecyclerView.Adapter<ImageAdapter.ImageViewHolder> {

        private Context context;
        private List<String> imageUrls;

        public ImageAdapter(Context context, List<String> imageUrls) {
            this.context = context;
            this.imageUrls = imageUrls;
        }

        @NonNull
        @Override
        public ImageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_post_image, parent, false);
            return new ImageViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull ImageViewHolder holder, int position) {
            String imageUrl = imageUrls.get(position);

            Glide.with(context)
                    .load(imageUrl)
                    .error(R.drawable.noimage)
                    .fallback(R.drawable.noimage)
                    .placeholder(R.drawable.loading)
                    .into(holder.imageView);
        }

        @Override
        public int getItemCount() {
            if (imageUrls == null) {
                return 0;
            }
            return imageUrls.size();
        }

        public class ImageViewHolder extends RecyclerView.ViewHolder {

            ImageView imageView;

            public ImageViewHolder(@NonNull View itemView) {
                super(itemView);
                imageView = itemView.findViewById(R.id.imageView);
            }
        }
    }
}

//인터페이스 추가
interface OnLikeButtonClickListener {
    void onLikeButtonClick(int position,String documentId, String userId, boolean isLiked);
}

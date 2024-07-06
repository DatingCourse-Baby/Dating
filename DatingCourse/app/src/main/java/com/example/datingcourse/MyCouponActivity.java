package com.example.datingcourse;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

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

import java.util.ArrayList;
import java.util.List;

public class MyCouponActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private FirebaseAuth mFirebaseAuth;
    private DatabaseReference mDatabaseRef;
    private List<String> imageUrlList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.my_coupon);
        //파이어베이스 인증 및 데이터베이스 초기화등
        mFirebaseAuth = FirebaseAuth.getInstance();
        mDatabaseRef = FirebaseDatabase.getInstance().getReference("FirebaseRegister");

        recyclerView = findViewById(R.id.coupon_recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        fetchSingleValueFromUserRef(mFirebaseAuth,mDatabaseRef);
    }

    public void fetchSingleValueFromUserRef(FirebaseAuth mFirebaseAuth, DatabaseReference mDatabaseRef){
        FirebaseUser currentUser = mFirebaseAuth.getCurrentUser(); //현재 로그인한 사용자
        if(currentUser != null) {
            String uid = currentUser.getUid();

            DatabaseReference userRef = mDatabaseRef.child("UserCoupon").child(uid);

            imageUrlList = new ArrayList<>();

            userRef.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    imageUrlList.clear();
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        String imageUrl = snapshot.getValue(String.class);
                        imageUrlList.add(imageUrl);
                    }
                    CouponAdapter adapter = new CouponAdapter(imageUrlList);
                    recyclerView.setAdapter(adapter);
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    // Handle errors here
                }
            });
        }
    }

    public static class CouponAdapter extends RecyclerView.Adapter<CouponAdapter.CouponViewHolder> {

        private List<String> imageUrlList;

        public CouponAdapter(List<String> imageUrlList) {
            this.imageUrlList = imageUrlList;
        }

        @NonNull
        @Override
        public CouponViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_coupon, parent, false);
            return new CouponViewHolder(itemView);
        }

        @Override
        public void onBindViewHolder(@NonNull CouponViewHolder holder, int position) {
            String url = imageUrlList.get(position);

            int width = dpToPx(holder.imageView.getContext(), 410);
            int height = dpToPx(holder.imageView.getContext(), 200);
            Glide.with(holder.imageView.getContext())
                    .load(url)
                    .error(R.drawable.noimage)
                    .fallback(R.drawable.noimage)
                    .placeholder(R.drawable.loading)
                    .override(width, height)
                    .into(holder.imageView);

            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String url = imageUrlList.get(position);
                    // Intent를 사용하여 ImageDisplayActivity로 URL을 전달합니다.
                    Intent intent = new Intent(holder.itemView.getContext(), ImageDisplayActivity.class);
                    intent.putExtra("IMAGE_URL", url);
                    holder.itemView.getContext().startActivity(intent);
                }
            });
        }

        @Override
        public int getItemCount() {
            return imageUrlList.size();
        }

        public static class CouponViewHolder extends RecyclerView.ViewHolder {
            public ImageView imageView;

            public CouponViewHolder(@NonNull View itemView) {
                super(itemView);
                imageView = itemView.findViewById(R.id.coupon_image);
            }
        }

        public static int dpToPx(Context context, int dp) {
            float density = context.getResources().getDisplayMetrics().density;
            return Math.round((float) dp * density);
        }
    }
}

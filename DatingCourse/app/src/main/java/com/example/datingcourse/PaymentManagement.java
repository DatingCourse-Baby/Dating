package com.example.datingcourse;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class PaymentManagement extends AppCompatActivity {

    private RecyclerView recyclerView;
    private List<AdminCouponInfo> adminCouponList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment_management);

        recyclerView = findViewById(R.id.payment_recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adminCouponList = new ArrayList<>();
        final AdminCouponAdapter adminCouponAdapter = new AdminCouponAdapter(adminCouponList);
        recyclerView.setAdapter(adminCouponAdapter);

        DatabaseReference mDatabaseRef = FirebaseDatabase.getInstance().getReference("FirebaseRegister").child("AdminCoupon");

        mDatabaseRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                adminCouponList.clear();
                for (DataSnapshot couponSnapshot : dataSnapshot.getChildren()) {
                    String name1 = couponSnapshot.getKey();
                    int count = couponSnapshot.child("count").getValue(Integer.class);
                    int point = couponSnapshot.child("point").getValue(Integer.class);

                    adminCouponList.add(new AdminCouponInfo(name1, count, point));
                }
                adminCouponAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Handle error
            }
        });
    }
    class AdminCouponAdapter extends RecyclerView.Adapter<AdminCouponAdapter.AdminCouponViewHolder> {

        private List<AdminCouponInfo> adminCouponList;

        public AdminCouponAdapter(List<AdminCouponInfo> adminCouponList) {
            this.adminCouponList = adminCouponList;
        }

        @NonNull
        @Override
        public AdminCouponViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.payment_management_item, parent, false);
            return new AdminCouponViewHolder(itemView);
        }

        @Override
        public void onBindViewHolder(@NonNull AdminCouponViewHolder holder, int position) {
            AdminCouponInfo adminCouponInfo = adminCouponList.get(position);
            holder.nameTextView.setText(adminCouponInfo.name1);
            holder.countTextView.setText(String.valueOf(adminCouponInfo.count));
            holder.pointTextView.setText(String.valueOf(adminCouponInfo.point));
        }

        @Override
        public int getItemCount() {
            return adminCouponList.size();
        }

        class AdminCouponViewHolder extends RecyclerView.ViewHolder {
            TextView nameTextView;
            TextView countTextView;
            TextView pointTextView;

            public AdminCouponViewHolder(@NonNull View itemView) {
                super(itemView);
                nameTextView = itemView.findViewById(R.id.nameTextView);
                countTextView = itemView.findViewById(R.id.countTextView);
                pointTextView = itemView.findViewById(R.id.pointTextView);
            }
        }
    }
    class AdminCouponInfo {
        public String name1;
        public int count;
        public int point;

        public AdminCouponInfo(String name1, int count, int point) {
            this.name1 = name1;
            this.count = count;
            this.point = point;
        }
    }
}
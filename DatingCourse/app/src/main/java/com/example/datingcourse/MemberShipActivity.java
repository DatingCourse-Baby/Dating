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

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserInfo;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class MemberShipActivity extends AppCompatActivity {
    private FirebaseAuth mFirebaseAuth;
    private FirebaseUser currentUser;

    private RecyclerView recyclerView;
    private List<UserInfo> filteredUsers;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_member_ship);

        recyclerView = findViewById(R.id.premiumMembersRecyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        filteredUsers = new ArrayList<>();
        final MemberAdapter memberAdapter = new MemberAdapter(filteredUsers);
        recyclerView.setAdapter(memberAdapter);

        mFirebaseAuth = FirebaseAuth.getInstance();
        currentUser = mFirebaseAuth.getCurrentUser();

        // 사용자가 관리자인 경우
        if(currentUser != null && "admin@a.com".equals(currentUser.getEmail())) {
            DatabaseReference mDatabaseRef = FirebaseDatabase.getInstance().getReference("FirebaseRegister").child("UserAccount");

            mDatabaseRef.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    // 기존 데이터를 지운다
                    filteredUsers.clear();

                    for (DataSnapshot userSnapshot : dataSnapshot.getChildren()) {
                        // 각 사용자로부터 구성원 자격 및 기타 정보 가져오기
                        Boolean membership = userSnapshot.child("memberShip").getValue(Boolean.class);
                        String emailId = userSnapshot.child("emailId").getValue(String.class);
                        String nickName = userSnapshot.child("NickName").getValue(String.class);

                        // 사용자가 구성원 자격이 있는지 확인합니다
                        if (Boolean.TRUE.equals(membership)) {
                            filteredUsers.add(new UserInfo(nickName, emailId));
                        }
                    }

                    // 리스트가 업데이트되었으므로 어댑터에 알립니다.
                    memberAdapter.setUserList(filteredUsers);
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    // Handle errors here
                }
            });
        }
    }
    class UserInfo {
        public String nickName;
        public String emailId;

        public UserInfo(String nickName, String emailId) {
            this.nickName = nickName;
            this.emailId = emailId;
        }
    }
    class MemberAdapter extends RecyclerView.Adapter<MemberAdapter.MemberViewHolder> {

        private List<UserInfo> userList;

        public MemberAdapter(List<UserInfo> userList) {
            this.userList = userList;
        }

        @NonNull
        @Override
        public MemberViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.premium_member_item, parent, false);
            return new MemberViewHolder(itemView);
        }

        @Override
        public void onBindViewHolder(@NonNull MemberViewHolder holder, int position) {
            UserInfo user = userList.get(position);
            holder.nickNameTextView.setText(user.nickName);
            holder.emailTextView.setText(user.emailId);
        }

        @Override
        public int getItemCount() {
            return userList.size();
        }

        public void setUserList(List<UserInfo> newUserList) {
            this.userList = newUserList;
            notifyDataSetChanged();
        }

        class MemberViewHolder extends RecyclerView.ViewHolder {
            TextView nickNameTextView;
            TextView emailTextView;

            public MemberViewHolder(@NonNull View itemView) {
                super(itemView);
                nickNameTextView = itemView.findViewById(R.id.nickNameTextView);
                emailTextView = itemView.findViewById(R.id.emailTextView);
            }
        }
    }
}
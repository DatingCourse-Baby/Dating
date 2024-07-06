package com.example.datingcourse;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class MyCourseAdapter extends RecyclerView.Adapter<MyCourseAdapter.BoardViewHolder> {

    private ArrayList<ArrayList<Photo>> AllBoardList;
    private Context context;

    private DatabaseReference mDatabaseRef;


    public MyCourseAdapter(Context context, ArrayList<ArrayList<Photo>> board) {
        this.context = context;
        this.AllBoardList = board;
        mDatabaseRef = FirebaseDatabase.getInstance().getReference("FirebaseRegister").child("UserCourse");
    }

    public class BoardViewHolder extends RecyclerView.ViewHolder {
        protected RecyclerView recyclerView;
        private ImageButton myCourse_Delete;
        private TextView myCourse_Nick;
        public BoardViewHolder(View view)
        {
            super(view);

            this.recyclerView = (RecyclerView)view.findViewById(R.id.recyclerViewVertical);
            this.myCourse_Delete = (ImageButton) view.findViewById(R.id.myCourse_Delete);
            myCourse_Delete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position = getAdapterPosition();
                    Log.d("position","position : " + position);
                    ArrayList<Photo> photoList = AllBoardList.get(position);
                    Log.d("123","123"+photoList.get(0).getTitleName());
                    Log.d("456","456"+photoList.get(0).getCourseId());
                    if (!photoList.isEmpty()) {
                        String courseId = photoList.get(0).getCourseId();
                        Log.d("Course","courseId" + courseId);
                        // Firebase Realtime Database에서 courseID에 해당하는 모든 사진들 삭제
                        mDatabaseRef.child(courseId).removeValue();
                        Toast.makeText(context, "게시물이 삭제되었습니다.", Toast.LENGTH_SHORT).show();
                        // 로컬 데이터 세트에서도 해당 리스트 제거
                        AllBoardList.remove(position);

                        notifyItemRemoved(position);
                    }
                }
            });
        }
    }

    @NonNull
    @Override
    public BoardViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_mycourse, null);
        return new MyCourseAdapter.BoardViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull BoardViewHolder holder, int position) {
        PointAdapter pointAdapter = new PointAdapter(context.getApplicationContext(), AllBoardList.get(position));
        pointAdapter.setItemClickListener(new PointAdapter.OnItemClickListener() {
            @Override
            public void onClick(View v, int pos, Photo photo) {
                Intent intent = new Intent(context, CourseListActivity.class);
                intent.putExtra("photo", photo);
                intent.putExtra("photosList", AllBoardList.get(position));
                context.startActivity(intent);
            }
        });


        holder.recyclerView.setHasFixedSize(true);
        holder.recyclerView.setLayoutManager(new LinearLayoutManager(context
                , LinearLayoutManager.HORIZONTAL
                ,false));
        holder.recyclerView.setAdapter(pointAdapter);
    }

    @Override
    public int getItemCount() {
        return AllBoardList.size();
    }


}
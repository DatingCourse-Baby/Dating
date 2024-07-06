package com.example.datingcourse;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;

public class PhotoAdapter extends RecyclerView.Adapter<PhotoAdapter.HorizontalViewHolder> {
    private ArrayList<Photo> dataList;
    private PhotoAdapter.OnItemClickListener itemClickListener;
    public Context context;

    public PhotoAdapter(Context context, ArrayList<Photo> dataList) {
        this.context = context;
        this.dataList = dataList;
    }

    public class HorizontalViewHolder extends RecyclerView.ViewHolder {
        ImageView photo_item;
        ImageButton delete_item;
        public HorizontalViewHolder(View itemView) {
            super(itemView);

            photo_item = itemView.findViewById(R.id.photo_item);
            delete_item = itemView.findViewById(R.id.closeButton);
        }
    }

    // 아이템 뷰를 위한 뷰홀더 객체를 생성하여 리턴
    @NonNull
    @Override
    public HorizontalViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater
                .from(parent.getContext())
                .inflate(R.layout.activity_photo, null);

        return new PhotoAdapter.HorizontalViewHolder(v);
    }

    // position에 해당하는 데이터를 뷰홀더의 아이템뷰에 표시
    @Override
    public void onBindViewHolder(@NonNull HorizontalViewHolder holder, int position) {
        Photo item = dataList.get(position);

        Glide.with(context)
                .load(item.getImgUrl())
                .error(R.drawable.noimage)
                .fallback(R.drawable.noimage)
                .placeholder(R.drawable.loading)
                .into(holder.photo_item);  // 결과 이미지를 표시할 ImageView

        holder.delete_item.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dataList.remove(position);
                notifyItemRemoved(position);
                // 이후의 포지션들이 변경되므로 아래의 코드를 추가합니다.
                notifyItemRangeChanged(position, dataList.size());
            }
        });

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(itemClickListener != null){
                    itemClickListener.onClick(v, position);
                }
            }
        });


    }

    public interface OnItemClickListener {
        void onClick(View v, int position);
    }

    @Override
    public int getItemCount() {
        return dataList.size();
    }

    public void setItemClickListener(PhotoAdapter.OnItemClickListener onItemClickListener) {
        this.itemClickListener = onItemClickListener;
    }
}

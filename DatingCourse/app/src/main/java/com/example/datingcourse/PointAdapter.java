package com.example.datingcourse;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;

public class PointAdapter extends RecyclerView.Adapter<PointAdapter.HorizontalViewHolder> {
    private ArrayList<Photo> dataList;
    private PointAdapter.OnItemClickListener itemClickListener;
    public Context context;

    public PointAdapter(Context context, ArrayList<Photo> dataList) {
        this.context = context;
        this.dataList = dataList;
    }

    public class HorizontalViewHolder extends RecyclerView.ViewHolder {
        ImageView imgView_item;
        TextView txt_main;
        TextView txt_sub;


        public HorizontalViewHolder(View itemView) {
            super(itemView);

            imgView_item = itemView.findViewById(R.id.imgView_item);
            txt_main = itemView.findViewById(R.id.txt_main);
            txt_sub = itemView.findViewById(R.id.txt_sub);
        }
    }

    // 아이템 뷰를 위한 뷰홀더 객체를 생성하여 리턴
    @NonNull
    @Override
    public HorizontalViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater
                .from(parent.getContext())
                .inflate(R.layout.item_point, null);

        return new PointAdapter.HorizontalViewHolder(v);
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
                .into(holder.imgView_item);  // 결과 이미지를 표시할 ImageView

        holder.txt_main.setText(item.getTitleName());
        holder.txt_sub.setText(item.getAddressName());

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(itemClickListener != null){
                    itemClickListener.onClick(v, position, item);
                }
            }
        });
    }

    public interface OnItemClickListener {
        void onClick(View v, int position, Photo photo);
    }

    @Override
    public int getItemCount() {
        return dataList.size();
    }

    public void setItemClickListener(PointAdapter.OnItemClickListener onItemClickListener) {
        this.itemClickListener = onItemClickListener;
    }
}

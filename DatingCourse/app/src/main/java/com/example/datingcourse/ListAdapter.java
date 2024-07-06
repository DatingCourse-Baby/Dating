package com.example.datingcourse;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

// 리사이클러 뷰 어댑터 클래스
public class ListAdapter extends RecyclerView.Adapter<ListAdapter.ViewHolder> {

    private ArrayList<List_Layout> itemList;
    private OnItemClickListener itemClickListener;

    public ListAdapter(ArrayList<List_Layout> itemList) {
        this.itemList = itemList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.activity_list_layout, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public int getItemCount() {
        return itemList.size();
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        List_Layout currentItem = itemList.get(position);

        Log.d("ListAdapter", "Item at position " + position + ": Name - " + currentItem.getName() + ", Road - " + currentItem.getRoad() + ", Address - " + currentItem.getAddress());

        holder.name.setText(itemList.get(position).getName());
        holder.road.setText(itemList.get(position).getRoad());
        holder.address.setText(itemList.get(position).getAddress());
        // 아이템 클릭 이벤트
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(itemClickListener != null){
                    itemClickListener.onClick(v, position);
                }
            }
        });
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView name;
        TextView road;
        TextView address;

        ViewHolder(View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.tv_list_name);
            road = itemView.findViewById(R.id.tv_list_road);
            address = itemView.findViewById(R.id.tv_list_address);
        }
    }

    public interface OnItemClickListener {
        void onClick(View v, int position);
    }

    public void setItemClickListener(OnItemClickListener onItemClickListener) {
        this.itemClickListener = onItemClickListener;
    }
}

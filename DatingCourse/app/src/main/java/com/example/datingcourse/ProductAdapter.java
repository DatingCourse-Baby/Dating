package com.example.datingcourse;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import org.w3c.dom.Text;

import java.util.List;

public class ProductAdapter extends RecyclerView.Adapter<ProductAdapter.ViewHolder> {

    private List<Product> productList;
    private OnItemClickListener onItemClickListener;

    public ProductAdapter(List<Product> productList) {
        this.productList = productList;
    }

    public interface OnItemClickListener {
        void onItemClick(View view, int position);
    }
    public void setOnItemClickListener(OnItemClickListener listener) {
        this.onItemClickListener = listener;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView name;
        public TextView nameTextView;
        public TextView priceTextView;
        public ImageView imageView;



        public ViewHolder(View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.TextView1);
            nameTextView = itemView.findViewById(R.id.nameTextView1);
            priceTextView = itemView.findViewById(R.id.priceTextView1);
            imageView = itemView.findViewById(R.id.imageView1);

        }
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_product, parent, false);
        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Product product = productList.get(position);
        holder.name.setText(product.getName1());;
        holder.nameTextView.setText(product.getName2());
        holder.priceTextView.setText(product.getPrice() + " 포인트");
        holder.imageView.setImageResource(product.getImageResId());


        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (onItemClickListener != null) {
                    int position = holder.getAdapterPosition();
                    if (position != RecyclerView.NO_POSITION) {
                        onItemClickListener.onItemClick(view, position);
                    }
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return productList.size();
    }
}
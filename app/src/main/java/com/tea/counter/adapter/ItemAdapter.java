package com.tea.counter.adapter;

import android.annotation.SuppressLint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.tea.counter.R;
import com.tea.counter.model.ItemModel;

import java.util.ArrayList;

public class ItemAdapter extends RecyclerView.Adapter<ItemAdapter.ViewHolder> {
    private final ArrayList<ItemModel> dataList;
    private final ItemClick itemClick;

    public ItemAdapter(ArrayList<ItemModel> dataList, ItemClick itemClick) {
        this.dataList = dataList;
        this.itemClick = itemClick;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.simple_row_item_list, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, @SuppressLint("RecyclerView") int position) {
        ItemModel model = dataList.get(position);
        holder.idNumber.setText(String.valueOf(position+1));
        holder.txtItemName.setText(model.getItemName());
        holder.txtItemPrice.setText(model.getPrice());

        holder.btnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                itemClick.onClick(position);
//                notifyDataSetChanged();
            }
        });


    }

    @Override
    public int getItemCount() {
        return dataList.size();
    }

    public interface ItemClick {
        void onClick(int position);
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView idNumber;
        TextView txtItemName;
        TextView txtItemPrice;
        ImageView btnDelete;

        public ViewHolder(View itemView) {
            super(itemView);
            idNumber = itemView.findViewById(R.id.idNumber);
            txtItemName = itemView.findViewById(R.id.txtItemName);
            txtItemPrice = itemView.findViewById(R.id.txtItemPrice);
            btnDelete = itemView.findViewById(R.id.btnDelete);
        }
    }
}


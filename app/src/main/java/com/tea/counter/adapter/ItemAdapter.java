package com.tea.counter.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.tea.counter.R;
import com.tea.counter.model.ItemModel;

import java.util.ArrayList;

public class ItemAdapter extends RecyclerView.Adapter<ItemAdapter.ViewHolder> {
    private final ArrayList<ItemModel> dataList;

    public ItemAdapter(ArrayList<ItemModel> dataList) {
        this.dataList = dataList;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.simple_row_item_list, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        ItemModel model = dataList.get(position);
        holder.idNumber.setText(String.valueOf(model.getId()));
        holder.txtItemName.setText(model.getItemName());
        holder.txtItemPrice.setText(model.getPrice());
    }

    @Override
    public int getItemCount() {
        return dataList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView idNumber;
        TextView txtItemName;
        TextView txtItemPrice;

        public ViewHolder(View itemView) {
            super(itemView);
            idNumber = itemView.findViewById(R.id.idNumber);
            txtItemName = itemView.findViewById(R.id.txtItemName);
            txtItemPrice = itemView.findViewById(R.id.txtItemPrice);
        }
    }
}


package com.tea.counter.adapter;

import android.annotation.SuppressLint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.tea.counter.R;
import com.tea.counter.model.ItemModel;

import java.util.ArrayList;

public class ItemInCustomerAdapter extends RecyclerView.Adapter<ItemInCustomerAdapter.ViewHolder> {
    private final ArrayList<ItemModel> dataList;
    private final ItemClick itemClick;

    public ItemInCustomerAdapter(ArrayList<ItemModel> dataList, ItemClick itemClick) {
        this.dataList = dataList;
        this.itemClick = itemClick;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.simple_data_item_customer, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, @SuppressLint("RecyclerView") int position) {
        ItemModel model = dataList.get(position);
        holder.itemName.setText(String.valueOf(model.getItemName()));
        holder.itemName.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                dataList.get(position).setClick(isChecked);
                itemClick.onClick(dataList);
//                if (isChecked) {
//                    Log.e("Checked", holder.itemName.getText().toString());
//                }
            }
        });

    }

    @Override
    public int getItemCount() {
        return dataList.size();
    }

    public interface ItemClick {
        void onClick(ArrayList<ItemModel> dataList);
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        CheckBox itemName;

        public ViewHolder(View itemView) {
            super(itemView);
            itemName = itemView.findViewById(R.id.itemName);

        }
    }
}


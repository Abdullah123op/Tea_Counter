package com.tea.counter.adapter;

import android.annotation.SuppressLint;
import android.graphics.Color;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.tea.counter.R;
import com.tea.counter.model.ItemModel;

import java.util.ArrayList;

public class ConfirmItemAdapter extends RecyclerView.Adapter<ConfirmItemAdapter.ViewHolder> {
    public final ArrayList<ItemModel> dataList;
    public final ItemClick itemClick;

    public ConfirmItemAdapter(ArrayList<ItemModel> dataList, ItemClick itemClick) {
        this.dataList = dataList;
        this.itemClick = itemClick;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.confirm_item_list, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, @SuppressLint("RecyclerView") int position) {
        ItemModel model = dataList.get(position);
        // holder.idNumber.setText(String.valueOf(model.getId()));
        holder.txtItemNameConfirmItem.setText(model.getItemName());
        holder.txtItemPriceConfirmItem.setText("₹" + model.getPrice());
        holder.etQTY.setText(model.getQty());

//        if (model.getClick()) {
//            holder.txtItemNameConfirmItem.setChecked(true);
//            holder.etQTY.setEnabled(true);
//            holder.etQTY.requestFocus();
//            holder.etQTY.setBackgroundResource(android.R.drawable.editbox_dropdown_dark_frame);
//            itemClick.onClick(dataList);
//        } else {
//            holder.txtItemNameConfirmItem.setChecked(false);
//            holder.etQTY.setBackgroundResource(android.R.color.transparent);
//            holder.etQTY.setText("0");
//            holder.txtTotalPrice.setText("₹0");
//            holder.etQTY.setEnabled(false);
//        }
//
//        holder.txtItemNameConfirmItem.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                dataList.get(position).setClick(!model.getClick());
//                notifyDataSetChanged();
//            }
//        });

        holder.txtItemNameConfirmItem.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                dataList.get(position).setClick(isChecked);
                if (isChecked) {
                    holder.txtItemNameConfirmItem.setChecked(true);
                    holder.etQTY.setTextColor(Color.parseColor("#FFFFFF"));
                    holder.etQTY.setEnabled(true);
                    holder.etQTY.requestFocus();
                    holder.etQTY.setBackgroundResource(android.R.drawable.editbox_dropdown_dark_frame);
                    itemClick.onClick(dataList);
                } else {
                    holder.txtItemNameConfirmItem.setChecked(false);
                    holder.etQTY.setTextColor(Color.parseColor("#000000"));
                    holder.etQTY.setBackgroundResource(android.R.color.transparent);
                    holder.etQTY.setText("");
                    holder.txtTotalPrice.setText("₹0");
                    holder.etQTY.setEnabled(false);
                }
            }
        });

        holder.etQTY.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (!holder.etQTY.getText().toString().isEmpty()) {
                    double qty = Double.parseDouble(String.valueOf(s));
                    double price = Double.parseDouble(dataList.get(position).getPrice());
                    double totalPrice = qty * price;

                    dataList.get(position).setQty(s.toString().trim());
                    dataList.get(position).setTotalPrice(String.valueOf(totalPrice));
                    itemClick.onClick(dataList);
                    holder.txtTotalPrice.setText(String.valueOf(totalPrice));
                } else {
                    dataList.get(position).setQty("");
                    dataList.get(position).setTotalPrice("0");
                    itemClick.onClick(dataList);
                    holder.txtTotalPrice.setText("0");
                }

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
        CheckBox txtItemNameConfirmItem;
        TextView txtItemPriceConfirmItem;
        TextView txtTotalPrice;
        EditText etQTY;

        public ViewHolder(View itemView) {
            super(itemView);
            txtItemNameConfirmItem = itemView.findViewById(R.id.txtItemNameConfirmItem);
            txtItemPriceConfirmItem = itemView.findViewById(R.id.txtItemPriceConfirmItem);
            etQTY = itemView.findViewById(R.id.etQTY);
            txtTotalPrice = itemView.findViewById(R.id.txtTotalPrice);
        }

    }
}


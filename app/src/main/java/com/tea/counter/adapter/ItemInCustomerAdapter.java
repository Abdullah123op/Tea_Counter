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
        // holder.idNumber.setText(String.valueOf(model.getId()));
        holder.txtItemNameReqItem.setText(model.getItemName());
        holder.txtItemPriceReqItem.setText("₹" + model.getPrice());
        holder.etQTYReqItem.setText(model.getQty());
        holder.txtItemNameReqItem.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                dataList.get(position).setClick(isChecked);
                if (isChecked) {
                    holder.txtItemNameReqItem.setChecked(true);
                    holder.etQTYReqItem.setTextColor(Color.parseColor("#FFFFFF"));
                    holder.etQTYReqItem.setEnabled(true);
                    holder.etQTYReqItem.requestFocus();
                    holder.etQTYReqItem.setBackgroundResource(android.R.drawable.editbox_dropdown_dark_frame);
                    itemClick.onClick(dataList);
                } else {
                    holder.txtItemNameReqItem.setChecked(false);
                    holder.etQTYReqItem.setTextColor(Color.parseColor("#000000"));
                    holder.etQTYReqItem.setBackgroundResource(android.R.color.transparent);
                    holder.etQTYReqItem.setText("");
                    holder.txtReqTotalPrice.setText("₹0");
                    holder.etQTYReqItem.setEnabled(false);
                }
            }
        });


        holder.etQTYReqItem.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (!holder.etQTYReqItem.getText().toString().isEmpty()) {
                    double qty = Double.parseDouble(String.valueOf(s));
                    double price = Double.parseDouble(dataList.get(position).getPrice());
                    double totalPrice = qty * price;

                    dataList.get(position).setQty(s.toString().trim());
                    dataList.get(position).setTotalPrice(String.valueOf(totalPrice));
                    itemClick.onClick(dataList);
                    holder.txtReqTotalPrice.setText(String.valueOf(totalPrice));
                } else {
                    dataList.get(position).setQty("");
                    dataList.get(position).setTotalPrice("0");
                    itemClick.onClick(dataList);
                    holder.txtReqTotalPrice.setText("0");
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
        CheckBox txtItemNameReqItem;
        TextView txtItemPriceReqItem , txtReqTotalPrice;
        EditText etQTYReqItem ;

        public ViewHolder(View itemView) {
            super(itemView);
            txtItemNameReqItem = itemView.findViewById(R.id.txtItemNameReqItem);
            txtItemPriceReqItem = itemView.findViewById(R.id.txtItemPriceReqItem);
            txtReqTotalPrice = itemView.findViewById(R.id.txtReqTotalPrice);
            etQTYReqItem = itemView.findViewById(R.id.etQTYReqItem);

        }
    }
}


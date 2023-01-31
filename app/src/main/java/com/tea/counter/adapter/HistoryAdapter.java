package com.tea.counter.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.tea.counter.R;
import com.tea.counter.model.OrderModel;
import java.util.ArrayList;

public class HistoryAdapter extends RecyclerView.Adapter<HistoryAdapter.ViewHolder> {
    private ArrayList<OrderModel> dataList;

    public HistoryAdapter(ArrayList<OrderModel> dataList) {
        this.dataList = dataList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.simple_history_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        OrderModel model = dataList.get(position);

        holder.txtTime.setText(String.valueOf(model.getTime()));
        holder.txtCup.setText(model.getCup());
        holder.txtPrice.setText(model.getPrice());
    }

    @Override
    public int getItemCount() {
        return dataList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView imgImage;
        TextView txtTime;
        TextView txtCup;
        TextView txtPrice;

        public ViewHolder(View itemView) {
            super(itemView);
            //idNumber = itemView.findViewById(R.id.idNumber);
            imgImage = itemView.findViewById(R.id.imgImageH);
            txtTime = itemView.findViewById(R.id.txtTimeH);
            txtCup = itemView.findViewById(R.id.txtCupH);
            txtPrice = itemView.findViewById(R.id.txtPriceH);
        }
    }
}


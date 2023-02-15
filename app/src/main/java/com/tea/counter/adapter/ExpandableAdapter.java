package com.tea.counter.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.tea.counter.R;
import com.tea.counter.model.OrderModel;

import java.util.ArrayList;
import java.util.List;

public class ExpandableAdapter extends RecyclerView.Adapter<ExpandableAdapter.ViewHolder> {
    List<OrderModel> dataList;
    boolean isHomePage;

    public ExpandableAdapter(ArrayList<OrderModel> dataList, boolean isHomePage) {
        this.dataList = dataList;
        this.isHomePage = isHomePage;
    }

    @NonNull
    @Override
    public ExpandableAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.expandable_list, parent, false);
        return new ExpandableAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ExpandableAdapter.ViewHolder holder, int position) {
        OrderModel model = dataList.get(position);

        holder.txtTitleExpandable.setText(String.valueOf(model.getOrderTitle()));
        if (isHomePage) {
            holder.txtTimeExpandable.setText(model.getOrderTime());
        } else {
            holder.txtTimeExpandable.setText(model.getOrderDate() + " , " + model.getOrderTime());
        }
        holder.txtPriceExpandable.setText("₹" + model.getOrderPrice());
        holder.orderItemDetails.setText(model.getOrderDetails());
        String imgUrl = model.getImageUrl();

        Glide.with(holder.imgUserAdapter.getContext()).load(imgUrl).into(holder.imgUserAdapter);

        boolean isVisible = dataList.get(position).isVisibility();
        holder.constraintLayoutExpand.setVisibility(isVisible ? View.VISIBLE : View.GONE);
    }

    @Override
    public int getItemCount() {
        return dataList.size();
    }


    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView txtTitleExpandable;
        TextView txtTimeExpandable;
        TextView txtPriceExpandable;
        TextView orderItemDetails;
        ConstraintLayout constraintLayoutExpand;
        ConstraintLayout mainLayoutExpandable;
        ImageView imgUserAdapter;

        public ViewHolder(View itemView) {
            super(itemView);
            txtTitleExpandable = itemView.findViewById(R.id.txtTitleExpandable);
            txtTimeExpandable = itemView.findViewById(R.id.txtTimeExpandable);
            txtPriceExpandable = itemView.findViewById(R.id.txtPriceExpandable);
            orderItemDetails = itemView.findViewById(R.id.orderItemDetails);
            constraintLayoutExpand = itemView.findViewById(R.id.constraintLayoutExpand);
            mainLayoutExpandable = itemView.findViewById(R.id.mainLayoutExpandable);
            imgUserAdapter = itemView.findViewById(R.id.imgUserAdapter);


            mainLayoutExpandable.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    for (int i = 0; i < dataList.size(); i++) {
                        if (dataList.get(i).isVisibility() && i != getAdapterPosition()) {
                            dataList.get(i).setVisibility(false);
                            notifyItemChanged(i);
                        }
                    }
                    OrderModel orderModel = dataList.get(getAdapterPosition());
                    orderModel.setVisibility(!orderModel.isVisibility());
                    notifyItemChanged(getAdapterPosition());
                }
            });
        }
    }
}

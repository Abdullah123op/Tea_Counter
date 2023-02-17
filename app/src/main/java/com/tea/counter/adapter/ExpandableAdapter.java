package com.tea.counter.adapter;

import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.tea.counter.R;
import com.tea.counter.model.OrderModel;

import java.util.ArrayList;
import java.util.List;

public class ExpandableAdapter extends RecyclerView.Adapter<ExpandableAdapter.ViewHolder> {
    List<OrderModel> dataList;
    boolean isHomePage;
    boolean isSellerSide;

    boolean isNotificationSide;

    public ExpandableAdapter(ArrayList<OrderModel> dataList, boolean isHomePage, boolean isSellerSide, boolean isNotificationSide) {
        this.dataList = dataList;
        this.isHomePage = isHomePage;
        this.isSellerSide = isSellerSide;
        this.isNotificationSide = isNotificationSide;
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

        if (isNotificationSide) {
            Drawable myDrawable = ContextCompat.getDrawable(holder.imgUserAdapter.getContext(), R.drawable.new_order);
            holder.imgUserAdapter.setImageDrawable(myDrawable);
            holder.txtPriceExpandable.setVisibility(View.GONE);
            if (model.getAdditionalComment() != null) {
                holder.txtAdditional.setVisibility(View.VISIBLE);
                holder.txtAdditional.setText("Message : " + model.getAdditionalComment());
            } else if (model.getAdditionalComment() == null) {
                holder.txtAdditional.setVisibility(View.GONE);
            }
            //  holder.txtAdditional.setVisibility(View.GONE);
            Log.d("45454", "" + model.getAdditionalComment());

            holder.txtTitleExpandable.setText(" New Order from  " + model.getOrderTitle());
            holder.orderItemDetails.setText(model.getOrderDetails());
            holder.txtTimeExpandable.setText(model.getOrderTime());

            boolean isVisible = dataList.get(position).isVisibility();
            holder.constraintLayoutExpand.setVisibility(isVisible ? View.VISIBLE : View.GONE);
            return;
        }

        if (isSellerSide) {
            holder.txtTitleExpandable.setText("Delivered To " + model.getOrderTitle());
        } else {
            holder.txtTitleExpandable.setText("Delivered from " + model.getOrderTitle());
        }

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
        TextView txtAdditional;
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
            txtAdditional = itemView.findViewById(R.id.txtAdditional);


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

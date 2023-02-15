package com.tea.counter.adapter;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.tea.counter.R;
import com.tea.counter.model.BillModel;
import com.tea.counter.services.FcmNotificationsSender;
import com.tea.counter.utils.Constants;

import java.util.ArrayList;
import java.util.List;

public class BillAdapter extends RecyclerView.Adapter<BillAdapter.ViewHolder> {
    List<BillModel> dataList;
    boolean isSellerSide;

    boolean isPaid = false;

    public BillAdapter(ArrayList<BillModel> dataList, boolean isSellerSide) {
        this.dataList = dataList;
        this.isSellerSide = isSellerSide;
    }

    @NonNull
    @Override
    public BillAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.bill_list, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        BillModel model = dataList.get(position);
        holder.txtMonthOfBill.setText(model.getBillMonth());
        holder.txtQtyOfBill.setText(model.getTotalQty());
        holder.txtPRiceOfBill.setText("₹" + model.getTotalAmountToPay());
        holder.txtCustomerNameBill.setText(model.getCustomerName());
        holder.txtSellerNameBill.setText(model.getSellerName());
        isPaid = model.getPaid();
//        holder.txtSellerNameBill.setText(String.valueOf(model.getPaid()));


        if (isSellerSide) {

            if (isPaid) {
                Log.d("8585", " This is paid");
                holder.txtConfirmPayment.setVisibility(View.GONE);
                holder.btnSendReminder.setVisibility(View.GONE);
                holder.txtSendReminder.setVisibility(View.GONE);
                holder.txtPRiceOfBill.setTextColor(Color.parseColor("#1C571F"));
                holder.layoutBill.setBackground(ContextCompat.getDrawable(holder.layoutBill.getContext(), R.drawable.bill_background_positive));
            } else {
                Log.d("8585", " This is unPaid");
                holder.txtConfirmPayment.setVisibility(View.VISIBLE);
                holder.txtPRiceOfBill.setTextColor(Color.parseColor("#ff3333"));
                holder.layoutBill.setBackground(ContextCompat.getDrawable(holder.layoutBill.getContext(), R.drawable.bill_background_negativ));
                holder.txtConfirmPayment.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        AlertDialog.Builder builder = new AlertDialog.Builder(v.getContext());
                        builder.setMessage("Did you received the Payment").setCancelable(false).setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                FirebaseFirestore db = FirebaseFirestore.getInstance();
                                DocumentReference documentReference = db.collection(Constants.COLLECTION_NAME_BILL).document(model.getDocumentName());
                                documentReference.update(Constants.IS_PAID_BILL, true).addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void unused) {
                                        Toast.makeText(v.getContext(), "Confirmed", Toast.LENGTH_SHORT).show();
                                        dataList.get(position).setPaid(true);
                                        notifyDataSetChanged();
                                    }
                                }).addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                    }
                                });
                            }
                        }).setNegativeButton("No", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
                            }
                        });
                        AlertDialog alert = builder.create();
                        alert.show();
                    }
                });
            }


            holder.btnSendReminder.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    FcmNotificationsSender notificationsSender = new FcmNotificationsSender(model.getFcmToken(), "Payment Reminder", "from " + model.getSellerName() + " for pending Bill of " + "₹" + model.getTotalAmountToPay(), v.getContext());
                    notificationsSender.SendNotifications();
                    Toast.makeText(v.getContext(), "Reminder Sent", Toast.LENGTH_SHORT).show();
                }
            });
        } else {
            holder.btnSendReminder.setVisibility(View.GONE);
            holder.txtSendReminder.setVisibility(View.GONE);
            holder.txtConfirmPayment.setVisibility(View.GONE);
            if (isPaid) {
                Log.d("8585", " This is paid");
                holder.txtConfirmPayment.setVisibility(View.GONE);
                holder.txtPRiceOfBill.setTextColor(Color.parseColor("#1C571F"));
                holder.layoutBill.setBackground(ContextCompat.getDrawable(holder.layoutBill.getContext(), R.drawable.bill_background_positive));
            } else {
                Log.d("8585", " This is unPaid");
                holder.txtPRiceOfBill.setTextColor(Color.parseColor("#ff3333"));
                holder.layoutBill.setBackground(ContextCompat.getDrawable(holder.layoutBill.getContext(), R.drawable.bill_background_negativ));

            }
        }

    }


    @Override
    public int getItemCount() {
        return dataList.size();
    }


    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView txtMonthOfBill;
        TextView txtQtyOfBill;
        TextView txtPRiceOfBill;
        TextView txtCustomerNameBill;
        TextView txtSellerNameBill;
        TextView txtSendReminder;
        ImageView btnSendReminder;

        TextView txtConfirmPayment;
        ConstraintLayout layoutBill;

        public ViewHolder(View itemView) {
            super(itemView);
            txtMonthOfBill = itemView.findViewById(R.id.txtMonthOfBill);
            txtQtyOfBill = itemView.findViewById(R.id.txtQtyOfBill);
            txtPRiceOfBill = itemView.findViewById(R.id.txtPRiceOfBill);
            txtCustomerNameBill = itemView.findViewById(R.id.txtCustomerNameBill);
            txtSellerNameBill = itemView.findViewById(R.id.txtSellerNameBill);
            btnSendReminder = itemView.findViewById(R.id.btnSendReminder);
            txtSendReminder = itemView.findViewById(R.id.txtSendReminder);
            txtConfirmPayment = itemView.findViewById(R.id.txtConfirmPayment);
            layoutBill = itemView.findViewById(R.id.layoutBill);


        }
    }
}

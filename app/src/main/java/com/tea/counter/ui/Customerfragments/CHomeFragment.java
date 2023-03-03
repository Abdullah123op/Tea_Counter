package com.tea.counter.ui.Customerfragments;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.gson.Gson;
import com.tea.counter.adapter.ExpandableAdapter;
import com.tea.counter.databinding.FragmentCHomeBinding;
import com.tea.counter.dialog.CustomProgressDialog;
import com.tea.counter.model.OrderModel;
import com.tea.counter.utils.Constants;
import com.tea.counter.utils.Preference;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.HashMap;


public class CHomeFragment extends Fragment {
    FragmentCHomeBinding binding;
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    String name;
    String price;
    String qty;
    String totalPrice;
    double totalAmountSum = 0;
    int totalQty = 0;
    CustomProgressDialog customProgressDialog;
    private Context mContext;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        mContext = context;
        customProgressDialog = new CustomProgressDialog(mContext);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentCHomeBinding.inflate(inflater, container, false);

        Glide.with(mContext).load(Preference.getImgUri(mContext)).into(binding.imgCustomerHome);
        getOrders();

        return binding.getRoot();

    }

    public void getOrders() {
        customProgressDialog.show();
        Calendar startOfToday = Calendar.getInstance();
        startOfToday.set(Calendar.HOUR_OF_DAY, 0);
        startOfToday.set(Calendar.MINUTE, 0);
        startOfToday.set(Calendar.SECOND, 0);
        startOfToday.set(Calendar.MILLISECOND, 0);

        Calendar startOfTomorrow = Calendar.getInstance();
        startOfTomorrow.add(Calendar.DATE, 1);
        startOfTomorrow.set(Calendar.HOUR_OF_DAY, 0);
        startOfTomorrow.set(Calendar.MINUTE, 0);
        startOfTomorrow.set(Calendar.SECOND, 0);
        startOfTomorrow.set(Calendar.MILLISECOND, 0);
        Timestamp startTimestamp = new Timestamp(startOfToday.getTime());
        Timestamp endTimestamp = new Timestamp(startOfTomorrow.getTime());

        db.collection(Constants.COLLECTION_NAME_ORDERS).whereEqualTo(Constants.CUSTOMER_UID, FirebaseAuth.getInstance().getUid()).whereGreaterThanOrEqualTo(Constants.TIMESTAMP_ORDER, startTimestamp).whereLessThanOrEqualTo(Constants.TIMESTAMP_ORDER, endTimestamp).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
//                    Log.e("Order list : ", new Gson().toJson(task.getResult().getDocuments()));

                    ArrayList<OrderModel> orderList = new ArrayList<>();
                    totalAmountSum = 0;
                    totalQty = 0;
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        String orderTime = (String) document.getData().get(Constants.TIME_ORDER);
                        String customerName = (String) document.getData().get(Constants.SELLER_NAME_ORDER);
                        String totalAmount = String.valueOf(document.getData().get(Constants.TOTAL_AMOUNT));
                        String sellerImgUrl = String.valueOf(document.getData().get(Constants.SELLER_IMAGE_ORDER));

                        totalAmountSum = totalAmountSum + Double.parseDouble(totalAmount);

                        Log.d("4545", "onComplete: " + totalAmountSum);
                        binding.txtCustomerPrice.setText("₹" + totalAmountSum);

                        ArrayList<HashMap<String, Object>> itemList = (ArrayList<HashMap<String, Object>>) document.getData().get(Constants.ORDER_ITEM_LIST);
                        Log.e("11111 : ", new Gson().toJson(itemList));

                        StringBuilder customItemList = new StringBuilder();
                        for (int i = 0; i < itemList.size(); i++) {
                            HashMap<String, Object> item = itemList.get(i);

                            name = (String) item.get(Constants.ITEM_NAME);
                            price = (String) item.get(Constants.ITEM_PRICE);
                            qty = (String) item.get("qty");
                            totalPrice = (String) item.get("totalPrice");

                            totalQty = (int) (totalQty + Double.parseDouble(qty));
                            Log.d("4545", "onComplete: " + totalQty);
                            binding.txtCustomerPcs.setText(String.valueOf(totalQty));
                            String customString = name + "   ×   " + qty + "   =   " + totalPrice + "\n";
                            customItemList.append(customString);

                        }
                        Log.e("454", String.valueOf(customItemList));

                        orderList.add(new OrderModel(customerName, orderTime, totalAmount, customItemList, sellerImgUrl, ""));


                    }


                    if (orderList.isEmpty()) {
                        binding.recyclereViewinCustomer.setVisibility(View.GONE);
                        binding.recyclerViewAlt.setVisibility(View.VISIBLE);
                    } else {
                        Collections.reverse(orderList);
                        binding.recyclereViewinCustomer.setLayoutManager(new LinearLayoutManager(getContext()));
                        ExpandableAdapter expandableAdapter = new ExpandableAdapter(orderList, true, false, false);
                        binding.recyclereViewinCustomer.setAdapter(expandableAdapter);

                        binding.recyclereViewinCustomer.setVisibility(View.VISIBLE);
                        binding.recyclerViewAlt.setVisibility(View.GONE);
                    }

                    customProgressDialog.dismiss();
                } else {
                    Log.d("ERR", "Error getting Orders: ", task.getException());
                    customProgressDialog.dismiss();
                }
            }
        });

    }
}
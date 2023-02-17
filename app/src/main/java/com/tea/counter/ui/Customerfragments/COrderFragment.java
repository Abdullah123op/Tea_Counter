package com.tea.counter.ui.Customerfragments;


import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.gson.Gson;
import com.tea.counter.R;
import com.tea.counter.adapter.ExpandableAdapter;
import com.tea.counter.adapter.SpinnerAdapter;
import com.tea.counter.databinding.FragmentCOrderBinding;
import com.tea.counter.dialog.CustomProgressDialog;
import com.tea.counter.model.OrderModel;
import com.tea.counter.model.SignupModel;
import com.tea.counter.utils.Constants;

import org.checkerframework.checker.nullness.qual.NonNull;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.HashMap;

public class COrderFragment extends Fragment {
    FragmentCOrderBinding binding;
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    String name;
    String price;
    String qty;
    String totalPrice;
    ArrayList<SignupModel> customerArrayList = new ArrayList<>();
    int itemPosition = 0;
    int totalQty = 0;
    double totalAmountSum = 0;
    private Context mContext;
    CustomProgressDialog customProgressDialog;
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
        binding = FragmentCOrderBinding.inflate(inflater, container, false);
        initView();
        return binding.getRoot();
    }

    public void initView() {
        retrieveSeller();
        binding.spinnerCustomerOrder.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                TextView textView = (TextView) view;
                textView.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.baseline_arrow_drop_down_24, 0);
                Log.e("55555 : ", new Gson().toJson(customerArrayList.get(position).getUserName()));
                itemPosition = position;
                getOrders(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

    }

    public void retrieveSeller() {
        customProgressDialog.show();
        db.collection(Constants.COLLECTION_NAME).whereEqualTo(Constants.USER_TYPE, "0").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {

                    for (QueryDocumentSnapshot document : task.getResult()) {
                        SignupModel signupModel = new SignupModel();
                        signupModel.setUid((String) document.getData().get(Constants.UID));
                        signupModel.setMobileNumber((String) document.getData().get(Constants.MOBILE_NUMBER));
                        signupModel.setUserType((String) document.getData().get(Constants.USER_TYPE));
                        signupModel.setUserName((String) document.getData().get(Constants.USER_NAME));
                        signupModel.setAddress((String) document.getData().get(Constants.ADDRESS));
                        signupModel.setCity((String) document.getData().get(Constants.CITY));
                        signupModel.setLatitude((String) document.getData().get(Constants.LATITUDE));
                        signupModel.setLongitude((String) document.getData().get(Constants.LONGITUDE));
                        signupModel.setImageURL((String) document.getData().get(Constants.IMAGE));
                        signupModel.setFcmToken((String) document.getData().get(Constants.FCM_TOKEN));
                        customerArrayList.add(signupModel);
                    }


                    if (customerArrayList.isEmpty()) {
                        binding.recyclerViewInCustomerOrder.setVisibility(View.GONE);
                        binding.recyclerViewInCustomerOrderAlt.setVisibility(View.VISIBLE);
                    } else {
                        binding.recyclerViewInCustomerOrder.setVisibility(View.VISIBLE);
                        binding.recyclerViewInCustomerOrderAlt.setVisibility(View.GONE);
                        SpinnerAdapter spinnerAdapter = new SpinnerAdapter(mContext, customerArrayList);
                        binding.spinnerCustomerOrder.setAdapter(spinnerAdapter);

                    }
                    //

                }
                if (task.getResult().isEmpty()) {
                    Toast.makeText(mContext, "There is no Seller In Database", Toast.LENGTH_SHORT).show();
                    customProgressDialog.dismiss();
                } else {
                    Log.d("ERR", "Error getting documents: ", task.getException());
                }
            }
        });
    }

    public void getOrders(int position) {

        Calendar startOfMonth = Calendar.getInstance();
        startOfMonth.set(Calendar.DAY_OF_MONTH, 1);
        startOfMonth.set(Calendar.HOUR_OF_DAY, 0);
        startOfMonth.set(Calendar.MINUTE, 0);
        startOfMonth.set(Calendar.SECOND, 0);
        startOfMonth.set(Calendar.MILLISECOND, 0);

        Calendar endOfMonth = Calendar.getInstance();
        endOfMonth.set(Calendar.DAY_OF_MONTH, endOfMonth.getActualMaximum(Calendar.DAY_OF_MONTH));
        endOfMonth.set(Calendar.HOUR_OF_DAY, 23);
        endOfMonth.set(Calendar.MINUTE, 59);
        endOfMonth.set(Calendar.SECOND, 59);
        endOfMonth.set(Calendar.MILLISECOND, 999);

        Timestamp startTimestamp = new Timestamp(startOfMonth.getTime());
        Timestamp endTimestamp = new Timestamp(endOfMonth.getTime());

        db.collection(Constants.COLLECTION_NAME_ORDERS).whereEqualTo(Constants.CUSTOMER_UID, FirebaseAuth.getInstance().getUid()).whereEqualTo(Constants.SELLER_UID_ORDER, customerArrayList.get(position).getUid()).whereGreaterThanOrEqualTo(Constants.TIMESTAMP_ORDER, startTimestamp).whereLessThanOrEqualTo(Constants.TIMESTAMP_ORDER, endTimestamp).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
//                    Log.e("Order list : ", new Gson().toJson(task.getResult().getDocuments()));

                    ArrayList<OrderModel> orderList = new ArrayList<>();
                    totalAmountSum = 0;
                    totalQty = 0;

                    for (QueryDocumentSnapshot document : task.getResult()) {
                        String orderTime = (String) document.getData().get(Constants.TIME_ORDER);
                        String orderDate = (String) document.getData().get(Constants.DATE_ORDER);
                        String sellerName = (String) document.getData().get(Constants.SELLER_NAME_ORDER);
                        String totalAmount = String.valueOf(document.getData().get(Constants.TOTAL_AMOUNT));
                        String sellerImgUrl = String.valueOf(document.getData().get(Constants.SELLER_IMAGE_ORDER));

                        totalAmountSum = totalAmountSum + Double.parseDouble(totalAmount);
//
//                        Log.d("4545", "onComplete: " + totalAmountSum);
                        binding.txtPriceCOrder.setText("₹" + totalAmountSum);

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
                            binding.txtCupsCOrder.setText(String.valueOf(totalQty));

                            String customString = name + "   ×   " + qty + "   =   " + totalPrice + "\n";
                            customItemList.append(customString);

                        }
                        Log.e("454", String.valueOf(customItemList));

                        orderList.add(new OrderModel(sellerName, orderTime, totalAmount, customItemList, sellerImgUrl, orderDate));
                    }

                    if (orderList.isEmpty()) {
                        binding.recyclerViewInCustomerOrder.setVisibility(View.GONE);
                        binding.recyclerViewInCustomerOrderAlt.setVisibility(View.VISIBLE);
                    } else {
                        binding.recyclerViewInCustomerOrder.setVisibility(View.VISIBLE);
                        binding.recyclerViewInCustomerOrderAlt.setVisibility(View.GONE);

                        Collections.reverse(orderList);
                        binding.recyclerViewInCustomerOrder.setLayoutManager(new LinearLayoutManager(getContext()));
                        ExpandableAdapter expandableAdapter = new ExpandableAdapter(orderList, false, false, false);
                        binding.recyclerViewInCustomerOrder.setAdapter(expandableAdapter);
                    }
                    customProgressDialog.dismiss();

                } else {
                    Log.d("ERR", "Error getting Orders: ", task.getException());

                }
            }
        });
    }


}
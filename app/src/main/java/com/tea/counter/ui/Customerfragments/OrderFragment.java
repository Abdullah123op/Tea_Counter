package com.tea.counter.ui.Customerfragments;


import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
import com.skydoves.powerspinner.IconSpinnerAdapter;
import com.skydoves.powerspinner.IconSpinnerItem;
import com.tea.counter.R;
import com.tea.counter.adapter.ExpandableAdapter;
import com.tea.counter.databinding.FragmentCOrderBinding;
import com.tea.counter.dialog.CustomProgressDialog;
import com.tea.counter.model.OrderModel;
import com.tea.counter.model.SignupModel;
import com.tea.counter.utils.Constants;
import com.tea.counter.utils.Preference;

import org.checkerframework.checker.nullness.qual.NonNull;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

public class OrderFragment extends Fragment {
    FragmentCOrderBinding binding;
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    String name;
    String price;
    String qty;
    String totalPrice;
    ArrayList<SignupModel> customerArrayList = new ArrayList<>();
    ArrayList<SignupModel> userArrayList = new ArrayList<>();
    List<IconSpinnerItem> iconSpinnerItems = new ArrayList<>();
    int itemPosition = 0;
    int totalQty = 0;
    double totalAmountSum = 0;
    CustomProgressDialog customProgressDialog;
    String selectedUserUid;
    String currentUserUid;
    String myUserType;
    String anotherSide;
    boolean isSellerSide;
    private Context mContext;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        mContext = context;
        customProgressDialog = new CustomProgressDialog(mContext);
        myUserType = (Preference.getUserType(mContext));
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

    @Override
    public void onPause() {
        super.onPause();
        binding.powerSpinnerView.dismiss();

    }

    public void initView() {
        if (Objects.equals(myUserType, "1")) {
            selectedUserUid = Constants.SELLER_UID_ORDER;
            currentUserUid = Constants.CUSTOMER_UID;
            isSellerSide = false;
            anotherSide = "0";
            binding.powerSpinnerView.setHint("SELECT SELLER");
        } else {
            selectedUserUid = Constants.CUSTOMER_UID;
            currentUserUid = Constants.SELLER_UID_ORDER;
            isSellerSide = true;
            anotherSide = "1";
            binding.powerSpinnerView.setHint("SELECT CUSTOMER");

        }

        getUserFromDb();

    }

    public void getUserFromDb() {
        customProgressDialog.show();
        db.collection(Constants.COLLECTION_NAME).whereEqualTo(Constants.USER_TYPE, anotherSide).get().addOnCompleteListener(task -> {
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

                    userArrayList.add(signupModel);
                    iconSpinnerItems.add(new IconSpinnerItem(signupModel.getUserName()));
                }


                if (customerArrayList.isEmpty()) {
                    binding.recyclerViewInCustomerOrder.setVisibility(View.GONE);
                    binding.recyclerViewInCustomerOrderAlt.setVisibility(View.VISIBLE);
                } else {
                    binding.recyclerViewInCustomerOrder.setVisibility(View.VISIBLE);
                    binding.recyclerViewInCustomerOrderAlt.setVisibility(View.GONE);


                    IconSpinnerAdapter iconSpinnerAdapter = new IconSpinnerAdapter(binding.powerSpinnerView);
                    binding.powerSpinnerView.setSpinnerAdapter(iconSpinnerAdapter);
                    binding.powerSpinnerView.setItems(iconSpinnerItems);


                    binding.powerSpinnerView.setOnSpinnerItemSelectedListener((i, o, newIndex, t1) -> {
                        Log.e("11111", "NEW ITEMS  Uid : " + userArrayList.get(newIndex).getUid() + " seller Name : " + userArrayList.get(newIndex).getUserName());
                        itemPosition = newIndex;
                        getOrders(newIndex);
                    });

                }
            }
            if (task.getResult().isEmpty()) {
                Toast.makeText(mContext, "There is no Seller In Database", Toast.LENGTH_SHORT).show();
                customProgressDialog.dismiss();
            } else {
                Log.d("ERR", "Error getting documents: ", task.getException());
                customProgressDialog.dismiss();
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

        db.collection(Constants.COLLECTION_NAME_ORDERS).whereEqualTo(currentUserUid, FirebaseAuth.getInstance().getUid()).whereEqualTo(selectedUserUid, userArrayList.get(position).getUid())
                .whereGreaterThanOrEqualTo(Constants.TIMESTAMP_ORDER, startTimestamp).whereLessThanOrEqualTo(Constants.TIMESTAMP_ORDER, endTimestamp).get().addOnCompleteListener(task -> {
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
                    binding.txtPriceCOrder.setText(getString(R.string.rupee_symbol) + totalAmountSum);

                    ArrayList<HashMap<String, Object>> itemList = (ArrayList<HashMap<String, Object>>) document.getData().get(Constants.ORDER_ITEM_LIST);
                    Log.e("11111 : ", new Gson().toJson(itemList));

                    StringBuilder customItemList = new StringBuilder();
                    for (int i = 0; i < Objects.requireNonNull(itemList).size(); i++) {
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
                    ExpandableAdapter expandableAdapter = new ExpandableAdapter(orderList, false, isSellerSide, false);
                    binding.recyclerViewInCustomerOrder.setAdapter(expandableAdapter);
                }
                customProgressDialog.dismiss();

            } else {
                Log.d("ERR2", "Error getting Orders: ", task.getException());
            }
        });
    }


}
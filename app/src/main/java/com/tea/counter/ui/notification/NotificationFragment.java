package com.tea.counter.ui.notification;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.tea.counter.adapter.ExpandableAdapter;
import com.tea.counter.databinding.FragmentNotificationBinding;
import com.tea.counter.dialog.CustomProgressDialog;
import com.tea.counter.model.OrderModel;
import com.tea.counter.utils.Constants;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.HashMap;


public class NotificationFragment extends Fragment {
    FragmentNotificationBinding binding;
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    String name;
    String price;
    String qty;
    int totalQty = 0;
    CustomProgressDialog customProgressDialog;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        customProgressDialog = new CustomProgressDialog(context);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentNotificationBinding.inflate(inflater, container, false);
        initView();

        return binding.getRoot();
    }

    private void initView() {
        getNotifications();
    }

    public void getNotifications() {
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
        ArrayList<OrderModel> notificationList = new ArrayList<>();

        db.collection(Constants.COLLECTION_NAME_NOTIFICATION).whereEqualTo(Constants.NOTI_SELLER_UID, FirebaseAuth.getInstance().getUid()).whereGreaterThanOrEqualTo(Constants.NOTI_TIME_STAMP, startTimestamp).whereLessThanOrEqualTo(Constants.NOTI_TIME_STAMP, endTimestamp).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {

                    for (QueryDocumentSnapshot document : task.getResult()) {

                        String customerUid = (String) document.getData().get(Constants.NOTI_CUSTOMER_UID);
                        String customerName = (String) document.getData().get(Constants.NOTI_CUSTOMER_NAME);
                        String orderTime = (String) document.getData().get(Constants.NOTI_TIME_ORDER);
                        String additionalComment = (String) document.getData().get(Constants.NOTI_ADDITIONAL_CMT);
                        ArrayList<HashMap<String, Object>> itemList = (ArrayList<HashMap<String, Object>>) document.getData().get(Constants.NOTI_MESSAGE_LIST);


                        StringBuilder customItemList = new StringBuilder();
                        for (int i = 0; i < itemList.size(); i++) {
                            HashMap<String, Object> item = itemList.get(i);
                            name = (String) item.get(Constants.ITEM_NAME);
                            price = (String) item.get(Constants.ITEM_PRICE);
                            qty = (String) item.get("qty");

                            assert qty != null;
                            totalQty = (int) (totalQty + Double.parseDouble(qty));
                            Log.d("4545", "onComplete: " + totalQty);

                            String customString = name + "   ×   " + qty + "\n";
                            customItemList.append(customString);
                        }

                        OrderModel orderModelNotification = new OrderModel(customerName, customItemList, orderTime, additionalComment);
                        notificationList.add(orderModelNotification);

                    }
                    if (notificationList.isEmpty()) {
                        binding.recyclerViewNotification.setVisibility(View.GONE);
                        binding.recyclerViewNotificationAlt.setVisibility(View.VISIBLE);
                    } else {
                        Collections.reverse(notificationList);
                        ExpandableAdapter expandableAdapter = new ExpandableAdapter(notificationList, false, false, true);
                        binding.recyclerViewNotification.setAdapter(expandableAdapter);
                        binding.recyclerViewNotification.setVisibility(View.VISIBLE);
                        binding.recyclerViewNotificationAlt.setVisibility(View.GONE);
                    }
                    customProgressDialog.dismiss();
                } else {
                    Log.d("ERR", "Error getting documents: ", task.getException());
                }
            }
        });
    }
}
package com.tea.counter.ui.notification;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.WriteBatch;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.gson.Gson;
import com.tea.counter.adapter.ExpandableAdapter;
import com.tea.counter.databinding.FragmentNotificationBinding;
import com.tea.counter.dialog.CustomProgressDialog;
import com.tea.counter.model.OrderModel;
import com.tea.counter.utils.Constants;
import com.tea.counter.utils.Preference;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;


public class NotificationFragment extends Fragment {
    FragmentNotificationBinding binding;
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    String name;
    String price;
    String qty;
    int totalQty = 0;
    CustomProgressDialog customProgressDialog;
    private Context mContext;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        mContext = context;

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
        updateList();
        getNotifications();
        deleteOldNotifications();
    }

    private void updateList() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection(Constants.COLLECTION_NAME_NOTIFICATION).whereEqualTo(Constants.NOTI_SELLER_UID, FirebaseAuth.getInstance().getUid()).addSnapshotListener(new com.google.firebase.firestore.EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(QuerySnapshot documentSnapshots, FirebaseFirestoreException e) {
                if (e != null) {
                    Log.e("ERR", "Listen failed: " + e);
                    return;
                }
                // Retrieve new notifications and update the list
                getNotifications();
            }
        });
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
                    ArrayList<HashMap<String, Object>> itemList = null;

                    for (QueryDocumentSnapshot document : task.getResult()) {
                        String audioUrl = null;
                        String customerUid = (String) document.getData().get(Constants.NOTI_CUSTOMER_UID);
                        String customerName = (String) document.getData().get(Constants.NOTI_CUSTOMER_NAME);
                        String orderTime = (String) document.getData().get(Constants.NOTI_TIME_ORDER);
                        String additionalComment = (String) document.getData().get(Constants.NOTI_ADDITIONAL_CMT);

                        if (document.getData().containsKey(Constants.NOTI_MESSAGE_LIST)) {
                            itemList = (ArrayList<HashMap<String, Object>>) document.getData().get(Constants.NOTI_MESSAGE_LIST);
                        }
                        if (document.getData().containsKey(Constants.NOTI_AUDIO_URL)) {
                            audioUrl = (String) document.getData().get(Constants.NOTI_AUDIO_URL);
                        }

                        StringBuilder customItemList = null;

                        if (itemList != null) {
                            for (int i = 0; i < itemList.size(); i++) {
                                HashMap<String, Object> item = itemList.get(i);
                                name = (String) item.get(Constants.ITEM_NAME);
                                price = (String) item.get(Constants.ITEM_PRICE);
                                qty = (String) item.get("qty");
                                assert qty != null;
                                totalQty = (int) (totalQty + Double.parseDouble(qty));
                                Log.d("4545", "onComplete: " + totalQty);
                                String customString = name + "   ×   " + qty + "\n";
                                customItemList = new StringBuilder();
                                customItemList.append(customString);
                            }
                        }

                        OrderModel orderModelNotification = new OrderModel(customerName, customItemList, orderTime, additionalComment, audioUrl);
                        notificationList.add(orderModelNotification);
                    }
                    if (notificationList.isEmpty()) {
                        binding.recyclerViewNotification.setVisibility(View.GONE);
                        binding.recyclerViewNotificationAlt.setVisibility(View.VISIBLE);
                    } else {
                        Collections.reverse(notificationList);
                        Log.e("11111 Notification List : ", new Gson().toJson(notificationList));
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

    public void deleteOldNotifications() {

        long lastExecutionTimestamp = Preference.getLastExecution(mContext);
        long currentTimestamp = System.currentTimeMillis();
        long timeSinceLastExecution = currentTimestamp - lastExecutionTimestamp;

        final long DAY_IN_MILLIS = 24 * 60 * 60 * 1000; // 24 hours in milliseconds

        if (timeSinceLastExecution >= DAY_IN_MILLIS) {

            // Call the method to delete old notifications
            Toast.makeText(mContext, "Method Called", Toast.LENGTH_SHORT).show();
            Calendar calendar = Calendar.getInstance();
            calendar.add(Calendar.HOUR_OF_DAY, -24);
            Date twentyFourHoursAgo = calendar.getTime();

            db.collection(Constants.COLLECTION_NAME_NOTIFICATION).whereLessThan(Constants.NOTI_TIME_STAMP, twentyFourHoursAgo).get().addOnSuccessListener(querySnapshot -> {
                WriteBatch batch = db.batch();
                for (QueryDocumentSnapshot document : querySnapshot) {
                    // Delete corresponding file from Firebase Storage
                    String downloadUrl = document.getString(Constants.NOTI_AUDIO_URL);
                    if (downloadUrl != null) {
                        StorageReference fileRef = FirebaseStorage.getInstance().getReferenceFromUrl(downloadUrl);
                        fileRef.delete().addOnSuccessListener(aVoid -> {
                            // File deleted successfully
                        }).addOnFailureListener(e -> {
                            // Handle error
                        });
                    }
                    // Delete document from Firestore
                    batch.delete(document.getReference());
                }
                batch.commit().addOnSuccessListener(aVoid -> {
                    // Documents deleted successfully
                }).addOnFailureListener(e -> {
                    // Handle error
                });
            }).addOnFailureListener(e -> {
                // Handle error
            });

            // Save the current timestamp as the last execution timestamp
            Preference.setLastExecution(mContext, currentTimestamp);
        }

    }


}
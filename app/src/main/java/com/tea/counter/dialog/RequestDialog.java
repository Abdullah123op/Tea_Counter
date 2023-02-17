package com.tea.counter.dialog;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatDialogFragment;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.gson.Gson;
import com.tea.counter.adapter.ItemInCustomerAdapter;
import com.tea.counter.adapter.SpinnerAdapter;
import com.tea.counter.databinding.DialogRequestBinding;
import com.tea.counter.model.ItemModel;
import com.tea.counter.model.SignupModel;
import com.tea.counter.services.FcmNotificationsSender;
import com.tea.counter.utils.Constants;
import com.tea.counter.utils.Preference;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;

public class RequestDialog extends AppCompatDialogFragment {

    DialogRequestBinding binding;
    String fcmToken;
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    ArrayList<ItemModel> messageArraylist = new ArrayList<>();
    ArrayList<SignupModel> sellersArrayList = new ArrayList<>();
    int itemPosition = 0;
    private Context mContext;

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        binding = DialogRequestBinding.inflate(LayoutInflater.from(mContext));
        return new AlertDialog.Builder(requireActivity()).setView(binding.getRoot()).create();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        Objects.requireNonNull(getDialog()).getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        getDialog().setCancelable(false);
        retriveSeller();
        initView();

        binding.spinnerSeller.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                itemPosition = position;
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        return binding.getRoot();
    }


    public void retriveSeller() {
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
                        //fcmToken = signupModel.getFcmToken();

                        //there is arraylist in the arrayList which type is hashmap
                        signupModel.setMyArrayList((ArrayList<HashMap<String, Object>>) document.getData().get(Constants.ITEM_ARRAY_LIST));
                        sellersArrayList.add(signupModel);
                    }

                    SpinnerAdapter spinnerAdapter = new SpinnerAdapter(mContext, sellersArrayList);
                    binding.spinnerSeller.setAdapter(spinnerAdapter);
                    binding.spinnerSeller.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                        @Override
                        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                            fcmToken = (sellersArrayList.get(position).getFcmToken());
                            Log.e("11111 : ", new Gson().toJson(sellersArrayList.get(position).getUserName()));

                            ArrayList<ItemModel> myArrayList = new ArrayList<>();
                            if (sellersArrayList.get(position).getMyArrayList() != null) {
                                ArrayList<HashMap<String, Object>> list = sellersArrayList.get(position).getMyArrayList();

                                for (int i = 0; i < list.size(); i++) {
                                    HashMap<String, Object> itemList = list.get(i);
                                    int itemId = Integer.parseInt(String.valueOf(itemList.get(Constants.ITEM_ID)));
                                    String itemName = (String) itemList.get(Constants.ITEM_NAME);
                                    String itemPrice = (String) itemList.get(Constants.ITEM_PRICE);

                                    ItemModel itemModel = new ItemModel(itemId, itemName, itemPrice);
                                    myArrayList.add(itemModel);
                                    for (ItemModel item2 : myArrayList) {
                                        Log.d("ArrayList Contents", "Id: " + item2.getId() + " Name: " + item2.getItemName() + " Price: " + item2.getPrice());
                                    }
                                }
                            } else {
                                Toast.makeText(mContext, "This Seller Has Not Any Item", Toast.LENGTH_SHORT).show();
                            }


                            ItemInCustomerAdapter adapter = new ItemInCustomerAdapter(myArrayList, new ItemInCustomerAdapter.ItemClick() {

                                @Override
                                public void onClick(ArrayList<ItemModel> dataList) {
                                    Log.e("11111 : ", new Gson().toJson(dataList));
                                    messageArraylist.clear();
                                    for (int i = 0; i < dataList.size(); i++) {
                                        if (dataList.get(i).getClick()) {
                                            messageArraylist.add(dataList.get(i));
                                        }
                                    }
                                }
                            });
                            binding.recyclerViewCustomerRequest.setAdapter(adapter);
                        }

                        @Override
                        public void onNothingSelected(AdapterView<?> parent) {
                        }
                    });
                }
                if (task.getResult().isEmpty()) {
                    Toast.makeText(mContext, "There is no Seller In Database", Toast.LENGTH_SHORT).show();
                    dismiss();
                } else {
                    Log.d("ERR", "Error getting documents: ", task.getException());
                }
            }
        });
    }

    private void initView() {
        binding.btnRequest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                binding.btnRequest.setEnabled(false); // disable the button
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        binding.btnRequest.setEnabled(true); // enable the button after 1 second
                    }
                }, 2000);
                if (messageArraylist.isEmpty()) {
                    Toast.makeText(mContext, "Please select at least one item.", Toast.LENGTH_SHORT).show();
                    return;
                }

                boolean atLeastOneItemValid = false;
                for (ItemModel item : messageArraylist) {
                    if (item.getClick() && item.getQty().equals("0") || item.getQty().equals("00") || item.getQty().equals("000") || item.getQty().equals("0000")) {
                        atLeastOneItemValid = true;
                        break;
                    }
                }
                if (atLeastOneItemValid) {
                    Toast.makeText(mContext, "Please set the quantity.", Toast.LENGTH_SHORT).show();
                } else {
                    binding.progressBarRequest.setVisibility(View.VISIBLE);
                    binding.btnRequest.setVisibility(View.GONE);
                    binding.btnCancelInRequestDialog.setVisibility(View.GONE);

                    StringBuilder messageBuilder = new StringBuilder();
                    for (int i = 0; i < messageArraylist.size(); i++) {
                        messageBuilder.append(messageArraylist.get(i).getItemName());
                        if (i != messageArraylist.size() - 1) {
                            messageBuilder.append(", ");
                        }
                    }
                    String message = messageBuilder.toString().replace(",", " and").replace("[", "").replace("]", "");
                    Log.d("1111 :  ", new Gson().toJson(messageArraylist));

                    Map<String, Object> notifications = new HashMap<>();
                    notifications.put(Constants.NOTI_MESSAGE_LIST, messageArraylist);
                    notifications.put(Constants.NOTI_TIME_STAMP, new Timestamp(System.currentTimeMillis()));
                    notifications.put(Constants.NOTI_CUSTOMER_UID, FirebaseAuth.getInstance().getUid());
                    notifications.put(Constants.NOTI_CUSTOMER_NAME, Preference.getName(mContext));
                    notifications.put(Constants.NOTI_SELLER_UID, sellersArrayList.get(itemPosition).getUid());
                    if (binding.etAdditionalComment.getText().toString().trim().length() > 0) {
                        notifications.put(Constants.NOTI_ADDITIONAL_CMT, binding.etAdditionalComment.getText().toString().trim());
                    }
                    notifications.put(Constants.NOTI_TIME_ORDER, new SimpleDateFormat("hh:mm a", Locale.getDefault()).format(Calendar.getInstance().getTime()));

                    db.collection(Constants.COLLECTION_NAME_NOTIFICATION).add(notifications).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                        @Override
                        public void onSuccess(DocumentReference documentReference) {
                            msgSend(Preference.getName(mContext) + " Wants " + message);
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(mContext, "Something Went Wrong", Toast.LENGTH_SHORT).show();
                            dismiss();
                        }
                    });


                }


            }

        });
        binding.btnCancelInRequestDialog.setOnClickListener(v -> dismiss());
    }

    private void msgSend(String message) {
        FcmNotificationsSender notificationsSender = new FcmNotificationsSender(fcmToken, "New Order", message, Preference.getImgUri(mContext), mContext);
        notificationsSender.SendNotifications();
        Toast.makeText(mContext, "Order Placed", Toast.LENGTH_SHORT).show();
        dismiss();
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        mContext = context;
    }
}


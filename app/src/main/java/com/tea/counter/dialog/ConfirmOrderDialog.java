package com.tea.counter.dialog;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatDialogFragment;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.DialogFragment;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.gson.Gson;
import com.tea.counter.R;
import com.tea.counter.adapter.ConfirmItemAdapter;
import com.tea.counter.adapter.SpinnerAdapter;
import com.tea.counter.databinding.DialogConfirmOrderBinding;
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
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;

public class ConfirmOrderDialog extends BottomSheetDialogFragment {
    DialogConfirmOrderBinding binding;
    int itemPosition = 0;
    ArrayList<SignupModel> customerArrayList = new ArrayList<>();
    ArrayList<ItemModel> myItemList = new ArrayList<>();
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    ArrayList<ItemModel> orderedItemList = new ArrayList<>();

    double totalAmount = 0;
    private Context mContext;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // set custom style for bottom sheet rounded top corners
        setStyle(DialogFragment.STYLE_NORMAL, R.style.DialogStyle);

    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        BottomSheetDialog dialog = (BottomSheetDialog) super.onCreateDialog(savedInstanceState);
        dialog.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface dialogInterface) {
                // Set the navigation bar color when the BottomSheetDialog is shown
                Window window = getDialog().getWindow();
                if (window != null) {
                    window.setNavigationBarColor(ContextCompat.getColor(getContext(), R.color.dialogboxBackGround));
                }
            }
        });
        return dialog;
    }

//    @NonNull
//    @Override
//    public Dialog onCreateDialog(Bundle savedInstanceState) {
//        binding = DialogConfirmOrderBinding.inflate(LayoutInflater.from(mContext));
//        return new AlertDialog.Builder(requireActivity()).setView(binding.getRoot()).create();
//    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = DialogConfirmOrderBinding.inflate(inflater, container, false);
        Objects.requireNonNull(getDialog()).getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        getDialog().setCancelable(false);
        initView();
        return binding.getRoot();
    }

    private void initView() {
        getItemList();
        retriveCustomer();
        btnConfirmClick();

        binding.btnCancelConfirmDialog.setOnClickListener(v -> dismiss());

        binding.spinnerCustomer.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                itemPosition = position;
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    public void retriveCustomer() {
        db.collection(Constants.COLLECTION_NAME).whereEqualTo(Constants.USER_TYPE, "1").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    //this loop will retrieve all document that's User Type is 0
                    //and store it in SignupModel
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
                        customerArrayList.add(signupModel);
                    }
                    SpinnerAdapter spinnerAdapter = new SpinnerAdapter(mContext, customerArrayList);
                    binding.spinnerCustomer.setAdapter(spinnerAdapter);

                    if (task.getResult().isEmpty()) {
                        Toast.makeText(mContext, "No Customer In Database", Toast.LENGTH_SHORT).show();
                        dismiss();
                    }

                } else {
                    Log.d("ERR", "Error getting documents: ", task.getException());
                }
            }
        });
    }

    public void getItemList() {
        DocumentReference documentReference = db.collection(Constants.COLLECTION_NAME).document(Objects.requireNonNull(FirebaseAuth.getInstance().getUid()));
        documentReference.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                if (documentSnapshot.exists() && documentSnapshot.contains(Constants.ITEM_ARRAY_LIST)) {
                    ArrayList<HashMap<String, Object>> list = (ArrayList<HashMap<String, Object>>) documentSnapshot.get(Constants.ITEM_ARRAY_LIST);
                    myItemList.clear();
                    for (int i = 0; i < list.size(); i++) {
                        HashMap<String, Object> item = list.get(i);
                        int id = Integer.parseInt(String.valueOf(item.get(Constants.ITEM_ID)));
                        String name = (String) item.get(Constants.ITEM_NAME);
                        String price = (String) item.get(Constants.ITEM_PRICE);
                        ItemModel itemModel = new ItemModel(id, name, price);
                        myItemList.add(itemModel);
//                        for (ItemModel item2 : myItemList) {
//                            Log.d("ArrayList Contents", "Id: " + item2.getId() + " Name: " + item2.getItemName() + " Price: " + item2.getPrice());
//
                    }
                    ConfirmItemAdapter confirmItemAdapter = new ConfirmItemAdapter(myItemList, new ConfirmItemAdapter.ItemClick() {
                        @Override
                        public void onClick(ArrayList<ItemModel> dataList) {
                            orderedItemList.clear();
                            totalAmount = 0;
                            for (int i = 0; i < dataList.size(); i++) {
                                Log.e("11111 : ", new Gson().toJson(dataList.get(i)));
                                if (dataList.get(i).getClick()) {
                                    orderedItemList.add(dataList.get(i));
                                    totalAmount = totalAmount + Double.parseDouble(dataList.get(i).getTotalPrice());
                                    binding.txtTotalAmount.setText(totalAmount + "");
                                } else {
                                    totalAmount = totalAmount - Double.parseDouble(dataList.get(i).getTotalPrice());
                                    binding.txtTotalAmount.setText(totalAmount + "");
                                }
                            }
                        }
                    });
                    binding.recyclerViewItems.setAdapter(confirmItemAdapter);
                } else {
                    Toast.makeText(mContext, "Add Items In Your Inventory", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    public void btnConfirmClick() {
        binding.btnPlace.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                binding.btnPlace.setEnabled(false); // disable the button
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        binding.btnPlace.setEnabled(true); // enable the button after 1 second
                    }
                }, 2000);

                long currentTime = System.currentTimeMillis();
                Date date = new Date(currentTime);
                Timestamp currentTimestamp = new Timestamp(date.getTime());

                if (orderedItemList.isEmpty()) {
                    Toast.makeText(mContext, "Please select at least one item.", Toast.LENGTH_SHORT).show();
                    return;
                }

                boolean atLeastOneItemValid = false;
                for (ItemModel item : orderedItemList) {
                    if (item.getClick() && item.getQty().equals("") || item.getQty().equals("0") || item.getQty().equals("00") || item.getQty().equals("000") || item.getQty().equals("0000")) {
                        atLeastOneItemValid = true;
                        break;
                    }
                }
                if (atLeastOneItemValid) {
                    Toast.makeText(mContext, "Please set the quantity.", Toast.LENGTH_SHORT).show();
                } else {
                    binding.progressBarConfirmDialog.setVisibility(View.VISIBLE);
                    binding.btnPlace.setVisibility(View.GONE);
                    binding.btnCancelConfirmDialog.setVisibility(View.GONE);

                    String docName = "" + Preference.getName(mContext) + currentTimestamp + customerArrayList.get(itemPosition).getUserName();
                    Map<String, Object> orders = new HashMap<>();
                    orders.put(Constants.ORDER_ITEM_LIST, orderedItemList);
                    orders.put(Constants.TOTAL_AMOUNT, totalAmount);
                    orders.put(Constants.SELLER_NAME_ORDER, Preference.getName(mContext));
                    orders.put(Constants.SELLER_IMAGE_ORDER, Preference.getImgUri(mContext));
                    orders.put(Constants.SELLER_UID_ORDER, FirebaseAuth.getInstance().getUid());
                    orders.put(Constants.TIMESTAMP_ORDER, currentTimestamp);
                    orders.put(Constants.TIME_ORDER, new SimpleDateFormat("hh:mm a", Locale.getDefault()).format(Calendar.getInstance().getTime()));
                    orders.put(Constants.DATE_ORDER, new SimpleDateFormat("dd-MM-yy", Locale.getDefault()).format(Calendar.getInstance().getTime()));
                    orders.put(Constants.CUSTOMER_NAME, customerArrayList.get(itemPosition).getUserName());
                    orders.put(Constants.CUSTOMER_UID, customerArrayList.get(itemPosition).getUid());
                    orders.put(Constants.CUSTOMER_IMAGE, customerArrayList.get(itemPosition).getImageURL());

                    db.collection(Constants.COLLECTION_NAME_ORDERS).document(docName).set(orders).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void unused) {
                            msgSend("Your Order Has Been Confirmed By " + Preference.getName(mContext));
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            dismiss();
                            Toast.makeText(mContext, "Something Went Wrong", Toast.LENGTH_SHORT).show();
                        }

                    });
                }

            }
        });
    }

    private void msgSend(String message) {
        FcmNotificationsSender notificationsSender = new FcmNotificationsSender(customerArrayList.get(itemPosition).getFcmToken(), "New Order", message, Preference.getImgUri(mContext), mContext);
        notificationsSender.SendNotifications();
        Toast.makeText(mContext, "Message Sent", Toast.LENGTH_SHORT).show();
        dismiss();
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        mContext = context;
    }
}


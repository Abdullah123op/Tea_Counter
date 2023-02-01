package com.tea.counter.dialog;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatDialogFragment;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Objects;

public class RequestDialog extends AppCompatDialogFragment {

    DialogRequestBinding binding;
    String fcmToken;
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    String message = "";

    ArrayList<String> messageArraylist = new ArrayList<>();
    private ExampleDialogListener listener;
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
        // msgSend();

        return binding.getRoot();
    }


    public void retriveSeller() {
        db.collection(Constants.COLLECTION_NAME).whereEqualTo(Constants.USER_TYPE, "0").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    ArrayList<SignupModel> arrayList = new ArrayList<>();
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
                        fcmToken = signupModel.getFcmToken();
                        signupModel.setMyArrayList((ArrayList<HashMap<String, Object>>) document.getData().get(Constants.ITEM_ARRAY_LIST));
                        arrayList.add(signupModel);
                    }

                    SpinnerAdapter spinnerAdapter = new SpinnerAdapter(mContext, arrayList);
                    binding.spinnerSeller.setAdapter(spinnerAdapter);
                    binding.spinnerSeller.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                        @Override
                        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                            Log.e("11111 : ", new Gson().toJson(arrayList.get(position).getMyArrayList()));
                            ArrayList<ItemModel> myArrayList = new ArrayList<>();
                            ArrayList<HashMap<String, Object>> list = arrayList.get(position).getMyArrayList();

                            for (int i = 0; i < list.size(); i++) {
                                HashMap<String, Object> item = list.get(i);
                                int itemId = Integer.parseInt(String.valueOf(item.get(Constants.ITEM_ID)));
                                String itemName = (String) item.get(Constants.ITEM_NAME);
                                String itemPrice = (String) item.get(Constants.ITEM_PRICE);

                                ItemModel itemModel = new ItemModel(itemId, itemName, itemPrice);
                                myArrayList.add(itemModel);
                                for (ItemModel item2 : myArrayList) {
                                    Log.d("ArrayList Contents", "Id: " + item2.getId() + " Name: " + item2.getItemName() + " Price: " + item2.getPrice());
                                }
                            }
                            ItemInCustomerAdapter adapter = new ItemInCustomerAdapter(myArrayList, new ItemInCustomerAdapter.ItemClick() {

                                @Override
                                public void onClick(ArrayList<ItemModel> dataList) {
                                    Log.e("11111 : ", new Gson().toJson(dataList));
                                    for (ItemModel dataItem : dataList) {
                                        if (dataItem.getClick()) {
                                            messageArraylist.add(dataItem.getItemName());
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
                Log.d("3333 ", "" + messageArraylist);
                //  msgSend(message);
            }
        });
        binding.btnCancelInRequestDialog.setOnClickListener(v -> dismiss());
    }

    private void msgSend(String message) {
        FcmNotificationsSender notificationsSender = new FcmNotificationsSender(fcmToken, "New Tea Order", message, mContext, getActivity());
        notificationsSender.SendNotifications();
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        mContext = context;
        try {
            listener = (ExampleDialogListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context + "must implement ExampleDialogListener");
        }
    }


    public interface ExampleDialogListener {
        void applyTexts(String mobileNo);
    }

}


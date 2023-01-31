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
import android.widget.ArrayAdapter;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatDialogFragment;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.tea.counter.databinding.DialogRequestBinding;
import com.tea.counter.services.FcmNotificationsSender;
import com.tea.counter.utils.Constants;
import com.tea.counter.utils.Preference;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class RequestDialog extends AppCompatDialogFragment {

    DialogRequestBinding binding;
    String SelcetedSeller = "";
    String fcmToken;
    FirebaseFirestore db = FirebaseFirestore.getInstance();
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
                    List<String> spinnerArray = new ArrayList<>();
                    Map<String, String> fcmTokenMap = new HashMap<>();
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        String name = document.getString(Constants.USER_NAME);
                        String Mobile = document.getString(Constants.MOBILE_NUMBER);
                        String fcmToken = document.getString(Constants.FCM_TOKEN);

                        Log.d("Seller", "" + name + "= " + Mobile + "Token " + fcmToken);

                        spinnerArray.add(document.getString(Constants.USER_NAME));
                        fcmTokenMap.put(name, fcmToken);

                    }
                    ArrayAdapter<String> adapter = new ArrayAdapter<>(requireActivity(), android.R.layout.simple_list_item_1, spinnerArray);
                    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    binding.spinnerSeller.setAdapter(adapter);

                    binding.spinnerSeller.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                        @Override
                        public void onItemSelected(AdapterView<?> adapterView, View view, int position, long id) {
                            Object item = adapterView.getItemAtPosition(position);
                            if (item != null) {
                                SelcetedSeller = adapterView.getItemAtPosition(position).toString();
                                fcmToken = fcmTokenMap.get(SelcetedSeller);
                            }
                        }

                        @Override
                        public void onNothingSelected(AdapterView<?> parent) {
                            //Toast.makeText(mContext, "Not Selected", Toast.LENGTH_SHORT).show();
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
                String selectedTea = "";
                String selectedCoffee = "";
                String selectedBiscuit = "";

                if (binding.btnTea.isChecked()) {
                    selectedTea = binding.btnTea.getText().toString();
                }
                if (binding.btnCoffe.isChecked()) {
                    selectedCoffee = binding.btnCoffe.getText().toString();
                }
                if (binding.btnBiscuit.isChecked()) {
                    selectedBiscuit = binding.btnBiscuit.getText().toString();
                }
                String rMessage = "SellerName = " + SelcetedSeller + "\nRequestedItem = " + selectedTea + selectedCoffee + selectedBiscuit;
                String notifiCationMessage = "" + Preference.getName(mContext) + " Wants " + selectedTea + " and " + selectedCoffee + selectedBiscuit;
                Log.d("Message ", "" + rMessage);
                msgSend(notifiCationMessage);
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


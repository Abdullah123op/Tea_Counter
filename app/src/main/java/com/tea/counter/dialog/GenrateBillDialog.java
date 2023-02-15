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
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatDialogFragment;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.gson.Gson;
import com.tea.counter.adapter.MonthAdapter;
import com.tea.counter.databinding.DialogGenrateBillBinding;
import com.tea.counter.model.MonthModel;
import com.tea.counter.utils.Constants;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;

public class GenrateBillDialog extends AppCompatDialogFragment {
    DialogGenrateBillBinding binding;
    String selectedMonth = "";
    int monthNumber;

    FirebaseFirestore db = FirebaseFirestore.getInstance();

    String customerName = "";
    String sellerName = "";
    String sellerUid = "";
    String customerUid = "";

    String qty;

    double totalAmountSum = 0;
    int totalQty = 0;
    private Context mContext;

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        binding = DialogGenrateBillBinding.inflate(LayoutInflater.from(mContext));
        return new AlertDialog.Builder(requireActivity()).setView(binding.getRoot()).create();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        Objects.requireNonNull(getDialog()).getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        getDialog().setCancelable(false);
        initView();
        return binding.getRoot();

    }

    private void initView() {
        String[] months = new java.text.DateFormatSymbols().getMonths();
        ArrayList<String> monthList = new ArrayList<>(Arrays.asList(months));

        ArrayList<MonthModel> monthModelList = new ArrayList<>();
        for (String month : monthList) {
            MonthModel model = new MonthModel(month);
            monthModelList.add(model);
        }

        MonthAdapter monthAdapter = new MonthAdapter(monthModelList, new MonthAdapter.ItemClick() {
            @Override
            public void onClick(ArrayList<MonthModel> dataList) {
                for (int i = 0; i < dataList.size(); i++) {
                    Log.e("2111 : ", new Gson().toJson(dataList.get(i)));
                    if (dataList.get(i).getClick()) {
                        selectedMonth = dataList.get(i).getMonthName();
                        Log.e("5000 : ", selectedMonth);
                    }
                }
            }
        });
        binding.btnGenrate2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                SimpleDateFormat monthParse = new SimpleDateFormat("MMMM", Locale.ENGLISH);
                SimpleDateFormat monthDisplay = new SimpleDateFormat("MM", Locale.ENGLISH);
                try {
                    if (selectedMonth.equals("")) {
                        Toast.makeText(mContext, "Please Select The Month", Toast.LENGTH_SHORT).show();
                        return;
                    } else {
                        Date date = monthParse.parse(selectedMonth);
                        assert date != null;
                        monthNumber = Integer.parseInt(monthDisplay.format(date)) - 1;

                        Log.e("Month number:  : ", String.valueOf(monthNumber));
                    }
                } catch (ParseException e) {
                    e.printStackTrace();
                }

                Calendar startCalendar = Calendar.getInstance();
                startCalendar.set(Calendar.YEAR, 2023);
                startCalendar.set(Calendar.MONTH, monthNumber);
                startCalendar.set(Calendar.DAY_OF_MONTH, 1);
                startCalendar.set(Calendar.HOUR_OF_DAY, 0);
                startCalendar.set(Calendar.MINUTE, 0);
                startCalendar.set(Calendar.SECOND, 0);
                Timestamp startTimestamp = new Timestamp(startCalendar.getTime());

                Calendar endCalendar = Calendar.getInstance();
                endCalendar.set(Calendar.YEAR, 2023);
                endCalendar.set(Calendar.MONTH, monthNumber);
                endCalendar.set(Calendar.DAY_OF_MONTH, endCalendar.getActualMaximum(Calendar.DAY_OF_MONTH));
                endCalendar.set(Calendar.HOUR_OF_DAY, 23);
                endCalendar.set(Calendar.MINUTE, 59);
                endCalendar.set(Calendar.SECOND, 59);
                Timestamp endTimestamp = new Timestamp(endCalendar.getTime());


                Bundle args = getArguments();
                assert args != null;
                String SelectedCustomerUid = args.getString("customerUid");

                db.collection(Constants.COLLECTION_NAME_ORDERS).whereEqualTo(Constants.CUSTOMER_UID, SelectedCustomerUid).whereEqualTo(Constants.SELLER_UID_ORDER, FirebaseAuth.getInstance().getUid()).whereGreaterThanOrEqualTo(Constants.TIMESTAMP_ORDER, startTimestamp).whereLessThan(Constants.TIMESTAMP_ORDER, endTimestamp).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            totalAmountSum = 0;
                            totalQty = 0;
                            for (QueryDocumentSnapshot document : task.getResult()) {

                                sellerName = (String) document.getData().get(Constants.SELLER_NAME_ORDER);
                                customerName = (String) document.getData().get(Constants.CUSTOMER_NAME);
                                sellerUid = (String) document.getData().get(Constants.SELLER_UID_ORDER);
                                customerUid = (String) document.getData().get(Constants.CUSTOMER_UID);
                                String totalAmount = String.valueOf(document.getData().get(Constants.TOTAL_AMOUNT));

                                totalAmountSum = totalAmountSum + Double.parseDouble(totalAmount);

                                ArrayList<HashMap<String, Object>> itemList = (ArrayList<HashMap<String, Object>>) document.getData().get(Constants.ORDER_ITEM_LIST);
                                Log.e("11111 : ", new Gson().toJson(itemList));

                                for (int i = 0; i < itemList.size(); i++) {
                                    HashMap<String, Object> item = itemList.get(i);
                                    qty = (String) item.get("qty");
                                    totalQty = (int) (totalQty + Double.parseDouble(qty));
                                }
                            }
                            Log.d("4545", "Bill Details: " + totalQty + "  " + totalAmountSum + "  " + customerName + "  " + sellerName + "  " + sellerUid + "  " + customerUid);

                            UploadBIllToDb(monthNumber, totalQty, totalAmountSum, customerName, sellerName, customerUid, sellerUid, false);

                        } else {
                            Log.d("ERR", "Error getting Orders: ", task.getException());
                        }
                    }
                });
            }
        });

        binding.recyclerViewMonths.setAdapter(monthAdapter);
        binding.btnCancelBill.setOnClickListener(v -> dismiss());
    }

    public void UploadBIllToDb(int billMonth, int TotalQty, Double TotalAmountSum, String CustomerName, String SellerName, String CustomerUid, String SellerUid, Boolean isPaid) {


        if (TotalQty <= 0 || TotalAmountSum <= 0 || Objects.equals(CustomerName, "") || Objects.equals(SellerName, "") || Objects.equals(CustomerUid, "") || Objects.equals(SellerUid, "")) {
            Toast.makeText(mContext, "User Didn't Buy Any Item In This Month ", Toast.LENGTH_SHORT).show();
            return;
        }

        Map<String, Object> bills = new HashMap<>();
        bills.put(Constants.BILL_MONTH, billMonth);
        bills.put(Constants.TOTAL_QTY_BILL, TotalQty);
        bills.put(Constants.TOTAL_AMOUNT_SUM_BILL, TotalAmountSum);
        bills.put(Constants.CUSTOMER_NAME_BILL, CustomerName);
        bills.put(Constants.SELLER_NAME_BILL, SellerName);
        bills.put(Constants.CUSTOMER_UID_BILL, CustomerUid + "_" + billMonth);
        bills.put(Constants.CUSTOMER_UID_MAIN_BILL, CustomerUid);
        bills.put(Constants.SELLER_UID_BILL, SellerUid);
        bills.put(Constants.IS_PAID_BILL, isPaid);


        db.collection(Constants.COLLECTION_NAME_BILL).whereEqualTo(Constants.CUSTOMER_UID_BILL, CustomerUid + "_" + billMonth).whereEqualTo(Constants.SELLER_UID_BILL, FirebaseAuth.getInstance().getUid()).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.getResult().isEmpty()) {
                    db.collection(Constants.COLLECTION_NAME_BILL).add(bills).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                        @Override
                        public void onSuccess(DocumentReference documentReference) {
                            Toast.makeText(mContext, "Bill Generated SuccessFully", Toast.LENGTH_SHORT).show();
                            dismiss();
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {

                        }
                    });
                } else if (task.isSuccessful()) {
                    Toast.makeText(mContext, "Your Bill Is Already Generated", Toast.LENGTH_SHORT).show();
                }
            }
        });


    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        mContext = context;
    }

}

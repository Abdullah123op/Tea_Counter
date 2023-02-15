package com.tea.counter.ui.Sellerfragments;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.gson.Gson;
import com.tea.counter.R;
import com.tea.counter.adapter.BillAdapter;
import com.tea.counter.adapter.SpinnerAdapter;
import com.tea.counter.databinding.FragmentSBillBinding;
import com.tea.counter.dialog.GenrateBillDialog;
import com.tea.counter.model.BillModel;
import com.tea.counter.model.SignupModel;
import com.tea.counter.utils.Constants;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;


public class SBillFragment extends Fragment {

    FragmentSBillBinding binding;
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    ArrayList<SignupModel> customerArrayList = new ArrayList<>();
    int itemPosition = 0;
    private Context mContext;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentSBillBinding.inflate(inflater, container, false);
        initView();

        return binding.getRoot();
    }

    public void initView() {
        retrieveCustomer();

        binding.spinnerSellerBill.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                TextView textView = (TextView) view;
                textView.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.baseline_arrow_drop_down_24, 0);
                Log.e("55555 : ", new Gson().toJson(customerArrayList.get(position).getUserName()));
                binding.btnGenerate1.setVisibility(View.VISIBLE);
                itemPosition = position;
                GetBills(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        binding.btnGenerate1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //TODO GENRATE BILL
                GenrateBillDialog genrateBillDialog = new GenrateBillDialog();
                Bundle args = new Bundle();
                args.putString("customerUid", customerArrayList.get(itemPosition).getUid());
                genrateBillDialog.setArguments(args);
                genrateBillDialog.show(getChildFragmentManager(), "months_dialog");
            }
        });

    }

    public void retrieveCustomer() {
        db.collection(Constants.COLLECTION_NAME).whereEqualTo(Constants.USER_TYPE, "1").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
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
                    SpinnerAdapter spinnerAdapter = new SpinnerAdapter(mContext, customerArrayList);
                    binding.spinnerSellerBill.setAdapter(spinnerAdapter);

                    //

                }
                if (task.getResult().isEmpty()) {
                    Toast.makeText(mContext, "There is no Customer In Database", Toast.LENGTH_SHORT).show();
                } else {
                    Log.d("ERR", "Error getting documents: ", task.getException());
                }
            }
        });
    }

    public void GetBills(int position) {

        ArrayList<BillModel> billDetailsList = new ArrayList<>();
        db.collection(Constants.COLLECTION_NAME_BILL).whereEqualTo(Constants.CUSTOMER_UID_MAIN_BILL, customerArrayList.get(position).getUid()).whereEqualTo(Constants.SELLER_UID_BILL, FirebaseAuth.getInstance().getUid()).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {

                    for (QueryDocumentSnapshot document : task.getResult()) {
                        BillModel billModel = new BillModel();
                        int billMonth = Integer.parseInt((String.valueOf(document.getData().get(Constants.BILL_MONTH))));

                        Calendar calendar = Calendar.getInstance();
                        calendar.set(Calendar.MONTH, billMonth);
                        SimpleDateFormat dateFormat = new SimpleDateFormat("MMM-yyyy");
                        String monthName = dateFormat.format(calendar.getTime());

                        billModel.setBillMonth(monthName);
                        billModel.setCustomerName(String.valueOf(document.getData().get(Constants.CUSTOMER_NAME_BILL)));
                        billModel.setSellerName(String.valueOf(document.getData().get(Constants.SELLER_NAME_BILL)));
                        billModel.setTotalAmountToPay(String.valueOf(document.getData().get(Constants.TOTAL_AMOUNT_SUM_BILL)));
                        billModel.setTotalQty(String.valueOf(document.getData().get(Constants.TOTAL_QTY_BILL)));
                        billModel.setPaid((Boolean) document.getData().get(Constants.IS_PAID_BILL));
                        billModel.setFcmToken(customerArrayList.get(position).getFcmToken());
                        billModel.setDocumentName(document.getId());
                        billDetailsList.add(billModel);

                    }


                    if (billDetailsList.isEmpty()) {
                        binding.recyclerViewBill.setVisibility(View.GONE);
                        binding.recyclerViewBillAlt.setVisibility(View.VISIBLE);
                    } else {
                        binding.recyclerViewBill.setVisibility(View.VISIBLE);
                        binding.recyclerViewBillAlt.setVisibility(View.GONE);
                        BillAdapter billAdapter = new BillAdapter(billDetailsList , true);
                        binding.recyclerViewBill.setAdapter(billAdapter);
                    }
                } else {
                    Log.d("ERR", "Error getting documents: ", task.getException());
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
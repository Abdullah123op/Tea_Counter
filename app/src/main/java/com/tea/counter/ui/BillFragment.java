package com.tea.counter.ui;

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
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.skydoves.powerspinner.IconSpinnerAdapter;
import com.skydoves.powerspinner.IconSpinnerItem;
import com.skydoves.powerspinner.OnSpinnerItemSelectedListener;
import com.tea.counter.adapter.BillAdapter;
import com.tea.counter.databinding.FragmentCBillBinding;
import com.tea.counter.dialog.CustomProgressDialog;
import com.tea.counter.dialog.GenrateBillDialog;
import com.tea.counter.model.BillModel;
import com.tea.counter.model.SignupModel;
import com.tea.counter.utils.Constants;
import com.tea.counter.utils.Preference;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Objects;


public class BillFragment extends Fragment {
    FragmentCBillBinding binding;
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    ArrayList<SignupModel> userArrayList = new ArrayList<>();
    List<IconSpinnerItem> iconSpinnerItems = new ArrayList<>();
    int itemPosition;
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
        myUserType = (Preference.getUserType(mContext));   // 0 is seller & 1 is Customer

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentCBillBinding.inflate(inflater, container, false);
        initView();
        return binding.getRoot();
    }

    @Override
    public void onPause() {
        super.onPause();
        binding.powerSpinnerView.dismiss();
    }

    private void initView() {

        //Merged two Fragment Into One all condition are based on these two condition

        if (Objects.equals(myUserType, "1")) {
            selectedUserUid = Constants.SELLER_UID_BILL;
            currentUserUid = Constants.CUSTOMER_UID_MAIN_BILL;
            isSellerSide = false;
            anotherSide = "0";
            binding.powerSpinnerView.setHint("SELECT SELLER");
        } else {
            selectedUserUid = Constants.CUSTOMER_UID_MAIN_BILL;
            currentUserUid = Constants.SELLER_UID_BILL;
            isSellerSide = true;
            anotherSide = "1";
            binding.powerSpinnerView.setHint("SELECT CUSTOMER");
            GenerateBill();
        }
        getUserFromDb();
    }


    public void getUserFromDb() {
        customProgressDialog.show();
        db.collection(Constants.COLLECTION_NAME).whereEqualTo(Constants.USER_TYPE, anotherSide).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
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
                        userArrayList.add(signupModel);
                        iconSpinnerItems.add(new IconSpinnerItem(signupModel.getUserName()));
                    }


                    if (userArrayList.isEmpty()) {
                        binding.recyclerViewBillCusotmer.setVisibility(View.GONE);
                        binding.recyclerViewBillCusotmerAlt.setVisibility(View.VISIBLE);
                    } else {
                        binding.recyclerViewBillCusotmer.setVisibility(View.VISIBLE);
                        binding.recyclerViewBillCusotmerAlt.setVisibility(View.GONE);


                        // for seller list custom spinner
                        IconSpinnerAdapter iconSpinnerAdapter = new IconSpinnerAdapter(binding.powerSpinnerView);
                        binding.powerSpinnerView.setSpinnerAdapter(iconSpinnerAdapter);
                        binding.powerSpinnerView.setItems(iconSpinnerItems);

                        binding.powerSpinnerView.setOnSpinnerItemSelectedListener(new OnSpinnerItemSelectedListener<Object>() {
                            @Override
                            public void onItemSelected(int i, @androidx.annotation.Nullable Object o, int newIndex, Object t1) {
                                Log.e("11111", "NEW ITEMS  Uid : " + userArrayList.get(newIndex).getUid() + " seller Name : " + userArrayList.get(newIndex).getUserName());
                                itemPosition = newIndex;
                                if (Objects.equals(myUserType, "0")) {
                                    binding.btnGenerate1.setVisibility(View.VISIBLE);
                                }
                                GetBills(newIndex);
                            }
                        });

                    }
                    //
                    customProgressDialog.dismiss();
                }
                if (task.getResult().isEmpty()) {
                    Toast.makeText(mContext, "There is no Seller In Database", Toast.LENGTH_SHORT).show();
                    customProgressDialog.dismiss();
                } else {
                    Log.d("ERR22", "Error getting documents: ", task.getException());
                }
            }
        });
    }

    public void GetBills(int position) {

        ArrayList<BillModel> billDetailsList = new ArrayList<>();

        db.collection(Constants.COLLECTION_NAME_BILL).whereEqualTo(selectedUserUid, userArrayList.get(position).getUid()).whereEqualTo(currentUserUid, FirebaseAuth.getInstance().getUid()).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
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
                        billDetailsList.add(billModel);

                    }
                    if (billDetailsList.isEmpty()) {
                        binding.recyclerViewBillCusotmer.setVisibility(View.GONE);
                        binding.recyclerViewBillCusotmerAlt.setVisibility(View.VISIBLE);
                    } else {
                        binding.recyclerViewBillCusotmer.setVisibility(View.VISIBLE);
                        binding.recyclerViewBillCusotmerAlt.setVisibility(View.GONE);

                        BillAdapter billAdapter = new BillAdapter(billDetailsList, isSellerSide);
                        binding.recyclerViewBillCusotmer.setAdapter(billAdapter);
                    }
                    customProgressDialog.dismiss();
                } else {
                    Log.d("ERR", "Error getting documents: ", task.getException());
                    customProgressDialog.dismiss();
                }
            }
        });


    }

    private void GenerateBill() {
        binding.btnGenerate1.setOnClickListener(v -> {
            //TODO GENRATE BILL
            GenrateBillDialog genrateBillDialog = new GenrateBillDialog();
            Bundle args = new Bundle();
            args.putString("customerUid", userArrayList.get(itemPosition).getUid());
            genrateBillDialog.setArguments(args);
            genrateBillDialog.show(getChildFragmentManager(), "months_dialog");
        });
    }

}
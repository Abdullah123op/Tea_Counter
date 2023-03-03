package com.tea.counter.dialog;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.DialogFragment;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.tea.counter.R;
import com.tea.counter.databinding.OtpdialogboxBinding;
import com.tea.counter.model.SignupModel;
import com.tea.counter.ui.CustomerActivity;
import com.tea.counter.ui.SellerActivity;
import com.tea.counter.ui.SignUpActivity;
import com.tea.counter.utils.Constants;
import com.tea.counter.utils.GenericTextWatcher;
import com.tea.counter.utils.Preference;

import java.util.Objects;
import java.util.concurrent.TimeUnit;

public class OtpDialog extends BottomSheetDialogFragment {

    OtpdialogboxBinding binding;
    String mainOTP;
    String phoneNumber;
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    private Context mContext;
    private int resendCount = 0;
    private CountDownTimer resendTimer;


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

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        mContext = context;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = OtpdialogboxBinding.inflate(LayoutInflater.from(mContext));
        Objects.requireNonNull(getDialog()).getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        getDialog().setCancelable(false);
        initView();
        return binding.getRoot();
    }

    private void initView() {
        setResendCount();
        Bundle bundle = getArguments();
        if (bundle != null) {
            phoneNumber = bundle.getString(Constants.BUNDLE_MB_NO);
            mainOTP = bundle.getString(Constants.BUNDLE_MAIN_OTP);
            binding.txtMbNuberDialog.setText(phoneNumber);
//            autoChangeEdittext();
            autoChangeField();

            binding.etC6.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {

                }

                @Override
                public void afterTextChanged(Editable s) {
                    binding.btnSubmitInDialog1.performClick();
                }
            });

            binding.btnSubmitInDialog1.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    binding.btnSubmitInDialog1.setEnabled(false); // disable the button
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            binding.btnSubmitInDialog1.setEnabled(true); // enable the button after 1 second
                        }
                    }, 2000);
                    if (binding.etC1.getText().toString().trim().isEmpty() || binding.etC2.getText().toString().trim().isEmpty() || binding.etC3.getText().toString().trim().isEmpty() || binding.etC4.getText().toString().trim().isEmpty() || binding.etC5.getText().toString().trim().isEmpty() || binding.etC6.getText().toString().trim().isEmpty()) {
                        Toast.makeText(mContext, getString(R.string.otp_empty_error), Toast.LENGTH_SHORT).show();
                        binding.etC1.setBackgroundResource(R.drawable.otp_box_error);
                        binding.etC2.setBackgroundResource(R.drawable.otp_box_error);
                        binding.etC3.setBackgroundResource(R.drawable.otp_box_error);
                        binding.etC4.setBackgroundResource(R.drawable.otp_box_error);
                        binding.etC5.setBackgroundResource(R.drawable.otp_box_error);
                        binding.etC6.setBackgroundResource(R.drawable.otp_box_error);
                    } else {
                        binding.txtResend.setVisibility(View.GONE);
                        binding.resendTimer.setVisibility(View.GONE);
                        resendTimer.cancel();
                        otpVerifyMethod();
                        InputMethodManager imm = (InputMethodManager) mContext.getSystemService(Context.INPUT_METHOD_SERVICE);
                        imm.hideSoftInputFromWindow(requireView().getWindowToken(), 0);
                    }
                }


            });

            binding.txtResend.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    PhoneAuthProvider.getInstance().verifyPhoneNumber("+91 " + phoneNumber,        // Phone number to verify
                            60,                 // Timeout duration
                            TimeUnit.SECONDS,   // Unit of timeout
                            requireActivity(),  // Activity (for callback binding)
                            new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
                                @Override
                                public void onVerificationCompleted(@NonNull PhoneAuthCredential credential) {
                                    // Verification completed successfully
                                }

                                @Override
                                public void onVerificationFailed(FirebaseException e) {
                                    // Verification failed
                                }

                                @Override
                                public void onCodeSent(@NonNull String verificationId, @NonNull PhoneAuthProvider.ForceResendingToken token) {
                                    // Code sent to user's phone number
                                    mainOTP = verificationId;
                                    Toast.makeText(mContext, getString(R.string.Otp_resent), Toast.LENGTH_SHORT).show();
                                    Log.e("onCodeSent", "Code Has been Sent");
                                    setResendCount();
                                }
                            });
                }
            });


            binding.btnCancel.setOnClickListener(v -> {
                dismiss();
                resendTimer.cancel();
            });

        }


    }

    public void setResendCount() {
        resendCount = 60;
        resendCount--;
        resendTimer = new CountDownTimer(60000, 1000) {

            public void onTick(long millisUntilFinished) {
                resendCount--;
                // Update UI to show remaining time
                binding.resendTimer.setVisibility(View.VISIBLE);
                binding.resendTimer.setTextColor(ContextCompat.getColor(mContext, R.color.teal_200));
                binding.txtResend.setVisibility(View.GONE);
                binding.resendTimer.setText(getString(R.string.resend_timer_text, resendCount));
            }

            public void onFinish() {
                // Enable the "resend" button
                // e.g. set a boolean flag to true
                binding.resendTimer.setVisibility(View.GONE);
                binding.txtResend.setVisibility(View.VISIBLE);
            }
        }.start();
    }

    private void otpVerifyMethod() {
        if (mainOTP != null) {
            binding.progressBar.setVisibility(View.VISIBLE);
            binding.btnSubmitInDialog1.setVisibility(View.GONE);
            binding.btnCancel.setVisibility(View.GONE);
            String code = binding.etC1.getText().toString().trim() + binding.etC2.getText().toString().trim() + binding.etC3.getText().toString().trim() + binding.etC4.getText().toString().trim() + binding.etC5.getText().toString().trim() + binding.etC6.getText().toString().trim();

            PhoneAuthCredential credential = PhoneAuthProvider.getCredential(mainOTP, code);
            FirebaseAuth.getInstance().signInWithCredential(credential).addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    binding.progressBar.setVisibility(View.VISIBLE);
                    binding.btnSubmitInDialog1.setVisibility(View.GONE);
                    binding.btnCancel.setVisibility(View.GONE);
                    CheckDatabase();
                } else {
                    setOtpError();

                }
            });
        }

    }

    private void setOtpError() {
        binding.etC1.setBackgroundResource(R.drawable.otp_box_error);
        binding.etC2.setBackgroundResource(R.drawable.otp_box_error);
        binding.etC3.setBackgroundResource(R.drawable.otp_box_error);
        binding.etC4.setBackgroundResource(R.drawable.otp_box_error);
        binding.etC5.setBackgroundResource(R.drawable.otp_box_error);
        binding.etC6.setBackgroundResource(R.drawable.otp_box_error);
        binding.progressBar.setVisibility(View.GONE);
        binding.btnSubmitInDialog1.setVisibility(View.VISIBLE);
        binding.btnCancel.setVisibility(View.VISIBLE);
        Toast.makeText(mContext, getString(R.string.wrong_otp_error), Toast.LENGTH_SHORT).show();
    }

    public void CheckDatabase() {
        db.collection(Constants.COLLECTION_NAME).whereEqualTo(Constants.MOBILE_NUMBER, phoneNumber).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.getResult().isEmpty()) {
                    Log.e("1111 Output : ", "New User");
                    Log.d("else", "Error getting documents: ", task.getException());
                    Intent intent = new Intent(mContext, SignUpActivity.class);
                    intent.putExtra(Constants.BUNDLE_MB_NO, phoneNumber);
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                    mContext.startActivity(intent);
                    dismiss();
                }
                if (task.isSuccessful()) {
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        String documentId = document.getId();
                        Log.d("TAG", "Document ID: " + documentId);
                        retriveDatabase();
                    }
                }

            }
        });
    }

    public void retriveDatabase() {
        SignupModel signupModel = new SignupModel();
        signupModel.setUid(FirebaseAuth.getInstance().getUid());

        db.collection(Constants.COLLECTION_NAME).whereEqualTo(Constants.UID, FirebaseAuth.getInstance().getUid()).get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                for (QueryDocumentSnapshot document : task.getResult()) {
                    String documentId = document.getId();
                    Log.d("TAG", "Document ID: " + documentId);
                    db.collection(Constants.COLLECTION_NAME).document(documentId).get().addOnCompleteListener(task1 -> {
                        if (task1.isSuccessful()) {
                            DocumentSnapshot document1 = task1.getResult();
                            if (document1.exists()) {
                                //document.getData().get("mobileNumber");
                                Log.d("5555", "Document Exists");
                                signupModel.setUid((String) document1.getData().get(Constants.UID));
                                signupModel.setMobileNumber((String) document1.getData().get(Constants.MOBILE_NUMBER));
                                signupModel.setUserType((String) document1.getData().get(Constants.USER_TYPE));
                                signupModel.setUserName((String) document1.getData().get(Constants.USER_NAME));
                                signupModel.setAddress((String) document1.getData().get(Constants.ADDRESS));
                                signupModel.setCity((String) document1.getData().get(Constants.CITY));
                                signupModel.setLatitude((String) document1.getData().get(Constants.LATITUDE));
                                signupModel.setLongitude((String) document1.getData().get(Constants.LONGITUDE));
                                signupModel.setImageURL((String) document1.getData().get(Constants.IMAGE));
                                signupModel.setFcmToken((String) document1.getData().get(Constants.FCM_TOKEN));


                                Preference.setName(mContext, signupModel.getUserName());
                                Preference.setUserType(mContext, signupModel.getUserType());
                                Preference.setMobileNo(mContext, signupModel.getMobileNumber());
                                Preference.setCity(mContext, signupModel.getCity());
                                Preference.setAddress(mContext, signupModel.getAddress());
                                Preference.setImgUri(mContext, signupModel.getImageURL());
                                Preference.setFcmToken(mContext, signupModel.getFcmToken());

                                Log.e("URL", "" + signupModel.getImageURL());
                                Log.e("URL", "" + document1.getData().get(Constants.IMAGE));

                                if (Preference.getUserType(mContext).equals("0")) {
                                    mContext.startActivity(new Intent(mContext, SellerActivity.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK));
                                    Toast.makeText(mContext, "Logged In As Seller", Toast.LENGTH_SHORT).show();
                                    dismiss();
                                } else if (Preference.getUserType(mContext).equals("1")) {
                                    mContext.startActivity(new Intent(mContext, CustomerActivity.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK));
                                    Toast.makeText(mContext, "Logged In As Customer", Toast.LENGTH_SHORT).show();
                                    dismiss();
                                }
                            } else {
                                Toast.makeText(mContext, "Something Went Wrong ", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });

                }
            }
        });
    }


    public void autoChangeField() {
        EditText[] edit = {binding.etC1, binding.etC2, binding.etC3, binding.etC4, binding.etC5, binding.etC6};

        binding.etC1.addTextChangedListener(new GenericTextWatcher(binding.etC1, edit));
        binding.etC2.addTextChangedListener(new GenericTextWatcher(binding.etC2, edit));
        binding.etC3.addTextChangedListener(new GenericTextWatcher(binding.etC3, edit));
        binding.etC4.addTextChangedListener(new GenericTextWatcher(binding.etC4, edit));
        binding.etC5.addTextChangedListener(new GenericTextWatcher(binding.etC5, edit));
        binding.etC6.addTextChangedListener(new GenericTextWatcher(binding.etC6, edit));
    }


}


package com.tea.counter.ui;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import com.google.firebase.FirebaseException;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthOptions;
import com.google.firebase.auth.PhoneAuthProvider;
import com.tea.counter.R;
import com.tea.counter.databinding.ActivityLoginBinding;
import com.tea.counter.dialog.OtpDialog;
import com.tea.counter.utils.Constants;

import java.util.Objects;
import java.util.concurrent.TimeUnit;

public class LoginActivity extends AppCompatActivity {

    ActivityLoginBinding binding;
    FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initView();
    }

    private void initView() {
        binding = DataBindingUtil.setContentView(this, R.layout.activity_login);
        firebaseAuth = FirebaseAuth.getInstance();

        binding.btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                binding.btnSubmit.setEnabled(false); // disable the button
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        binding.btnSubmit.setEnabled(true); // enable the button after 1 second
                    }
                }, 2000);

                if (binding.etMobileNumber.getText().toString().trim().isEmpty()) {
                    binding.etMobileNumber.setError(getString(R.string.empty_mb_number));
                } else if (binding.etMobileNumber.getText().toString().trim().length() != 10) {
                    binding.etMobileNumber.setError(getString(R.string.invalid_mb_number));
                } else {
                    otpSendMethod();
                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(binding.mainLayout.getWindowToken(), 0);
                }
            }
        });
    }

    private void otpSendMethod() {
        binding.progressBar.setVisibility(View.VISIBLE);
        binding.btnSubmit.setVisibility(View.GONE);
        PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallback = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {

            @Override
            public void onVerificationCompleted(@NonNull PhoneAuthCredential credential) {
            }

            @Override
            public void onVerificationFailed(@NonNull FirebaseException e) {
                binding.progressBar.setVisibility(View.GONE);
                binding.btnSubmit.setVisibility(View.VISIBLE);
                Toast.makeText(LoginActivity.this, e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                Log.e("onVerificationFailed: ", "er " + e.getLocalizedMessage());
            }

            @Override
            public void onCodeSent(@NonNull String verificationId, @NonNull PhoneAuthProvider.ForceResendingToken token) {
                binding.progressBar.setVisibility(View.GONE);
                binding.btnSubmit.setVisibility(View.VISIBLE);
                OtpDialog otpDialog = new OtpDialog();
                Bundle bundle = new Bundle();
                bundle.putString(Constants.BUNDLE_MB_NO, binding.etMobileNumber.getText().toString().trim());
                bundle.putString(Constants.BUNDLE_MAIN_OTP, verificationId);
                otpDialog.setArguments(bundle);
                otpDialog.show(getSupportFragmentManager(), "example_dialog");
                Toast.makeText(LoginActivity.this, getString(R.string.otp_sent_msg), Toast.LENGTH_SHORT).show();
            }
        };
        PhoneAuthOptions options = PhoneAuthOptions.newBuilder(firebaseAuth).setPhoneNumber("+91" + Objects.requireNonNull(binding.etMobileNumber.getText()).toString().trim()).setTimeout(60L, TimeUnit.SECONDS).setActivity(LoginActivity.this).setCallbacks(mCallback).build();
        PhoneAuthProvider.verifyPhoneNumber(options);
    }
//    public void openDialog() {
//        OtpDialog otpDialog = new OtpDialog();
//        Bundle bundle = new Bundle();
//        bundle.putString("phoneNumber", binding.etMobileNumber.getText().toString().trim());
//
//        otpDialog.setArguments(bundle);
//        otpDialog.show(getSupportFragmentManager(), "example_dialog");
//    }

}
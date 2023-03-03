package com.tea.counter.ui;

import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.navigation.NavigationBarView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.messaging.FirebaseMessaging;
import com.tea.counter.R;
import com.tea.counter.databinding.ActivitySellerBinding;
import com.tea.counter.dialog.ConfirmOrderDialog;
import com.tea.counter.ui.Customerfragments.OrderFragment;
import com.tea.counter.ui.Sellerfragments.SHomeFragment;
import com.tea.counter.ui.Sellerfragments.SItemsFragment;
import com.tea.counter.ui.Sellerfragments.SProfileFragment;
import com.tea.counter.ui.notification.NotificationFragment;
import com.tea.counter.utils.Constants;
import com.tea.counter.utils.Preference;

import java.util.HashMap;
import java.util.Map;

public class SellerActivity extends AppCompatActivity {
    ActivitySellerBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_seller);
        initView();
    }

    private void initView() {
        Preference.setIsLogin(this);
        replaceFragment(new SHomeFragment());
        updateFcm();
        onClicks();
        iconHighLite();
    }
// Check if there is a new notification that is not read

    public void iconHighLite() {
        boolean newNotificationNotRead = false;
        if (newNotificationNotRead) {

            //  binding.btnNotification.setBackgroundResource(R.drawable.notification_icon_background);
        }

    }


    public void onClicks() {
        binding.sellerNav.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                item.setEnabled(false); // disable the selected item
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        item.setEnabled(true); // enable the selected item after 3 seconds
                    }
                }, 2000);

                if (item.getItemId() == R.id.sellerHome) {

                    replaceFragment(new SHomeFragment());

                }
                if (item.getItemId() == R.id.sellerOrder) {
                    replaceFragment(new OrderFragment());
                }
                if (item.getItemId() == R.id.confirmOrder) {
                    ConfirmOrderDialog confirmOrderDialog = new ConfirmOrderDialog();
                    confirmOrderDialog.show(getSupportFragmentManager(), "Confirm_dialog");
                }
                if (item.getItemId() == R.id.sellerBill) {
                    replaceFragment(new BillFragment());
                }
                if (item.getItemId() == R.id.sellerProfile) {
                    replaceFragment(new SProfileFragment());
                }
                return true;
            }
        });

        binding.btnItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                binding.btnItem.setEnabled(false); // disable the button
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        binding.btnItem.setEnabled(true); // enable the button after 1 second
                    }
                }, 2000);
                replaceFragment(new SItemsFragment());
            }
        });

        binding.btnNotification.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                binding.btnNotification.setEnabled(false); // disable the button
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        binding.btnNotification.setEnabled(true); // enable the button after 1 second
                    }
                }, 2000);
                replaceFragment(new NotificationFragment());
            }
        });
    }

    private void updateFcm() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        String documentName = FirebaseAuth.getInstance().getCurrentUser().getUid();

        FirebaseMessaging.getInstance().getToken().addOnCompleteListener(task -> {
            if (!task.isSuccessful()) {
                Log.e("Token Fail", "Fetching FCM registration token failed", task.getException());
                return;
            }
            String firebaseToken = task.getResult();
            Log.d("Token ", firebaseToken);

            // Retrieve the current token from the database
            db.collection(Constants.COLLECTION_NAME).document(documentName).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    if (task.isSuccessful()) {
                        DocumentSnapshot document = task.getResult();
                        if (document.exists()) {
                            String currentToken = document.getString(Constants.FCM_TOKEN);
                            if (currentToken != null) {
                                // Compare the current token with the firebaseToken
                                if (!currentToken.equals(firebaseToken)) {
                                    // Update the database with the new firebaseToken
                                    Map<String, Object> data = new HashMap<>();
                                    data.put(Constants.FCM_TOKEN, firebaseToken);
                                    db.collection(Constants.COLLECTION_NAME).document(documentName).update(data).addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if (task.isSuccessful()) {
                                                Preference.setFcmToken(SellerActivity.this, firebaseToken);
                                                Log.d("Token", "Token updated in the database");
                                            } else {
                                                Log.e("Token", "Failed to update the token in the database", task.getException());
                                            }
                                        }
                                    });
                                }
                            } else {
                                // If the current token is null, add the firebaseToken to the database
                                Map<String, Object> data = new HashMap<>();
                                data.put(Constants.FCM_TOKEN, firebaseToken);
                                db.collection(Constants.COLLECTION_NAME).document(documentName).update(data).addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if (task.isSuccessful()) {
                                            Preference.setFcmToken(SellerActivity.this, firebaseToken);
                                            Log.d("Token", "Token added to the database");
                                        } else {
                                            Log.e("Token", "Failed to add the token to the database", task.getException());
                                        }
                                    }
                                });
                            }
                        }
                    }
                }
            });

        });

    }

    private void replaceFragment(Fragment fragment) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.sellerFrameLayout, fragment);
        fragmentTransaction.commit();
    }
}
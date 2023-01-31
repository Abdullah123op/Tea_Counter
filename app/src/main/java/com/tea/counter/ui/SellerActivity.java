package com.tea.counter.ui;

import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;

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
import com.tea.counter.dialog.AddItemDialog;
import com.tea.counter.ui.Sellerfragments.SBillFragment;
import com.tea.counter.ui.Sellerfragments.SHomeFragment;
import com.tea.counter.ui.Sellerfragments.SItemsFragment;
import com.tea.counter.ui.Sellerfragments.SOrderFragment;
import com.tea.counter.ui.Sellerfragments.SProfileFragment;
import com.tea.counter.utils.Constants;
import com.tea.counter.utils.Preference;

import java.util.HashMap;
import java.util.Map;

public class SellerActivity extends AppCompatActivity implements AddItemDialog.AddItemListener {
    ActivitySellerBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_seller);
        Preference.setIsLogin(this);
        updateFcm();
        binding.sellerNav.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                if (item.getItemId() == R.id.sellerHome) {
                    replaceFragment(new SHomeFragment());
                }
                if (item.getItemId() == R.id.sellerOrder) {
                    replaceFragment(new SOrderFragment());
                }
                if (item.getItemId() == R.id.sellerItems) {
                    replaceFragment(new SItemsFragment());
                }
                if (item.getItemId() == R.id.sellerBill) {
                    replaceFragment(new SBillFragment());
                }
                if (item.getItemId() == R.id.sellerProfile) {
                    replaceFragment(new SProfileFragment());
                }
                return true;
            }
        });
    }

    private void replaceFragment(Fragment fragment) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.sellerFrameLayout, fragment);
        fragmentTransaction.commit();
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
                        }
                    }
                }
            });
        });

    }


    @Override
    public void onAddItemClick(String itemName, String itemPrice) {

    }
}
package com.tea.counter.ui.Sellerfragments;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.tea.counter.databinding.FragmentSHomeBinding;
import com.tea.counter.model.SignupModel;
import com.tea.counter.utils.Constants;

public class SHomeFragment extends Fragment {
    FragmentSHomeBinding binding;
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    private Context mContext;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentSHomeBinding.inflate(inflater, container, false);
        retriveDatabase();
        return binding.getRoot();
    }

    public void retriveDatabase() {
        SignupModel signupModel = new SignupModel();
        signupModel.setUid(FirebaseAuth.getInstance().getUid());

        db.collection(Constants.COLLECTION_NAME).whereEqualTo(Constants.USER_TYPE, "0").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        Log.d("USERNAME", document.getString(Constants.USER_NAME));
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
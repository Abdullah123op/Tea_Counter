package com.tea.counter.ui.Customerfragments;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.content.res.AppCompatResources;
import androidx.core.content.ContextCompat;
import androidx.core.content.res.ResourcesCompat;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bumptech.glide.Glide;
import com.tea.counter.R;
import com.tea.counter.databinding.FragmentCHomeBinding;
import com.tea.counter.databinding.FragmentCProfileBinding;
import com.tea.counter.dialog.OtpDialog;
import com.tea.counter.ui.HomeActivity;
import com.tea.counter.ui.LoginActivity;
import com.tea.counter.utils.Preference;

import java.util.Objects;


public class CProfileFragment extends Fragment {

    FragmentCProfileBinding binding;
    private Context mContext;

    public CProfileFragment() {

    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentCProfileBinding.inflate(inflater, container, false);
        getDataPref();
        BtnEdit();
        BtnSave();
        Logout();
        return binding.getRoot();

    }

    public void getDataPref() {
        Glide.with(mContext).load(Preference.getImgUri(mContext)).into(binding.imgUserHome);
        binding.etEditName.setText(Preference.getName(mContext));
        binding.etEditNumber.setText(Preference.getMobileNo(mContext));
        binding.etEditCity.setText(Preference.getCity(mContext));
        binding.etEditAddress.setText(Preference.getAddress(mContext));
    }

    public void BtnEdit() {
        binding.btnEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                binding.btnEdit.setVisibility(View.GONE);
                binding.btnSave.setVisibility(View.VISIBLE);

                binding.etEditName.setEnabled(true);
                binding.etEditNumber.setEnabled(true);
                binding.etEditAddress.setEnabled(true);
                binding.etEditCity.setEnabled(true);

            }
        });
    }

    public void BtnSave() {
        binding.btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                binding.btnSave.setVisibility(View.GONE);
                binding.btnEdit.setVisibility(View.VISIBLE);

                binding.etEditName.setEnabled(false);
                binding.etEditNumber.setEnabled(false);
                binding.etEditAddress.setEnabled(false);
                binding.etEditCity.setEnabled(false);
            }
        });
    }

    public void Logout() {
        binding.btnLogoutProfile.setOnClickListener(v -> {
            Preference.clearAllPref(mContext);
            Intent intent = new Intent(getActivity(), LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);

        });
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        mContext = context;

    }
}
package com.tea.counter.ui.Sellerfragments;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.tea.counter.databinding.FragmentSProfileBinding;
import com.tea.counter.ui.LoginActivity;
import com.tea.counter.utils.Preference;

import java.util.Objects;


public class SProfileFragment extends Fragment {

    FragmentSProfileBinding binding;
    private Context mContext;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentSProfileBinding.inflate(inflater, container, false);
        getDataPref();
        BtnEdit();
        BtnSave();
        Logout();
        return binding.getRoot();
    }

    public void getDataPref() {
        Glide.with(mContext).load(Preference.getImgUri(mContext)).into(binding.imgSuserHome);
        binding.etSeditName.setText(Preference.getName(mContext));
        binding.etSeditNumber.setText(Preference.getMobileNo(mContext));
        binding.etSeditCity.setText(Preference.getCity(mContext));
        binding.etSeditAddress.setText(Preference.getAddress(mContext));
    }

    public void BtnEdit() {
        binding.btnEditSeller.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                binding.btnEditSeller.setVisibility(View.GONE);
                binding.btnSaveSeller.setVisibility(View.VISIBLE);

                binding.etSeditName.setEnabled(true);
                binding.etSeditNumber.setEnabled(true);
                binding.etSeditCity.setEnabled(true);
                binding.etSeditAddress.setEnabled(true);

            }
        });
    }

    public void BtnSave() {
        binding.btnSaveSeller.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                binding.btnSaveSeller.setVisibility(View.GONE);
                binding.btnEditSeller.setVisibility(View.VISIBLE);

                binding.etSeditName.setEnabled(false);
                binding.etSeditNumber.setEnabled(false);
                binding.etSeditCity.setEnabled(false);
                binding.etSeditAddress.setEnabled(false);
            }
        });
    }

    public void Logout() {
        binding.btnLogoutSprofile.setOnClickListener(v -> {
            Preference.clearAllPref(Objects.requireNonNull(getContext()));
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
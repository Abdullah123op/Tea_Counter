package com.tea.counter.dialog;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatDialogFragment;

import com.bumptech.glide.Glide;
import com.tea.counter.databinding.DialogImageViewerBinding;

import java.util.Objects;

public class ImageViewerDialog extends AppCompatDialogFragment {

    DialogImageViewerBinding binding;
    private Context mContext;

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        binding = DialogImageViewerBinding.inflate(LayoutInflater.from(mContext));
        return new AlertDialog.Builder(requireActivity()).setView(binding.getRoot()).create();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        Objects.requireNonNull(getDialog()).getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        getDialog().setCancelable(true);
        initView();
        return binding.getRoot();

    }

    private void initView() {
        Bundle args = getArguments();
        assert args != null;
        String imageUri = args.getString("imgUri");
        Glide.with(mContext).load(imageUri).into(binding.imgImageViewer);
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        mContext = context;
    }

}

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

import com.tea.counter.databinding.DialogitemBinding;

import java.util.Objects;

public class AddItemDialog extends AppCompatDialogFragment {

    DialogitemBinding binding;


    private AddItemListener listener;
    private Context mContext;

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        binding = DialogitemBinding.inflate(LayoutInflater.from(mContext));
        return new AlertDialog.Builder(requireActivity()).setView(binding.getRoot()).create();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        Objects.requireNonNull(getDialog()).getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        getDialog().setCancelable(false);
        initView();
        return binding.getRoot();
    }

    private void initView() {


        binding.btnAddItemdilaog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

//                ItemModel itemModel = new ItemModel(myArrayList.size() + 1, binding.etItemName.getText().toString(), binding.etPrice.getText().toString());
//                dbHelper.addContact(itemModel);
//                myArrayList.add(itemModel);
//                itemAdapter.notifyDataSetChanged();
                String ItemName = binding.etItemName.getText().toString();
                String ItemPrice = binding.etPrice.getText().toString();
                listener.onAddItemClick(ItemName, ItemPrice);
                dismiss();
            }
        });
        binding.btnCanceladdItemDilaog.setOnClickListener(v -> dismiss());
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        mContext = context;
        try {
            listener = (AddItemListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context + " must implement AddItemListener");
        }
    }


    public interface AddItemListener {
        void onAddItemClick(String itemName, String itemPrice);
    }
}



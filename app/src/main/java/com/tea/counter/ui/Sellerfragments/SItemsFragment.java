package com.tea.counter.ui.Sellerfragments;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.tea.counter.adapter.ItemAdapter;
import com.tea.counter.databinding.FragmentSItemsBinding;
import com.tea.counter.dialog.AddItemDialog;
import com.tea.counter.model.ItemModel;
import com.tea.counter.utils.MyDBHelper;

import java.util.ArrayList;


public class SItemsFragment extends Fragment implements AddItemDialog.AddItemListener {
    FragmentSItemsBinding binding;
    MyDBHelper dbHelper;
    ItemAdapter itemAdapter;
    ArrayList<ItemModel> myArrayList = new ArrayList<>();
    private Context mContext;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentSItemsBinding.inflate(inflater, container, false);
        initView();
        return binding.getRoot();
    }

    public void initView() {
        binding.btnAddItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentTransaction ft = getChildFragmentManager().beginTransaction();
                AddItemDialog addItemDialog = new AddItemDialog();
                addItemDialog.show(ft, "dialog");
            }
        });
        dbHelper = new MyDBHelper(mContext);

        myArrayList = dbHelper.fetchContact();
        itemAdapter = new ItemAdapter(myArrayList);
        binding.itemRecyclerView.setAdapter(itemAdapter);

        LinearLayoutManager layoutManager = new LinearLayoutManager(mContext);
        layoutManager.setOrientation(RecyclerView.VERTICAL);
        binding.itemRecyclerView.setLayoutManager(layoutManager);
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        mContext = context;

    }

    @Override
    public void onAddItemClick(String itemName, String itemPrice) {
        ItemModel itemModel = new ItemModel(myArrayList.size() + 1, itemName, itemPrice);
        dbHelper.addContact(itemModel);
        myArrayList.add(itemModel);
        itemAdapter.notifyDataSetChanged();
    }
}
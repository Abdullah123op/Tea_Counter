package com.tea.counter.ui.Customerfragments;

import android.app.AlertDialog;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.tea.counter.R;
import com.tea.counter.adapter.HistoryAdapter;
import com.tea.counter.databinding.ActivityCustomerBinding;
import com.tea.counter.databinding.FragmentCHomeBinding;
import com.tea.counter.model.OrderModel;

import java.util.ArrayList;
import java.util.Objects;


public class CHomeFragment extends Fragment {
    FragmentCHomeBinding binding;

    public CHomeFragment() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentCHomeBinding.inflate(inflater, container, false);
//        recyclerView = findViewById(R.id.recyclerView);
//        recyclerView.setLayoutManager(new LinearLayoutManager(Objects.requireNonNull(getContext())));
//        HistoryAdapter historyAdapter = new HistoryAdapter(arr);
//        recyclerView.setAdapter(historyAdapter);
//        recyclerView.addItemDecoration(new DividerItemDecoration(Objects.requireNonNull(getContext()), DividerItemDecoration.VERTICAL));

        ArrayList<OrderModel> customList = new ArrayList<>();
        customList.add(new OrderModel("12:00 PM", "12 cup", "₹ 100"));
        customList.add(new OrderModel("12:00 PM", "12 cup", "₹ 100"));
        customList.add(new OrderModel("12:00 PM", "12 cup", "₹ 100"));
        customList.add(new OrderModel("12:00 PM", "12 cup", "₹ 100"));
        customList.add(new OrderModel("12:00 PM", "12 cup", "₹ 100"));
        customList.add(new OrderModel("12:00 PM", "12 cup", "₹ 100"));
        customList.add(new OrderModel("12:00 PM", "12 cup", "₹ 100"));
        customList.add(new OrderModel("12:00 PM", "12 cup", "₹ 100"));
        customList.add(new OrderModel("12:00 PM", "12 cup", "₹ 100"));
        customList.add(new OrderModel("12:00 PM", "12 cup", "₹ 100"));
        customList.add(new OrderModel("12:00 PM", "12 cup", "₹ 100"));
        customList.add(new OrderModel("12:00 PM", "12 cup", "₹ 100"));
        customList.add(new OrderModel("12:00 PM", "12 cup", "₹ 100"));
        customList.add(new OrderModel("12:00 PM", "12 cup", "₹ 100"));
        customList.add(new OrderModel("12:00 PM", "12 cup", "₹ 100"));
        customList.add(new OrderModel("12:00 PM", "12 cup", "₹ 100"));
        customList.add(new OrderModel("12:00 PM", "12 cup", "₹ 100"));
        customList.add(new OrderModel("12:00 PM", "12 cup", "₹ 100"));
        customList.add(new OrderModel("12:00 PM", "12 cup", "₹ 100"));
        customList.add(new OrderModel("12:00 PM", "12 cup", "₹ 100"));
        customList.add(new OrderModel("12:00 PM", "12 cup", "₹ 100"));
        customList.add(new OrderModel("12:00 PM", "12 cup", "₹ 100"));
        customList.add(new OrderModel("12:00 PM", "12 cup", "₹ 100"));



        binding.recyclereView.setLayoutManager(new LinearLayoutManager(getContext()));
        HistoryAdapter historyAdapter = new HistoryAdapter(customList);
        binding.recyclereView.setAdapter(historyAdapter);

        return binding.getRoot();

    }
}
package com.tea.counter.ui.Sellerfragments;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.tea.counter.adapter.ItemAdapter;
import com.tea.counter.databinding.FragmentSItemsBinding;
import com.tea.counter.model.ItemModel;
import com.tea.counter.utils.Constants;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Objects;


public class SItemsFragment extends Fragment {
    FragmentSItemsBinding binding;

    ArrayList<ItemModel> myArrayList = new ArrayList<>();
    ItemAdapter adapter;

    ItemModel itemModel;
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    private Context mContext;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentSItemsBinding.inflate(inflater, container, false);
        binding.btnAddItemSubmit.setVisibility(View.GONE);
        binding.progressBar.setVisibility(View.VISIBLE);
        initView();
        return binding.getRoot();
    }

    public void initView() {
        getArrayList();
        binding.btnAddItemSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                binding.btnAddItemSubmit.setVisibility(View.GONE);
                binding.progressBar.setVisibility(View.VISIBLE);
                if (!binding.etItemName.getText().toString().isEmpty() && !binding.etPrice.getText().toString().isEmpty()) {
                    addArrayListdb();
                    getArrayList();

                } else {
                    Toast.makeText(mContext, "Enter Detail Of Item", Toast.LENGTH_SHORT).show();
                    binding.btnAddItemSubmit.setVisibility(View.VISIBLE);
                    binding.progressBar.setVisibility(View.GONE);
                }
            }
        });
        LinearLayoutManager layoutManager = new LinearLayoutManager(mContext);
        layoutManager.setOrientation(RecyclerView.VERTICAL);
        binding.itemRecyclerView.setLayoutManager(layoutManager);
    }


    public void addArrayListdb() {
        itemModel = new ItemModel(myArrayList.size() + 1, binding.etItemName.getText().toString(), binding.etPrice.getText().toString());
        myArrayList.add(itemModel);

        for (ItemModel item : myArrayList) {
            Log.d("ArrayList Contents", "Id: " + item.getId() + " Name: " + item.getItemName() + " Price: " + item.getPrice());
        }
        DocumentReference documentReference = db.collection(Constants.COLLECTION_NAME).document(Objects.requireNonNull(FirebaseAuth.getInstance().getUid()));
        documentReference.update(Constants.ITEM_ARRAY_LIST, myArrayList).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Log.d("ONSUCCESS", "DocumentSnapshot successfully updated!");
                binding.btnAddItemSubmit.setVisibility(View.VISIBLE);
                binding.progressBar.setVisibility(View.GONE);
                binding.etItemName.getText().clear();
                binding.etPrice.getText().clear();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.w("OnFailure", "Error updating document", e);
            }
        });
    }

    public void getArrayList() {
        DocumentReference documentReference = db.collection(Constants.COLLECTION_NAME).document(Objects.requireNonNull(FirebaseAuth.getInstance().getUid()));
        documentReference.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                if (documentSnapshot.exists() && documentSnapshot.contains(Constants.ITEM_ARRAY_LIST)) {
                    ArrayList<HashMap<String, Object>> list = (ArrayList<HashMap<String, Object>>) documentSnapshot.get(Constants.ITEM_ARRAY_LIST);
                    myArrayList.clear();
                    for (int i = 0; i < list.size(); i++) {
                        HashMap<String, Object> item = list.get(i);
                        int id = Integer.parseInt(String.valueOf(item.get(Constants.ITEM_ID)));
                        String name = (String) item.get(Constants.ITEM_NAME);
                        String price = (String) item.get(Constants.ITEM_PRICE);

                        ItemModel itemModel = new ItemModel(id, name, price);
                        myArrayList.add(itemModel);
                        for (ItemModel item2 : myArrayList) {
                            Log.d("ArrayList Contents", "Id: " + item2.getId() + " Name: " + item2.getItemName() + " Price: " + item2.getPrice());
                        }

                    }
                    adapter = new ItemAdapter(myArrayList, new ItemAdapter.ItemClick() {
                        @Override
                        public void onClick(int position) {

                            AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                            builder.setMessage("Are you sure you want to delete this item?").setCancelable(false).setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    deleteArrayListdb(position);
                                }
                            }).setNegativeButton("No", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    dialog.cancel();
                                }
                            });
                            AlertDialog alert = builder.create();
                            alert.show();
                        }
                    });
                    binding.itemRecyclerView.setAdapter(adapter);
                    binding.btnAddItemSubmit.setVisibility(View.VISIBLE);
                    binding.progressBar.setVisibility(View.GONE);
                } else {
                    binding.btnAddItemSubmit.setVisibility(View.VISIBLE);
                    binding.progressBar.setVisibility(View.GONE);
                }

            }
        });
    }

    public void deleteArrayListdb(int position) {

        myArrayList.remove(position);
        adapter.notifyDataSetChanged();
        DocumentReference documentReference = db.collection(Constants.COLLECTION_NAME).document(Objects.requireNonNull(FirebaseAuth.getInstance().getUid()));
        documentReference.update(Constants.ITEM_ARRAY_LIST, myArrayList).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Log.d("ONSUCCESS", "DocumentSnapshot successfully updated!");
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.w("OnFailure", "Error updating document", e);
            }
        });
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        mContext = context;
    }
}
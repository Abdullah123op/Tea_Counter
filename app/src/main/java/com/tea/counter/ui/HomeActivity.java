package com.tea.counter.ui;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.tea.counter.R;
import com.tea.counter.databinding.ActivityHomeBinding;
import com.tea.counter.model.SignupModel;
import com.tea.counter.utils.Constants;
import com.tea.counter.utils.Preference;

import java.io.File;
import java.io.IOException;

public class HomeActivity extends AppCompatActivity {
    ActivityHomeBinding binding;
    SignupModel signupModel = new SignupModel();
    FirebaseFirestore db = FirebaseFirestore.getInstance();

    StorageReference storageReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_home);
        Preference.setIsLogin(this);

        loadImg();
        getData();

        binding.btnLogOut.setOnClickListener(v -> {
            Preference.clearAllPref(HomeActivity.this);
            startActivity(new Intent(HomeActivity.this, LoginActivity.class));
            finish();
        });

    }


    public void printLog() {
        //   signupModel = new signupModel();
        String Database = "\n MobileNumber :- " + signupModel.getMobileNumber() +
                "\n UserType :-" + signupModel.getUserType() +
                "\n UserName :-" + signupModel.getUserName() +
                "\n city :-" + signupModel.getCity() +
                "\n address :-" + signupModel.getAddress() +
                "\n Latitude :-" + signupModel.getLatitude() +
                "\n Longitude :-" + signupModel.getLongitude() +
                "\n UID :-" + signupModel.getUid();

        Log.e("1001", "" + Database);
    }

    public void getData() {
        signupModel.setUid(FirebaseAuth.getInstance().getUid());

        db.collection(Constants.COLLECTION_NAME).whereEqualTo(Constants.UID, FirebaseAuth.getInstance().getUid())
                .get().addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            String documentId = document.getId();
                            Log.d("TAG", "Document ID: " + documentId);
                            db.collection(Constants.COLLECTION_NAME).document(documentId)
                                    .get().addOnCompleteListener(task1 -> {
                                        if (task1.isSuccessful()) {
                                            DocumentSnapshot document1 = task1.getResult();
                                            if (document1.exists()) {
                                                //document.getData().get("mobileNumber");
                                                Log.d("5555", "Document Exists");
                                                signupModel.setUid((String) document1.getData().get(Constants.UID));
                                                signupModel.setMobileNumber((String) document1.getData().get(Constants.MOBILE_NUMBER));
                                                signupModel.setUserType((String) document1.getData().get(Constants.USER_TYPE));
                                                signupModel.setUserName((String) document1.getData().get(Constants.USER_NAME));
                                                signupModel.setAddress((String) document1.getData().get(Constants.ADDRESS));
                                                signupModel.setCity((String) document1.getData().get(Constants.CITY));
                                                signupModel.setLatitude((String) document1.getData().get(Constants.LATITUDE));
                                                signupModel.setLongitude((String) document1.getData().get(Constants.LONGITUDE));
                                                printLog();
                                                setDataToView();
                                            } else {
                                                Toast.makeText(HomeActivity.this, "Something Went Wrong ", Toast.LENGTH_SHORT).show();
                                            }
                                        }
                                    });

                        }
                    }
                });
    }

    private void setDataToView() {
        binding.txtUsernameHome.setText(signupModel.getUserName());
        binding.txtMobileTitleHome.setText(signupModel.getMobileNumber());
        binding.txtUserTypeHome.setText(signupModel.getUserType());
        binding.txtAddress.setText(signupModel.getAddress());
    }

    public void loadImg() {
        storageReference = FirebaseStorage.getInstance().getReference("profileImg/" + FirebaseAuth.getInstance().getUid() + ".jpg");

        try {
            File localFile = File.createTempFile("tempfile", "img");
            storageReference.getFile(localFile).addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                    Bitmap bitmap = BitmapFactory.decodeFile(localFile.getAbsolutePath());
                    binding.imgUserHome.setImageBitmap(bitmap);

                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(HomeActivity.this, "Failed To load Image", Toast.LENGTH_SHORT).show();
                }
            });
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }

}
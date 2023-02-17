package com.tea.counter.ui.Customerfragments;

import static android.app.Activity.RESULT_OK;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.WriteBatch;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.tea.counter.R;
import com.tea.counter.databinding.FragmentCProfileBinding;
import com.tea.counter.dialog.CustomProgressDialog;
import com.tea.counter.dialog.ImageViewerDialog;
import com.tea.counter.ui.LoginActivity;
import com.tea.counter.utils.Constants;
import com.tea.counter.utils.Preference;

import org.checkerframework.checker.nullness.qual.Nullable;

import java.io.ByteArrayOutputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;


public class CProfileFragment extends Fragment {

    private final int GALLERY_REQ_CODE = 1000;
    private final int CAMARA_REQ_CODE = 2000;
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    FirebaseStorage storage = FirebaseStorage.getInstance();
    FragmentCProfileBinding binding;
    Uri imageUri = null;
    CustomProgressDialog progressDialog;
    private Context mContext;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        mContext = context;
        progressDialog = new CustomProgressDialog(mContext);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentCProfileBinding.inflate(inflater, container, false);
        getDataPref();
        initView();

        return binding.getRoot();

    }

    private void initView() {
        BtnEdit();
        BtnSave();
        Logout();

        binding.imgUserHome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (binding.btnEdit.getVisibility() == View.VISIBLE) {
                    ImageViewerDialog imageViewerDialog = new ImageViewerDialog();
                    Bundle args = new Bundle();
                    args.putString("imgUri", Preference.getImgUri(mContext));
                    imageViewerDialog.setArguments(args);
                    imageViewerDialog.show(getChildFragmentManager(), "image_dialog");
                }
            }
        });
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
                binding.btnEdit.setEnabled(false); // disable the button
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        binding.btnEdit.setEnabled(true); // enable the button after 1 second
                    }
                }, 2000);
                binding.btnEdit.setVisibility(View.GONE);
                binding.btnSave.setVisibility(View.VISIBLE);

                binding.etEditName.setEnabled(true);
                binding.etEditNumber.setEnabled(true);
                binding.etEditAddress.setEnabled(true);
                binding.etEditCity.setEnabled(true);

                InputMethodManager imm = ContextCompat.getSystemService(mContext, InputMethodManager.class);
                binding.etEditName.requestFocus();
                assert imm != null;
                imm.showSoftInput(binding.etEditName, InputMethodManager.SHOW_IMPLICIT);


                if (binding.btnSave.getVisibility() == View.VISIBLE) {
                    binding.imgUserHome.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            androidx.appcompat.app.AlertDialog.Builder builder = new androidx.appcompat.app.AlertDialog.Builder(mContext);
                            builder.setTitle(getString(R.string.img_pick_title)).setItems(new CharSequence[]{getString(R.string.img_pick_camara), getString(R.string.img_pick_gallery)}, new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    switch (which) {
                                        case 0:
                                            // Code for picking an image from the gallery goes here
                                            Intent iCamara = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                                            startActivityForResult(iCamara, CAMARA_REQ_CODE);
                                            break;
                                        case 1:
                                            // Code for taking a photo with the camera goes here
                                            Intent iGallery = new Intent(Intent.ACTION_PICK);
                                            iGallery.setType("image/*");
                                            iGallery.setData(MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                                            startActivityForResult(iGallery, GALLERY_REQ_CODE);
                                            break;
                                    }
                                }
                            });
                            androidx.appcompat.app.AlertDialog alert = builder.create();
                            alert.show();
                        }
                    });
                }


            }
        });
    }

    public void BtnSave() {
        binding.btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                binding.btnSave.setEnabled(false); // disable the button
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        binding.btnSave.setEnabled(true); // enable the button after 1 second
                    }
                }, 2000);
                String usernameInput = binding.etEditName.getText().toString().trim();
                String userCityInput = binding.etEditCity.getText().toString().trim();
                String userAddressInput = binding.etEditAddress.getText().toString().trim();


                if (usernameInput.equals(Preference.getName(mContext)) && userCityInput.equals(Preference.getCity(mContext)) && userAddressInput.equals(Preference.getAddress(mContext)) && imageUri == null) {
                    Toast.makeText(mContext, "Same as database", Toast.LENGTH_SHORT).show();
                    binding.btnSave.setVisibility(View.GONE);
                    binding.btnEdit.setVisibility(View.VISIBLE);

                    binding.etEditName.setEnabled(false);
                    binding.etEditCity.setEnabled(false);
                    binding.etEditAddress.setEnabled(false);

                } else if (!validateInputs()) {
                    return;
                } else if (imageUri != null) {
                    updateImg();
                } else {
                    Toast.makeText(mContext, "Updating..", Toast.LENGTH_SHORT).show();

                    progressDialog.show();

                    FirebaseFirestore db = FirebaseFirestore.getInstance();
                    String documentName = FirebaseAuth.getInstance().getCurrentUser().getUid();
                    Map<String, Object> data = new HashMap<>();
                    data.put(Constants.USER_NAME, usernameInput);

                    data.put(Constants.CITY, userCityInput);
                    data.put(Constants.ADDRESS, userAddressInput);
                    db.collection(Constants.COLLECTION_NAME).document(documentName).update(data).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {

                                Preference.setName(mContext, usernameInput);
                                Preference.setCity(mContext, userCityInput);
                                Preference.setAddress(mContext, userAddressInput);
                                if (imageUri != null) {
                                    updateImg();
                                }
                                updateOrdersTable();

                            } else {
                                Log.e("Token", "Failed to update the Data in the database", task.getException());
                                Toast.makeText(mContext, "Something Went Wrong ", Toast.LENGTH_SHORT).show();
                                progressDialog.dismiss();
                            }
                        }
                    });

                    binding.btnSave.setVisibility(View.GONE);
                    binding.btnEdit.setVisibility(View.VISIBLE);

                    binding.etEditName.setEnabled(false);
                    binding.etEditCity.setEnabled(false);
                    binding.etEditAddress.setEnabled(false);
                }


                binding.btnSave.setVisibility(View.GONE);
                binding.btnEdit.setVisibility(View.VISIBLE);

                binding.etEditName.setEnabled(false);
                binding.etEditNumber.setEnabled(false);
                binding.etEditAddress.setEnabled(false);
                binding.etEditCity.setEnabled(false);

                binding.imgUserHome.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (binding.btnEdit.getVisibility() == View.VISIBLE) {
                            ImageViewerDialog imageViewerDialog = new ImageViewerDialog();
                            Bundle args = new Bundle();
                            args.putString("imgUri", Preference.getImgUri(mContext));
                            imageViewerDialog.setArguments(args);
                            imageViewerDialog.show(getChildFragmentManager(), "image_dialog");
                        }
                    }
                });

            }
        });
    }

    private void updateImg() {
        progressDialog = new CustomProgressDialog(mContext);
        progressDialog.show();

        Toast.makeText(mContext, "Profile Image Is Updating...", Toast.LENGTH_SHORT).show();
        final StorageReference storageReference = storage.getReference().child("profileImage").child(Objects.requireNonNull(FirebaseAuth.getInstance().getUid()));
        storageReference.putFile(imageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                storageReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        db.collection(Constants.COLLECTION_NAME).document(FirebaseAuth.getInstance().getCurrentUser().getUid()).update(Constants.IMAGE, String.valueOf(uri)).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                Toast.makeText(mContext, getString(R.string.update_image_message), Toast.LENGTH_SHORT).show();
                                Preference.setImgUri(mContext, String.valueOf(uri));
                                progressDialog.dismiss();
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Log.w("TAG", "Error adding document", e);
                                Toast.makeText(mContext, getText(R.string.failed_database_msg), Toast.LENGTH_SHORT).show();
                                progressDialog.dismiss();
                            }
                        });
                    }
                });
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == CAMARA_REQ_CODE) {
                Bitmap imageBitmap = (Bitmap) data.getExtras().get("data");
                imageUri = getImageUri(imageBitmap);
                binding.imgUserHome.setImageURI(imageUri);
            }
            if (requestCode == GALLERY_REQ_CODE) {
                assert data != null;
                imageUri = data.getData();
                binding.imgUserHome.setImageURI(imageUri);

            }
        }

    }

    private Uri getImageUri(Bitmap inImage) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        inImage.compress(Bitmap.CompressFormat.JPEG, 20, bytes);
        String path = MediaStore.Images.Media.insertImage(mContext.getContentResolver(), inImage, "Title", null);
        return Uri.parse(path);
    }





    public void updateOrdersTable() {
        db.collection(Constants.COLLECTION_NAME_ORDERS).whereEqualTo(Constants.CUSTOMER_UID, FirebaseAuth.getInstance().getUid()).get().addOnSuccessListener(queryDocumentSnapshots -> {
            WriteBatch batch = db.batch();
            for (QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                DocumentReference documentReference = db.collection(Constants.COLLECTION_NAME_ORDERS).document(documentSnapshot.getId());
                batch.update(documentReference, Constants.CUSTOMER_NAME, Preference.getName(mContext));
            }
            batch.commit().addOnSuccessListener(aVoid -> {
                // update successful
                updateBillsTable();
            }).addOnFailureListener(e -> {
                // update failed
            });
        }).addOnFailureListener(e -> {
            // query failed
            Toast.makeText(mContext, "Something Went Wrong ", Toast.LENGTH_SHORT).show();
            progressDialog.dismiss();
        });

    }

    public void updateBillsTable() {
        db.collection(Constants.COLLECTION_NAME_BILL).whereEqualTo(Constants.CUSTOMER_UID_MAIN_BILL, FirebaseAuth.getInstance().getUid()).get().addOnSuccessListener(queryDocumentSnapshots -> {
            WriteBatch batch = db.batch();
            for (QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                DocumentReference documentReference = db.collection(Constants.COLLECTION_NAME_BILL).document(documentSnapshot.getId());
                batch.update(documentReference, Constants.CUSTOMER_NAME_BILL, Preference.getName(mContext));
            }
            batch.commit().addOnSuccessListener(aVoid -> {
                // update successful
                updateNotificationTable();
            }).addOnFailureListener(e -> {
                // update failed
            });
        }).addOnFailureListener(e -> {
            // query failed
            Toast.makeText(mContext, "Something Went Wrong ", Toast.LENGTH_SHORT).show();
            progressDialog.dismiss();
        });
    }

    public void updateNotificationTable() {
        db.collection(Constants.COLLECTION_NAME_NOTIFICATION).whereEqualTo(Constants.NOTI_CUSTOMER_UID, FirebaseAuth.getInstance().getUid()).get().addOnSuccessListener(queryDocumentSnapshots -> {
            WriteBatch batch = db.batch();
            for (QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                DocumentReference documentReference = db.collection(Constants.COLLECTION_NAME_NOTIFICATION).document(documentSnapshot.getId());
                batch.update(documentReference, Constants.NOTI_CUSTOMER_NAME, Preference.getName(mContext));
            }
            batch.commit().addOnSuccessListener(aVoid -> {
                // update successful
                progressDialog.dismiss();
                Toast.makeText(mContext, "Updated SuccessFully", Toast.LENGTH_SHORT).show();
            }).addOnFailureListener(e -> {
                // update failed
            });
        }).addOnFailureListener(e -> {
            // query failed
            Toast.makeText(mContext, "Something Went Wrong ", Toast.LENGTH_SHORT).show();
            progressDialog.dismiss();
        });
    }

    private boolean validateInputs() {
        String usernameInput = binding.etEditName.getText().toString().trim();
        String userCityInput = binding.etEditCity.getText().toString().trim();
        String userAddressInput = binding.etEditAddress.getText().toString().trim();
        if (usernameInput.isEmpty()) {
            binding.etEditName.setError(getString(R.string.empty_username_error));
            return false;
        } else if (usernameInput.length() > 20) {
            binding.etEditName.setError(getString(R.string.long_username_error));
            return false;
        } else if (userCityInput.isEmpty()) {
            binding.etEditCity.setError(getString(R.string.empty_city_error));
            return false;
        } else if (userAddressInput.isEmpty()) {
            binding.etEditAddress.setError(getString(R.string.empty_address_error));
            return false;
        } else {
            binding.etEditName.setError(null);
            binding.etEditCity.setError(null);
            binding.etEditAddress.setError(null);
        }
        return true;
    }

    public void Logout() {
        binding.btnLogoutProfile.setOnClickListener(v -> {
            AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
            builder.setMessage(Constants.LOGOUT_CONFIRMATION_MESSAGE).setCancelable(false).setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    Preference.clearAllPref(mContext);
                    Intent intent = new Intent(getActivity(), LoginActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                }
            }).setNegativeButton("No", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    dialog.cancel();
                }
            });
            AlertDialog alert = builder.create();
            alert.show();
        });
    }



}
package com.tea.counter.ui.Sellerfragments;

import static android.app.Activity.RESULT_OK;

import android.app.AlertDialog;
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
import com.tea.counter.databinding.FragmentSProfileBinding;
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


public class SProfileFragment extends Fragment {
    private final int GALLERY_REQ_CODE = 1000;
    private final int CAMARA_REQ_CODE = 2000;
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    FirebaseStorage storage = FirebaseStorage.getInstance();
    FragmentSProfileBinding binding;
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
        binding = FragmentSProfileBinding.inflate(inflater, container, false);
        getDataPref();

        initView();
        return binding.getRoot();
    }

    private void initView() {
        btnEditSeller();
        BtnSave();
        Logout();

        binding.imgSuserHome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (binding.btnEditSeller.getVisibility() == View.VISIBLE) {
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
        Glide.with(mContext).load(Preference.getImgUri(mContext)).into(binding.imgSuserHome);
        binding.etSeditName.setText(Preference.getName(mContext));
        binding.etSeditNumber.setText(Preference.getMobileNo(mContext));
        binding.etSeditCity.setText(Preference.getCity(mContext));
        binding.etSeditAddress.setText(Preference.getAddress(mContext));
    }

    public void btnEditSeller() {
        binding.btnEditSeller.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                binding.btnEditSeller.setEnabled(false); // disable the button
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        binding.btnEditSeller.setEnabled(true); // enable the button after 1 second
                    }
                }, 2000);


                binding.btnEditSeller.setVisibility(View.GONE);
                binding.btnSaveSeller.setVisibility(View.VISIBLE);


                binding.etSeditName.setEnabled(true);

                binding.etSeditCity.setEnabled(true);
                binding.etSeditAddress.setEnabled(true);

                InputMethodManager imm = ContextCompat.getSystemService(mContext, InputMethodManager.class);
                binding.etSeditName.requestFocus();
                imm.showSoftInput(binding.etSeditName, InputMethodManager.SHOW_IMPLICIT);


                if (binding.btnSaveSeller.getVisibility() == View.VISIBLE) {
                    binding.imgSuserHome.setOnClickListener(new View.OnClickListener() {
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
        binding.btnSaveSeller.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                binding.btnSaveSeller.setEnabled(false); // disable the button
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        binding.btnSaveSeller.setEnabled(true); // enable the button after 1 second
                    }
                }, 2000);

                String usernameInput = binding.etSeditName.getText().toString().trim();
                String userCityInput = binding.etSeditCity.getText().toString().trim();
                String userAddressInput = binding.etSeditAddress.getText().toString().trim();


                if (usernameInput.equals(Preference.getName(mContext)) && userCityInput.equals(Preference.getCity(mContext)) && userAddressInput.equals(Preference.getAddress(mContext)) && imageUri == null) {
                    Toast.makeText(mContext, "Same as database", Toast.LENGTH_SHORT).show();
                    binding.btnSaveSeller.setVisibility(View.GONE);
                    binding.btnEditSeller.setVisibility(View.VISIBLE);

                    binding.etSeditName.setEnabled(false);
                    binding.etSeditCity.setEnabled(false);
                    binding.etSeditAddress.setEnabled(false);

                } else if (!validateInputs()) {
                    return;
                } else if (imageUri != null) {
                    updateImg();
                } else {
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
                                progressDialog.dismiss();
                            }
                        }
                    });

                    binding.btnSaveSeller.setVisibility(View.GONE);
                    binding.btnEditSeller.setVisibility(View.VISIBLE);

                    binding.etSeditName.setEnabled(false);
                    binding.etSeditCity.setEnabled(false);
                    binding.etSeditAddress.setEnabled(false);
                }


                binding.btnSaveSeller.setVisibility(View.GONE);
                binding.btnEditSeller.setVisibility(View.VISIBLE);

                binding.etSeditName.setEnabled(false);
                binding.etSeditNumber.setEnabled(false);
                binding.etSeditAddress.setEnabled(false);
                binding.etSeditCity.setEnabled(false);

                binding.imgSuserHome.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (binding.btnEditSeller.getVisibility() == View.VISIBLE) {
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


    public void updateOrdersTable() {
        db.collection(Constants.COLLECTION_NAME_ORDERS).whereEqualTo(Constants.SELLER_UID_ORDER, FirebaseAuth.getInstance().getUid()).get().addOnSuccessListener(queryDocumentSnapshots -> {
            WriteBatch batch = db.batch();
            for (QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                DocumentReference documentReference = db.collection(Constants.COLLECTION_NAME_ORDERS).document(documentSnapshot.getId());
                batch.update(documentReference, Constants.SELLER_NAME_ORDER, Preference.getName(mContext));
            }
            batch.commit().addOnSuccessListener(aVoid -> {
                // update successful
                updateBillsTable();
            }).addOnFailureListener(e -> {
                progressDialog.dismiss();
            });
        });

    }

    public void updateBillsTable() {
        db.collection(Constants.COLLECTION_NAME_BILL).whereEqualTo(Constants.SELLER_UID_BILL, FirebaseAuth.getInstance().getUid()).get().addOnSuccessListener(queryDocumentSnapshots -> {
            WriteBatch batch = db.batch();
            for (QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                DocumentReference documentReference = db.collection(Constants.COLLECTION_NAME_BILL).document(documentSnapshot.getId());
                batch.update(documentReference, Constants.SELLER_NAME_BILL, Preference.getName(mContext));
            }
            batch.commit().addOnSuccessListener(aVoid -> {
                // update successful
                Toast.makeText(mContext, "Updated SuccessFully", Toast.LENGTH_SHORT).show();
                progressDialog.dismiss();
            }).addOnFailureListener(e -> {
                progressDialog.dismiss();
            });
        });

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == CAMARA_REQ_CODE) {
                Bitmap imageBitmap = (Bitmap) data.getExtras().get("data");
                imageUri = getImageUri(imageBitmap);
                binding.imgSuserHome.setImageURI(imageUri);
            }
            if (requestCode == GALLERY_REQ_CODE) {
                assert data != null;
                imageUri = data.getData();
                binding.imgSuserHome.setImageURI(imageUri);

            }
        }

    }


    private Uri getImageUri(Bitmap inImage) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        inImage.compress(Bitmap.CompressFormat.JPEG, 20, bytes);
        String path = MediaStore.Images.Media.insertImage(mContext.getContentResolver(), inImage, "Title", null);
        return Uri.parse(path);
    }

    private boolean validateInputs() {
        String usernameInput = binding.etSeditName.getText().toString().trim();

        String userCityInput = binding.etSeditCity.getText().toString().trim();
        String userAddressInput = binding.etSeditAddress.getText().toString().trim();
        if (usernameInput.isEmpty()) {
            binding.etSeditName.setError(getString(R.string.empty_username_error));
            return false;
        } else if (usernameInput.length() > 20) {
            binding.etSeditName.setError(getString(R.string.long_username_error));
            return false;
        } else if (userCityInput.isEmpty()) {
            binding.etSeditCity.setError(getString(R.string.empty_city_error));
            return false;
        } else if (userAddressInput.isEmpty()) {
            binding.etSeditAddress.setError(getString(R.string.empty_address_error));
            return false;
        } else {
            binding.etSeditName.setError(null);
            binding.etSeditCity.setError(null);
            binding.etSeditAddress.setError(null);
        }
        return true;
    }

    public void Logout() {
        binding.btnLogoutSprofile.setOnClickListener(v -> {
            binding.btnLogoutSprofile.setEnabled(false); // disable the button
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    binding.btnLogoutSprofile.setEnabled(true); // enable the button after 1 second
                }
            }, 2000);
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
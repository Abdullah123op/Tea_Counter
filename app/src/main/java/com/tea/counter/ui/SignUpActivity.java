package com.tea.counter.ui;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.location.Address;
import android.location.Geocoder;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.databinding.DataBindingUtil;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.tea.counter.R;
import com.tea.counter.databinding.ActivitySignupBinding;
import com.tea.counter.model.SignupModel;
import com.tea.counter.utils.Constants;
import com.tea.counter.utils.Preference;

import org.checkerframework.checker.nullness.qual.Nullable;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class SignUpActivity extends AppCompatActivity {

    private final static int REQUEST_CODE = 100;
    private final int GALLERY_REQ_CODE = 1000;
    private final int CAMARA_REQ_CODE = 2000;
    ActivitySignupBinding binding;
    FirebaseStorage storage = FirebaseStorage.getInstance();
    StorageReference imagesRef;
    FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
    FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();
    String userType = "";
    FusedLocationProviderClient fusedLocationProviderClient;
    SignupModel signupModel = new SignupModel();

    Uri imageUri = null;
    private String firebaseToken;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_signup);
        initView();
    }

    private void initView() {

        FirebaseMessaging.getInstance().getToken().addOnCompleteListener(task -> {
            if (!task.isSuccessful()) {
                Log.e("Token Fail", "Fetching FCM registration token failed", task.getException());
                return;
            }
            // Get new FCM registration token
            firebaseToken = task.getResult();
            signupModel.setFcmToken(firebaseToken);

            // Log and toast
            Log.d("Token ", firebaseToken);
        });

        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
        StorageReference storageRef = storage.getReference();
        imagesRef = storageRef.child(Constants.STORAGE_REF_NAME);
        signupModel.setUid(firebaseAuth.getUid());
        signupModel.setMobileNumber(getIntent().getStringExtra(Constants.BUNDLE_MB_NO));
        getLastLocation();
        binding.imgUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(SignUpActivity.this);
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
                                iGallery.setData(MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                                startActivityForResult(iGallery, GALLERY_REQ_CODE);
                                break;
                        }
                    }
                });
                AlertDialog alert = builder.create();
                alert.show();
            }

        });

        SignupBtnClickMethod();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == CAMARA_REQ_CODE) {
                Bitmap imageBitmap = (Bitmap) data.getExtras().get("data");
                imageUri = getImageUri(imageBitmap);
                binding.imgUser.setImageURI(imageUri);
            }
            if (requestCode == GALLERY_REQ_CODE) {
                assert data != null;
                imageUri = data.getData();
                binding.imgUser.setImageURI(imageUri);

            }
        }
    }

    public void SignupBtnClickMethod() {
        binding.btnSignUpSubmit.setOnClickListener(v -> {

            if (!validateName() | !validateRdButton() | !validateCity() | !validateAddress() | !validateImage()) {

                return;
            } else {
                binding.progressBar.setVisibility(View.VISIBLE);
                binding.btnSignUpSubmit.setVisibility(View.GONE);
                signupModel.setUserType(userType);
                signupModel.setUserName(binding.etName.getText().toString().trim());
                saveToDataBase();
                printLog();
            }
        });

    }

    private Uri getImageUri(Bitmap inImage) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        inImage.compress(Bitmap.CompressFormat.JPEG, 20, bytes);
        String path = MediaStore.Images.Media.insertImage(getContentResolver(), inImage, "Title", null);
        return Uri.parse(path);
    }


    private boolean validateRdButton() {
        int radioId = binding.radioGroup.getCheckedRadioButtonId();
        if (radioId == -1) {
            Toast.makeText(this, getString(R.string.radio_btn_error), Toast.LENGTH_SHORT).show();
            return false;
        } else {
//            RadioButton radioButton = findViewById(radioId);
//            userTypeuserType = radioButton.getText().toString();
            switch (radioId) {
                case R.id.rdBtnSeller:
                    userType = "0";
                    break;

                case R.id.rdBtnCustomer:
                    userType = "1";
                    break;
            }
            return true;
        }
    }

    private boolean validateName() {
        String usernameInput = binding.etName.getText().toString().trim();

        if (usernameInput.isEmpty()) {
            binding.etName.setError(getString(R.string.empty_username_error));
            return false;
        } else if (usernameInput.length() > 15) {
            binding.etName.setError(getString(R.string.long_username_error));
            return false;
        } else {
            binding.etName.setError(null);
            return true;
        }
    }

    private boolean validateImage() {
        if (imageUri == null) {
            Toast.makeText(this, "Please Select Image", Toast.LENGTH_SHORT).show();
            return false;
        }

        return true;
    }

    private boolean validateCity() {
        String usernameInput = binding.etCity.getText().toString().trim();

        if (usernameInput.isEmpty()) {
            binding.etCity.setError(getString(R.string.empty_city_error));
            return false;
        } else if (usernameInput.length() < 3) {
            binding.etCity.setError(getString(R.string.short_city_error));
            return false;
        } else {
            binding.etCity.setError(null);
            return true;
        }
    }

    private boolean validateAddress() {
        String usernameInput = binding.etCity.getText().toString().trim();

        if (usernameInput.isEmpty()) {
            binding.etAddress.setError(getString(R.string.empty_address_error));
            return false;
        } else if (usernameInput.length() < 3) {
            binding.etAddress.setError(getString(R.string.short_address_error));
            return false;
        } else {
            binding.etAddress.setError(null);
            return true;
        }
    }

    private void getLastLocation() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            fusedLocationProviderClient.getLastLocation().addOnSuccessListener(location -> {
                if (location != null) {
                    Geocoder geocoder = new Geocoder(SignUpActivity.this, Locale.getDefault());
                    List<Address> addresses;
                    try {
                        addresses = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);
                        //signupModel = new signupModel();
                        signupModel.setAddress(addresses.get(0).getAddressLine(0));
                        signupModel.setCity(addresses.get(0).getLocality());
                        signupModel.setLatitude(String.valueOf(addresses.get(0).getLatitude()));
                        signupModel.setLongitude(String.valueOf(addresses.get(0).getLongitude()));

                        binding.etCity.setText(signupModel.getCity());
                        binding.etAddress.setText(signupModel.getAddress());

                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            });
        } else {
            askPermission();
        }
    }

    private void askPermission() {
        ActivityCompat.requestPermissions(SignUpActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_CODE);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                getLastLocation();
            } else {
                onBackPressed();
                Toast.makeText(this, getString(R.string.permission_require_error), Toast.LENGTH_SHORT).show();
            }
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    public void printLog() {
        //   signupModel = new signupModel();
        String Database = "\n MobileNumber :- " + signupModel.getMobileNumber() + "\n UserType :-" + signupModel.getUserType() + "\n UserName :-" + signupModel.getUserName() + "\n city :-" + signupModel.getCity() + "\n address :-" + signupModel.getAddress() + "\n Latitude :-" + signupModel.getLatitude() + "\n Longitude :-" + signupModel.getLongitude() + "\n UID :-" + signupModel.getUid();

        Log.e("User", "" + Database);
    }

    public void saveToDataBase() {
        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        final StorageReference storageReference = storage.getReference().child("profileImage").child(FirebaseAuth.getInstance().getUid());
        storageReference.putFile(imageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                storageReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        signupModel.setImageURL(String.valueOf(uri));
                        Map<String, Object> user = new HashMap<>();


                        user.put(Constants.MOBILE_NUMBER, signupModel.getMobileNumber());
                        user.put(Constants.USER_TYPE, signupModel.getUserType());
                        user.put(Constants.USER_NAME, signupModel.getUserName());
                        user.put(Constants.CITY, signupModel.getCity());
                        user.put(Constants.ADDRESS, signupModel.getAddress());
                        user.put(Constants.LATITUDE, signupModel.getLatitude());
                        user.put(Constants.LONGITUDE, signupModel.getLongitude());
                        user.put(Constants.UID, signupModel.getUid());
                        user.put(Constants.FCM_TOKEN, signupModel.getFcmToken());
                        user.put(Constants.IMAGE, signupModel.getImageURL());

                        // Add a new document with a generated ID
                        firebaseFirestore.collection(Constants.COLLECTION_NAME).document(userId).set(user).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                SetDataPref();
                                Toast.makeText(SignUpActivity.this, getString(R.string.saved_database_success_msg), Toast.LENGTH_SHORT).show();

                                if (Preference.getUserType(SignUpActivity.this).equals("0")) {
                                    startActivity(new Intent(SignUpActivity.this, SellerActivity.class));
                                } else if (Preference.getUserType(SignUpActivity.this).equals("1")) {
                                    startActivity(new Intent(SignUpActivity.this, CustomerActivity.class));
                                }
                                finish();
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Log.w("TAG", "Error adding document", e);
                                Toast.makeText(SignUpActivity.this, getText(R.string.failed_database_msg), Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                });
            }
        });
    }

//    public void saveImg() {
//        BitmapDrawable drawable = (BitmapDrawable) binding.imgUser.getDrawable();
//        Bitmap bitmap = drawable.getBitmap();
//        ByteArrayOutputStream baos = new ByteArrayOutputStream();
//        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
//        byte[] data = baos.toByteArray();
//        StorageReference imageRef = imagesRef.child(FirebaseAuth.getInstance().getUid() + ".jpg");
//
//        UploadTask uploadTask = imageRef.putBytes(data);
//        uploadTask.addOnFailureListener(new OnFailureListener() {
//            @Override
//            public void onFailure(@org.checkerframework.checker.nullness.qual.NonNull Exception exception) {
//                Log.e("Failed", "Task Failed");
//            }
//        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
//            @Override
//            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
//                Log.e("Success", "Saved " + "");
//                Toast.makeText(SignUpActivity.this, getString(R.string.saved_database_success_msg), Toast.LENGTH_SHORT).show();
//                startActivity(new Intent(SignUpActivity.this, HomeActivity.class));
//                finish();
//            }
//        });
//
//    }

    public void SetDataPref() {
        Preference.setName(this, signupModel.getUserName());
        Preference.setUserType(this, signupModel.getUserType());
        Preference.setMobileNo(this, signupModel.getMobileNumber());
        Preference.setCity(this, signupModel.getCity());
        Preference.setAddress(this, signupModel.getAddress());
        Preference.setImgUri(this, signupModel.getImageURL());
        Preference.setFcmToken(this, signupModel.getFcmToken());
    }

}
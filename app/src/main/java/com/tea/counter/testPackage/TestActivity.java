package com.tea.counter.testPackage;

import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.tea.counter.R;
import com.tea.counter.databinding.ActivityTestBinding;
import com.tea.counter.services.FcmNotificationsSender;

public class TestActivity extends AppCompatActivity {
    private final static int REQUEST_CODE = 100;
    private final int GALLERY_REQ_CODE = 1000;
    private final int CAMARA_REQ_CODE = 2000;
    ActivityTestBinding binding;
    FirebaseStorage storage = FirebaseStorage.getInstance();
    StorageReference storageReference;

    //    ImageView img_img;
//    Button btnSaveDatabase;
//    EditText otp_textbox_one, otp_textbox_two, otp_textbox_three, otp_textbox_four;
//    Button verify_otp;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_test);
//        img_img = findViewById(R.id.img_img);
//        btnSaveDatabase = findViewById(R.id.btnSaveDatabase);
//        StorageReference storageRef = storage.getReference();
//        StorageReference imagesRef = storageRef.child("images");
//
//
//        img_img.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                AlertDialog.Builder builder = new AlertDialog.Builder(TestActivity.this);
//                builder.setTitle("Pick an image").setItems(new CharSequence[]{"Camara", "Photos"}, new DialogInterface.OnClickListener() {
//                    public void onClick(DialogInterface dialog, int which) {
//                        switch (which) {
//                            case 0:
//                                // Code for picking an image from the gallery goes here
//                                Intent iCamara = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
//                                startActivityForResult(iCamara, CAMARA_REQ_CODE);
//                                break;
//                            case 1:
//                                // Code for taking a photo with the camera goes here
//                                Intent iGallery = new Intent(Intent.ACTION_PICK);
//                                iGallery.setData(MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
//                                startActivityForResult(iGallery, GALLERY_REQ_CODE);
//                                break;
//                        }
//                    }
//                });
//                AlertDialog alert = builder.create();
//                alert.show();
//            }
//
//        });
//        btnSaveDatabase.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                BitmapDrawable drawable = (BitmapDrawable) img_img.getDrawable();
//                Bitmap bitmap = drawable.getBitmap();
//                Date currentDate = new Date();
//                ByteArrayOutputStream baos = new ByteArrayOutputStream();
//                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
//                byte[] data = baos.toByteArray();
//                StorageReference imageRef = imagesRef.child("image" + currentDate.getTime() + ".jpg");
//
//                UploadTask uploadTask = imageRef.putBytes(data);
//                uploadTask.addOnFailureListener(new OnFailureListener() {
//                    @Override
//                    public void onFailure(@NonNull Exception exception) {
//                        Log.e("Failed" , "Task Failed");
//                    }
//                }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
//                    @Override
//                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
//                        Log.e("Success" , "Saved Success");
//                    }
//                });
//            }
//        });
//
//    }
//
//    @Override
//    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
//        super.onActivityResult(requestCode, resultCode, data);
//        if (resultCode == RESULT_OK) {
//            if (requestCode == CAMARA_REQ_CODE && data != null && data.getExtras() != null) {
//                Bitmap imageBitmap = (Bitmap) data.getExtras().get("data");
//                img_img.setImageBitmap(imageBitmap);
//            }
//            if (requestCode == GALLERY_REQ_CODE) {
//                assert data != null;
//                img_img.setImageURI(data.getData());
//
//            }
//        }
//    }

//        otp_textbox_one = findViewById(R.id.otp_edit_box1);
//        otp_textbox_two = findViewById(R.id.otp_edit_box2);
//        otp_textbox_three = findViewById(R.id.otp_edit_box3);
//        otp_textbox_four = findViewById(R.id.otp_edit_box4);
//        verify_otp = findViewById(R.id.verify_otp_btn);
//
//
//        EditText[] edit = {otp_textbox_one, otp_textbox_two, otp_textbox_three, otp_textbox_four};
//
////        otp_textbox_one.addTextChangedListener(new GenericTextWatcher(otp_textbox_one, edit));
////        otp_textbox_two.addTextChangedListener(new GenericTextWatcher(otp_textbox_two, edit));
////        otp_textbox_three.addTextChangedListener(new GenericTextWatcher(otp_textbox_three, edit));
////        otp_textbox_four.addTextChangedListener(new GenericTextWatcher(otp_textbox_four, edit));
//
//
//        verify_otp.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Toast.makeText(getApplicationContext(), "Login Successfull", Toast.LENGTH_SHORT).show();
//
//            }
//        });
//        binding.btnLoad.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                storageReference = FirebaseStorage.getInstance().getReference("images/EmGElqgFb0bYOgrsyjt3PbSHWXv1.jpg");
//
//                try {
//                    File localFile = File.createTempFile("tempfile", "img");
//                    storageReference.getFile(localFile).addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
//                        @Override
//                        public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
//                            Bitmap bitmap = BitmapFactory.decodeFile(localFile.getAbsolutePath());
//                            binding.imgLoad.setImageBitmap(bitmap);
//
//                        }
//                    }).addOnFailureListener(new OnFailureListener() {
//                        @Override
//                        public void onFailure(@NonNull Exception e) {
//                            Toast.makeText(TestActivity.this, "Failed To load Image", Toast.LENGTH_SHORT).show();
//                        }
//                    });
//                } catch (IOException e) {
//                    throw new RuntimeException(e);
//                }
//            }
//        });
//        String MobileNumber = "971562435";
//        Preference.setMobileNo(this,  MobileNumber);
//
//        binding.pref.setText(Preference.getMobileNo(this));

        binding.button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String fcmToken = "dzCmlmsqTCevM7XZIkL244:APA91bEc-iP0JMziV6UCAY31brPTfmOTSaTWkvawCPus2AEtK-Y0dQS47h2LCvNbwKbs_rFTD_zBUteSjVWeGL1kqA7CTQjLG6s8ylqcI7vwzvPOC4XZPr7ropo23QzG5__L8cqvV3Zj";
                FcmNotificationsSender notificationsSender = new FcmNotificationsSender(fcmToken, "Title", "Message", " ", getApplicationContext());
                notificationsSender.SendNotifications();

            }
        });
    }
}
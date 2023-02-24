package com.tea.counter.dialog;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.SeekBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.devlomi.record_view.OnBasketAnimationEnd;
import com.devlomi.record_view.OnRecordListener;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.google.gson.Gson;
import com.tea.counter.R;
import com.tea.counter.adapter.ItemInCustomerAdapter;
import com.tea.counter.adapter.SpinnerAdapter;
import com.tea.counter.databinding.DialogRequestBinding;
import com.tea.counter.model.ItemModel;
import com.tea.counter.model.SignupModel;
import com.tea.counter.services.FcmNotificationsSender;
import com.tea.counter.utils.Constants;
import com.tea.counter.utils.Preference;

import java.io.File;
import java.io.IOException;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;

public class RequestDialog extends BottomSheetDialogFragment  {
    private final Handler handler = new Handler();
    DialogRequestBinding binding;
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    FirebaseStorage storage = FirebaseStorage.getInstance();
    ArrayList<ItemModel> messageArraylist = new ArrayList<>();
    ArrayList<SignupModel> sellersArrayList = new ArrayList<>();
    MediaPlayer mediaPlayer = new MediaPlayer();
    String fcmToken;
    int itemPosition = 0;
    String audioPath = null;
    private Context mContext;
    private Runnable runnable;
    private MediaRecorder mediaRecorder;
    private boolean isAudioPlaybackCompleted = false;


    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        return new BottomSheetDialog(requireContext(), getTheme());
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = DialogRequestBinding.inflate(inflater, container, false);
        Objects.requireNonNull(getDialog().getWindow()).setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        getDialog().setCancelable(false);

        initView();

        return binding.getRoot();
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        mContext = context;

    }

    @Override
    public void onDismiss(@NonNull DialogInterface dialog) {
        super.onDismiss(dialog);
        if (mediaPlayer != null) {
            mediaPlayer.release();
            mediaPlayer = null;

            if (audioPath != null) {
                File audioFile = new File(audioPath);

                if (audioFile.exists()) {
                    audioFile.delete();
                }
            }
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        if (mediaPlayer != null) {
            mediaPlayer.pause();
            binding.animationView.pauseAnimation();
            binding.btnPlayPause.setImageResource(R.drawable.play);
        }
    }


    private void initView() {
        retrieveSeller();
        btnOnClick();
        setConstraints();
        setUpRecordingView();

        binding.spinnerSeller.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                itemPosition = position;
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    private void setConstraints() {
        if (binding.recyclerViewCustomerRequest.getVisibility() == View.VISIBLE) {

            ConstraintLayout.LayoutParams recordButtonParams = (ConstraintLayout.LayoutParams) binding.recordButton.getLayoutParams();
            recordButtonParams.topToBottom = binding.recyclerViewCustomerRequest.getId();
            binding.recordButton.setLayoutParams(recordButtonParams);

        } else if (binding.audioPlayerView.getVisibility() == View.VISIBLE) {

            ConstraintLayout.LayoutParams recordButtonParams = (ConstraintLayout.LayoutParams) binding.recordButton.getLayoutParams();
            recordButtonParams.topToBottom = binding.audioPlayerView.getId();
            binding.recordButton.setLayoutParams(recordButtonParams);
        }
    }

    public void retrieveSeller() {
        db.collection(Constants.COLLECTION_NAME).whereEqualTo(Constants.USER_TYPE, "0").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {


                    for (QueryDocumentSnapshot document : task.getResult()) {
                        SignupModel signupModel = new SignupModel();
                        signupModel.setUid((String) document.getData().get(Constants.UID));
                        signupModel.setMobileNumber((String) document.getData().get(Constants.MOBILE_NUMBER));
                        signupModel.setUserType((String) document.getData().get(Constants.USER_TYPE));
                        signupModel.setUserName((String) document.getData().get(Constants.USER_NAME));
                        signupModel.setAddress((String) document.getData().get(Constants.ADDRESS));
                        signupModel.setCity((String) document.getData().get(Constants.CITY));
                        signupModel.setLatitude((String) document.getData().get(Constants.LATITUDE));
                        signupModel.setLongitude((String) document.getData().get(Constants.LONGITUDE));
                        signupModel.setImageURL((String) document.getData().get(Constants.IMAGE));
                        signupModel.setFcmToken((String) document.getData().get(Constants.FCM_TOKEN));
                        //fcmToken = signupModel.getFcmToken();

                        //there is arraylist in the arrayList which type is hashmap
                        signupModel.setMyArrayList((ArrayList<HashMap<String, Object>>) document.getData().get(Constants.ITEM_ARRAY_LIST));
                        sellersArrayList.add(signupModel);
                    }

                    SpinnerAdapter spinnerAdapter = new SpinnerAdapter(mContext, sellersArrayList);
                    binding.spinnerSeller.setAdapter(spinnerAdapter);
                    binding.spinnerSeller.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                        @Override
                        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                            fcmToken = (sellersArrayList.get(position).getFcmToken());
                            Log.e("11111 : ", new Gson().toJson(sellersArrayList.get(position).getUserName()));

                            ArrayList<ItemModel> myArrayList = new ArrayList<>();
                            if (sellersArrayList.get(position).getMyArrayList() != null) {
                                ArrayList<HashMap<String, Object>> list = sellersArrayList.get(position).getMyArrayList();

                                for (int i = 0; i < list.size(); i++) {
                                    HashMap<String, Object> itemList = list.get(i);
                                    int itemId = Integer.parseInt(String.valueOf(itemList.get(Constants.ITEM_ID)));
                                    String itemName = (String) itemList.get(Constants.ITEM_NAME);
                                    String itemPrice = (String) itemList.get(Constants.ITEM_PRICE);

                                    ItemModel itemModel = new ItemModel(itemId, itemName, itemPrice);
                                    myArrayList.add(itemModel);
                                    for (ItemModel item2 : myArrayList) {
                                        Log.d("ArrayList Contents", "Id: " + item2.getId() + " Name: " + item2.getItemName() + " Price: " + item2.getPrice());
                                    }
                                }
                            } else {
                                Toast.makeText(mContext, "This Seller Has Not Any Item", Toast.LENGTH_SHORT).show();
                            }


                            ItemInCustomerAdapter adapter = new ItemInCustomerAdapter(myArrayList, new ItemInCustomerAdapter.ItemClick() {

                                @Override
                                public void onClick(ArrayList<ItemModel> dataList) {
                                    Log.e("11111 : ", new Gson().toJson(dataList));
                                    messageArraylist.clear();
                                    for (int i = 0; i < dataList.size(); i++) {
                                        if (dataList.get(i).getClick()) {
                                            messageArraylist.add(dataList.get(i));
                                        }
                                    }
                                }
                            });
                            binding.recyclerViewCustomerRequest.setAdapter(adapter);
                        }

                        @Override
                        public void onNothingSelected(AdapterView<?> parent) {
                        }
                    });
                }
                if (task.getResult().isEmpty()) {
                    Toast.makeText(mContext, "There is no Seller In Database", Toast.LENGTH_SHORT).show();
                    dismiss();
                } else {
                    Log.d("ERR", "Error getting documents: ", task.getException());
                }
            }
        });
    }

    private void btnOnClick() {
        binding.btnRequest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                binding.btnRequest.setEnabled(false); // disable the button
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        binding.btnRequest.setEnabled(true); // enable the button after 1 second
                    }
                }, 2000);
                if (audioPath != null) {
                    audioSendMethod();
                    return;
                }

                if (messageArraylist.isEmpty()) {
                    Toast.makeText(mContext, "Please select at least one item.", Toast.LENGTH_SHORT).show();
                    return;
                }

                boolean atLeastOneItemValid = false;
                for (ItemModel item : messageArraylist) {
                    if (item.getClick() && item.getQty().equals("") || item.getQty().equals("0") || item.getQty().equals("00") || item.getQty().equals("000") || item.getQty().equals("0000")) {
                        atLeastOneItemValid = true;
                        break;
                    }
                }
                if (atLeastOneItemValid) {
                    Toast.makeText(mContext, "Please set the quantity.", Toast.LENGTH_SHORT).show();
                } else {
                    messageSendMethod();
                }

            }

        });
        binding.btnCancelInRequestDialog.setOnClickListener(v -> dismiss());
    }

    private void messageSendMethod() {
        binding.progressBarRequest.setVisibility(View.VISIBLE);
        binding.btnRequest.setVisibility(View.GONE);
        binding.btnCancelInRequestDialog.setVisibility(View.GONE);

        StringBuilder messageBuilder = new StringBuilder();
        for (int i = 0; i < messageArraylist.size(); i++) {
            messageBuilder.append(messageArraylist.get(i).getItemName());
            if (i != messageArraylist.size() - 1) {
                messageBuilder.append(", ");
            }
        }
        String message = messageBuilder.toString().replace(",", " and").replace("[", "").replace("]", "");
        Log.d("1111 :  ", new Gson().toJson(messageArraylist));

        Map<String, Object> notifications = new HashMap<>();
        notifications.put(Constants.NOTI_MESSAGE_LIST, messageArraylist);
        notifications.put(Constants.NOTI_TIME_STAMP, new Timestamp(System.currentTimeMillis()));
        notifications.put(Constants.NOTI_CUSTOMER_UID, FirebaseAuth.getInstance().getUid());
        notifications.put(Constants.NOTI_CUSTOMER_NAME, Preference.getName(mContext));
        notifications.put(Constants.NOTI_SELLER_UID, sellersArrayList.get(itemPosition).getUid());

        if (binding.etAdditionalComment.getText().toString().trim().length() > 0) {
            notifications.put(Constants.NOTI_ADDITIONAL_CMT, binding.etAdditionalComment.getText().toString().trim());
        }
        notifications.put(Constants.NOTI_TIME_ORDER, new SimpleDateFormat("hh:mm a", Locale.getDefault()).format(Calendar.getInstance().getTime()));

        db.collection(Constants.COLLECTION_NAME_NOTIFICATION).add(notifications).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
            @Override
            public void onSuccess(DocumentReference documentReference) {
                msgSend(Preference.getName(mContext) + " Wants " + message);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(mContext, "Something Went Wrong", Toast.LENGTH_SHORT).show();
                dismiss();
            }
        });
    }

    private void audioSendMethod() {

        binding.progressBarRequest.setVisibility(View.VISIBLE);
        binding.btnRequest.setVisibility(View.GONE);
        binding.btnCancelInRequestDialog.setVisibility(View.GONE);

        Uri audioUri = Uri.fromFile(new File(audioPath));

        final StorageReference storageReference = storage.getReference().child("RequestAudio").child("audio-" + FirebaseAuth.getInstance().getUid() + "-" + new Timestamp(System.currentTimeMillis()));
        storageReference.putFile(audioUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                storageReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        Log.w("storageSuccessAudio", " Uploaded In Firebase Storage");
                        Map<String, Object> notifications = new HashMap<>();
                        notifications.put(Constants.NOTI_TIME_STAMP, new Timestamp(System.currentTimeMillis()));
                        notifications.put(Constants.NOTI_CUSTOMER_UID, FirebaseAuth.getInstance().getUid());
                        notifications.put(Constants.NOTI_CUSTOMER_NAME, Preference.getName(mContext));
                        notifications.put(Constants.NOTI_SELLER_UID, sellersArrayList.get(itemPosition).getUid());
                        notifications.put(Constants.NOTI_AUDIO_URL, uri);
                        notifications.put(Constants.NOTI_TIME_ORDER, new SimpleDateFormat("hh:mm a", Locale.getDefault()).format(Calendar.getInstance().getTime()));
                        db.collection(Constants.COLLECTION_NAME_NOTIFICATION).add(notifications).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                            @Override
                            public void onSuccess(DocumentReference documentReference) {
                                Log.w("firebaseSuccessAudio", " Uploaded In Firebase FireStore");
                                msgSend(Preference.getName(mContext) + " Sent Audio ");
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(mContext, "Something Went Wrong", Toast.LENGTH_SHORT).show();
                                dismiss();
                            }
                        });
                    }
                });
            }
        });

    }

    public void setUpRecordingView() {

        binding.recordButton.setRecordView(binding.recordView);
        binding.recordButton.setListenForRecord(true);
        binding.recordView.setSlideToCancelTextColor(Color.parseColor("#322E2E"));
        binding.recordView.setTrashIconColor(Color.parseColor("#FFFFFF"));
        binding.recordView.setSlideToCancelArrowColor(Color.parseColor("#322E2E"));
        binding.recordView.setTimeLimit(30000);
//        binding.recordView.setCounterTimeColor(Color.parseColor("#5737D7"));
//        binding.recordView.setSmallMicColor(Color.parseColor("#2D4CDD"));

        binding.recordView.setOnRecordListener(new OnRecordListener() {
            @Override
            public void onStart() {
                //Start Recording..
                Log.w("2222", "onStart");
                setUpRecording();
                try {
                    mediaRecorder.prepare();
                    mediaRecorder.start();
                } catch (IOException e) {
                    e.printStackTrace();
                }

                binding.etAdditionalComment.setVisibility(View.GONE);
                binding.recordView.setVisibility(View.VISIBLE);
            }

            @Override
            public void onCancel() {
                //On Swipe To Cancel
                Log.w("11111", "onCancel");
                mediaRecorder.reset();
                mediaRecorder.release();
                File file = new File(audioPath);
                if (file.exists()) file.delete();
            }

            @Override
            public void onFinish(long recordTime, boolean limitReached) {
                // Stop recording
                try {
                    mediaRecorder.stop();
                    mediaRecorder.release();
                } catch (Exception e) {
                    e.printStackTrace();
                }

                Toast.makeText(mContext, "Recorded  ", Toast.LENGTH_SHORT).show();

                // Hide the record view
                binding.recordView.setVisibility(View.GONE);
                binding.recyclerViewCustomerRequest.setVisibility(View.GONE);
                binding.recordView.setVisibility(View.GONE);
                binding.recordButton.setVisibility(View.GONE);
                binding.audioPlayerView.setVisibility(View.VISIBLE);


                setConstraints();

                // Create a MediaPlayer instance and set the data source to the recorded audio file

                try {
                    mediaPlayer.setDataSource(audioPath);
                    mediaPlayer.prepare();
                } catch (IOException e) {
                    e.printStackTrace();
                }

                // Start playing the recorded audio
                mediaPlayer.start();
                // Change the icon of the play/pause button to "pause"
                binding.btnPlayPause.setImageResource(R.drawable.pause);

                binding.seekBar.setMax(mediaPlayer.getDuration());
                binding.seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                    @Override
                    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

                    }

                    @Override
                    public void onStartTrackingTouch(SeekBar seekBar) {

                    }

                    @Override
                    public void onStopTrackingTouch(SeekBar seekBar) {
                        mediaPlayer.seekTo(seekBar.getProgress());
                    }
                });

                runnable = new Runnable() {
                    @Override
                    public void run() {
                        if (mediaPlayer != null) {
                            int currentPosition = mediaPlayer.getCurrentPosition();
                            binding.seekBar.setProgress(currentPosition);
                        }
                        handler.postDelayed(this, 10);
                    }
                };
//                threadUpdateSeek.start();

                mediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                    @Override
                    public void onPrepared(MediaPlayer mp) {
                        // Set the maximum value of the seekbar to the duration of the media
                        binding.seekBar.setMax(mp.getDuration());
                        // Start the handler to update the seekbar
                        handler.postDelayed(runnable, 10);
                    }
                });

                mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                    @Override
                    public void onCompletion(MediaPlayer mp) {
                        isAudioPlaybackCompleted = true;
                        binding.btnPlayPause.setImageResource(R.drawable.play);
                        binding.animationView.pauseAnimation();
                        handler.removeCallbacks(runnable);
                    }
                });
            }


            @Override
            public void onLessThanSecond() {
                //When the record time is less than One Second
                Log.w("6666", "onLessThanSecond");
                mediaRecorder.reset();
                mediaRecorder.release();

                File file = new File(audioPath);
                if (file.exists()) {
                    file.delete();
                }
                binding.recordView.setVisibility(View.GONE);
                binding.etAdditionalComment.setVisibility(View.VISIBLE);
            }
        });
        binding.recordView.setOnBasketAnimationEndListener(new OnBasketAnimationEnd() {
            @Override
            public void onAnimationEnd() {
                Log.w("RecordView", "Basket Animation Finished");
                binding.recordView.setVisibility(View.GONE);
                binding.etAdditionalComment.setVisibility(View.VISIBLE);

            }
        });
        playRecording();
    }

    public void playRecording() {
        binding.btnPlayPause.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (audioPath != null) {
                    if (mediaPlayer.isPlaying()) {
                        mediaPlayer.pause();
                        binding.animationView.pauseAnimation();
                        binding.btnPlayPause.setImageResource(R.drawable.play);
                    } else {
                        if (isAudioPlaybackCompleted) {
                            // If the audio playback is completed, reset the MediaPlayer
                            // and set the data source again before starting playback
                            try {
                                mediaPlayer.reset();
                                mediaPlayer.setDataSource(audioPath);
                                mediaPlayer.prepare();
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                            isAudioPlaybackCompleted = false;
                        }
                        mediaPlayer.start();
                        binding.animationView.playAnimation();
                        binding.btnPlayPause.setImageResource(R.drawable.pause);
                    }
                } else {
                    Toast.makeText(mContext, "Null", Toast.LENGTH_SHORT).show();
                }
            }
        });

    }

    public void setUpRecording() {
        mediaRecorder = new MediaRecorder();
        mediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
        mediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AAC);

        File file = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);

        if (!file.exists()) file.mkdirs();
        audioPath = file.getAbsolutePath() + File.separator + "Tea-Counter.mp3";

        mediaRecorder.setOutputFile(audioPath);
    }

    private void msgSend(String message) {
        FcmNotificationsSender notificationsSender = new FcmNotificationsSender(fcmToken, "New Order", message, Preference.getImgUri(mContext), mContext);
        notificationsSender.SendNotifications();
        Toast.makeText(mContext, "Order Placed", Toast.LENGTH_SHORT).show();
        dismiss();
    }

}


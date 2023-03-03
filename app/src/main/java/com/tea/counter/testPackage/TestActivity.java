package com.tea.counter.testPackage;

import android.graphics.Color;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.SeekBar;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import com.devlomi.record_view.OnBasketAnimationEnd;
import com.devlomi.record_view.OnRecordListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.tea.counter.R;
import com.tea.counter.databinding.ActivityTestBinding;

import java.io.File;
import java.io.IOException;

public class TestActivity extends AppCompatActivity {

    ActivityTestBinding binding;
    FirebaseStorage storage = FirebaseStorage.getInstance();
    StorageReference storageReference;
    String audioPath;
    MediaPlayer mediaPlayer = new MediaPlayer();
    private boolean isAudioPlaybackCompleted = false;
    private MediaRecorder mediaRecorder;


//     if (model.isAudio()) {
//        mediaPlayer = new MediaPlayer();
//        holder.textView6.setVisibility(View.GONE);
//        holder.orderItemDetails.setVisibility(View.GONE);
//        holder.seekBarList.setVisibility(View.VISIBLE);
//        holder.btnPlayPauseList.setVisibility(View.VISIBLE);
//
//        holder.seekBarList.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
//            @Override
//            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
//
//            }
//            @Override
//            public void onStartTrackingTouch(SeekBar seekBar) {
//
//            }
//
//            @Override
//            public void onStopTrackingTouch(SeekBar seekBar) {
//                mediaPlayer.seekTo(seekBar.getProgress());
//            }
//        });
//        runnable = new Runnable() {
//            @Override
//            public void run() {
//                if (mediaPlayer != null) {
//                    int currentPosition = mediaPlayer.getCurrentPosition();
//                    holder.seekBarList.setProgress(currentPosition);
//                }
//                handler.postDelayed(this, 10);
//            }
//        };
//
//        mediaPlayer.setOnPreparedListener(mp -> {
//            // Set the maximum value of the seekbar to the duration of the media
//            holder.seekBarList.setMax(mp.getDuration());
//            // Start the handler to update the seekbar
//            handler.postDelayed(runnable, 10);
//        });
//
//        holder.btnPlayPauseList.setOnClickListener(v -> {
//
//            if (mediaPlayer.isPlaying()) {
//                mediaPlayer.pause();
//                holder.btnPlayPauseList.setImageResource(R.drawable.play);
//            } else {
//
//                Log.e("TAG", "onClick: ");
//
//                if (!isPlayedOnce) {
//                    holder.btnPlayPauseList.setVisibility(View.GONE);
//                    holder.btnPlayPauseListAlt.setVisibility(View.VISIBLE);
//
//                    try {
//                        mediaPlayer.setDataSource(model.getAudioUrl());
//                        mediaPlayer.prepare();
//                        isPlayedOnce = true;
//                    } catch (IOException e) {
//                        throw new RuntimeException(e);
//                    }
//                }
//
//                if (isAudioPlaybackCompleted) {
//                    mediaPlayer.reset();
//                    try {
//                        mediaPlayer.setDataSource(model.getAudioUrl());
//                        mediaPlayer.prepare();
//                    } catch (IOException e) {
//                        throw new RuntimeException(e);
//                    }
//                    isAudioPlaybackCompleted = false;
//                }
//
//                holder.btnPlayPauseList.setImageResource(R.drawable.pause);
//                mediaPlayer.start();
//                holder.btnPlayPauseListAlt.setVisibility(View.GONE);
//                holder.btnPlayPauseList.setVisibility(View.VISIBLE);
//            }
//        });
//
//        mediaPlayer.setOnCompletionListener(mp -> {
//            isAudioPlaybackCompleted = true;
//            holder.btnPlayPauseList.setImageResource(R.drawable.play);
//            handler.removeCallbacks(runnable);
//        });
//
//        holder.txtTitleExpandable.setText(" New Voice Order from  " + model.getOrderTitle());
//        holder.txtTimeExpandable.setText(model.getOrderTime());
//
//        holder.layoutExpand.setVisibility(isVisible ? View.VISIBLE : View.GONE);

//        return;
//    }

//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        binding = DataBindingUtil.setContentView(this, R.layout.activity_test);
//
//       // setUpRecordingView();
//    }

//    public void setUpRecordingView() {
//
//
//        binding.recordButton.setRecordView(binding.recordView);
//        binding.recordButton.setListenForRecord(true);
//        binding.recordView.setSlideToCancelTextColor(Color.parseColor("#322E2E"));
//        binding.recordView.setTrashIconColor(Color.parseColor("#FFFFFF"));
//        binding.recordView.setSlideToCancelArrowColor(Color.parseColor("#322E2E"));
////        binding.recordView.setCounterTimeColor(Color.parseColor("#5737D7"));
////        binding.recordView.setSmallMicColor(Color.parseColor("#2D4CDD"));
//
//        binding.recordView.setOnRecordListener(new OnRecordListener() {
//            @Override
//            public void onStart() {
//                //Start Recording..
//                Log.w("2222", "onStart");
//                setUpRecording();
//                try {
//                    mediaRecorder.prepare();
//                    mediaRecorder.start();
//                } catch (IOException e) {
//                    e.printStackTrace();
//                }
//
////                binding.etAdditionalComment.setVisibility(View.GONE);
//                binding.recordView.setVisibility(View.VISIBLE);
//            }
//
//            @Override
//            public void onCancel() {
//                //On Swipe To Cancel
//                Log.w("11111", "onCancel");
//                mediaRecorder.reset();
//                mediaRecorder.release();
//                File file = new File(audioPath);
//                if (file.exists()) file.delete();
//            }
//
//            @Override
//            public void onFinish(long recordTime, boolean limitReached) {
//                // Stop recording
//                try {
//                    mediaRecorder.stop();
//                    mediaRecorder.release();
//                } catch (Exception e) {
//                    e.printStackTrace();
//                }
//
//                // Show the path to the recorded file in a Toast
//                Toast.makeText(TestActivity.this, "Recorded  ",  Toast.LENGTH_SHORT).show();
//
//                // Hide the record view
//                binding.recordView.setVisibility(View.GONE);
//
//                // Create a MediaPlayer instance and set the data source to the recorded audio file
//
//                try {
//                    mediaPlayer.setDataSource(audioPath);
//                    mediaPlayer.prepare();
//                } catch (IOException e) {
//                    e.printStackTrace();
//                }
//
//                // Start playing the recorded audio
//                mediaPlayer.start();
//                // Change the icon of the play/pause button to "pause"
//                binding.btnPlayPause.setImageResource(R.drawable.pause);
//
//                mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
//                    @Override
//                    public void onCompletion(MediaPlayer mp) {
//                        isAudioPlaybackCompleted = true;
//                        binding.btnPlayPause.setImageResource(R.drawable.play);
//                    }
//                });
//            }
//
//
//            @Override
//            public void onLessThanSecond() {
//                //When the record time is less than One Second
//                Log.w("6666", "onLessThanSecond");
//                mediaRecorder.reset();
//                mediaRecorder.release();
//
//                File file = new File(audioPath);
//                if (file.exists()) {
//                    file.delete();
//                }
//                binding.recordView.setVisibility(View.GONE);
//                // binding.etAdditionalComment.setVisibility(View.VISIBLE);
//            }
//        });
//        binding.recordView.setOnBasketAnimationEndListener(new OnBasketAnimationEnd() {
//            @Override
//            public void onAnimationEnd() {
//                Log.w("RecordView", "Basket Animation Finished");
//                binding.recordView.setVisibility(View.GONE);
//                //  binding.etAdditionalComment.setVisibility(View.VISIBLE);
//
//            }
//        });
//
//        playRecording();
//    }
//
//    public void playRecording() {
//        binding.btnPlayPause.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                if (audioPath != null) {
//                    if (mediaPlayer.isPlaying()) {
//                        mediaPlayer.pause();
//                        binding.btnPlayPause.setImageResource(R.drawable.play);
//                    } else {
//                        if (isAudioPlaybackCompleted) {
//                            // If the audio playback is completed, reset the MediaPlayer
//                            // and set the data source again before starting playback
//                            try {
//                                mediaPlayer.reset();
//                                mediaPlayer.setDataSource(audioPath);
//                                mediaPlayer.prepare();
//                            } catch (IOException e) {
//                                e.printStackTrace();
//                            }
//                            isAudioPlaybackCompleted = false;
//                        }
//                        mediaPlayer.start();
//                        binding.btnPlayPause.setImageResource(R.drawable.pause);
//                    }
//                } else {
//                    Toast.makeText(TestActivity.this, "Null", Toast.LENGTH_SHORT).show();
//                }
//            }
//        });
//
//    }
//
//    @Override
//    protected void onDestroy() {
//        super.onDestroy();
//
//        if (mediaPlayer != null) {
//            mediaPlayer.release();
//            mediaPlayer = null;
//        }
//    }
//
//
//    public void setUpRecording() {
//        mediaRecorder = new MediaRecorder();
//        mediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
//        mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
//        mediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AAC);
//
//        File file = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
//
//        if (!file.exists()) file.mkdirs();
//        audioPath = file.getAbsolutePath() + File.separator + "Tea-Counter.mp3";
//
//        mediaRecorder.setOutputFile(audioPath);
//
//    }
//
//
//    private String getHumanTimeText(long time) {
//        long seconds = time / 1000;
//        long minutes = seconds / 60;
//        long hours = minutes / 60;
//
//        return String.format("%02d:%02d:%02d", hours, minutes % 60, seconds % 60);
//    }
}
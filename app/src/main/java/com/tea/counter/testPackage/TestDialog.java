package com.tea.counter.testPackage;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.SeekBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.DialogFragment;

import com.devlomi.record_view.OnBasketAnimationEnd;
import com.devlomi.record_view.OnRecordListener;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.tea.counter.R;
import com.tea.counter.databinding.DialogRequestBinding;
import com.tea.counter.databinding.DialogTestBinding;

import java.io.File;
import java.io.IOException;
import java.util.Objects;

public class TestDialog extends BottomSheetDialogFragment {

    DialogTestBinding binding;
    private Context mContext;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // set custom style for bottom sheet rounded top corners
        setStyle(DialogFragment.STYLE_NORMAL, R.style.DialogStyle);

    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        mContext = context;

    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        BottomSheetDialog dialog = (BottomSheetDialog) super.onCreateDialog(savedInstanceState);
        dialog.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface dialogInterface) {
                // Set the navigation bar color when the BottomSheetDialog is shown
                Window window = getDialog().getWindow();
                if (window != null) {
                    window.setNavigationBarColor(ContextCompat.getColor(getContext(), R.color.dialogboxBackGround));
                }
            }
        });
        return dialog;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = DialogTestBinding.inflate(inflater, container, false);
        Objects.requireNonNull(getDialog().getWindow()).setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        getDialog().setCancelable(true);
        initView();

        return binding.getRoot();
    }

    private void initView() {
        setUpRecordingView();
    }

    public void setUpRecordingView() {

        binding.recordButton.setRecordView(binding.recordView);
        binding.recordButton.setListenForRecord(true);
        binding.recordView.setSlideToCancelTextColor(Color.parseColor("#322E2E"));
        binding.recordView.setTrashIconColor(Color.parseColor("#FFFFFF"));
        binding.recordView.setSlideToCancelArrowColor(Color.parseColor("#322E2E"));
        binding.recordView.setTimeLimit(30000);
        binding.recordButton.setScaleUpTo(1.4f);
//        binding.recordView.setCounterTimeColor(Color.parseColor("#5737D7"));
//        binding.recordView.setSmallMicColor(Color.parseColor("#2D4CDD"));


        binding.recordView.setOnRecordListener(new OnRecordListener() {
            @Override
            public void onStart() {
                //Start Recording..
                Log.w("2222", "onStart");



                binding.etAdditionalComment.setVisibility(View.GONE);
                binding.recordView.setVisibility(View.VISIBLE);
            }

            @Override
            public void onCancel() {
                //On Swipe To Cancel
                Log.w("11111", "onCancel");

            }

            @Override
            public void onFinish(long recordTime, boolean limitReached) {
                // Stop recording


                Toast.makeText(mContext, "Recorded  ", Toast.LENGTH_SHORT).show();

                // Hide the record view
                //  binding.recyclerViewCustomerRequest.setVisibility(View.GONE);
                binding.recordView.setVisibility(View.GONE);
                binding.recordButton.setVisibility(View.GONE);
                binding.etAdditionalComment.setVisibility(View.VISIBLE);



                //  setConstraints();

                // Create a MediaPlayer instance and set the data source to the recorded audio file

            }


            @Override
            public void onLessThanSecond() {
                //When the record time is less than One Second
                Log.w("6666", "onLessThanSecond");

                binding.recordView.setVisibility(View.GONE);
                binding.etAdditionalComment.setVisibility(View.VISIBLE);
            }

            @Override
            public void onLock() {

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

    }

}

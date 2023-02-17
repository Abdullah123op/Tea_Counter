package com.tea.counter.dialog;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;

import com.tea.counter.R;

public class CustomProgressDialog {
    private final Context mContext;
    private Dialog mProgressDialog;


    public CustomProgressDialog(Context context) {
        mContext = context;
        if (mProgressDialog == null) {
            mProgressDialog = new Dialog(mContext, android.R.style.Theme_Black);
            @SuppressLint("InflateParams") View view = LayoutInflater.from(mContext).inflate(R.layout.loader_layout, null);
            mProgressDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            mProgressDialog.getWindow().setBackgroundDrawableResource(R.color.transparent);
            mProgressDialog.setContentView(view);
            mProgressDialog.setCancelable(false);
        }
    }

    public void show() {
        mProgressDialog.show();
    }

    public void dismiss() {
        if (mProgressDialog.isShowing()) {
            mProgressDialog.dismiss();
        }
    }
}

package com.tea.counter.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.tea.counter.R;
import com.tea.counter.model.SignupModel;

import java.util.ArrayList;


public class SpinnerAdapter extends BaseAdapter implements android.widget.SpinnerAdapter {

    private final ArrayList<SignupModel> dataList;
    private final Context mContext;

    public SpinnerAdapter(Context context, ArrayList<SignupModel> dataList) {
        this.mContext = context;
        this.dataList = dataList;
    }

    @Override
    public int getCount() {
        return dataList.size();
    }

    @Override
    public Object getItem(int position) {
        return dataList.get(position);
    }

    @Override
    public long getItemId(int position) {
//        itemClick.onClick(position);
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = convertView;
        if (view == null) {
            LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = inflater.inflate(R.layout.spinner_item, parent, false);
        }
        TextView textView = view.findViewById(R.id.txtSellerName);
        textView.setText(dataList.get(position).getUserName());
        return textView;
    }
}


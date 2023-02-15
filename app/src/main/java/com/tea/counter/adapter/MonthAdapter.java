package com.tea.counter.adapter;

import android.annotation.SuppressLint;
import android.content.res.ColorStateList;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.tea.counter.R;
import com.tea.counter.model.MonthModel;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

public class MonthAdapter extends RecyclerView.Adapter<MonthAdapter.ViewHolder> {
    public final ArrayList<MonthModel> dataList;
    public final ItemClick itemClick;

    int currentMonth;


    public MonthAdapter(ArrayList<MonthModel> dataList, ItemClick itemClick) {
        this.itemClick = itemClick;
        this.dataList = dataList;

        Calendar calendar = Calendar.getInstance();
        currentMonth = calendar.get(Calendar.MONTH);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.simple_month_list, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, @SuppressLint("RecyclerView") int position) {
        MonthModel model = dataList.get(position);
        holder.monthName.setText(model.getMonthName());


        SimpleDateFormat sdf = new SimpleDateFormat("MMMM");
        Date date;
        try {
            date = sdf.parse(dataList.get(position).getMonthName());
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
        Calendar itemCalendar = Calendar.getInstance();
        assert date != null;
        itemCalendar.setTime(date);
        int itemMonth = itemCalendar.get(Calendar.MONTH);

        if (itemMonth >= currentMonth) {
            holder.monthName.setEnabled(false);
            holder.monthName.setTextColor(ContextCompat.getColor(holder.monthName.getContext(), R.color.grey));
            ColorStateList colorStateList = ColorStateList.valueOf(ContextCompat.getColor(holder.monthName.getContext(), R.color.grey));
            holder.monthName.setButtonTintList(colorStateList);
        } else {
            holder.monthName.setEnabled(true);
            holder.monthName.setTextColor(ContextCompat.getColor(holder.monthName.getContext(), R.color.white));
            ColorStateList colorStateList = ColorStateList.valueOf(ContextCompat.getColor(holder.monthName.getContext(), R.color.white));
            holder.monthName.setButtonTintList(colorStateList);
        }

        holder.monthName.setChecked(model.getClick());

        holder.monthName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                for (int i = 0; i < dataList.size(); i++) {
                    if (i != position) {
                        dataList.get(i).setClick(false);
                    }
                }
                dataList.get(position).setClick(!model.getClick());
                notifyDataSetChanged();

            }
        });

        holder.monthName.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                Log.e("111511 : ", isChecked + " : " + position);
                if (isChecked) {
                    itemClick.onClick(dataList);
                }


            }
        });
    }

    @Override
    public int getItemCount() {
        return dataList.size();
    }

    public interface ItemClick {
        void onClick(ArrayList<MonthModel> dataList);
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        CheckBox monthName;

        public ViewHolder(View itemView) {
            super(itemView);
            monthName = itemView.findViewById(R.id.monthName);

        }
    }
}


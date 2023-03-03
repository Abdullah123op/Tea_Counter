package com.tea.counter.adapter;

import android.graphics.drawable.Drawable;
import android.media.MediaPlayer;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.LinearLayoutCompat;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.gson.Gson;
import com.tea.counter.R;
import com.tea.counter.model.OrderModel;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class ExpandableAdapter extends RecyclerView.Adapter<ExpandableAdapter.ViewHolder> {
    private final Handler handler = new Handler();
    List<OrderModel> dataList;
    boolean isHomePage;
    boolean isSellerSide;
    boolean isNotificationSide;
    MediaPlayer mediaPlayer;
    private boolean isAudioPlaybackCompleted = false;
    private boolean isPlayedOnce = false;
    private Runnable runnable;


    public ExpandableAdapter(ArrayList<OrderModel> dataList, boolean isHomePage, boolean isSellerSide, boolean isNotificationSide) {
        this.dataList = dataList;
        this.isHomePage = isHomePage;
        this.isSellerSide = isSellerSide;
        this.isNotificationSide = isNotificationSide;
    }

    @NonNull
    @Override
    public ExpandableAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.expandable_list, parent, false);
        return new ExpandableAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ExpandableAdapter.ViewHolder holder, int position) {
        OrderModel model = dataList.get(position);
        model.setAudio(model.getAudioUrl() != null);

        if (isNotificationSide) {
            Log.e("8585 List : ", new Gson().toJson(dataList));
            boolean isVisible = dataList.get(position).isVisibility();
            Drawable myDrawable = ContextCompat.getDrawable(holder.imgUserAdapter.getContext(), R.drawable.new_order);
            holder.imgUserAdapter.setImageDrawable(myDrawable);
            holder.txtPriceExpandable.setVisibility(View.GONE);

            if (model.getOrderDetails() == null) {
                holder.layoutTxtList.setVisibility(View.GONE);
            } else if (model.getOrderDetails() != null) {

                holder.layoutTxtList.setVisibility(View.VISIBLE);
                if (model.getAdditionalComment() != null) {
                    holder.txtAdditional.setVisibility(View.VISIBLE);
                    holder.txtAdditional.setText("Message : " + model.getAdditionalComment());
                } else if (model.getAdditionalComment() == null) {
                    holder.txtAdditional.setVisibility(View.INVISIBLE);
                }
                Log.d("4554", "" + model.getAdditionalComment());
                holder.txtTitleExpandable.setText(" New Order from  " + model.getOrderTitle());

                holder.orderItemDetails.setText(model.getOrderDetails());
                holder.txtTimeExpandable.setText(model.getOrderTime());
            }

            if (!model.isAudio()) {
                holder.layoutAudioList.setVisibility(View.GONE);
            } else if (model.isAudio()) {
                holder.layoutAudioList.setVisibility(View.VISIBLE);
                mediaPlayer = new MediaPlayer();

                holder.seekBarList.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
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
                            holder.seekBarList.setProgress(currentPosition);
                        }
                        handler.postDelayed(this, 10);
                    }
                };

                mediaPlayer.setOnPreparedListener(mp -> {
                    // Set the maximum value of the seekbar to the duration of the media
                    holder.seekBarList.setMax(mp.getDuration());
                    // Start the handler to update the seekbar
                    handler.postDelayed(runnable, 10);
                });

                holder.btnPlayPauseList.setOnClickListener(v -> {
                    holder.btnPlayPauseListAlt.setVisibility(View.VISIBLE);
                    holder.btnPlayPauseList.setVisibility(View.GONE);
                    if (mediaPlayer.isPlaying()) {
                        mediaPlayer.pause();
                        holder.btnPlayPauseList.setImageResource(R.drawable.play);
                        holder.btnPlayPauseListAlt.setVisibility(View.GONE);
                        holder.btnPlayPauseList.setVisibility(View.VISIBLE);
                    } else {

                        Log.e("TAG", "onClick: ");

                        if (!isPlayedOnce) {
                            holder.btnPlayPauseList.setVisibility(View.GONE);
                            holder.btnPlayPauseListAlt.setVisibility(View.VISIBLE);

                            try {
                                mediaPlayer.setDataSource(model.getAudioUrl());
                                mediaPlayer.prepare();
                                isPlayedOnce = true;
                            } catch (IOException e) {
                                throw new RuntimeException(e);
                            }
                        }

                        if (isAudioPlaybackCompleted) {
                            mediaPlayer.reset();
                            try {
                                mediaPlayer.setDataSource(model.getAudioUrl());
                                mediaPlayer.prepare();
                            } catch (IOException e) {
                                throw new RuntimeException(e);
                            }
                            isAudioPlaybackCompleted = false;
                        }

                        holder.btnPlayPauseList.setImageResource(R.drawable.pause);
                        mediaPlayer.start();
                        holder.btnPlayPauseListAlt.setVisibility(View.GONE);
                        holder.btnPlayPauseList.setVisibility(View.VISIBLE);
                    }
                });

                mediaPlayer.setOnCompletionListener(mp -> {
                    isAudioPlaybackCompleted = true;
                    holder.btnPlayPauseList.setImageResource(R.drawable.play);
                    handler.removeCallbacks(runnable);
                });

                holder.txtTitleExpandable.setText(" New Voice Order from  " + model.getOrderTitle());
                holder.txtTimeExpandable.setText(model.getOrderTime());

                holder.layoutExpand.setVisibility(isVisible ? View.VISIBLE : View.GONE);
                holder.arrow_icon_collapsed.setRotation(isVisible ? 180 : 0);
            }


            holder.layoutExpand.setVisibility(isVisible ? View.VISIBLE : View.GONE);
            holder.arrow_icon_collapsed.setRotation(isVisible ? 180 : 0);
            return;
        }

        //-------------------   sellerSide--------------------Customer Side-------------------------
        if (isSellerSide) {
            holder.txtTitleExpandable.setText("Delivered To " + model.getOrderTitle());
        } else {
            holder.txtTitleExpandable.setText("Delivered from " + model.getOrderTitle());
        }

        if (isHomePage) {
            holder.txtTimeExpandable.setText(model.getOrderTime());
        } else {
            holder.txtTimeExpandable.setText(model.getOrderDate() + " , " + model.getOrderTime());
        }
        holder.txtPriceExpandable.setText("₹" + model.getOrderPrice());
        holder.orderItemDetails.setText(model.getOrderDetails());
        String imgUrl = model.getImageUrl();

        Glide.with(holder.imgUserAdapter.getContext()).load(imgUrl).into(holder.imgUserAdapter);

        boolean isVisible = dataList.get(position).isVisibility();
        holder.layoutExpand.setVisibility(isVisible ? View.VISIBLE : View.GONE);
        holder.arrow_icon_collapsed.setRotation(isVisible ? 180 : 0);

    }

    @Override
    public int getItemCount() {
        return dataList.size();
    }


    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView txtTitleExpandable, textView6, txtTimeExpandable, txtPriceExpandable, orderItemDetails, txtAdditional;
        LinearLayoutCompat layoutExpand, layoutAudioList, layoutTxtList;
        ConstraintLayout mainLayoutExpandable;
        ImageView imgUserAdapter, btnPlayPauseList, arrow_icon_collapsed;
        SeekBar seekBarList;
        ProgressBar btnPlayPauseListAlt;

        public ViewHolder(View itemView) {
            super(itemView);
            txtTitleExpandable = itemView.findViewById(R.id.txtTitleExpandable);
            txtTimeExpandable = itemView.findViewById(R.id.txtTimeExpandable);
            txtPriceExpandable = itemView.findViewById(R.id.txtPriceExpandable);
            orderItemDetails = itemView.findViewById(R.id.orderItemDetails);
            layoutExpand = itemView.findViewById(R.id.layoutExpand);
            mainLayoutExpandable = itemView.findViewById(R.id.mainLayoutExpandable);
            imgUserAdapter = itemView.findViewById(R.id.imgUserAdapter);
            txtAdditional = itemView.findViewById(R.id.txtAdditional);
            textView6 = itemView.findViewById(R.id.textView6);
            seekBarList = itemView.findViewById(R.id.seekBarList);
            btnPlayPauseList = itemView.findViewById(R.id.btnPlayPauseList);
            btnPlayPauseListAlt = itemView.findViewById(R.id.btnPlayPauseListAlt);

            layoutAudioList = itemView.findViewById(R.id.layoutAudioList);
            layoutTxtList = itemView.findViewById(R.id.layoutTxtList);
            arrow_icon_collapsed = itemView.findViewById(R.id.arrow_icon_collapsed);


            RotateAnimation rotateAnimation;
            if (arrow_icon_collapsed.getRotation() == 0) {
                rotateAnimation = new RotateAnimation(0, 180, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
            } else {
                rotateAnimation = new RotateAnimation(180, 0, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
            }
            rotateAnimation.setDuration(300);
            rotateAnimation.setFillAfter(true);


            mainLayoutExpandable.setOnClickListener(view -> {
                arrow_icon_collapsed.startAnimation(rotateAnimation);

                for (int i = 0; i < dataList.size(); i++) {
                    if (dataList.get(i).isVisibility() && i != getAdapterPosition()) {
                        dataList.get(i).setVisibility(false);
                        notifyItemChanged(i);
                    }
                }
                Log.d("8989", " Open/close");

                if (mediaPlayer != null) {
                    mediaPlayer.release();
                    mediaPlayer = null;
                    btnPlayPauseList.setImageResource(R.drawable.play);
                }
                OrderModel orderModel = dataList.get(getAdapterPosition());
                orderModel.setVisibility(!orderModel.isVisibility());
                notifyItemChanged(getAdapterPosition());
            });


        }
    }
}

package com.tea.counter.ui;

import android.animation.Animator;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import com.tea.counter.R;
import com.tea.counter.databinding.ActivitySplashBinding;
import com.tea.counter.utils.Preference;

public class SplashActivity extends AppCompatActivity {

    ActivitySplashBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_splash);
//        new Handler().postDelayed(new Runnable() {
//            @Override
//            public void run() {
//
//                Intent mainIntent = new Intent(SplashScreenActivity.this, MainActivity.class);
//                startActivity(mainIntent);
//                finish();
//            }
//        }, 1000);
//        binding.animationView.setAnimation("splash_animation.json");
        binding.animationView.playAnimation();
        binding.animationView.setRepeatCount(0);
        binding.animationView.addAnimatorListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(@NonNull Animator animation) {
            }

            @Override
            public void onAnimationEnd(@NonNull Animator animation) {
                if (Preference.getIsLogin(SplashActivity.this)) {
                    //   AlreadyLogin();
                    startActivity(new Intent(SplashActivity.this, SignUpActivity.class));
                } else {
                    // Startlogin();
                    startActivity(new Intent(SplashActivity.this, SignUpActivity.class));
                }

            }

            @Override
            public void onAnimationCancel(@NonNull Animator animation) {

            }

            @Override
            public void onAnimationRepeat(@NonNull Animator animation) {

            }
        });
    }

    public void AlreadyLogin() {
        if (Preference.getUserType(SplashActivity.this).equals("0")) {
            startActivity(new Intent(SplashActivity.this, SellerActivity.class));
        } else if (Preference.getUserType(SplashActivity.this).equals("1")) {
            startActivity(new Intent(SplashActivity.this, CustomerActivity.class));
        }
        finish();
    }

    public void Startlogin() {
        startActivity(new Intent(SplashActivity.this, LoginActivity.class));
        finish();
    }
}
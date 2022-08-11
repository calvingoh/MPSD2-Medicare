package com.example.medicarempsd.splash;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.example.medicarempsd.admin.AdminHomeActivity;
import com.example.medicarempsd.auth.AuthActivity;
import com.example.medicarempsd.constant.Constants;
import com.example.medicarempsd.databinding.ActivitySplashBinding;
import com.example.medicarempsd.doctor.DoctorHomeActivity;
import com.example.medicarempsd.users.activities.home.UserHomeActivity;

public class SplashActivity extends AppCompatActivity {
    private ActivitySplashBinding binding;
    private FirebaseAuth mAuth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySplashBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        mAuth = FirebaseAuth.getInstance();
        new Handler().postDelayed(() -> {
            //if user logged in
            if (mAuth.getCurrentUser() != null){
                SharedPreferences sharedPreferences = getSharedPreferences(Constants.sharedPrefName, MODE_PRIVATE);
                String userType = sharedPreferences.getString(Constants.userType, "");
                if (userType.equals("admin")) {
                    Intent intent = new Intent(SplashActivity.this, AdminHomeActivity.class);
                    startActivity(intent);
                   finish();
                } else if (userType.equals("doctor")) {
                    Intent intent = new Intent(SplashActivity.this, DoctorHomeActivity.class);
                    startActivity(intent);
                  finish();
                } else if (userType.equals("user")) {
                    Intent intent = new Intent(SplashActivity.this, UserHomeActivity.class);
                    startActivity(intent);
                   finish();
                }
            }else {
                startActivity(new Intent(SplashActivity.this, AuthActivity.class));
            }
            finish();
        }, 3000);
    }
}
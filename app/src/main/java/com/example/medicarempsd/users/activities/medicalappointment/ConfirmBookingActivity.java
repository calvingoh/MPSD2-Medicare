package com.example.medicarempsd.users.activities.medicalappointment;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.example.medicarempsd.databinding.ActivityConfirmBookingBinding;
import com.example.medicarempsd.users.activities.home.UserHomeActivity;

public class ConfirmBookingActivity extends AppCompatActivity {
    private ActivityConfirmBookingBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityConfirmBookingBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        Bundle bundle = getIntent().getExtras();
        if (bundle != null){
            binding.des.setText("Your booking with Dr "+ bundle.getString("doctorName") +  " is successful!");
        }

        binding.continueTv.setOnClickListener(v -> {
            Intent intent = new Intent(ConfirmBookingActivity.this, UserHomeActivity.class);
            startActivity(intent);
            finishAffinity();
        });

    }
}
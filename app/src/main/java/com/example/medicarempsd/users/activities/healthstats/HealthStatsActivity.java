package com.example.medicarempsd.users.activities.healthstats;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.example.medicarempsd.databinding.ActivityHealthStatusBinding;
import com.example.medicarempsd.Class.Health;

import java.util.Objects;

public class HealthStatsActivity extends AppCompatActivity {
    private ActivityHealthStatusBinding binding;
    private FirebaseAuth mAuth;
    private DatabaseReference healthRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityHealthStatusBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        //initialize firebase
        mAuth = FirebaseAuth.getInstance();
        healthRef = FirebaseDatabase.getInstance().getReference("health").child(Objects.requireNonNull(mAuth.getUid()));

        binding.backBtn.setOnClickListener(v -> onBackPressed());
        binding.addDataBtn.setOnClickListener(v -> startActivity(new Intent(HealthStatsActivity.this, AddHealthDataActivity.class)));

    }

    @Override
    protected void onStart() {
        super.onStart();
        getHealthData();
    }

    private void getHealthData() {

        binding.loading.setVisibility(View.VISIBLE);
        healthRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                binding.loading.setVisibility(View.GONE);

                if (snapshot.exists()) {

                    Health health = snapshot.getValue(Health.class);
                    binding.weightTv.setText(health.getWeight());
                    binding.heightTv.setText(health.getHeight());
                    binding.bmiTv.setText(health.getBmi());
                    binding.bloodPressureTv.setText(health.getBloodPressure());
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                binding.loading.setVisibility(View.GONE);

            }
        });

    }
}
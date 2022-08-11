package com.example.medicarempsd.users.activities.healthstats;

import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.example.medicarempsd.databinding.ActivityAddHealthDataBinding;
import com.example.medicarempsd.Class.Health;

import java.util.Objects;

public class AddHealthDataActivity extends AppCompatActivity {
    private ActivityAddHealthDataBinding binding;
    private FirebaseAuth mAuth;
    private DatabaseReference healthRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityAddHealthDataBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        //initialize
        mAuth = FirebaseAuth.getInstance();
        healthRef = FirebaseDatabase.getInstance().getReference("health").child(mAuth.getUid());

        binding.backBtn.setOnClickListener(v -> onBackPressed());
        binding.addDataBtn.setOnClickListener(v -> {
            //check validations
            if (Objects.requireNonNull(binding.weightEt.getText()).toString().isEmpty()) {
                binding.weightEt.setError("Please enter weight");
                return;
            }
            if (Objects.requireNonNull(binding.heightEt.getText()).toString().isEmpty()) {
                binding.heightEt.setError("Please enter height");
                return;
            }
            if (Objects.requireNonNull(binding.bmiEt.getText()).toString().isEmpty()) {
                binding.bmiEt.setError("Please enter valid BMI");
                return;
            }
            if (Objects.requireNonNull(binding.bloodPressureEt.getText()).toString().isEmpty()) {
                binding.bloodPressureEt.setError("Please enter blood pressure");
                return;
            }
            addHealthData();
        });
    }

    private void addHealthData() {
        binding.loading.setVisibility(View.VISIBLE);

        Health health = new Health(
                binding.weightEt.getText().toString(),
                binding.heightEt.getText().toString(),
                binding.bmiEt.getText().toString(),
                binding.bloodPressureEt.getText().toString()
        );

        healthRef.setValue(health)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        binding.loading.setVisibility(View.GONE);

                        Toast.makeText(AddHealthDataActivity.this, "Health data added successfully", Toast.LENGTH_SHORT).show();
                        onBackPressed();

                    }
                }).addOnFailureListener(e -> {
                    binding.loading.setVisibility(View.GONE);
                    Toast.makeText(AddHealthDataActivity.this, e.getMessage(), Toast.LENGTH_SHORT);
                });

    }
}
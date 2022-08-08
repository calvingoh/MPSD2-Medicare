package com.example.medicarempsd.auth.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.example.medicarempsd.databinding.FragmentForgotPasswordBinding;


public class ForgotPasswordFragment extends Fragment {
    private FragmentForgotPasswordBinding binding;
    private FirebaseAuth mAuth;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        binding = FragmentForgotPasswordBinding.inflate(inflater, container, false);
        mAuth = FirebaseAuth.getInstance();

        binding.sendBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //check validation
                if(binding.etEmail.getText().toString().isEmpty()){
                    binding.etEmail.setError("Please enter email");
                    return;
                }

                //sendEmail
                sendResetEmail();
            }
        });

        return binding.getRoot();
    }

    private void sendResetEmail() {
        binding.loading.setVisibility(View.VISIBLE);
        binding.sendBtn.setEnabled(false);

        mAuth.sendPasswordResetEmail(binding.etEmail.getText().toString()).addOnCompleteListener(task -> {

            if (task.isSuccessful()) {
                binding.loading.setVisibility(View.GONE);
                Snackbar snackbar = Snackbar
                        .make(binding.getRoot(), "Please check your email to reset Password", Snackbar.LENGTH_LONG);
                snackbar.show();

                Navigation.findNavController(requireView()).navigateUp();

            }

        }).addOnFailureListener(e -> {
            binding.loading.setVisibility(View.GONE);
            binding.sendBtn.setEnabled(true);
            Snackbar snackbar = Snackbar
                    .make(binding.getRoot(), e.getMessage(), Snackbar.LENGTH_LONG);
            snackbar.show();
        });
    }
}
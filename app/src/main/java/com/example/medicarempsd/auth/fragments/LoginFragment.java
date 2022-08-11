package com.example.medicarempsd.auth.fragments;

import static android.content.Context.MODE_PRIVATE;

import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.example.medicarempsd.R;
import com.example.medicarempsd.admin.AdminHomeActivity;
import com.example.medicarempsd.constant.Constants;
import com.example.medicarempsd.databinding.FragmentLoginBinding;
import com.example.medicarempsd.doctor.DoctorHomeActivity;
import com.example.medicarempsd.Class.Users;
import com.example.medicarempsd.users.activities.home.UserHomeActivity;

import java.util.Objects;


public class LoginFragment extends Fragment {
    private FragmentLoginBinding binding;
    private FirebaseAuth mAuth;
    private DatabaseReference userRef;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentLoginBinding.inflate(inflater, container, false);


        //initialize
        mAuth = FirebaseAuth.getInstance();

        userRef = FirebaseDatabase.getInstance().getReference("users");


        //listeners
        binding.forgotPasswordTv.setOnClickListener(view -> Navigation.findNavController(view).navigate(R.id.action_loginFragment_to_forgotPasswordFragment));
        binding.registerTv.setOnClickListener(view -> Navigation.findNavController(view).navigate(R.id.action_loginFragment_to_registerFragment));


        //permissions
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            try {
                requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE,
                        Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.CAMERA}, 555);
            } catch (Exception e) {
                Log.e("userPer", e.toString());
            }
        }


        binding.loginBtn.setOnClickListener(view -> {
            //checking validations
            if (Objects.requireNonNull(binding.emailEt.getText()).toString().isEmpty()) {
                binding.emailEt.setError("Please enter valid email");
                return;
            }

            if (Objects.requireNonNull(binding.passwordEt.getText()).toString().isEmpty()) {
                binding.passwordEt.setError("Please enter password");
                return;
            }

            //login
            login();
        });


        return binding.getRoot();
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    private void login() {
        binding.loading.setVisibility(View.VISIBLE);
        binding.loginBtn.setEnabled(false);

        mAuth.signInWithEmailAndPassword(binding.emailEt.getText().toString(), binding.passwordEt.getText().toString())
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        userRef.child(Objects.requireNonNull(mAuth.getUid()))
                                .addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                                        if (snapshot.exists()) {

                                            Snackbar snackbar = Snackbar
                                                    .make(binding.getRoot(), "Login Successfully", Snackbar.LENGTH_LONG);
                                            snackbar.show();
                                            binding.loading.setVisibility(View.GONE);
                                            Users users = snapshot.getValue(Users.class);

                                            // Storing data into SharedPreferences
                                            SharedPreferences sharedPreferences = requireActivity().getSharedPreferences(Constants.sharedPrefName, MODE_PRIVATE);
                                            SharedPreferences.Editor editor = sharedPreferences.edit();
                                            editor.putString(Constants.userType, users.getType());
                                            editor.apply();

                                            if (users.getType().equals("admin")) {

                                                SharedPreferences.Editor editor1 = sharedPreferences.edit();
                                                editor1.putString(Constants.adminEmail, binding.emailEt.getText().toString());
                                                editor1.putString(Constants.adminPassword, binding.passwordEt.getText().toString());
                                                editor1.apply();

                                                Intent intent = new Intent(requireContext(), AdminHomeActivity.class);
                                                startActivity(intent);
                                                requireActivity().finish();
                                            } else if (users.getType().equals("doctor")) {
                                                Intent intent = new Intent(requireContext(), DoctorHomeActivity.class);
                                                startActivity(intent);
                                                requireActivity().finish();
                                            } else if (users.getType().equals("user")) {
                                                Intent intent = new Intent(requireContext(), UserHomeActivity.class);
                                                startActivity(intent);
                                                requireActivity().finish();
                                            }
                                        }
                                    }
                                    @Override
                                    public void onCancelled(@NonNull DatabaseError error) {

                                    }
                                });
                    }
                }).addOnFailureListener(e -> {
                    binding.loading.setVisibility(View.GONE);
                    binding.loginBtn.setEnabled(true);
                    Snackbar snackbar = Snackbar
                            .make(binding.getRoot(), e.getMessage(), Snackbar.LENGTH_LONG);
                    snackbar.show();
                });
    }
}
package com.example.medicarempsd.admin.activities;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.example.medicarempsd.admin.adapters.UserAdapter;
import com.example.medicarempsd.databinding.ActivityDoctorsBinding;
import com.example.medicarempsd.Class.Users;

import java.util.ArrayList;

public class DoctorsActivity extends AppCompatActivity {
    private ActivityDoctorsBinding binding;
    private ArrayList<Users> usersArrayList = new ArrayList<>();
    private UserAdapter mAdapter;
    private FirebaseAuth mAuth;
    private DatabaseReference userRef;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityDoctorsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        //initialize
        mAuth = FirebaseAuth.getInstance();
        userRef = FirebaseDatabase.getInstance().getReference("users");
        binding.backBtn.setOnClickListener(v -> onBackPressed());

        binding.addDoctorBtn.setOnClickListener(v -> startActivity(new Intent(DoctorsActivity.this, AddDoctorActivity.class)));

        binding.searchEt.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (!binding.searchEt.getText().toString().isEmpty()) {
                    final ArrayList<Users> filterModList = filter(usersArrayList, binding.searchEt.getText().toString());
                    if (!filterModList.isEmpty())
                        mAdapter.setFilter(filterModList);
                } else {
                    getDoctors();
                }

            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

    }

    @Override
    protected void onStart() {
        super.onStart();
        getDoctors();
    }

    private void getDoctors() {
        usersArrayList.clear();
        binding.loading.setVisibility(View.VISIBLE);
        userRef.orderByChild("type").equalTo("doctor").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    binding.loading.setVisibility(View.GONE);

                    for (DataSnapshot ds: snapshot.getChildren()){
                        Users users = ds.getValue(Users.class);
                        usersArrayList.add(users);

                    }
                    mAdapter = new UserAdapter(DoctorsActivity.this, usersArrayList);
                    binding.doctorRv.setLayoutManager(new LinearLayoutManager(DoctorsActivity.this));
                    binding.doctorRv.setAdapter(mAdapter);


                }else {
                    binding.loading.setVisibility(View.GONE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                binding.loading.setVisibility(View.GONE);

                Snackbar snackbar = Snackbar
                        .make(binding.getRoot(), error.getMessage(), Snackbar.LENGTH_LONG);
                snackbar.show();
            }
        });

    }

    //getting filter list
    private ArrayList<Users> filter(ArrayList<Users> hi, String query) {
        query = query.toLowerCase();
        final ArrayList<Users> filterModeList = new ArrayList<>();
        for (Users modal : hi) {

            final String sTitle = modal.getName().toLowerCase();


            if (sTitle.startsWith(query)) {
                filterModeList.add(modal);
            }
        }
        return filterModeList;
    }
}
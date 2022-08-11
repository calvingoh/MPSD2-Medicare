package com.example.medicarempsd.admin;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;

import com.bumptech.glide.Glide;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.example.medicarempsd.R;
import com.example.medicarempsd.admin.activities.DoctorsActivity;
import com.example.medicarempsd.admin.activities.UsersActivity;
import com.example.medicarempsd.databinding.ActivityAdminHomeBinding;
import com.example.medicarempsd.Class.Home;
import com.example.medicarempsd.Class.Users;
import com.example.medicarempsd.users.activities.healthstats.HealthStatsActivity;
import com.example.medicarempsd.users.activities.profile.ProfileActivity;
import com.example.medicarempsd.users.adapter.HomeAdapter;

import java.util.ArrayList;
import java.util.Objects;

public class AdminHomeActivity extends AppCompatActivity implements HomeAdapter.OnItemClickListener {
    private ActivityAdminHomeBinding binding;
    private HomeAdapter mAdapter;
    private ArrayList<Home> homeArrayList = new ArrayList<>();
    private FirebaseAuth mAuth;
    private DatabaseReference userRef;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityAdminHomeBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        //initialize
        mAuth = FirebaseAuth.getInstance();
        userRef = FirebaseDatabase.getInstance().getReference("users");
        binding.userImage.setOnClickListener(v -> startActivity(new Intent(AdminHomeActivity.this, ProfileActivity.class)));

        setHomeMenu();

    }
    @Override
    protected void onStart() {
        super.onStart();
        getUserData();

    }

    private void getUserData() {
        userRef.child(Objects.requireNonNull(mAuth.getUid()))
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.exists()) {


                            Users users = snapshot.getValue(Users.class);

                            assert users != null;
                            if (!users.getImage().equals("")) {
                                Glide.with(AdminHomeActivity.this)
                                        .load(users.getImage())
                                        .into(binding.userImage);
                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Snackbar snackbar = Snackbar
                                .make(binding.getRoot(), error.getMessage(), Snackbar.LENGTH_LONG);
                        snackbar.show();
                    }
                });

    }
    private void setHomeMenu() {
        homeArrayList.add(new Home("1", "Doctors", R.drawable.doctor));
        homeArrayList.add(new Home("2", "Users", R.drawable.man));
        homeArrayList.add(new Home("3", "Health Stats", R.drawable.stats));
        homeArrayList.add(new Home("4", "Call Ambulance", R.drawable.ambulas));
        mAdapter = new HomeAdapter(this, homeArrayList);
        binding.homeMenuRv.setLayoutManager(new GridLayoutManager(this, 3));
        binding.homeMenuRv.setAdapter(mAdapter);
        mAdapter.setOnItemClickListener(this);
    }

    @Override
    public void onListItemClick(Home home) {
        if (home.getId().equals("1")){
            startActivity(new Intent(AdminHomeActivity.this, DoctorsActivity.class));
        }if (home.getId().equals("2")){
            startActivity(new Intent(AdminHomeActivity.this, UsersActivity.class));
        }if (home.getId().equals("3")){
            startActivity(new Intent(AdminHomeActivity.this, HealthStatsActivity.class));
        }if (home.getId().equals("4")){
            // Use format with "tel:" and phoneNumber created is
            // stored in u.
            Uri u = Uri.parse("tel:" +"999");

            // Create the intent and set the data for the
            // intent as the phone number.
            Intent i = new Intent(Intent.ACTION_DIAL, u);
            try
            {
                // Launch the Phone app's dialer with a phone
                // number to dial a call.
                startActivity(i);
            }
            catch (SecurityException s)
            {
                // show() method display the toast with
                // exception message.
                Toast.makeText(this, "An error occurred", Toast.LENGTH_LONG)
                        .show();
            }
        }
    }
}
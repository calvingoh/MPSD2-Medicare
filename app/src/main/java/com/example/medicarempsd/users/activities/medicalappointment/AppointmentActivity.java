package com.example.medicarempsd.users.activities.medicalappointment;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.example.medicarempsd.databinding.ActivityAppointmentBinding;
import com.example.medicarempsd.doctor.adapters.AppointmentAdapter;
import com.example.medicarempsd.Class.Appointment;

import java.util.ArrayList;

public class AppointmentActivity extends AppCompatActivity implements AppointmentAdapter.OnItemClickListener {
    private ActivityAppointmentBinding binding;
    private FirebaseAuth mAuth;
    private DatabaseReference userRef, appointmentRef;
    private ArrayList<Appointment> appointmentArrayList = new ArrayList<>();
    private AppointmentAdapter mAdapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityAppointmentBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.backBtn.setOnClickListener(v -> onBackPressed());

        //initialize
        mAuth = FirebaseAuth.getInstance();
        userRef = FirebaseDatabase.getInstance().getReference("users");
        appointmentRef = FirebaseDatabase.getInstance().getReference("appointments");

        getAppointments();
    }
    private void getAppointments() {
        appointmentArrayList.clear();
        binding.loading.setVisibility(View.VISIBLE);
        appointmentRef.orderByChild("userId").equalTo(mAuth.getUid())
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.exists()) {
                            binding.loading.setVisibility(View.GONE);

                            for (DataSnapshot ds: snapshot.getChildren()){
                                Appointment appointment = ds.getValue(Appointment.class);
                                appointmentArrayList.add(appointment);
                            }
                            mAdapter = new AppointmentAdapter(AppointmentActivity.this, appointmentArrayList);
                            binding.appointmentRv.setLayoutManager(new LinearLayoutManager(AppointmentActivity.this));
                            binding.appointmentRv.setAdapter(mAdapter);
                            mAdapter.setOnItemClickListener(AppointmentActivity.this);
                        }else {
                            binding.loading.setVisibility(View.GONE);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                    }
                });
    }


    public void onAcceptClick(Appointment home) {
    }

    public void onRejectClick(Appointment home) {
    }

    public void onMeetingLinkClick(Appointment home) {
        String data = home.getMeetingLink();
        Intent defaultBrowser = Intent.makeMainSelectorActivity(Intent.ACTION_MAIN, Intent.CATEGORY_APP_BROWSER);
        defaultBrowser.setData(Uri.parse(data));
        startActivity(defaultBrowser);
    }

    public void onPrescriptionClick(Appointment home) {
    }
}
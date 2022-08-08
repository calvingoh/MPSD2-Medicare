package com.example.medicarempsd.doctor.activities;

import android.app.Dialog;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.example.medicarempsd.R;
import com.example.medicarempsd.databinding.ActivityDoctorAppointmentBinding;
import com.example.medicarempsd.doctor.adapters.AppointmentAdapter;
import com.example.medicarempsd.Class.Appointment;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class DoctorAppointmentActivity extends AppCompatActivity implements AppointmentAdapter.OnItemClickListener  {
    private ActivityDoctorAppointmentBinding binding;
    private FirebaseAuth mAuth;
    private DatabaseReference userRef, appointmentRef;
    private ArrayList<Appointment> appointmentArrayList = new ArrayList<>();

    private AppointmentAdapter mAdapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityDoctorAppointmentBinding.inflate(getLayoutInflater());
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
        appointmentRef.orderByChild("doctorId").equalTo(mAuth.getUid())
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.exists()) {
                            binding.loading.setVisibility(View.GONE);

                            for (DataSnapshot ds: snapshot.getChildren()){
                                Appointment appointment = ds.getValue(Appointment.class);
                                appointmentArrayList.add(appointment);

                            }
                            mAdapter = new AppointmentAdapter(DoctorAppointmentActivity.this, appointmentArrayList);
                            binding.appointmentRv.setLayoutManager(new LinearLayoutManager(DoctorAppointmentActivity.this));
                            binding.appointmentRv.setAdapter(mAdapter);
                            mAdapter.setOnItemClickListener(DoctorAppointmentActivity.this);


                        }else {
                            binding.loading.setVisibility(View.GONE);
                        }

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

    }

    public void onAcceptClick(Appointment data) {
        Map<String, Object> appointment = new HashMap<>();
        appointment.put("status","accepted");

        appointmentRef.child(data.getAppointmentId())
                .updateChildren(appointment).addOnCompleteListener(task -> {
                    if (task.isSuccessful()){
                        getAppointments();
                    }
                });

    }


    public void onRejectClick(Appointment data) {
        Map<String, Object> appointment = new HashMap<>();
        appointment.put("status","rejected");
        appointmentRef.child(data.getAppointmentId())
                .updateChildren(appointment).addOnCompleteListener(task -> {
                    if (task.isSuccessful()){
                        getAppointments();
                    }
                });

    }


    public void onMeetingLinkClick(Appointment data) {
        final Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.custom_add_meeting_layout);

        TextInputEditText meetingLinkEt = dialog.findViewById(R.id.meeting_link_et);
        CardView addBtn = dialog.findViewById(R.id.add_card_view);
        CardView cancelBtn = dialog.findViewById(R.id.cancel_card_view);
        cancelBtn.setOnClickListener(v -> dialog.dismiss());



        addBtn.setOnClickListener(v -> {
            if (meetingLinkEt.getText().toString().isEmpty()) {
                meetingLinkEt.setError("Please add meeting link");
                return;
            }

            Map<String, Object> appointment = new HashMap<>();
            appointment.put("meetingLink",meetingLinkEt.getText().toString());
            appointmentRef.child(data.getAppointmentId())
                    .updateChildren(appointment).addOnCompleteListener(task -> {
                        if (task.isSuccessful()){
                            getAppointments();
                        }
                    });
            Toast.makeText(this, "Meeting link added", Toast.LENGTH_SHORT).show();
            dialog.dismiss();

        });


        dialog.show();
    }


    public void onPrescriptionClick(Appointment home) {
        final Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.custom_add_meeting_layout);

        TextInputEditText prescriptionEt = dialog.findViewById(R.id.meeting_link_et);
        TextInputLayout prescriptionLayout = dialog.findViewById(R.id.a);
        prescriptionLayout.setHint("Prescription");
        TextView title = dialog.findViewById(R.id.title);
        title.setText("Prescription");
        CardView addBtn = dialog.findViewById(R.id.add_card_view);
        CardView cancelBtn = dialog.findViewById(R.id.cancel_card_view);
        cancelBtn.setOnClickListener(v -> dialog.dismiss());



        addBtn.setOnClickListener(v -> {
            if (prescriptionEt.getText().toString().isEmpty()) {
                prescriptionEt.setError("Please add prescription");
                return;
            }

            Map<String, Object> appointment = new HashMap<>();
            appointment.put("prescription",prescriptionEt.getText().toString());
            appointmentRef.child(home.getAppointmentId())
                    .updateChildren(appointment).addOnCompleteListener(task -> {
                        if (task.isSuccessful()){
                            getAppointments();
                        }
                    });
            Toast.makeText(this, "Prescription added", Toast.LENGTH_SHORT).show();
            dialog.dismiss();

        });


        dialog.show();
    }
}
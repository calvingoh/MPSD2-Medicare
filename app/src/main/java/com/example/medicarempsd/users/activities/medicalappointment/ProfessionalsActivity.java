package com.example.medicarempsd.users.activities.medicalappointment;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.example.medicarempsd.R;
import com.example.medicarempsd.admin.adapters.UserAdapter;
import com.example.medicarempsd.databinding.ActivityProfessionalsBinding;
import com.example.medicarempsd.Class.Users;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;

public class ProfessionalsActivity extends AppCompatActivity implements UserAdapter.OnItemClickListener {
    private ActivityProfessionalsBinding binding;
    private FirebaseAuth mAuth;
    private UserAdapter mAdapter;
    private DatabaseReference userRef, appointmentRef;
    private ArrayList<Users> doctorArrayList = new ArrayList<>();
    final Calendar myCalendar = Calendar.getInstance();
    private String specialization;
    private String patientName= "";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityProfessionalsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.backBtn.setOnClickListener(v -> onBackPressed());
        //initialize
        mAuth = FirebaseAuth.getInstance();
        userRef = FirebaseDatabase.getInstance().getReference("users");
        appointmentRef = FirebaseDatabase.getInstance().getReference("appointments");

        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            specialization = bundle.getString("title");
            binding.title.setText(specialization);
            getDoctors(specialization);
        }
        getUserData();
    }

    private void getUserData() {
        userRef.child(Objects.requireNonNull(mAuth.getUid()))
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.exists()) {
                            Users users = snapshot.getValue(Users.class);
                            patientName = users.getName();
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

    private void getDoctors(String specialization) {
        doctorArrayList.clear();
        binding.loading.setVisibility(View.VISIBLE);
        userRef.orderByChild("specialization").equalTo(specialization).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    binding.loading.setVisibility(View.GONE);

                    for (DataSnapshot ds : snapshot.getChildren()) {
                        Users users = ds.getValue(Users.class);
                        doctorArrayList.add(users);
                        Log.e("doctorrr", doctorArrayList.toString());

                    }
                    mAdapter = new UserAdapter(ProfessionalsActivity.this, doctorArrayList);
                    binding.doctorsRv.setLayoutManager(new LinearLayoutManager(ProfessionalsActivity.this));
                    binding.doctorsRv.setAdapter(mAdapter);
                    mAdapter.setOnItemClickListener(ProfessionalsActivity.this);

                } else {
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

    @Override
    public void onListItemClick(Users users) {
        final Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.custom_dialog_layout);

        TextInputEditText selectDate = dialog.findViewById(R.id.booking_date_et);
        TextInputEditText selectTime = dialog.findViewById(R.id.booking_time_et);
        CardView bookingBtn = dialog.findViewById(R.id.book_card_view);
        CardView cancelBtn = dialog.findViewById(R.id.cancel_card_view);
        cancelBtn.setOnClickListener(v -> dialog.dismiss());

        DatePickerDialog.OnDateSetListener date = (view, year, month, day) -> {
            myCalendar.set(Calendar.YEAR, year);
            myCalendar.set(Calendar.MONTH, month);
            myCalendar.set(Calendar.DAY_OF_MONTH, day);
            String myFormat = "dd/MM/yyyy";
            SimpleDateFormat dateFormat = new SimpleDateFormat(myFormat, Locale.US);
            selectDate.setText(dateFormat.format(myCalendar.getTime()));
        };
        selectDate.setOnClickListener(v -> new DatePickerDialog(ProfessionalsActivity.this, date, myCalendar.get(Calendar.YEAR),
                myCalendar.get(Calendar.MONTH), myCalendar.get(Calendar.DAY_OF_MONTH)).show());

        selectTime.setOnClickListener(v -> {
            Calendar mcurrentTime = Calendar.getInstance();
            int hour = mcurrentTime.get(Calendar.HOUR_OF_DAY);
            int minute = mcurrentTime.get(Calendar.MINUTE);
            TimePickerDialog mTimePicker;
            mTimePicker = new TimePickerDialog(ProfessionalsActivity.this, (timePicker, selectedHour, selectedMinute) ->
                    selectTime.setText(selectedHour + ":" + selectedMinute), hour, minute, true);//Yes 24 hour time
            mTimePicker.setTitle("Select Time");
            mTimePicker.show();
        });

        bookingBtn.setOnClickListener(v -> {
            if (selectDate.getText().toString().isEmpty()) {
                selectDate.setError("Please select date");
                return;
            }
            if (selectTime.getText().toString().isEmpty()) {
                selectTime.setError("Please select time");
                return;
            }
            bookAppointment(dialog, selectDate.getText().toString(), selectTime.getText().toString(),
                    users.getUserId(), users.getName());
        });
        dialog.show();
    }

    private void bookAppointment(Dialog dialog, String selectedData, String selectedTime, String doctorId, String doctorName) {
        String appointmentId = appointmentRef.push().getKey();
        Map<String, Object> appointment = new HashMap<>();
        appointment.put("appointmentId",appointmentId);
        appointment.put("doctorId",doctorId);
        appointment.put("userId", mAuth.getUid());
        appointment.put("date", selectedData);
        appointment.put("time", selectedTime);
        appointment.put("meetingLink", "");
        appointment.put("doctorName",doctorName);
        appointment.put("patientName",patientName);
        appointment.put("status","pending");
        appointment.put("specialization",specialization);
        appointment.put("prescription","");

        appointmentRef.child(appointmentId)
                .setValue(appointment)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()){
                        dialog.dismiss();
                        Intent intent = new Intent(ProfessionalsActivity.this, ConfirmBookingActivity.class);
                        intent.putExtra("doctorName", doctorName);
                        startActivity(intent);
                    }
                }).addOnFailureListener(e -> Toast.makeText(ProfessionalsActivity.this, ""+ e.toString(), Toast.LENGTH_SHORT).show());
            }
        }
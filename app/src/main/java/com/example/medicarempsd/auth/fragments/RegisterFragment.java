package com.example.medicarempsd.auth.fragments;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.example.medicarempsd.databinding.FragmentRegisterBinding;
import com.example.medicarempsd.Class.Users;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Objects;


public class RegisterFragment extends Fragment {

    private FragmentRegisterBinding binding;
    private DatabaseReference userRef;
    private FirebaseAuth mAuth;
    private String profileType = "user";
    private StringBuffer strBuf;
    private String dobDate, formateDate;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentRegisterBinding.inflate(inflater, container, false);

        //initialize
        userRef = FirebaseDatabase.getInstance().getReference("users");
        mAuth = FirebaseAuth.getInstance();

        //click listeners
        binding.backArrow.setOnClickListener(view -> Navigation.findNavController(view).navigateUp());

        binding.dobEt.setOnClickListener(v -> showDatePickerDialog());


        binding.registerBtn.setOnClickListener(view -> {

            //check validations
            if (Objects.requireNonNull(binding.name.getText()).toString().isEmpty()) {
                binding.name.setError("Please enter user name");
                return;
            }
            if (Objects.requireNonNull(binding.phoneNoEt.getText()).toString().isEmpty()) {
                binding.phoneNoEt.setError("Please enter phone number");
                return;
            }


            if (Objects.requireNonNull(binding.emailEt.getText()).toString().isEmpty()) {
                binding.emailEt.setError("Please enter valid email");
                return;
            }
            if (Objects.requireNonNull(binding.passwordEt.getText()).toString().isEmpty()) {
                binding.passwordEt.setError("Please enter password");
                return;
            }

            if (Objects.requireNonNull(binding.dobEt.getText()).toString().isEmpty()) {
                binding.dobEt.setError("Please select date of birth");
                return;
            }
            //register user
            register();
        });
        return binding.getRoot();
    }


    private void showDatePickerDialog() {
        // Get open DatePickerDialog button.
        // Create a new OnDateSetListener instance. This listener will be invoked when user click ok button in DatePickerDialog.
        DatePickerDialog.OnDateSetListener onDateSetListener = (datePicker, year, month, dayOfMonth) -> {
            strBuf = new StringBuffer();
            // strBuf.append("You select date is ");


            if ((month + 1) < 10) {
                strBuf.append("0" + (month + 1));
            } else {
                strBuf.append(month + 1);
            }
            strBuf.append("-");

            if (dayOfMonth < 10) {
                strBuf.append("0" + dayOfMonth);
            } else {
                strBuf.append(dayOfMonth);
            }
            strBuf.append("-");

            strBuf.append(year);

            String ages = getAge(year, month + 1, dayOfMonth);

            dobDate = strBuf.toString();

            try {
                DateFormat inputFormat = new SimpleDateFormat("MM-dd-yyyy");
                DateFormat outputFormat = new SimpleDateFormat("MMM-dd-yyyy");
                String inputDateStr = strBuf.toString();
                Date date = inputFormat.parse(inputDateStr);
                formateDate = outputFormat.format(date);
            } catch (ParseException e) {
                e.printStackTrace();
            }


            binding.dobEt.setText(ages + " " + "Years" + " / " + formateDate);
        };
        // Get current year, month and day.
        Calendar now = Calendar.getInstance();
        int year = now.get(Calendar.YEAR);
        int month = now.get(Calendar.MONTH);
        int day = now.get(Calendar.DAY_OF_MONTH);
        DatePickerDialog datePickerDialog = new DatePickerDialog(requireContext(), onDateSetListener, year, month, day);
        now.add(Calendar.YEAR, -18);

        datePickerDialog.getDatePicker().setMaxDate(now.getTimeInMillis());
        datePickerDialog.show();
    }

    //to get date of birth
    private String getAge(int year, int month, int day) {

        Calendar dob = Calendar.getInstance();
        Calendar today = Calendar.getInstance();
        int todaymonth = today.get(Calendar.MONTH);
        int todayyear = today.get(Calendar.YEAR);
        int today_date = today.get(Calendar.DAY_OF_MONTH);
        today.set(todayyear, todaymonth + 1, today_date + 1);
        dob.set(year, month, day);
        int age = today.get(Calendar.YEAR) - dob.get(Calendar.YEAR);

        System.out.println("DateFormat1 " + today.get(Calendar.YEAR) + "---" + dob.get(Calendar.YEAR));
        System.out.println("DateFormat2 " + today.get(Calendar.DAY_OF_YEAR) + "---" + dob.get(Calendar.DAY_OF_YEAR));

        if (today.get(Calendar.DAY_OF_YEAR) < dob.get(Calendar.DAY_OF_YEAR)) {
            age--;
        }

        Integer ageInt = new Integer(age);
        String ageS = ageInt.toString();
        return ageS;
    }


    private void register() {
        binding.loading.setVisibility(View.VISIBLE);
        binding.registerBtn.setEnabled(false);
        mAuth.createUserWithEmailAndPassword(Objects.requireNonNull(binding.emailEt.getText()).toString(),
                        Objects.requireNonNull(binding.passwordEt.getText()).toString())
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {

                        //store user detail in firebase
                        Users users;
                        users = new Users(
                                mAuth.getUid(),
                                Objects.requireNonNull(binding.name.getText()).toString(),
                                Objects.requireNonNull(binding.phoneNoEt.getText()).toString(),
                                dobDate,
                                profileType,
                                binding.emailEt.getText().toString(),
                                "",
                                "",
                                ""

                        );

                        userRef.child(Objects.requireNonNull(mAuth.getUid())).setValue(users).addOnCompleteListener(task1 -> {
                            if (task1.isSuccessful()) {
                                binding.loading.setVisibility(View.GONE);
                                Snackbar snackbar = Snackbar
                                        .make(binding.getRoot(),  " Register Successfully", Snackbar.LENGTH_LONG);
                                snackbar.show();
                                mAuth.signOut();
                                Navigation.findNavController(requireView()).navigateUp();
                            }
                        });

                    }

                }).addOnFailureListener(e -> {
                    binding.loading.setVisibility(View.GONE);
                    binding.registerBtn.setEnabled(true);
                    Snackbar snackbar = Snackbar
                            .make(binding.getRoot(), Objects.requireNonNull(e.getMessage()), Snackbar.LENGTH_LONG);
                    snackbar.show();
                });
    }
}
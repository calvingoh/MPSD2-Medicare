package com.example.medicarempsd.users.activities.profile;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.github.dhaval2404.imagepicker.ImagePicker;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.example.medicarempsd.R;
import com.example.medicarempsd.auth.AuthActivity;
import com.example.medicarempsd.constant.Constants;
import com.example.medicarempsd.databinding.ActivityProfileBinding;
import com.example.medicarempsd.Class.Users;
import com.tiper.MaterialSpinner;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class ProfileActivity extends AppCompatActivity {
    private ActivityProfileBinding binding;
    private FirebaseAuth mAuth;
    private DatabaseReference userRef;
    private Uri resultUri;
    private Bitmap bitmap;
    private StringBuffer strBuf;
    private String dobDate, formateDate;
    private String qualifications = "";
    private String specializationValue = "";
    private String[] specialization;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityProfileBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());


        //initialize
        mAuth = FirebaseAuth.getInstance();
        userRef = FirebaseDatabase.getInstance().getReference("users");

        binding.backBtn.setOnClickListener(v -> onBackPressed());
        binding.logoutBtn.setOnClickListener(v -> logoutUser());
        specialization = getResources().getStringArray(R.array.specialization_array);
        ArrayAdapter arrayAdapter = new ArrayAdapter(
                this,
                android.R.layout.simple_dropdown_item_1line,
                specialization
        );
        //set spinner adapter
        binding.specializationSpinner.setAdapter(arrayAdapter);
        //getting profile type
        binding.specializationSpinner.setOnItemSelectedListener(new MaterialSpinner.OnItemSelectedListener() {
            @Override
            public void onItemSelected(@NonNull MaterialSpinner materialSpinner, @Nullable View view, int i, long l) {
                specializationValue = Objects.requireNonNull(materialSpinner.getSelectedItem()).toString();
            }

            @Override
            public void onNothingSelected(@NonNull MaterialSpinner materialSpinner) {

            }
        });

        binding.editBtn.setOnClickListener(v -> {
            if (binding.editBtn.getText().toString().equals("Edit")) {

                binding.name.setEnabled(true);
                binding.dobEt.setEnabled(true);
                binding.phoneNoEt.setEnabled(true);
                binding.userImage.setEnabled(true);
                binding.specializationSpinner.setEnabled(true);
                binding.qualificationEt.setEnabled(true);
                binding.editBtn.setText("Update");
            } else {

                //check validations
                if (Objects.requireNonNull(binding.name.getText()).toString().isEmpty()) {
                    binding.name.setError("Please enter user name");
                    return;
                }
                if (Objects.requireNonNull(binding.phoneNoEt.getText()).toString().isEmpty()) {
                    binding.phoneNoEt.setError("Please enter phone number");
                    return;
                }

                if (Objects.requireNonNull(binding.dobEt.getText()).toString().isEmpty()) {
                    binding.dobEt.setError("Please select date of birth");
                    return;
                }
                updateProfile();
            }
        });

        binding.userImage.setOnClickListener(v -> checkAndroidVersion());
        binding.dobEt.setOnClickListener(v -> showDatePickerDialog());
        getUserData();

    }

    private void logoutUser() {
        new AlertDialog.Builder(this)
                .setTitle("Logout")
                .setMessage("Do you really want to logout?")
                .setIcon(android.R.drawable.ic_dialog_alert)
                .setPositiveButton(android.R.string.yes, (dialog, whichButton) -> {
                    SharedPreferences settings = getSharedPreferences(Constants.sharedPrefName, Context.MODE_PRIVATE);
                    settings.edit().clear().apply();
                    mAuth.signOut();
                    Intent intent = new Intent(ProfileActivity.this, AuthActivity.class);
                    startActivity(intent);
                    finishAffinity();
                })
                .setNegativeButton(android.R.string.no, null).show();
    }

    private void showDatePickerDialog() {
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
        DatePickerDialog datePickerDialog = new DatePickerDialog(ProfileActivity.this, onDateSetListener, year, month, day);
        now.add(Calendar.YEAR, -18);

        datePickerDialog.getDatePicker().setMaxDate(now.getTimeInMillis());
        datePickerDialog.show();
    }

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

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        //RESULT FROM SELECTED IMAGE
        super.onActivityResult(requestCode, resultCode, data);


        if (resultCode == Activity.RESULT_OK && requestCode == ImagePicker.REQUEST_CODE && data != null) {
            //Image Uri will not be null for RESULT_OK
            Uri uri = data.getData();

            resultUri = uri;

            try {
                bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), uri);
                binding.userImage.setImageBitmap(bitmap);
            } catch (IOException e) {
                e.printStackTrace();
            }
            // Use Uri object instead of File to avoid storage permissions

        }
    }

    private void getUserData() {
        binding.loading.setVisibility(View.VISIBLE);
        userRef.child(Objects.requireNonNull(mAuth.getUid()))
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.exists()) {
                            binding.loading.setVisibility(View.GONE);
                            binding.emailEt.setEnabled(false);
                            binding.name.setEnabled(false);
                            binding.dobEt.setEnabled(false);
                            binding.phoneNoEt.setEnabled(false);
                            binding.userImage.setEnabled(false);
                            binding.qualificationEt.setEnabled(false);
                            binding.specializationSpinner.setEnabled(false);

                            Users users = snapshot.getValue(Users.class);

                            assert users != null;
                            if (users.getType().equals("doctor")) {
                                binding.qualificationLayout.setVisibility(View.VISIBLE);
                                binding.qualificationEt.setText(users.getQualification());
                                binding.specializationSpinner.setVisibility(View.VISIBLE);
                                specializationValue = users.getSpecialization();
                            }

                            binding.emailEt.setText(users.getEmail());
                            binding.name.setText(users.getName());
                            binding.dobEt.setText(users.getDob());
                            binding.phoneNoEt.setText(users.getPhone());
                            dobDate = users.getDob();


                            if (!users.getImage().equals("")) {
                                Glide.with(ProfileActivity.this)
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
                        binding.loading.setVisibility(View.GONE);
                    }
                });
    }

    public void checkAndroidVersion() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            try {
                requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.CAMERA}, 555);
            } catch (Exception e) {

            }
        } else {
            ImagePicker.with(ProfileActivity.this)
                    .crop()                    //Crop image(Optional), Check Customization for more option
                    .compress(1024)            //Final image size will be less than 1 MB(Optional)
                    .maxResultSize(1080, 1080)    //Final image resolution will be less than 1080 x 1080(Optional)
                    .start();
        }

    }

    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 555 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            ImagePicker.with(ProfileActivity.this)
                    .crop()                    //Crop image(Optional), Check Customization for more option
                    .compress(1024)            //Final image size will be less than 1 MB(Optional)
                    .maxResultSize(1080, 1080)    //Final image resolution will be less than 1080 x 1080(Optional)
                    .start();
        } else {
            checkAndroidVersion();
        }
    }

    private void updateProfile() {
        if (resultUri != null) {
            binding.loading.setVisibility(View.VISIBLE);

            final StorageReference filePath = FirebaseStorage.getInstance().getReference().child("Users").child(resultUri.getLastPathSegment());
            Bitmap bitmap = null;
            try {
                bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), resultUri);
            } catch (IOException e) {
                e.printStackTrace();
            }

            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 20, baos);
            byte[] data = baos.toByteArray();
            UploadTask uploadTask = filePath.putBytes(data);

            uploadTask.addOnFailureListener(e -> {
                binding.loading.setVisibility(View.GONE);
                Toast.makeText(ProfileActivity.this, "" + e.getMessage(), Toast.LENGTH_SHORT).show();
                return;
            });
            uploadTask.addOnSuccessListener(taskSnapshot -> filePath.getDownloadUrl().addOnSuccessListener(uri -> {


                Map<String, Object> updateValue = new HashMap<>();
                updateValue.put("name", binding.name.getText().toString());
                updateValue.put("phone", binding.phoneNoEt.getText().toString());
                updateValue.put("dob", dobDate);
                updateValue.put("image", uri.toString());
                updateValue.put("specialization", specializationValue);
                if (!binding.qualificationEt.getText().toString().isEmpty()) {
                    updateValue.put("qualification", binding.qualificationEt.getText().toString());
                } else {
                    updateValue.put("qualification", qualifications);
                }

                userRef.child(mAuth.getUid()).updateChildren(updateValue).addOnCompleteListener(task -> {
                    binding.loading.setVisibility(View.GONE);
                    Toast.makeText(ProfileActivity.this, "Profile Updated Successfully", Toast.LENGTH_SHORT).show();
                    onBackPressed();

                }).addOnFailureListener(e -> {
                    binding.loading.setVisibility(View.GONE);
                    Toast.makeText(ProfileActivity.this, "" + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
                return;

            }).addOnFailureListener(exception -> {
                binding.loading.setVisibility(View.GONE);
                Toast.makeText(ProfileActivity.this, "" + exception.getMessage(), Toast.LENGTH_SHORT).show();

            }));
        } else {
            binding.loading.setVisibility(View.VISIBLE);

            Map<String, Object> updateValue = new HashMap<>();
            updateValue.put("name", binding.name.getText().toString());
            updateValue.put("phone", binding.phoneNoEt.getText().toString());
            updateValue.put("dob", dobDate);
            updateValue.put("specialization", specializationValue);
            if (!binding.qualificationEt.getText().toString().isEmpty()) {
                updateValue.put("qualification", binding.qualificationEt.getText().toString());
            } else {
                updateValue.put("qualification", qualifications);
            }

            userRef.child(mAuth.getUid()).updateChildren(updateValue)
                    .addOnCompleteListener(task -> {
                        binding.loading.setVisibility(View.GONE);
                        Toast.makeText(ProfileActivity.this, "Profile Updated Successfully", Toast.LENGTH_SHORT).show();
                        onBackPressed();

                    }).addOnFailureListener(e -> {
                        binding.loading.setVisibility(View.GONE);
                        Toast.makeText(ProfileActivity.this, "" + e.getMessage(), Toast.LENGTH_SHORT).show();
                    });

        }
    }
}
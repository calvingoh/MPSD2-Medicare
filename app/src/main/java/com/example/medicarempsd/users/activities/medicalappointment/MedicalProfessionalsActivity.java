package com.example.medicarempsd.users.activities.medicalappointment;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;

import com.example.medicarempsd.databinding.ActivityMedicalProfessionalsBinding;
import com.example.medicarempsd.Class.Specialist;
import com.example.medicarempsd.users.adapter.SpecialistAdapter;

import java.util.ArrayList;

public class MedicalProfessionalsActivity extends AppCompatActivity implements SpecialistAdapter.OnItemClickListener {
    private ActivityMedicalProfessionalsBinding binding;
    private SpecialistAdapter mAdapter;
    private ArrayList<Specialist> specialistArrayList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMedicalProfessionalsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.backBtn.setOnClickListener(v -> onBackPressed());

        setUpSpecialist();


        //search articles
        binding.searchEt.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (!binding.searchEt.getText().toString().isEmpty()) {
                    final ArrayList<Specialist> filterModList = filter(specialistArrayList, binding.searchEt.getText().toString());
                    if (!filterModList.isEmpty())
                        mAdapter.setFilter(filterModList);
                } else {
                  setUpSpecialist();
                }

            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

    }

    private void setUpSpecialist() {

        specialistArrayList.clear();
        specialistArrayList.add(new Specialist("1", "Cardiology"));
        specialistArrayList.add(new Specialist("2", "Dentistry"));
        specialistArrayList.add(new Specialist("3", "Dermatology"));
        specialistArrayList.add(new Specialist("4", "Gynecology"));
        specialistArrayList.add(new Specialist("5", "Physician"));

        mAdapter = new SpecialistAdapter(this, specialistArrayList);
        binding.specialistRv.setLayoutManager(new GridLayoutManager(this, 2));
        binding.specialistRv.setAdapter(mAdapter);
        mAdapter.setOnItemClickListener(this);

    }


    @Override
    public void onListItemClick(Specialist specialist) {

        Intent intent = new Intent(MedicalProfessionalsActivity.this, ProfessionalsActivity.class);
        intent.putExtra("title", specialist.getTitle());
        startActivity(intent);

    }

    //getting filter list
    private ArrayList<Specialist> filter(ArrayList<Specialist> hi, String query) {
        query = query.toLowerCase();
        final ArrayList<Specialist> filterModeList = new ArrayList<>();
        for (Specialist modal : hi) {

            final String sTitle = modal.getTitle().toLowerCase();


            if (sTitle.startsWith(query)) {
                filterModeList.add(modal);
            }
        }
        return filterModeList;
    }

}
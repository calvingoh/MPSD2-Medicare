package com.example.medicarempsd.users.activities.medicwiki;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.medicarempsd.R;
import com.example.medicarempsd.databinding.ActivityMedicWikiBinding;
import com.example.medicarempsd.Class.MedicWiki;
import com.example.medicarempsd.users.adapter.MedicWikiAdapter;

import java.util.ArrayList;

public class MedicWikiActivity extends AppCompatActivity implements MedicWikiAdapter.OnItemClickListener {
    private ActivityMedicWikiBinding binding;
    private MedicWikiAdapter mAdapter;
    private ArrayList<MedicWiki> medicWikiArrayList = new ArrayList<>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMedicWikiBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.backBtn.setOnClickListener(v -> onBackPressed());

        getWikiData();
    }

    private void getWikiData() {
        medicWikiArrayList.add(new MedicWiki("1", "Covid and Its Effects", R.drawable.covid_image));
        medicWikiArrayList.add(new MedicWiki("2", "Learn About Vaccines", R.drawable.covid_image_2));
        medicWikiArrayList.add(new MedicWiki("3", "Fight Against Cancer", R.drawable.cancer_image));
        medicWikiArrayList.add(new MedicWiki("4", "Healthy Eating and\n Lifestyle", R.drawable.health_image));

        mAdapter = new MedicWikiAdapter(this, medicWikiArrayList);
        binding.medicWikiRv.setLayoutManager(new LinearLayoutManager(MedicWikiActivity.this));
        binding.medicWikiRv.setAdapter(mAdapter);
        mAdapter.setOnItemClickListener(this);
    }

    @Override
    public void onListItemClick(MedicWiki home) {
        Intent intent = new Intent(MedicWikiActivity.this, MedicWikiDetailActivity.class);
        intent.putExtra("title", home.getTitle());
        intent.putExtra("image", home.getImage());
        startActivity(intent);
    }
}
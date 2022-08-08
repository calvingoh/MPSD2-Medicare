package com.example.medicarempsd.users.activities.medicwiki;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.example.medicarempsd.databinding.ActivityMedicWikiDetailBinding;

public class MedicWikiDetailActivity extends AppCompatActivity {
    private ActivityMedicWikiDetailBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMedicWikiDetailBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.backBtn.setOnClickListener(v -> onBackPressed());

        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            Glide.with(this)
                    .load(bundle.getInt("image"))
                    .into(binding.wikiImage);

            binding.title.setText(bundle.getString("title"));
        }

    }
}
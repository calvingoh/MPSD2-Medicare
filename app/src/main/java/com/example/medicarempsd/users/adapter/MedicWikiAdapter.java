package com.example.medicarempsd.users.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.medicarempsd.databinding.CustomMedicWikiLayoutBinding;
import com.example.medicarempsd.Class.MedicWiki;

import java.util.ArrayList;

public class MedicWikiAdapter extends RecyclerView.Adapter<MedicWikiAdapter.ViewHolder> {
    private Context context;
    ArrayList<MedicWiki> homeArrayList;
    OnItemClickListener mListener;

    public MedicWikiAdapter(Context context, ArrayList<MedicWiki> homeArrayList) {
        this.context = context;
        this.homeArrayList = homeArrayList;
    }

    public interface OnItemClickListener {
        void onListItemClick(MedicWiki home);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        mListener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        CustomMedicWikiLayoutBinding binding = CustomMedicWikiLayoutBinding
                .inflate(
                        LayoutInflater.from(parent.getContext()),
                        parent,
                        false);

        return new ViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, int position) {
        MedicWiki home = homeArrayList.get(position);
        holder.bind(home);
    }

    @Override
    public int getItemCount() {
        return homeArrayList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private final CustomMedicWikiLayoutBinding binding;

        public ViewHolder(CustomMedicWikiLayoutBinding binding) {
            super(binding.getRoot());
            this.binding = binding;

            binding.cardView.setOnClickListener(view -> {
                int position = getAdapterPosition();
                if (position != RecyclerView.NO_POSITION) {
                    MedicWiki home = homeArrayList.get(position);
                    mListener.onListItemClick(home);
                }
            });
        }

        public void bind(MedicWiki home) {
            binding.title.setText(home.getTitle());
            Glide.with(context)
                    .load(home.getImage())
                    .into(binding.image);
        }
    }

}

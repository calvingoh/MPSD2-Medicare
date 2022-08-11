package com.example.medicarempsd.users.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.medicarempsd.Class.Specialist;
import com.example.medicarempsd.databinding.CustomSpecialistLayoutBinding;

import java.util.ArrayList;

public class SpecialistAdapter extends RecyclerView.Adapter<SpecialistAdapter.ViewHolder> {
    private Context context;
    ArrayList<Specialist> specialistArrayList;
    OnItemClickListener mListener;

    public SpecialistAdapter(Context context, ArrayList<Specialist> specialistArrayList) {
        this.context = context;
        this.specialistArrayList = specialistArrayList;
    }

    public interface OnItemClickListener {
        void onListItemClick(Specialist home);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        mListener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        CustomSpecialistLayoutBinding binding = CustomSpecialistLayoutBinding
                .inflate(
                        LayoutInflater.from(parent.getContext()),
                        parent,
                        false);

        return new ViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, int position) {
        Specialist home = specialistArrayList.get(position);
        holder.bind(home);

    }


    @Override
    public int getItemCount() {
        return specialistArrayList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private final CustomSpecialistLayoutBinding binding;

        public ViewHolder(CustomSpecialistLayoutBinding binding) {
            super(binding.getRoot());
            this.binding = binding;

            binding.linear.setOnClickListener(view -> {
                int position = getAdapterPosition();
                if (position != RecyclerView.NO_POSITION) {
                    Specialist home = specialistArrayList.get(position);
                    mListener.onListItemClick(home);
                }
            });
        }

        public void bind(Specialist home) {
            binding.title.setText(home.getTitle());
        }
    }

    public void setFilter(ArrayList<Specialist> list) {
        specialistArrayList = new ArrayList<>();
        specialistArrayList.addAll(list);
        notifyDataSetChanged();
    }
}

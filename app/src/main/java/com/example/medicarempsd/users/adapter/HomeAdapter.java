package com.example.medicarempsd.users.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.medicarempsd.Class.Home;
import com.example.medicarempsd.databinding.CustomUserHomeLayoutBinding;

import java.util.ArrayList;

public class HomeAdapter extends RecyclerView.Adapter<HomeAdapter.ViewHolder> {

    private Context context;
    ArrayList<Home> homeArrayList;
    OnItemClickListener mListener;

    public HomeAdapter(Context context, ArrayList<Home> homeArrayList) {

        this.context = context;
        this.homeArrayList = homeArrayList;
    }

    public interface OnItemClickListener {
        void onListItemClick(Home home);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        mListener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        CustomUserHomeLayoutBinding binding = CustomUserHomeLayoutBinding
                .inflate(
                        LayoutInflater.from(parent.getContext()),
                        parent,
                        false);


        return new ViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, int position) {
        Home home = homeArrayList.get(position);
        holder.bind(home);
    }

    @Override
    public int getItemCount() {
        return homeArrayList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private final CustomUserHomeLayoutBinding binding;

        public ViewHolder(CustomUserHomeLayoutBinding binding) {
            super(binding.getRoot());
            this.binding = binding;

            binding.linear.setOnClickListener(view -> {
                int position = getAdapterPosition();
                if (position != RecyclerView.NO_POSITION) {
                    Home home = homeArrayList.get(position);
                    mListener.onListItemClick(home);
                }
            });
        }

        public void bind(Home home) {
            binding.title.setText(home.getTitle());
            Glide.with(context)
                    .load(home.getImage())
                    .into(binding.homeIcon);

        }
    }
}

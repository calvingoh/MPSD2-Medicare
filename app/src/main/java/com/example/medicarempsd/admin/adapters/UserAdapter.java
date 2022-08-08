package com.example.medicarempsd.admin.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.medicarempsd.R;
import com.example.medicarempsd.databinding.CustomUsersLayoutBinding;
import com.example.medicarempsd.Class.Users;

import java.util.ArrayList;

public class UserAdapter extends RecyclerView.Adapter<UserAdapter.ViewHolder> {

    private Context context;
    ArrayList<Users> usersArrayList;
    OnItemClickListener mListener;


    public UserAdapter(Context context, ArrayList<Users> usersArrayList) {

        this.context = context;
        this.usersArrayList = usersArrayList;
    }

    public interface OnItemClickListener {
        void onListItemClick(Users users);

    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        mListener = listener;
    }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        CustomUsersLayoutBinding binding = CustomUsersLayoutBinding
                .inflate(
                        LayoutInflater.from(parent.getContext()),
                        parent,
                        false);


        return new ViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, int position) {
        Users users = usersArrayList.get(position);
        holder.bind(users);

    }


    @Override
    public int getItemCount() {
        return usersArrayList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private final CustomUsersLayoutBinding binding;

        public ViewHolder(CustomUsersLayoutBinding binding) {
            super(binding.getRoot());
            this.binding = binding;

            binding.bookCardView.setOnClickListener(view -> {
                int position = getAdapterPosition();
                if (position != RecyclerView.NO_POSITION) {
                    Users users = usersArrayList.get(position);
                    mListener.onListItemClick(users);
                }
            });


        }

        public void bind(Users users) {
            binding.userName.setText(users.getName());
            binding.userEmail.setText(users.getEmail());
            binding.userPhone.setText(users.getPhone());


            if(users.getType().equals("doctor")){
                binding.bookCardView.setVisibility(View.VISIBLE);
            }

            if (users.getSpecialization().equals("")){
                binding.specializationTv.setVisibility(View.GONE);
            }else {
                binding.specializationTv.setText(users.getSpecialization());
            }
            if (users.getQualification().equals("")){
                binding.qualificationTv.setVisibility(View.GONE);
            }else {
                binding.qualificationTv.setText(users.getQualification());
            }
            if (!binding.userImage.equals("")) {
                if (users.getType().equals("user")) {
                    Glide.with(context)
                            .load(users.getImage())
                            .placeholder(R.drawable.man)
                            .into(binding.userImage);
                }else {
                    Glide.with(context)
                            .load(users.getImage())
                            .placeholder(R.drawable.doctor)
                            .into(binding.userImage);
                }
            }

        }
    }

    public void setFilter(ArrayList<Users> list) {
        usersArrayList = new ArrayList<>();
        usersArrayList.addAll(list);
        notifyDataSetChanged();

    }


}

package com.example.medicarempsd.doctor.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.medicarempsd.Class.Appointment;
import com.example.medicarempsd.databinding.CustomAppointmentLayoutBinding;
import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;

public class AppointmentAdapter extends RecyclerView.Adapter<AppointmentAdapter.ViewHolder> {
    private Context context;
    ArrayList<Appointment> appointmentArrayList;
    OnItemClickListener mListener;
    private FirebaseAuth mAuth = FirebaseAuth.getInstance();

    public AppointmentAdapter(Context context, ArrayList<Appointment> appointmentArrayList) {

        this.context = context;
        this.appointmentArrayList = appointmentArrayList;
    }

    public interface OnItemClickListener {
        void onAcceptClick(Appointment home);
        void onRejectClick(Appointment home);
        void onMeetingLinkClick(Appointment home);
        void onPrescriptionClick(Appointment home);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        mListener = listener;
    }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        CustomAppointmentLayoutBinding binding = CustomAppointmentLayoutBinding
                .inflate(
                        LayoutInflater.from(parent.getContext()),
                        parent,
                        false);

        return new ViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, int position) {
        Appointment home = appointmentArrayList.get(position);
        holder.bind(home);
    }

    @Override
    public int getItemCount() {
        return appointmentArrayList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private final CustomAppointmentLayoutBinding binding;

        public ViewHolder(CustomAppointmentLayoutBinding binding) {
            super(binding.getRoot());
            this.binding = binding;

            binding.acceptBtn.setOnClickListener(view -> {
                int position = getAdapterPosition();
                if (position != RecyclerView.NO_POSITION) {
                    Appointment home = appointmentArrayList.get(position);
                    mListener.onAcceptClick(home);
                }
            });
            binding.rejectBtn.setOnClickListener(view -> {
                int position = getAdapterPosition();
                if (position != RecyclerView.NO_POSITION) {
                    Appointment home = appointmentArrayList.get(position);
                    mListener.onRejectClick(home);
                }
            });
            binding.meetingLink.setOnClickListener(view -> {
                int position = getAdapterPosition();
                if (position != RecyclerView.NO_POSITION) {
                    Appointment home = appointmentArrayList.get(position);
                    mListener.onMeetingLinkClick(home);
                }
            });
            binding.addPrescription.setOnClickListener(view -> {
                int position = getAdapterPosition();
                if (position != RecyclerView.NO_POSITION) {
                    Appointment home = appointmentArrayList.get(position);
                    mListener.onPrescriptionClick(home);
                }
            });
        }

        public void bind(Appointment home) {
            binding.patientName.setText(home.getPatientName());
            binding.doctorName.setText(home.getDoctorName());
            binding.bookDate.setText(home.getDate());
            binding.bookTime.setText(home.getTime());
            binding.bookSpecialization.setText(home.getSpecialization());

            if (!home.getMeetingLink().equals("")){
                binding.meetingLink.setText(home.getMeetingLink());
            }
            if (!home.getPrescription().equals("")){
                binding.addPrescription.setText(home.getPrescription());
            }
            if (home.getStatus().equals("accepted")){
                binding.acceptBtn.setText("Accepted");
                binding.acceptBtn.setEnabled(false);
                binding.rejectBtn.setVisibility(View.GONE);
            }else {
                binding.acceptBtn.setEnabled(true);
                binding.rejectBtn.setVisibility(View.VISIBLE);
            }
            if (home.getStatus().equals("rejected")){
                binding.rejectBtn.setText("Rejected");
                binding.rejectBtn.setEnabled(false);
                binding.acceptBtn.setVisibility(View.GONE);
            }else {
                binding.rejectBtn.setEnabled(true);
                binding.acceptBtn.setVisibility(View.VISIBLE);
            }

            if (home.getUserId().equals(mAuth.getUid())){
                binding.rejectBtn.setVisibility(View.GONE);
                if (home.getStatus().equals("pending")){
                    binding.rejectBtn.setVisibility(View.GONE);
                    binding.acceptBtn.setVisibility(View.VISIBLE);
                    binding.acceptBtn.setEnabled(false);
                    binding.acceptBtn.setText("Pending");
                }

                if (binding.meetingLink.getText().toString().equals("Add Link")){
                    binding.meetingLink.setText("No meeting available");
                    binding.meetingLink.setEnabled(false);
                }
                if (binding.addPrescription.getText().toString().equals("Add Prescription")){
                    binding.addPrescription.setText("No Prescription Available");
                    binding.addPrescription.setEnabled(false);
                }
            }
        }
    }




}

package com.easymi.common.register;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.easymi.common.R;
import com.easymi.common.entity.Vehicles;

import java.util.ArrayList;
import java.util.List;

public class VehicleAdapter extends RecyclerView.Adapter<VehicleAdapter.VehicleViewHolder> {

    private List<Vehicles.Vehicle> vehicleList = new ArrayList<>();

    public void setDatas(List<Vehicles.Vehicle> vehicles) {
        vehicleList.clear();
        if (vehicles != null && !vehicles.isEmpty()) {
            vehicleList.addAll(vehicles);
        }
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public VehicleViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.com_item_vehicle, parent, false);
        return new VehicleViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull VehicleViewHolder holder, int position) {
        Vehicles.Vehicle vehicle = vehicleList.get(position);
        holder.tv.setText(vehicle.chinese);
        holder.bind(position);
    }

    @Override
    public int getItemCount() {
        return vehicleList.size();
    }

    class VehicleViewHolder extends RecyclerView.ViewHolder {
        private int mPosition;

        private TextView tv;

        VehicleViewHolder(View itemView) {
            super(itemView);
            tv = itemView.findViewById(R.id.tv);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (listener != null) {
                        listener.onVehicleClick(vehicleList.get(mPosition));
                    }
                }
            });
        }

        private void bind(int position) {
            this.mPosition = position;
        }
    }

    private OnVehicleClickListener listener;

    public void setOnVehicleClickListener(OnVehicleClickListener listener) {
        this.listener = listener;
    }

    public interface OnVehicleClickListener {
        void onVehicleClick(Vehicles.Vehicle vehicle);
    }


}

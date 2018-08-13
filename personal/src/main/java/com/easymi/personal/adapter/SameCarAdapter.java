package com.easymi.personal.adapter;

import android.app.Activity;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.easymi.component.utils.PhoneUtil;
import com.easymi.personal.R;
import com.easymi.personal.entity.CarInfo;

import java.util.ArrayList;
import java.util.List;

public class SameCarAdapter extends RecyclerView.Adapter<SameCarAdapter.ViewHolder> {

    private List<CarInfo.Employee> employees = new ArrayList<>();
    private Activity activity;


    public SameCarAdapter(Activity activity,List<CarInfo.Employee> employees) {
        if (employees != null && !employees.isEmpty()) {
            this.employees.addAll(employees);
        }
        this.activity = activity;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.p_item_same_car, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        CarInfo.Employee data = employees.get(position);
        holder.phone.setText(data.phone);
        holder.name.setText(data.name);
        holder.callView.setOnClickListener(v -> {
            if (data.phone != null) {
                PhoneUtil.call(activity, data.phone);
            }

        });
    }

    @Override
    public int getItemCount() {
        return employees.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        TextView name;
        TextView phone;
        View callView;

        public ViewHolder(View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.name);
            phone = itemView.findViewById(R.id.phone);
            callView = itemView.findViewById(R.id.callView);
        }
    }

}


package com.easymi.personal.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.easymi.personal.R;
import com.easymi.personal.entity.Notifity;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by developerLzh on 2017/11/10 0010.
 */

public class NotifityAdapter extends RecyclerView.Adapter<NotifityAdapter.NotifityHolder> {

    private Context context;

    private List<Notifity> list;

    public NotifityAdapter(Context context) {
        this.context = context;
        list = new ArrayList<>();
    }

    public void setList(List<Notifity> list) {
        this.list = list;
        notifyDataSetChanged();
    }

    @Override
    public NotifityHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.notifity_item, null);

        return new NotifityHolder(view);
    }

    @Override
    public void onBindViewHolder(NotifityHolder holder, int position) {
        Notifity notifity = list.get(position);
        holder.notifityContent.setText(notifity.message);
        holder.notifityTime.setText(notifity.time);
        holder.isNew.setVisibility(notifity.isNew ? View.VISIBLE : View.GONE);
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    class NotifityHolder extends RecyclerView.ViewHolder {

        TextView notifityContent;
        ImageView isNew;
        TextView notifityTime;

        public NotifityHolder(View itemView) {
            super(itemView);
            notifityContent = itemView.findViewById(R.id.notifity_content);
            isNew = itemView.findViewById(R.id.is_new);
            notifityTime = itemView.findViewById(R.id.notifity_time);
        }
    }
}

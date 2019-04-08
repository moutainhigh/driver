package com.easymi.personal.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.easymi.component.utils.TimeUtil;
import com.easymi.personal.R;
import com.easymi.personal.entity.Notifity;

import java.util.ArrayList;
import java.util.List;

/**
 * Copyright (C), 2012-2018, Sichuan Xiaoka Technology Co., Ltd.
 * FileName: NotifityAdapter
 * @Author: shine
 * Date: 2018/12/24 下午1:10
 * Description:
 * History:
 */

public class NotifityAdapter extends RecyclerView.Adapter<NotifityAdapter.NotifityHolder> {

    private Context context;

    private List<Notifity> list;

    private OnItemClickListener listener;

    public interface OnItemClickListener {
        /**
         * 点击监听
         * @param id
         * @param position
         */
        void onClick(long id, int position);
    }

    public NotifityAdapter(Context context) {
        this.context = context;
        list = new ArrayList<>();
    }

    /**
     * 设置数据
     * @param list
     */
    public void setList(List<Notifity> list) {
        this.list = list;
        notifyDataSetChanged();
    }

    public void setListener(OnItemClickListener listener) {
        this.listener = listener;
    }

    @Override
    public NotifityHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.notifity_item, parent, false);

        return new NotifityHolder(view);
    }

    @Override
    public void onBindViewHolder(NotifityHolder holder, int position) {
        Notifity notifity = list.get(position);
        holder.notifityContent.setText(notifity.message);
        long time = notifity.time * 1000;
        long todayBegin = TimeUtil.parseTime("yyyy-MM-dd", TimeUtil.getTime("yyyy-MM-dd", System.currentTimeMillis()));
        long timeleft = System.currentTimeMillis() - time;

        long day = timeleft / (24 * 60 * 60 * 1000);
        if (day == 0) {
            if (time > todayBegin) {
                holder.notifityTime.setText(context.getString(R.string.today));
            } else {
                holder.notifityTime.setText("1" + context.getString(R.string.day_ago));
            }
        } else {
            if (day < 30) {
                holder.notifityTime.setText(day + context.getString(R.string.day_ago));
            } else {
                holder.notifityTime.setText(TimeUtil.getTime("yyyy-MM-dd HH:mm", time));
            }

        }

        holder.isNew.setVisibility(notifity.state == 1 ? View.VISIBLE : View.GONE);

        holder.rootView.setOnClickListener(v -> {
            if (null != listener) {
                if (notifity.state == 1) {
                    listener.onClick(notifity.id, position);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    class NotifityHolder extends RecyclerView.ViewHolder {

        TextView notifityContent;
        ImageView isNew;
        TextView notifityTime;

        View rootView;

        public NotifityHolder(View itemView) {
            super(itemView);
            notifityContent = itemView.findViewById(R.id.notifity_content);
            isNew = itemView.findViewById(R.id.is_new);
            notifityTime = itemView.findViewById(R.id.notifity_time);
            rootView = itemView.findViewById(R.id.rl_root);
        }
    }
}

package com.easymi.personal.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.easymi.component.Config;
import com.easymi.component.network.ApiManager;
import com.easymi.component.network.HttpResultFunc;
import com.easymi.component.network.MySubscriber;
import com.easymi.component.network.NoErrSubscriberListener;
import com.easymi.component.result.EmResult;
import com.easymi.component.rxmvp.RxManager;
import com.easymi.component.utils.TimeUtil;
import com.easymi.personal.McService;
import com.easymi.personal.R;
import com.easymi.personal.entity.Notifity;

import java.util.ArrayList;
import java.util.List;

import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by developerLzh on 2017/11/10 0010.
 */

public class NotifityAdapter extends RecyclerView.Adapter<NotifityAdapter.NotifityHolder> {

    private Context context;

    private List<Notifity> list;

    private OnItemClickListener listener;

    public interface OnItemClickListener {
        void onClick(long id, int position);
    }

    public NotifityAdapter(Context context) {
        this.context = context;
        list = new ArrayList<>();
    }

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
        holder.notifityTime.setText(TimeUtil.getTime("yyyy-MM-dd HH:mm", notifity.time * 1000));
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

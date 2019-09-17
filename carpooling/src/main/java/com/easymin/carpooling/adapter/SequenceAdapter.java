package com.easymin.carpooling.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.easymi.component.Config;
import com.easymi.component.utils.DensityUtil;
import com.easymi.component.utils.GlideCircleTransform;
import com.easymi.component.utils.Log;
import com.easymin.carpooling.R;
import com.easymin.carpooling.entity.Sequence;
import com.easymin.carpooling.widget.OnItemTouchListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Copyright (C), 2012-2018, Sichuan Xiaoka Technology Co., Ltd.
 * FileName:SequenceAdapter
 *
 * @Author: hufeng
 * Date: 2018/12/24 下午1:10
 * Description:
 * History:
 */

public class SequenceAdapter extends RecyclerView.Adapter<SequenceAdapter.ViewHolder> implements OnItemTouchListener {

    private static final String TAG = SequenceAdapter.class.getSimpleName();

    private List<Sequence> lists;
    private Context context;

    private ItemTouchHelper itemTouchHelper;

    /**
     * 最大最小位置
     */
    private int minPos = -1;
    private int maxPos = -1;

    /**
     * 构造器
     *
     * @param context
     */
    public SequenceAdapter(Context context) {
        this.context = context;
        lists = new ArrayList<>();
    }

    /**
     * 设置排序列表
     *
     * @param sequences
     */
    public void setSequences(List<Sequence> sequences) {
        this.lists = sequences;
        notifyDataSetChanged();
    }

    /**
     * 拖动排序帮助类
     *
     * @param itemTouchHelper
     */
    public void setItemTouchHelper(ItemTouchHelper itemTouchHelper) {
        this.itemTouchHelper = itemTouchHelper;
    }

    /**
     * 设置最小最大数
     *
     * @param min
     * @param max
     */
    public void setMinAndMax(int min, int max) {

//        if (min > lists.size() - 1
//                || min < 0) {
//            min = -1;
//        }
//        if (max > lists.size() - 1
//                || max < 0) {
//            max = -1;
//        }
        minPos = min;
        maxPos = max;

        Log.e(TAG, "max:" + maxPos + "----" + "min:" + minPos);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.cp_sequence_item, parent, false);
        FrameLayout frameLayout = new FrameLayout(context);
        int width = (DensityUtil.getDisplayWidth(context) - DensityUtil.dp2px(context, 40)) / 8;
        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(width, FrameLayout.LayoutParams.WRAP_CONTENT);
        params.gravity = Gravity.CENTER;
        frameLayout.addView(view, params);

        return new ViewHolder(frameLayout);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Sequence sequence = lists.get(position);

        holder.linSeqNum.setOnTouchListener((v, event) -> {
            Log.e(TAG, "----" + event.getAction() + "\n-----" + event.getActionMasked());

            if (position > minPos) {
                if (event.getActionMasked() == MotionEvent.ACTION_DOWN) {
                    itemTouchHelper.startDrag(holder);
                }
            }
            return false;
        });

        if (sequence.type == 1) {
            holder.linSeqNum.setVisibility(View.VISIBLE);

            if (position > minPos) {
                holder.seqTxt.setVisibility(View.GONE);
                holder.seqImg.setVisibility(View.GONE);
                holder.tvStatus.setVisibility(View.GONE);
                holder.linSeqNum.getBackground().setAlpha(128);
            } else {
                holder.seqTxt.setVisibility(View.GONE);
                holder.seqImg.setVisibility(View.GONE);
                holder.tvStatus.setVisibility(View.VISIBLE);
                holder.linSeqNum.getBackground().setAlpha(255);

                holder.tvSeqNum.setText(sequence.num+"");

                if (sequence.status == 0){
                    holder.tvSeqNum.setBackgroundResource(R.drawable.circle_dark_22);
                    holder.tvStatus.setText("上车");
                }else {
                    holder.tvSeqNum.setBackgroundResource(R.drawable.circle_accent);
                    holder.tvStatus.setText("下车");
                }

//                // 0 未接 1 已接 2 跳过接 3 未送 4 已送 5 跳过送
//                if (sequence.status == 1) {
//                    holder.tv_get.setText("已接");
//                } else if (sequence.status == 2) {
//                    holder.tv_get.setText("已跳过");
//                } else if (sequence.status == 4) {
//                    holder.tv_get.setText("已送");
//                } else if (sequence.status == 5) {
//                    holder.tv_get.setText("跳过送");
//                }
            }
        } else if (sequence.type == 2) {
            holder.seqTxt.setVisibility(View.VISIBLE);
            holder.seqImg.setVisibility(View.GONE);
            holder.linSeqNum.setVisibility(View.GONE);
            holder.seqTxt.setText(sequence.text);
        } else {
            holder.seqTxt.setVisibility(View.GONE);
            holder.seqImg.setVisibility(View.VISIBLE);
            holder.linSeqNum.setVisibility(View.GONE);
        }
    }

    @Override
    public int getItemCount() {
        return lists.size();
    }

    @Override
    public boolean onMove(int fromPosition, int toPosition) {
        Log.e(TAG, fromPosition + "----" + toPosition);

        if (maxPos != -1) {
            if (toPosition >= maxPos) {
                toPosition = maxPos - 1;
//                onMove(fromPosition,toPosition);
//                return false;
            }
        }
        if (minPos != -1) {
            if (toPosition <= minPos) {
                toPosition = minPos + 1;
//                onMove(fromPosition,toPosition);
//                return false;
            }
        }

        if (fromPosition < toPosition) {
            //从上往下拖动，每滑动一个item，都将list中的item向下交换，向上滑同理。
            for (int i = fromPosition; i < toPosition; i++) {
                Collections.swap(lists, i, i + 1);
            }
        } else {
            for (int i = fromPosition; i > toPosition; i--) {
                Collections.swap(lists, i, i - 1);
            }
        }
        notifyItemMoved(fromPosition, toPosition);
        //注意此处只是notifyItemMoved并没有notifyDataSetChanged
        //原因下面会说明
        return true;
    }

    @Override
    public void onSwiped(int position) {
        //侧滑时
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        View itemView;
        TextView seqTxt;
        ImageView seqImg;

        LinearLayout linSeqNum;
        TextView tvStatus;
        TextView tvSeqNum;

//        RelativeLayout rl_person;
//        ImageView iv_avater;
//        TextView tv_get;

        public ViewHolder(View itemView) {
            super(itemView);
            this.itemView = itemView;
            seqImg = itemView.findViewById(R.id.seq_car_img);
            seqTxt = itemView.findViewById(R.id.seq_text);

            linSeqNum = itemView.findViewById(R.id.lin_seq_num);
            tvStatus = itemView.findViewById(R.id.tv_status);
            tvSeqNum = itemView.findViewById(R.id.tv_seq_num);

//            rl_person = itemView.findViewById(R.id.rl_person);
//            iv_avater = itemView.findViewById(R.id.iv_avater);
//            tv_get = itemView.findViewById(R.id.tv_get);
        }
    }

    /**
     * 获取当前排序列表
     *
     * @return
     */
    public List<Sequence> getLists() {
        return lists;
    }
}

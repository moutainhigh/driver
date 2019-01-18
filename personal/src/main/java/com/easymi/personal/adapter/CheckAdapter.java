package com.easymi.personal.adapter;

import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.easymi.personal.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Copyright (C), 2012-2018, Sichuan Xiaoka Technology Co., Ltd.
 * FileName: CheckAdapter
 * @Author: shine
 * Date: 2018/12/24 下午1:10
 * Description:
 * History:
 */
public class CheckAdapter extends PagerAdapter {


    private List<String> strings;
    private Context context;
    /**
     * View重用缓存列表
     */
    private List<View> mViewList = new ArrayList<>();

    public CheckAdapter(Context context) {
        this.context = context;
        strings = new ArrayList<>();
        strings.add(context.getString(R.string.check_net_work));
        strings.add(context.getString(R.string.check_loc));
        strings.add(context.getString(R.string.check_notice));
    }

    @Override
    public int getCount() {
        return strings.size();
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }

    @Override
    public Object instantiateItem(ViewGroup container, final int position) {
        View view;
        final ViewHolder holder;
        if (mViewList.isEmpty()) {
            view = View.inflate(context, R.layout.check_item, null);
            holder = new ViewHolder(view);

            view.setTag(holder);
        } else {
            //存在可重用的视图
            //获取可重用的视图，并从缓存中移除
            view = mViewList.remove(0);
            holder = (ViewHolder) view.getTag();
        }

        String string = strings.get(position);
        holder.checkType.setText(string);
        if (position == 1) {
            holder.leftImg.setImageResource(R.mipmap.ic_launcher);
        } else if (position == 2) {
            holder.leftImg.setImageResource(R.mipmap.ic_launcher);
        } else if (position == 3) {
            holder.leftImg.setImageResource(R.mipmap.ic_launcher);
        }
        //添加到ViewPager中，并显示
        container.addView(view);
        return view;
    }

    @Override
    public int getItemPosition(Object object) {
        return POSITION_NONE;
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        ImageView leftImg;
        TextView checkType;

        public ViewHolder(View itemView) {
            super(itemView);

            leftImg = itemView.findViewById(R.id.left_img);
            checkType = itemView.findViewById(R.id.check_type);
        }
    }
}

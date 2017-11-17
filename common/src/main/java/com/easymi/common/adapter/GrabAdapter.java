package com.easymi.common.adapter;

import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.view.View;
import android.view.ViewGroup;

import com.easymi.common.R;
import com.easymi.common.entity.BaseOrder;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by developerLzh on 2017/11/3 0003.
 */

public class GrabAdapter extends PagerAdapter {

    private List<BaseOrder> baseOrderList;
    private Context context;

    private List<View> mViewList = new ArrayList<>();   //View重用缓存列表

    public GrabAdapter(Context context) {
        this.context = context;
        baseOrderList = new ArrayList<>();
    }

    public void setBaseOrderList(List<BaseOrder> baseOrderList) {
        this.baseOrderList = baseOrderList;
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return baseOrderList.size();
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        View view;
        final Holder holder;

        if (mViewList.isEmpty()) {
            //不存在可重用的视图，new 视图
            holder = new Holder();
            view = View.inflate(context, R.layout.grab_item, null);
//            holder.dingDanId = (TextView) view.findViewById(R.id.dingDan_Id);

            view.setTag(holder);
        } else {
            //存在可重用的视图
            view = mViewList.remove(0); //获取可重用的视图，并从缓存中移除
            holder = (Holder) view.getTag();
        }

        //设置view上相关数据
        final BaseOrder baseOrder = baseOrderList.get(position);

        //添加到ViewPager中，并显示
        container.addView(view);
        return view;

    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        View view = (View) object;
        container.removeView(view); //从ViewPager中移除view
        mViewList.add(view);    //移除的view添加到回收缓存列表
    }

    @Override
    public int getItemPosition(Object object) {
        return POSITION_NONE;
    }

    /**
     * 销毁Adapter里所有资源
     */
    public void destroy() {
        mViewList.clear();  //清空缓存队列
    }

    class Holder{

    }
}

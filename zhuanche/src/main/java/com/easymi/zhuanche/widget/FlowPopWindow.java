package com.easymi.zhuanche.widget;

import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.easymi.zhuanche.R;

/**
 * Copyright (C), 2012-2018, Sichuan Xiaoka Technology Co., Ltd.
 * FileName: FlowPopWindow
 * @Author: shine
 * Date: 2018/12/24 下午1:10
 * Description: 跑单右上角弹窗
 * History:
 */
public class FlowPopWindow extends PopupWindow implements View.OnClickListener {

    Context context;
    View anchor;

    /**
     * 在window中无引力
     */
    private int popupGravity = Gravity.NO_GRAVITY;
    /**
     * /显示view的宽度
     */
    private int viewWidth;


    private OnMenuClickListener mOnMenuClickListener;

    /**
     * 点击监听
     */
    public interface OnMenuClickListener {
        /**
         * 监听对应view点击事件
         * @param view
         */
        void setMenuOnClickListener(View view);
    }


    public void setOnClickListener(OnMenuClickListener onMenuClickListener) {
        mOnMenuClickListener = onMenuClickListener;
    }

    /**
     * 取消订单按钮
     */
    TextView cancel;
    /**
     * 联系客服按钮
     */
    TextView contract;

    /**
     * 构造器 初始化popwindow
     * @param context
     */
    public FlowPopWindow(Context context) {
        this.context = context;
        //设置可以获得焦点
        setFocusable(true);
        //设置弹窗内可点击
        setTouchable(true);
        //设置弹窗外可点击
        setOutsideTouchable(true);

        //设置弹窗的宽度和高度,否则不会正常显示
        setWidth(ViewGroup.LayoutParams.WRAP_CONTENT);
        setHeight(ViewGroup.LayoutParams.WRAP_CONTENT);

        //设置背景,否则不会消失
        setBackgroundDrawable(new ColorDrawable());

        //设置需要显示的veiw
        View view = View.inflate(context, R.layout.zc_flow_pop_layout, null);
        setContentView(view);

        //在没有绘制出来前,测量控件的尺寸
        //设置测量模式
        int w = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED);
        int h = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED);
        //开始测量
        view.measure(w, h);
        //得到实际的宽度
        viewWidth = view.getMeasuredWidth();

        //设置监听
        //取消订单
        cancel = view.findViewById(R.id.pop_cancel_order);
        //联系后台
        contract = view.findViewById(R.id.pop_contract_service);

        cancel.setOnClickListener(this);
        contract.setOnClickListener(this);

    }

    /**
     * 隐藏取消订单
     */
    public void hideCancel() {
        if (null != cancel) {
            cancel.setVisibility(View.GONE);
        }
    }

    /**
     * 显示取消订单
     */
    public void showCancel() {
        if (null != cancel) {
            cancel.setVisibility(View.VISIBLE);
        }
    }

    /**
     * 文本点击事件监听
     * @param v
     */
    @Override
    public void onClick(View v) {

        if (mOnMenuClickListener == null) {
            return;
        }
        //执行回调
        mOnMenuClickListener.setMenuOnClickListener(v);
        dismiss();
    }


    /**
     * 显示popup内容
     *
     * @param anchor popupWindow绑定的控件,即,点击了anchor这个控件后弹出PopupWindow
     */
    public void show(View anchor) {

        this.anchor = anchor;

        //存放位置坐标
        int[] mLocation = new int[2];

        //将anchor右上角坐标存放在mlocation数组中
        anchor.getLocationInWindow(mLocation);

        //获取anchor左右padding
        int leftPadding = anchor.getPaddingLeft();
        int rightPadding = anchor.getPaddingRight();
        //anchor内容宽度
        int w = anchor.getWidth() - leftPadding - rightPadding;

        //无引力设置,设置显示PopupWindow的位置
        showAtLocation(anchor, popupGravity, mLocation[0] + anchor.getWidth() - viewWidth, mLocation[1] + anchor.getHeight());

    }

    /**
     * 消失时候调用该方法
     */
    @Override
    public void dismiss() {
        super.dismiss();
    }

}

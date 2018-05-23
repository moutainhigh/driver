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
 *
 */
public class FlowPopWindow extends PopupWindow implements View.OnClickListener {

    Context context;
    View anchor;

    private int popupGravity = Gravity.NO_GRAVITY;  //在window中无引力

    private int viewWidth;  //显示view的宽度


    private OnMenuClickListener mOnMenuClickListener;

    public interface OnMenuClickListener {
        void setMenuOnClickListener(View view);
    }


    public void setOnClickListener(OnMenuClickListener onMenuClickListener) {
        mOnMenuClickListener = onMenuClickListener;
    }

    TextView cancel;
    TextView contract;
    TextView same;
    TextView consumer;

    public FlowPopWindow(Context context) {
        this.context = context;

        setFocusable(true);  //设置可以获得焦点
        setTouchable(true); //设置弹窗内可点击
        setOutsideTouchable(true);    //设置弹窗外可点击

        //设置弹窗的宽度和高度,否则不会正常显示
        setWidth(ViewGroup.LayoutParams.WRAP_CONTENT);
        setHeight(ViewGroup.LayoutParams.WRAP_CONTENT);

//        setBackgroundDrawable(new BitmapDrawable());
        setBackgroundDrawable(new ColorDrawable()); //设置背景,否则不会消失

        //设置需要显示的veiw
        View view = View.inflate(context, R.layout.zc_flow_pop_layout, null);
        setContentView(view);

        //在没有绘制出来前,测量控件的尺寸
        int w = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED);  //设置测量模式
        int h = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED);
        view.measure(w, h);  //开始测量
        viewWidth = view.getMeasuredWidth();    //得到实际的宽度

        //设置监听
        cancel = view.findViewById(R.id.pop_cancel_order);   //取消订单
        contract = view.findViewById(R.id.pop_contract_service);    //联系后台
        same = view.findViewById(R.id.pop_same_order);        //同单司机
        consumer = view.findViewById(R.id.pop_consumer_msg);   //客户信息

        cancel.setOnClickListener(this);
        contract.setOnClickListener(this);
        consumer.setOnClickListener(this);
        same.setOnClickListener(this);

    }

    public void hideCancel() {
        if (null != cancel) {
            cancel.setVisibility(View.GONE);
        }
    }

    public void showCancel() {
        if (null != cancel) {
            cancel.setVisibility(View.VISIBLE);
        }
    }

    public void hideSame() {
        if (null != same) {
            same.setVisibility(View.GONE);
        }
    }

    public void showSame() {
        if (null != same) {
            same.setVisibility(View.VISIBLE);
        }
    }

    public void showConsumer(){
        if (null != consumer) {
            consumer.setVisibility(View.VISIBLE);
        }
    }

    public void hideConsumer(){
        if (null != consumer) {
            consumer.setVisibility(View.GONE);
        }
    }

    //文本点击事件监听
    @Override
    public void onClick(View v) {

        if (mOnMenuClickListener == null) {
            return;
        }

        mOnMenuClickListener.setMenuOnClickListener(v); //执行回调
        dismiss();

    }


    /**
     * 显示popup内容
     *
     * @param anchor popupWindow绑定的控件,即,点击了anchor这个控件后弹出PopupWindow
     */
    public void show(View anchor) {

        this.anchor = anchor;

        int[] mLocation = new int[2];   //存放位置坐标

        anchor.getLocationInWindow(mLocation);  //将anchor右上角坐标存放在mlocation数组中

        //获取anchor左右padding
        int leftPadding = anchor.getPaddingLeft();
        int rightPadding = anchor.getPaddingRight();
        int w = anchor.getWidth() - leftPadding - rightPadding; //anchor内容宽度

        //无引力设置,设置显示PopupWindow的位置
        showAtLocation(anchor, popupGravity, mLocation[0] + anchor.getWidth() - viewWidth, mLocation[1] + anchor.getHeight());

    }

    //消失时候调用该方法
    @Override
    public void dismiss() {
        super.dismiss();
    }

}

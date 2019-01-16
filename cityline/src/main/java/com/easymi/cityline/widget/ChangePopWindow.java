package com.easymi.cityline.widget;

import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.easymi.cityline.R;

/**
 *
 * @author hufeng
 */
public class ChangePopWindow extends PopupWindow implements View.OnClickListener {

    Context context;
    View anchor;

    /**
     * 在window中无引力
     */
    private int popupGravity = Gravity.NO_GRAVITY;

    /**
     * 显示view的宽度
     */
    private int viewWidth;


    private OnMenuClickListener mOnMenuClickListener;

    /**
     * 点击listener
     */
    public interface OnMenuClickListener {
        /**
         * 实现菜单子项点击监听
         * @param view
         */
        void setMenuOnClickListener(View view);
    }

    /**
     *  设置监听
     * @param onMenuClickListener
     */
    public void setOnClickListener(OnMenuClickListener onMenuClickListener) {
        mOnMenuClickListener = onMenuClickListener;
    }

    TextView send;
    TextView accept;

    public void hideAccept(){
        accept.setVisibility(View.GONE);
    }

    public ChangePopWindow(Context context) {
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

//        setBackgroundDrawable(new BitmapDrawable());
        //设置背景,否则不会消失
        setBackgroundDrawable(new ColorDrawable());

        //设置需要显示的veiw
        View view = View.inflate(context, R.layout.flow_change_pop_layout, null);
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
        accept = view.findViewById(R.id.pop_change_accept);
        //联系后台
        send = view.findViewById(R.id.pop_change_send);

        accept.setOnClickListener(this);
        send.setOnClickListener(this);

    }


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

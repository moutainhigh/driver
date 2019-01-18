package com.easymi.common.widget;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import com.alibaba.android.arouter.launcher.ARouter;
import com.easymi.common.R;
import com.easymi.common.activity.CreateActivity;
import com.easymi.component.Config;
import com.easymi.component.utils.EmUtil;
import com.easymi.component.utils.ToastUtil;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Copyright (C), 2012-2018, Sichuan Xiaoka Technology Co., Ltd.
 * FileName: MakeOrderPopWindow
 *@Author: shine
 * Date: 2018/11/19 上午10:04
 * Description:
 * History:
 * @author hufeng
 */
public class MakeOrderPopWindow extends PopupWindow implements View.OnClickListener{

    Context context;
    View anchor;

    /**
     * 在window中无引力
     */
    private int popupGravity = Gravity.NO_GRAVITY;

    /**
     *  显示view的宽度
     */
    private int viewWidth;

    /**
     *  创建数据
     */
    List<HashMap<String,Object>> listdata = new ArrayList<HashMap<String,Object>>();

    private OnMenuClickListener mOnMenuClickListener;

    public interface OnMenuClickListener {
        /**
         * 按钮点击监听
         * @param view
         */
        void setMenuOnClickListener(View view);
    }

    public void setOnClickListener(OnMenuClickListener onMenuClickListener) {
        mOnMenuClickListener = onMenuClickListener;
    }

    ListView listview;

    /**
     *  设置弹窗信息
     * @param context
     */
    public MakeOrderPopWindow(Context context) {
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
        View view = View.inflate(context, R.layout.make_order_popwindow, null);
        setContentView(view);

        //在没有绘制出来前,测量控件的尺寸 //设置测量模式
        int w = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED);
        int h = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED);
        //开始测量
        view.measure(w, h);
        //得到实际的宽度
        viewWidth = view.getMeasuredWidth();

        //设置监听
        listview = view.findViewById(R.id.listview);

        initData();
        initAdapter();
    }

    /**
     * 设置数据
     */
    public void initData(){
        String orderType = EmUtil.getEmployInfo().serviceType;
        String[] types = orderType.split(",");
        for (int i = 0;i<types.length;i++){
            HashMap<String,Object> map = new HashMap<String,Object>();
            map.put("type",types[i]);
            switch (types[i]){
                case Config.ZHUANCHE:
                    map.put("name",context.getResources().getString(R.string.create_zhuanche));
                    break;
                case Config.CITY_LINE:
                    map.put("name",context.getResources().getString(R.string.create_zhuanxian));
                    break;
            }
            listdata.add(map);
        }
    }

    /**
     * 设置适配器
     */
    public void initAdapter(){
        SimpleAdapter adapter=new SimpleAdapter(context,
                listdata,
                R.layout.item_popwindow,
                new String[]{"name"},
                new int[]{R.id.tv_type}
        );

        listview.setAdapter(adapter);
        //设置监听器
        listview.setOnItemClickListener(new mItemClick());
    }

    /**
     * 点击事件回调监听
     */
    class mItemClick implements AdapterView.OnItemClickListener
    {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int positon, long arg3) {
            HashMap<String,Object> map=(HashMap<String,Object>)parent.getItemAtPosition(positon);
            if (TextUtils.equals((String) map.get("type"),Config.CITY_LINE)){
                ARouter.getInstance().build("/cityline/CreateOrderActivity").navigation();
            }else {
                Intent intent = new Intent(context, CreateActivity.class);
                intent.putExtra("type",(String) map.get("type"));
                context.startActivity(intent);
            }
        }
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

package com.easymin.custombus.dialog;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.drawable.ColorDrawable;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomSheetBehavior;
import android.support.design.widget.BottomSheetDialog;
import android.support.design.widget.CoordinatorLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.easymi.component.utils.TimeUtil;
import com.easymi.component.utils.UIDisplayHelper;
import com.easymin.custombus.R;
import com.easymin.custombus.entity.TimeBean;

import java.util.List;

public class DateSelectDialog extends BottomSheetDialog implements View.OnClickListener {

    private BottomSheetBehavior<View> behavior;
    private ImageView dialogDateSelectIv;
    private RecyclerView dialogDateSelectRv;
    private List<TimeBean> data;

    public DateSelectDialog(@NonNull Context context, List<TimeBean> timeBeans, OnDateSelectDialogClickListener onDateSelectDialogClickListener) {
        super(context);
        View view = LayoutInflater.from(context).inflate(R.layout.dialog_date_select, null);
        this.data = timeBeans;
        dialogDateSelectIv = view.findViewById(R.id.dialogDateSelectIv);
        dialogDateSelectIv.setOnClickListener(this);
        dialogDateSelectRv = view.findViewById(R.id.dialogDateSelectRv);
        setContentView(view);
        dialogDateSelectRv.setLayoutManager(new LinearLayoutManager(context));
        BaseQuickAdapter<TimeBean, BaseViewHolder> adapter =
                new BaseQuickAdapter<TimeBean, BaseViewHolder>(R.layout.dialog_date_select_item, data) {
                    @Override
                    protected void convert(BaseViewHolder helper, TimeBean item) {
                        helper.setText(R.id.dialogDataSelectItemTv, TimeUtil.getTime("MM月dd日", item.getTimeStamp()) + " " + item.getDesc());
                    }
                };
        adapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                if (onDateSelectDialogClickListener != null) {
                    onDateSelectDialogClickListener.onClick((TimeBean) adapter.getData().get(position));
                }
                dismiss();
            }
        });
        dialogDateSelectRv.setAdapter(adapter);
    }

    private int getHeight() {
        int height = Resources.getSystem().getDisplayMetrics().heightPixels;
        if (getContext() != null) {
            WindowManager wm = (WindowManager) getContext().getSystemService(Context.WINDOW_SERVICE);
            Point point = new Point();
            if (wm != null) {
                wm.getDefaultDisplay().getSize(point);
                height = point.y;
            }
        }
        return height;
    }

    @Override
    public void onStart() {
        super.onStart();
        View bottomSheet = getDelegate().findViewById(R.id.design_bottom_sheet);
        if (bottomSheet != null) {
            bottomSheet.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            CoordinatorLayout.LayoutParams layoutParams = (CoordinatorLayout.LayoutParams) bottomSheet.getLayoutParams();

            int fragmentHeight = 0;
            //最多6个 虽少2个
            int totalCount = data.size() > 6 ? 6 : data.size();
            //24是iv 40是textview56是bt 12是recyclerView底部padding
            int totalHeight = UIDisplayHelper.dpToPx(totalCount * 60 + 24 + 40 + 12);

            //getHeight是除了statusbar的高度
            if (totalHeight > getHeight()) {
                fragmentHeight = (int) (Resources.getSystem().getDisplayMetrics().heightPixels * 0.7);
            } else {
                fragmentHeight = totalHeight;
            }
            layoutParams.height = fragmentHeight;
            bottomSheet.setLayoutParams(layoutParams);
            behavior = BottomSheetBehavior.from(bottomSheet);
            //默认有peekHeight 可能进来时候看不全
            behavior.setPeekHeight(fragmentHeight);
            behavior.setState(BottomSheetBehavior.STATE_EXPANDED);
        }
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.dialogDateSelectIv) {
            dismiss();
        }
    }

    public interface OnDateSelectDialogClickListener {
        void onClick(TimeBean str);
    }

}

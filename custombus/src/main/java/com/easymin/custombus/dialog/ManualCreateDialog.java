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
import com.easymi.component.utils.UIDisplayHelper;
import com.easymin.custombus.R;
import com.easymin.custombus.entity.ManualCreateLineBean;

import java.util.List;

public class ManualCreateDialog extends BottomSheetDialog implements View.OnClickListener {

    private BottomSheetBehavior<View> behavior;
    private ImageView dialogManualCreateIv;
    private RecyclerView dialogManualCreateRv;
    private List<ManualCreateLineBean> data;

    public ManualCreateDialog(@NonNull Context context, List<ManualCreateLineBean> manualCreateBeans, OnManualCreateDialogClickListener onManualCreateDialogClickListener) {
        super(context);
        View view = LayoutInflater.from(context).inflate(R.layout.dialog_manual_create, null);
        this.data = manualCreateBeans;
        dialogManualCreateIv = view.findViewById(R.id.dialogManualCreateIv);
        dialogManualCreateIv.setOnClickListener(this);
        dialogManualCreateRv = view.findViewById(R.id.dialogManualCreateRv);
        setContentView(view);
        dialogManualCreateRv.setLayoutManager(new LinearLayoutManager(context));
        BaseQuickAdapter<ManualCreateLineBean, BaseViewHolder> adapter =
                new BaseQuickAdapter<ManualCreateLineBean, BaseViewHolder>(R.layout.dialog_manual_create_item, data) {
            @Override
            protected void convert(BaseViewHolder helper, ManualCreateLineBean item) {
                helper.setText(R.id.dialogManualCreateItemTv, item.name);
            }
        };
        adapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                if (onManualCreateDialogClickListener != null) {
                    onManualCreateDialogClickListener.onClick((ManualCreateLineBean) adapter.getData().get(position));
                }
                dismiss();
            }
        });
        dialogManualCreateRv.setAdapter(adapter);
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
        if (v.getId() == R.id.dialogManualCreateIv) {
            dismiss();
        }
    }

    public interface OnManualCreateDialogClickListener {
        void onClick(ManualCreateLineBean manualCreateLineBean);
    }

}

package com.easymi.zhuanche.fragment;

import android.content.Context;
import android.content.Intent;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.easymi.component.ZCOrderStatus;
import com.easymi.component.entity.DymOrder;
import com.easymi.component.utils.StringUtils;
import com.easymi.component.widget.CusBottomSheetDialog;
import com.easymi.component.widget.LoadingButton;
import com.easymi.zhuanche.R;
import com.easymi.zhuanche.activity.FeeDetailActivity;
import com.easymi.zhuanche.entity.ZCOrder;
import com.easymi.zhuanche.flowMvp.ActFraCommBridge;

/**
 * Copyright (C), 2012-2018, Sichuan Xiaoka Technology Co., Ltd.
 * FileName: SettleFragmentDialog
 *
 * @Author: shine
 * Date: 2018/12/24 下午1:10
 * Description:
 * History:
 */
public class SettleFragmentDialog {

    ImageView closeFragment;
    TextView needPayText;
    EditText remarkEdit;
    TextView dialogTitle;
    LoadingButton confirmBtn;
    LoadingButton payButton;
    TextView feeDetail;

    private Context context;
    /**
     * 专车订单
     */
    ZCOrder zcOrder;
    /**
     * 本地动态订单
     */
    DymOrder dymOrder;

    /**
     * activity和fragment的通信接口
     */
    ActFraCommBridge bridge;
    CusBottomSheetDialog dialog;
    private LinearLayout zcSettleDialogLl;

    /**
     * @param context 上下文
     * @param zcOrder 专车订单
     * @param bridge  通信接口
     */
    public SettleFragmentDialog(Context context, ZCOrder zcOrder, ActFraCommBridge bridge) {
        this.context = context;
        this.zcOrder = zcOrder;
        this.bridge = bridge;
        dymOrder = DymOrder.findByIDType(zcOrder.orderId, zcOrder.serviceType);
        if (null == dymOrder) {
            if (zcOrder.orderFee != null) {
                dymOrder = zcOrder.orderFee;
            } else {
                return;
            }
        }
        View view = LayoutInflater.from(context).inflate(R.layout.zc_settle_dialog, null, false);
        closeFragment = view.findViewById(R.id.close_fragment);
        needPayText = view.findViewById(R.id.need_pay_money);
        remarkEdit = view.findViewById(R.id.remark);
        confirmBtn = view.findViewById(R.id.confirm_button);
        payButton = view.findViewById(R.id.pay_button);
        feeDetail = view.findViewById(R.id.fee_detail);
        dialogTitle = view.findViewById(R.id.dialog_title);
        zcSettleDialogLl = view.findViewById(R.id.zcSettleDialogLl);
        dialog = new CusBottomSheetDialog(context);
        initView();
        dialog.setCancelable(false);
        dialog.setContentView(view);
    }

    /**
     * 初始化edittext监听
     */
    private void initEdit() {
        remarkEdit.setOnFocusChangeListener((v, hasFocus) -> {
            if (hasFocus) {
                if (StringUtils.isNotBlank(remarkEdit.getText().toString())) {
                    remarkEdit.setSelection(remarkEdit.getText().toString().length());
                }
            }
        });

        addEditWatcher();
    }

    /**
     * 添加edittext监听回调
     */
    private void addEditWatcher() {
        remarkEdit.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (null != s && StringUtils.isNotBlank(s.toString())) {
                    dymOrder.remark = s.toString();
                    remarkEdit.setSelection(s.toString().length());
                }
            }
        });
    }

    /**
     * 显示弹窗
     */
    public void show() {
        dialog.show();
    }

    /**
     * 设置代驾订单
     *
     * @param zcOrder
     */
    public void setDjOrder(ZCOrder zcOrder) {
        this.zcOrder = zcOrder;
        initView();
    }

    /**
     * 关闭弹窗
     */
    public void dismiss() {
        dialog.dismiss();
    }

    /**
     * 初始化布局
     */
    private void initView() {
        initEdit();
        if (zcOrder.orderStatus == ZCOrderStatus.GOTO_DESTINATION_ORDER) {
            DymOrder dOrder = DymOrder.findByIDType(zcOrder.orderId, zcOrder.serviceType);
            if (dOrder == null) {
                return;
            }
            confirmBtn.setVisibility(View.VISIBLE);
            payButton.setVisibility(View.GONE);
            dialogTitle.setText(context.getString(R.string.confirm_money));
            needPayText.setText(String.valueOf(dOrder.totalFee));
        } else {
            confirmBtn.setVisibility(View.GONE);
            payButton.setVisibility(View.VISIBLE);
            dialogTitle.setText(context.getString(R.string.settle));
            needPayText.setText(String.valueOf(dymOrder.totalFee));
            if (!TextUtils.isEmpty(dymOrder.remark)) {
                remarkEdit.setText(dymOrder.remark);
                remarkEdit.setEnabled(false);
                zcSettleDialogLl.setVisibility(View.VISIBLE);
            } else {
                zcSettleDialogLl.setVisibility(View.GONE);
            }

        }

        confirmBtn.setOnClickListener(v -> {
            if (zcOrder.orderStatus == ZCOrderStatus.GOTO_DESTINATION_ORDER) {
                if (null != bridge) {
                    bridge.doPay(dymOrder.orderShouldPay);
                }
            }
        });
        payButton.setOnClickListener(v -> {
            if (null != bridge) {
                bridge.doPay(dymOrder.orderShouldPay);
            }
        });
        closeFragment.setOnClickListener(v -> {
            bridge.doFinish();
            dialog.dismiss();
        });

        feeDetail.setOnClickListener(v -> {
            bridge.toFeeDetail();
            Intent intent = new Intent(context, FeeDetailActivity.class);
            intent.putExtra("dymOrder", dymOrder);
            intent.putExtra("zcOrder", zcOrder);
            context.startActivity(intent);
        });
    }

    public boolean isShowing() {
        return dialog.isShowing();
    }

}

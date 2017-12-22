package com.easymi.daijia.fragment;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.easymi.component.DJOrderStatus;
import com.easymi.component.utils.StringUtils;
import com.easymi.component.widget.CusBottomSheetDialog;
import com.easymi.component.widget.LoadingButton;
import com.easymi.daijia.R;
import com.easymi.daijia.activity.FeeDetailActivity;
import com.easymi.daijia.entity.DJOrder;
import com.easymi.daijia.flowMvp.ActFraCommBridge;

/**
 * Created by developerLzh on 2017/11/20 0020.
 */

public class SettleFragmentDialog {

    ImageView closeFragment;
    TextView prepayMoneyText;
    TextView needPayText;
    EditText extraFeeEdit;
    EditText paymentEdit;
    TextView carNoText;
    EditText remarkEdit;

    LoadingButton confirmBtn;

    DJOrder djOrder;

    ActFraCommBridge bridge;

    private Context context;

    private CusBottomSheetDialog dialog;

    TextView feeDetail;

    public SettleFragmentDialog(Context context, DJOrder djOrder, ActFraCommBridge bridge) {
        this.context = context;
        this.djOrder = djOrder;
        this.bridge = bridge;
        View view = LayoutInflater.from(context).inflate(R.layout.settle_dialog, null, false);
        closeFragment = view.findViewById(R.id.close_fragment);
        prepayMoneyText = view.findViewById(R.id.prepay_money);
        needPayText = view.findViewById(R.id.need_pay_money);
        extraFeeEdit = view.findViewById(R.id.extra_fee);
        paymentEdit = view.findViewById(R.id.payment_fee);
        carNoText = view.findViewById(R.id.car_no);
        remarkEdit = view.findViewById(R.id.remark);
        confirmBtn = view.findViewById(R.id.confirm_button);
        feeDetail = view.findViewById(R.id.fee_detail);

        extraFeeEdit.setSelection(extraFeeEdit.getText().toString().length());
        paymentEdit.setSelection(paymentEdit.getText().toString().length());

        dialog = new CusBottomSheetDialog(context);
        initView();
        dialog.setCancelable(false);
        dialog.setContentView(view);
    }

    public void show() {
        dialog.show();
    }

    public void dismiss() {
        dialog.dismiss();
    }

    private void initView() {

        if (djOrder.orderStatus == DJOrderStatus.GOTO_DESTINATION_ORDER) {
            confirmBtn.setText(context.getString(R.string.confirm_money));
        } else {
            confirmBtn.setText(context.getString(R.string.dai_fu));
        }

        confirmBtn.setOnClickListener(v -> {
            if (djOrder.orderStatus == DJOrderStatus.GOTO_DESTINATION_ORDER) {
                if (null != bridge) {
                    bridge.doConfirmMoney(confirmBtn);
                }
            } else {
                if (null != bridge) {
                    bridge.doPay();
                }
            }
        });
        closeFragment.setOnClickListener(v -> dialog.dismiss());

        feeDetail.setOnClickListener(v -> {
            Intent intent = new Intent(context, FeeDetailActivity.class);
            intent.putExtra("djOrder", djOrder);
            context.startActivity(intent);
        });

        setText();
    }

    private void setText() {
        prepayMoneyText.setText(String.valueOf(djOrder.budgetFee));
        needPayText.setText(String.valueOf(djOrder.orderMoney));

        if (StringUtils.isBlank(djOrder.carNo)) {
            carNoText.setVisibility(View.GONE);
        } else {
            carNoText.setText(djOrder.carNo);
            carNoText.setVisibility(View.VISIBLE);
        }
    }
}

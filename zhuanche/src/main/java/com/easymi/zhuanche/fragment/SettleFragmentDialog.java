package com.easymi.zhuanche.fragment;

import android.content.Context;
import android.content.Intent;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.easymi.component.ZCOrderStatus;
import com.easymi.component.entity.DymOrder;
import com.easymi.component.entity.Setting;
import com.easymi.component.utils.Log;
import com.easymi.component.utils.MathUtil;
import com.easymi.component.utils.StringUtils;
import com.easymi.component.widget.CusBottomSheetDialog;
import com.easymi.component.widget.LoadingButton;
import com.easymi.zhuanche.R;
import com.easymi.zhuanche.activity.FeeDetailActivity;
import com.easymi.zhuanche.entity.ZCOrder;
import com.easymi.zhuanche.flowMvp.ActFraCommBridge;

import java.math.RoundingMode;
import java.text.DecimalFormat;

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
    TextView dialogTitle;
    TextView addedHint;

    LoadingButton confirmBtn;
    LoadingButton payButton;

    ZCOrder zcOrder;
    DymOrder dymOrder;

    ActFraCommBridge bridge;
    private Context context;
    private CusBottomSheetDialog dialog;

    TextView feeDetail;

    private double extraFee = 0.0;
    private double paymentFee = 0.0;
    private String remark = "";

    public SettleFragmentDialog(Context context, ZCOrder zcOrder, ActFraCommBridge bridge) {
        this.context = context;
        this.zcOrder = zcOrder;
        this.bridge = bridge;
        dymOrder = DymOrder.findByIDType(zcOrder.orderId, zcOrder.orderType);
        if (null == dymOrder) {
            if (zcOrder.orderFee != null) {
                dymOrder = zcOrder.orderFee;
            } else {
                return;
            }
        }
        View view = LayoutInflater.from(context).inflate(R.layout.settle_dialog, null, false);
        closeFragment = view.findViewById(R.id.close_fragment);
        prepayMoneyText = view.findViewById(R.id.prepay_money);
        needPayText = view.findViewById(R.id.need_pay_money);
        extraFeeEdit = view.findViewById(R.id.extra_fee);
        paymentEdit = view.findViewById(R.id.payment_fee);
        carNoText = view.findViewById(R.id.car_no);
        remarkEdit = view.findViewById(R.id.remark);
        confirmBtn = view.findViewById(R.id.confirm_button);
        payButton = view.findViewById(R.id.pay_button);
        feeDetail = view.findViewById(R.id.fee_detail);
        dialogTitle = view.findViewById(R.id.dialog_title);
        addedHint = view.findViewById(R.id.added_hint);

        extraFeeEdit.setSelection(extraFeeEdit.getText().toString().length());
        paymentEdit.setSelection(paymentEdit.getText().toString().length());

        dialog = new CusBottomSheetDialog(context);
        initView();
        dialog.setCancelable(false);
        dialog.setContentView(view);
    }

    private void initEdit() {
        boolean canChangeFee = Setting.findOne().employChangePrice == 1;
        if (!canChangeFee) {
            extraFeeEdit.setEnabled(false);
            paymentEdit.setEnabled(false);
            addedHint.setVisibility(View.GONE);
            return;
        }
        extraFeeEdit.setOnFocusChangeListener((view, hasFocus) -> {
            if (hasFocus) {
                String s = extraFeeEdit.getText().toString();
                if (StringUtils.isNotBlank(s)) {
                    extraFeeEdit.setSelection(s.length());

                    if (s.equals("0")) {
                        extraFeeEdit.setText("");
                    }
                }
                Log.e("SettleFragmentDialog", "extraFeeEdit has focus");
            } else {
                Log.e("SettleFragmentDialog", "extraFeeEdit lose focus");
            }
        });
        paymentEdit.setOnFocusChangeListener((v, hasFocus) -> {
            if (hasFocus) {
                String s = paymentEdit.getText().toString();
                if (StringUtils.isNotBlank(s)) {
                    paymentEdit.setSelection(s.length());

                    if (s.equals("0")) {
                        paymentEdit.setText("");
                    }
                }
                Log.e("SettleFragmentDialog", "paymentEdit has focus");
            } else {
                Log.e("SettleFragmentDialog", "paymentEdit lose focus");
            }
        });
        remarkEdit.setOnFocusChangeListener((v, hasFocus) -> {
            if (hasFocus) {
                if (StringUtils.isNotBlank(remarkEdit.getText().toString())) {
                    remarkEdit.setSelection(remarkEdit.getText().toString().length());
                }
            }
        });

        addEditWatcher();
    }

    private void addEditWatcher() {
        DecimalFormat decimalFormat = new DecimalFormat("#0.0");
        decimalFormat.setRoundingMode(RoundingMode.DOWN);
        extraFeeEdit.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (null != s && StringUtils.isNotBlank(s.toString())) {
                    String str = s.toString();
                    if (str.startsWith("0")) {
                        if (str.length() >= 2) {
                            if (!String.valueOf(str.charAt(1)).equals(".")) {
                                str = str.substring(1, str.length());
                                extraFeeEdit.setText(str);
                                extraFeeEdit.setSelection(str.length());
                            }
                        }
                    }
                    extraFee = Double.parseDouble(s.toString());
                    if (!MathUtil.isDoubleLegal(extraFee, 1)) {
                        extraFeeEdit.setText(decimalFormat.format(extraFee));
                        extraFeeEdit.setSelection(decimalFormat.format(extraFee).length());
                    }
                    if (extraFee > 1000) {
                        extraFeeEdit.setText("" + 1000);
                        extraFeeEdit.setSelection(4);
                    }
                    calcMoney();
                } else {
                    extraFeeEdit.setText("0");
                    extraFeeEdit.setSelection(1);
                }
            }
        });
        paymentEdit.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s != null && StringUtils.isNotBlank(s.toString())) {
                    String str = s.toString();
                    if (str.startsWith("0")) {
                        if (str.length() >= 2) {
                            if (!String.valueOf(str.charAt(1)).equals(".")) {
                                str = str.substring(1, str.length());
                                paymentEdit.setText(str);
                                paymentEdit.setSelection(str.length());
                            }
                        }
                    }
                    paymentFee = Double.parseDouble(s.toString());
                    if (!MathUtil.isDoubleLegal(paymentFee, 1)) {
                        paymentEdit.setText(decimalFormat.format(paymentFee));
                        paymentEdit.setSelection(decimalFormat.format(paymentFee).length());
                    }
                    if (paymentFee > 1000) {
                        paymentEdit.setText("" + 1000);
                        paymentEdit.setSelection(4);
                    }
                    calcMoney();
                } else {
                    paymentEdit.setText("0");
                    paymentEdit.setSelection(1);
                }
            }
        });
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

    public void show() {
        dialog.show();
    }

    public void setDjOrder(ZCOrder zcOrder) {
        this.zcOrder = zcOrder;
        initView();
    }

    public void dismiss() {
        dialog.dismiss();
    }

    private void initView() {
        initEdit();
        if (zcOrder.orderStatus == ZCOrderStatus.GOTO_DESTINATION_ORDER) {
            confirmBtn.setVisibility(View.VISIBLE);
            payButton.setVisibility(View.GONE);
            dialogTitle.setText(context.getString(R.string.confirm_money));
        } else {
            confirmBtn.setVisibility(View.GONE);
            payButton.setVisibility(View.VISIBLE);
            dialogTitle.setText(context.getString(R.string.settle));
            extraFeeEdit.setText(String.valueOf(dymOrder.extraFee));
            paymentEdit.setText(String.valueOf(dymOrder.paymentFee));
            remarkEdit.setText(dymOrder.remark);
            prepayMoneyText.setText(String.valueOf(dymOrder.prepay));
            needPayText.setText(String.valueOf(dymOrder.orderShouldPay));
            extraFeeEdit.setEnabled(false);
            paymentEdit.setEnabled(false);
            remarkEdit.setEnabled(false);

        }

        confirmBtn.setOnClickListener(v -> {
            if (zcOrder.orderStatus == ZCOrderStatus.GOTO_DESTINATION_ORDER) {
                if (null != bridge) {
                    bridge.doConfirmMoney(confirmBtn, dymOrder);
                }
            }
        });
        payButton.setOnClickListener(v -> {
            if (null != bridge) {
                bridge.doPay(dymOrder.orderShouldPay);
            }
        });
        closeFragment.setOnClickListener(v -> {
            if (zcOrder.orderStatus == ZCOrderStatus.ARRIVAL_DESTINATION_ORDER) {
                bridge.doFinish();
            }
            dialog.dismiss();
        });

        feeDetail.setOnClickListener(v -> {
            bridge.toFeeDetail();
            Intent intent = new Intent(context, FeeDetailActivity.class);
            intent.putExtra("dymOrder", dymOrder);
            intent.putExtra("zcOrder", zcOrder);
            context.startActivity(intent);
        });

        setText();
    }

    private void setText() {
        calcMoney();

        if (StringUtils.isBlank(zcOrder.carNo)) {
            carNoText.setVisibility(View.GONE);
        } else {
            carNoText.setText(zcOrder.carNo);
            carNoText.setVisibility(View.VISIBLE);
        }
    }

    public void setDymOrder(DymOrder dymOrder) {
        this.dymOrder = dymOrder;
        calcMoney();
    }

//    /**
//     * 附加费相当于就是totalFee的一部分 在不影响之前逻辑的情况下 他没有被加到totalFee里面去
//     */
//    private void calcMoney() {
//        DecimalFormat df = new DecimalFormat("#0.0");
//        if (zcOrder.orderStatus == ZCOrderStatus.ARRIVAL_DESTINATION_ORDER) {//到达于目的地后就无需计算了
//            return;
//        }
//        dymOrder.extraFee = extraFee;
//        dymOrder.paymentFee = paymentFee;
//        dymOrder.remark = remark;
//        dymOrder.prepay = zcOrder.prepay;
//        if (zcOrder.coupon != null) {
//            if (zcOrder.coupon.couponType == 2) {
//                dymOrder.couponFee = zcOrder.coupon.deductible;
//            } else if (zcOrder.coupon.couponType == 1) {
//                dymOrder.couponFee = Double.parseDouble(df.format((dymOrder.totalFee + dymOrder.extraFee) * (100 - zcOrder.coupon.discount) / 100));//附加费参与优惠券打折
//            }
//
//        }
//        dymOrder.orderTotalFee = Double.parseDouble(df.format(dymOrder.totalFee + dymOrder.extraFee + dymOrder.paymentFee));
//
//        double prepay = dymOrder.prepay;
//        double otherFee = Double.parseDouble(df.format(dymOrder.paymentFee));//附加费因为要参与优惠券打折，所以这里就不需要加附加费了
//
//        double leftMoney = Double.parseDouble(df.format(prepay - otherFee));
//        if (leftMoney >= 0) {//预付的钱大于附加的钱 相当于预付的钱就是这么减剩的
//            prepay = leftMoney;
//            otherFee = 0;
//        } else {//预付的钱小于附加的钱 相当于附加的钱就是减剩的
//            prepay = 0;
//            otherFee = -leftMoney;
//        }
//        //开始计算优惠券
//        double shouldPay = Double.parseDouble(df.format(dymOrder.totalFee + dymOrder.extraFee - dymOrder.couponFee));//附加费参与优惠券抵扣
//        if (shouldPay > 0) {
//            shouldPay -= prepay;
//        } else {
//            dymOrder.couponFee = dymOrder.totalFee + dymOrder.extraFee;
//            shouldPay = -prepay;
//        }
//        //最后加上其他费用
//        shouldPay += otherFee;
//        dymOrder.orderShouldPay = Double.parseDouble(df.format(shouldPay));
//
//        if (dymOrder.orderShouldPay < dymOrder.minestMoney) {//低于最低消费金额时
//            dymOrder.orderShouldPay = dymOrder.minestMoney;
//        }
//
//        prepayMoneyText.setText(String.valueOf(dymOrder.prepay));
//        needPayText.setText(String.valueOf(dymOrder.orderShouldPay));
//    }

    private void calcMoney() {
        DecimalFormat df = new DecimalFormat("#0.0");
        if (zcOrder.orderStatus == ZCOrderStatus.ARRIVAL_DESTINATION_ORDER) {//到达于目的地后就无需计算了
            return;
        }
        dymOrder.extraFee = extraFee;
        dymOrder.paymentFee = paymentFee;
        dymOrder.remark = remark;
        dymOrder.prepay = zcOrder.prepay;

        dymOrder.orderTotalFee = Double.parseDouble(df.format(dymOrder.totalFee + dymOrder.extraFee + dymOrder.paymentFee));

        double canCouponMoney = dymOrder.totalFee + dymOrder.extraFee;//可以参与优惠券抵扣的钱
        if (canCouponMoney < dymOrder.minestMoney) {
            canCouponMoney = dymOrder.minestMoney;
        }
        if (zcOrder.coupon != null) {
            if (zcOrder.coupon.couponType == 2) {
                dymOrder.couponFee = zcOrder.coupon.deductible;
            } else if (zcOrder.coupon.couponType == 1) {
                dymOrder.couponFee = Double.parseDouble(df.format(canCouponMoney * (100 - zcOrder.coupon.discount) / 100));
            }
        }
        double exls = Double.parseDouble(df.format(canCouponMoney - dymOrder.couponFee));//打折抵扣后应付的钱
        if (exls < 0) {
            exls = 0;//优惠券不退钱
        }
        dymOrder.orderShouldPay = Double.parseDouble(df.format(exls + paymentFee - dymOrder.prepay));

        prepayMoneyText.setText(String.valueOf(dymOrder.prepay));
        needPayText.setText(String.valueOf(dymOrder.orderShouldPay));
    }

    public boolean isShowing() {
        return dialog.isShowing();
    }


}

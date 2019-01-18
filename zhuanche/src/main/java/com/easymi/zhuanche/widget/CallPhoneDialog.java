package com.easymi.zhuanche.widget;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageButton;

import com.easymi.component.utils.PhoneUtil;
import com.easymi.component.utils.StringUtils;
import com.easymi.zhuanche.R;
import com.easymi.zhuanche.entity.ZCOrder;

/**
 * Copyright (C), 2012-2018, Sichuan Xiaoka Technology Co., Ltd.
 * FileName:
 * @Author: shine
 * Date: 2018/12/24 下午1:10
 * Description:
 * History:
 */

public class CallPhoneDialog extends Dialog {

    public CallPhoneDialog(@NonNull Context context) {
        super(context);
    }

    public CallPhoneDialog(@NonNull Context context, int themeResId) {
        super(context, themeResId);
    }

    protected CallPhoneDialog(@NonNull Context context, boolean cancelable, @Nullable OnCancelListener cancelListener) {
        super(context, cancelable, cancelListener);
    }

    @Override
    protected void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        getWindow().setGravity(Gravity.CENTER); //

        WindowManager m = getWindow().getWindowManager();
        Display d = m.getDefaultDisplay();
        WindowManager.LayoutParams p = getWindow().getAttributes();
        p.width = d.getWidth(); //设置dialog的宽度为当前手机屏幕的宽度
        getWindow().setAttributes(p);
    }

    public static class Builder {

        private Context context;

        private CallPhoneDialog dialog;

        private ImageButton callUser;
        private ImageButton callPassenger;
        private Button cancel;

        private ZCOrder zcOrder;

        public Builder(Context context, ZCOrder zcOrder) {
            this.context = context;
            this.zcOrder = zcOrder;
        }

        public CallPhoneDialog create() {
            dialog = new CallPhoneDialog(context, R.style.Dialog);
            View view = LayoutInflater.from(context).inflate(R.layout.zc_call_phone_dialog, null, true);
            callUser = view.findViewById(R.id.call_user);
            callPassenger = view.findViewById(R.id.call_passenger);
            cancel = view.findViewById(R.id.cancel);

            callUser.setOnClickListener(view1 -> {
                dialog.dismiss();
                PhoneUtil.call((Activity) context, zcOrder.userPhone);
            });
            callPassenger.setOnClickListener(view12 -> {
                dialog.dismiss();
                PhoneUtil.call((Activity) context, zcOrder.passengerPhone);
            });
            cancel.setOnClickListener(view13 -> dialog.dismiss());

            dialog.setContentView(view);

            return dialog;
        }
    }


    public static void callDialog(Activity context, ZCOrder zcOrder) {
        if (StringUtils.isNotBlank(zcOrder.userPhone)) {
            CallPhoneDialog dialog = new CallPhoneDialog.Builder(context, zcOrder).create();
            dialog.show();
        } else {
            PhoneUtil.call(context, zcOrder.passengerPhone);
        }
    }


}

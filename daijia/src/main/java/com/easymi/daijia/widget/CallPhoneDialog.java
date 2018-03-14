package com.easymi.daijia.widget;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;

import com.easymi.component.entity.DymOrder;
import com.easymi.component.utils.PhoneUtil;
import com.easymi.component.utils.StringUtils;
import com.easymi.daijia.R;
import com.easymi.daijia.entity.DJOrder;

/**
 * Created by liuzihao on 2018/3/13.
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

        private DJOrder djOrder;

        public Builder(Context context, DJOrder djOrder) {
            this.context = context;
            this.djOrder = djOrder;
        }

        public CallPhoneDialog create() {
            dialog = new CallPhoneDialog(context, R.style.Dialog);
            View view = LayoutInflater.from(context).inflate(R.layout.call_phone_dialog, null, true);
            callUser = view.findViewById(R.id.call_user);
            callPassenger = view.findViewById(R.id.call_passenger);
            cancel = view.findViewById(R.id.cancel);

            callUser.setOnClickListener(view1 -> {
                dialog.dismiss();
                PhoneUtil.call((Activity) context, djOrder.userPhone);
            });
            callPassenger.setOnClickListener(view12 -> {
                dialog.dismiss();
                PhoneUtil.call((Activity) context, djOrder.passengerPhone);
            });
            cancel.setOnClickListener(view13 -> dialog.dismiss());

            dialog.setContentView(view);

            return dialog;
        }
    }


    public static void callDialog(Activity context, DJOrder djOrder) {
        if (StringUtils.isNotBlank(djOrder.userPhone)) {
            CallPhoneDialog dialog = new CallPhoneDialog.Builder(context, djOrder).create();
            dialog.show();
        } else {
            PhoneUtil.call(context, djOrder.passengerPhone);
        }
    }


}

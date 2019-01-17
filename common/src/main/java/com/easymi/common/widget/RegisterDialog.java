package com.easymi.common.widget;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.easymi.common.R;
import com.easymi.component.app.XApp;
import com.easymi.component.utils.EmUtil;

/**
 * @author hufeng
 */
public class RegisterDialog extends DialogFragment {

    public static RegisterDialog newInstance(String title, String content, String btnTxt, boolean showClose) {
        RegisterDialog dialog = new RegisterDialog();
        Bundle arguments = new Bundle();
        arguments.putString("title", title);
        arguments.putString("content", content);
        arguments.putString("btnTxt", btnTxt);
        arguments.putBoolean("showClose", showClose);
        dialog.setArguments(arguments);
        return dialog;
    }

    private OnClickListener listener;

    public RegisterDialog setOnClickListener(OnClickListener listener) {
        this.listener = listener;
        return this;
    }

    @Override
    public void onStart() {
        super.onStart();
        try {
            int dividerId = getResources().getIdentifier("android:id/titleDivider", null, null);
            View divider = getDialog().findViewById(dividerId);
            if (divider != null) {
                divider.setBackgroundColor(Color.TRANSPARENT);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        getDialog().setCancelable(false);
        Window ww = getDialog().getWindow();
        if (ww != null) {
            WindowManager.LayoutParams lp = ww.getAttributes();
            ww.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            lp.gravity = Gravity.CENTER;
            ww.setAttributes(lp);
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.com_dialog_register, container, false);
        view.findViewById(R.id.ensure).setOnClickListener(v -> {
            if (listener != null) {
                listener.onClick();
            }
        });

        ImageView imv = view.findViewById(R.id.close);
        TextView tvTitle = view.findViewById(R.id.title);
        TextView tvContent = view.findViewById(R.id.content);
        Button btnEnsure = view.findViewById(R.id.ensure);

        imv.setOnClickListener(v -> EmUtil.employLogout(getContext()));

        Bundle arguments = getArguments();
        if (arguments != null) {
            String title = arguments.getString("title");
            String content = arguments.getString("content");
            String btnTxt = arguments.getString("btnTxt");
            boolean showClose = arguments.getBoolean("showClose");
            if (showClose) {
                imv.setVisibility(View.VISIBLE);
            } else {
                imv.setVisibility(View.GONE);
            }
            tvTitle.setText(title);
            tvContent.setText(content);
            btnEnsure.setText(btnTxt);
        }
        return view;
    }

    /**
     * 显示弹窗
     * @return
     */
    public boolean isShow() {
        return getDialog() != null && getDialog().isShowing();
    }

    public interface OnClickListener {
        /**
         * 点击事件监听
         */
        void onClick();
    }

}

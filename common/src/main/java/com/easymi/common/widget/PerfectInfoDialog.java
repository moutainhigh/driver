package com.easymi.common.widget;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;

import com.easymi.common.R;

public class PerfectInfoDialog extends DialogFragment{

    public static PerfectInfoDialog newInstance() {
        return new PerfectInfoDialog();
    }

    private OnPerfectInfoClickListener listener;

    public PerfectInfoDialog setOnPerfectInfoClickListener(OnPerfectInfoClickListener listener) {
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
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.com_dialog_perfect_info, container, false);
        view.findViewById(R.id.ensure).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (listener != null) {
                    listener.OnPerfectInfoClick();
                }
            }
        });
        return view;
    }

    public interface OnPerfectInfoClickListener {
        void OnPerfectInfoClick();
    }



}

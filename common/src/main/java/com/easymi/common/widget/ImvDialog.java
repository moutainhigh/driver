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
import android.widget.ImageView;

import com.easymi.common.R;

public class ImvDialog extends DialogFragment{

    public static ImvDialog newInstance(int imvId) {
        ImvDialog dialog = new ImvDialog();
        Bundle arguments = new Bundle();
        arguments.putInt("imvId", imvId);
        dialog.setArguments(arguments);
        return dialog;
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
        View view = inflater.inflate(R.layout.com_dialog_imv, container, false);
        ImageView imageView = view.findViewById(R.id.imv);
        Bundle arguments = getArguments();
        if (arguments != null) {
            imageView.setImageResource(arguments.getInt("imvId"));
        }
        return view;
    }


}

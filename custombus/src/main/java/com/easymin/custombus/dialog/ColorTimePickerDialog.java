package com.easymin.custombus.dialog;

import android.app.Dialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.widget.TextView;

import com.easymin.custombus.R;
import com.jzxiang.pickerview.TimePickerDialog;

public class ColorTimePickerDialog extends TimePickerDialog {

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Dialog dialog = super.onCreateDialog(savedInstanceState);
        TextView cancel = dialog.findViewById(R.id.tv_cancel);
        cancel.setTextColor(ContextCompat.getColor(getContext(), R.color.colorSub));
        TextView sure = dialog.findViewById(R.id.tv_sure);
        sure.setTextColor(ContextCompat.getColor(getContext(), R.color.colorBlue));
        TextView title = dialog.findViewById(R.id.tv_title);
        title.setTextColor(ContextCompat.getColor(getContext(), R.color.colorBlack));
        return dialog;
    }
}

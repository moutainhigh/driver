package com.easymi.common.widget;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;

public abstract class CommonDialog extends AlertDialog {
    private long id;

    public void setId(long id) {
        this.id = id;
    }

    public long getId() {
        return id;
    }

    protected CommonDialog(@NonNull Context context, int layoutId) {
        super(context, 0);
        View view = LayoutInflater.from(context).inflate(layoutId, null, false);
        setView(view);
        initData(view);
    }

    public abstract void initData(View view);

    @Override
    public void show() {
        super.show();
        if (getWindow() != null && getWindow().getDecorView() != null) {
            getWindow().getDecorView().setBackground(null);
            getWindow().getDecorView().setPadding(0, 0, 0, 0);
        }
    }
}

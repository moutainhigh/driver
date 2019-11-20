package com.easymi.personal.widget;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.Button;

import com.easymi.component.widget.dialog.BaseCenterDialog;
import com.easymi.personal.R;
import com.easymi.personal.adapter.ScrollSchedulAdapter;

import java.util.ArrayList;

/**
 * @Copyright (C), 2012-2019, Sichuan Xiaoka Technology Co., Ltd.
 * @FileName: ScrollSchedulDialog
 * @Author: hufeng
 * @Date: 2019/11/21 上午12:27
 * @Description:
 * @History:
 */
public class ScrollSchedulDialog extends BaseCenterDialog {

    RecyclerView recyclerView;
    Button btn_cancel;
    Button btn_sure;

    ArrayList<String> datas = new ArrayList<>();

    ScrollSchedulAdapter adapter;

    public ScrollSchedulDialog(Context context) {
        super(context);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_scroll_schedul);
        recyclerView = findViewById(R.id.recyclerView);
        btn_cancel = findViewById(R.id.btn_cancel);
        btn_sure = findViewById(R.id.btn_sure);

        adapter = new ScrollSchedulAdapter(getContext());

        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);

        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(adapter);


        for (int i = 0 ;i<4;i++){
            datas.add("sssssssssss");
        }
        adapter.setScheduls(datas);
    }
}

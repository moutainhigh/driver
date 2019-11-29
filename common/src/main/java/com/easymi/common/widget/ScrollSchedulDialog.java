package com.easymi.common.widget;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;

import com.easymi.common.R;
import com.easymi.common.adapter.ScrollSchedulAdapter;
import com.easymi.common.entity.ScrollSchedul;
import com.easymi.component.app.XApp;
import com.easymi.component.entity.ZCSetting;
import com.easymi.component.widget.dialog.BaseCenterDialog;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

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

    ArrayList<ScrollSchedul> datas;

    ScrollSchedulAdapter adapter;

    private long lineId = 0;

    /**
     * 标志位 1 点听单弹出  2 班次跑完弹出
     */
    private int flag;

    public ScrollSchedulDialog(Context context,int flag) {
        super(context);
        datas = new ArrayList<>();
        this.flag = flag;
    }

    public void setDatas(ArrayList<ScrollSchedul> list) {
        this.datas = list;
        adapter.setScheduls(datas);
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

        adapter.setOnItemOnClickListener(position -> {
            for (int i = 0; i < datas.size(); i++) {
                if (i == position){
                    datas.get(i).select = true;
                    lineId = datas.get(i).id;
                }else {
                    datas.get(i).select = false;
                }
            }
            adapter.notifyDataSetChanged();
            btn_sure.setBackgroundResource(R.drawable.com_corners_blue);
        });

        btn_cancel.setOnClickListener(v -> {
            if (getOnMyClickListener() != null){
                getOnMyClickListener().onItemClick(v,"");
            }
            dismiss();
        });

        btn_sure.setOnClickListener(v -> {
            if (getOnMyClickListener() != null){
                getOnMyClickListener().onItemClick(v,"");
            }
        });

//        if (flag == 2){
//            setCuntdownTime();
//        }
    }

    /**
     * 获取选中的线路id
     */
    public long getSelectLineId(){
        return lineId;
    }

    /**
     * 倒计时计时器
     */
    Timer timer;
    TimerTask timerTask;

    /**
     * 取消定时器
     */
    public void cancelTimer() {
        if (timer != null) {
            timer.cancel();
            timer = null;
        }
        if (timerTask != null) {
            timerTask.cancel();
            timerTask = null;
        }
    }

    /**
     * 等待时间
     */
    private long timeSeq = 60;

    /**
     * 等待倒计时
     */
    public void setCuntdownTime() {
        cancelTimer();
        timer = new Timer();
        timerTask = new TimerTask() {
            @Override
            public void run() {
                timeSeq--;
                if (timeSeq>=0){
                    btn_cancel.setText("下线("+timeSeq+"s)");
                }else {
                    cancelTimer();
                    dismiss();
                }
            }
        };
        timer.schedule(timerTask, 0, 1000);
    }

}

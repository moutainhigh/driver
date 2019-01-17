package com.easymi.personal.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.easymi.component.Config;
import com.easymi.component.base.RxBaseActivity;
import com.easymi.component.network.ApiManager;
import com.easymi.component.network.HttpResultFunc;
import com.easymi.component.network.MySubscriber;
import com.easymi.component.utils.EmUtil;
import com.easymi.component.utils.TimeUtil;
import com.easymi.component.widget.CusToolbar;
import com.easymi.personal.McService;
import com.easymi.personal.R;
import com.easymi.personal.entity.Announcement;
import com.easymi.personal.entity.Notifity;
import com.easymi.personal.result.AnnouncementResult;
import com.easymi.personal.result.NotifityResult;

import java.util.List;

import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Copyright (C), 2012-2018, Sichuan Xiaoka Technology Co., Ltd.
 * FileName: FinishActivity
 * Author: shine
 * Date: 2018/12/24 下午1:10
 * Description:
 * History:
 */


public class MsgActivity extends RxBaseActivity {

    TextView noticeContent;
    TextView noticeTime;
    TextView announcementContent;
    TextView announcementTime;

    CusToolbar toolbar;

    @Override
    public int getLayoutId() {
        return R.layout.activity_message;
    }

    @Override
    public void initViews(Bundle savedInstanceState) {
        noticeContent = findViewById(R.id.notice_content);
        noticeTime = findViewById(R.id.notice_time);
        announcementContent = findViewById(R.id.announcement_content);
        announcementTime = findViewById(R.id.announcement_time);

        queryNotify();
        queryAnn();
    }

    @Override
    public void initToolBar() {
        toolbar = findViewById(R.id.cus_toolbar);
        toolbar.setLeftIcon(R.drawable.ic_arrow_back, view -> onBackPressed());
        toolbar.setTitle(R.string.my_message);
    }

    public void tuijian(View view) {
//        Intent intent = new Intent(MsgActivity.this, ArticleActivity.class);
//        intent.putExtra("tag", "DriverPromotion");
//        intent.putExtra("title", getString(R.string.person_tuiguang));
//        startActivity(intent);
    }

    public void toNotice(View view) {
        startActivity(new Intent(MsgActivity.this, NotifityActivity.class));
    }

    public void toAnnouncement(View view) {
        startActivity(new Intent(MsgActivity.this, AnnouncementActivity.class));
    }

    private void queryNotify() {
        Observable<NotifityResult> observable = ApiManager.getInstance().createApi(Config.HOST, McService.class)
                .notices( 1, 10)
                .filter(new HttpResultFunc<>())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());

        mRxManager.add(observable.subscribe(new MySubscriber<>(this, false,
                false, notifityResult -> {
            List<Notifity> notifityList = notifityResult.data;
            if (null != notifityList && notifityList.size() != 0) {
                Notifity notifity = notifityList.get(0);
                noticeContent.setText(notifity.message);
                noticeTime.setText(TimeUtil.getTime("yyy-MM-dd HH:mm", notifity.time * 1000));
            }
        })));
    }

    private void queryAnn() {
        Observable<AnnouncementResult> observable = ApiManager.getInstance().createApi(Config.HOST, McService.class)
                .employAffiches(1, 10)
                .filter(new HttpResultFunc<>())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());

        mRxManager.add(observable.subscribe(new MySubscriber<>(this, false,
                false, notifityResult -> {
            List<Announcement> announcements = notifityResult.employAffiches;
            if (null != announcements && announcements.size() != 0) {
                Announcement announcement = announcements.get(0);
                announcementContent.setText(announcement.message);
                announcementTime.setText(TimeUtil.getTime("yyy-MM-dd HH:mm", announcement.time * 1000));
            }
        })));
    }

    @Override
    public boolean isEnableSwipe() {
        return true;
    }
}

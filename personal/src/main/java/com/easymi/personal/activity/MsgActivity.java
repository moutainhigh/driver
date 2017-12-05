package com.easymi.personal.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.easymi.component.base.RxBaseActivity;
import com.easymi.personal.R;

/**
 * Created by developerLzh on 2017/11/9 0009.
 */

public class MsgActivity extends RxBaseActivity {
    @Override
    public int getLayoutId() {
        return R.layout.activity_message;
    }

    @Override
    public void initViews(Bundle savedInstanceState) {

    }

    public void tuijian(View view){
        Intent intent = new Intent(MsgActivity.this, ArticleActivity.class);
        intent.putExtra("tag", "DriverPromotion");
        intent.putExtra("title", getString(R.string.person_tuiguang));
        startActivity(intent);
    }

    public void toNotice(View view){
        startActivity(new Intent(MsgActivity.this,NotifityActivity.class));
    }

    public void toAnnouncement(View view){
        startActivity(new Intent(MsgActivity.this,AnnouncementActivity.class));
    }
}

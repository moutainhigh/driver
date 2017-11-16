package com.easymi.personal.activity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.alibaba.android.arouter.launcher.ARouter;
import com.easymi.component.app.ActivityManager;
import com.easymi.component.base.RxBaseActivity;
import com.easymi.component.utils.PhoneUtil;
import com.easymi.personal.R;

/**
 * Created by developerLzh on 2017/11/6 0006.
 */
@Route(path = "/personal/SetActivity")
public class SetActivity extends RxBaseActivity {
    @Override
    public int getLayoutId() {
        return R.layout.activity_set;
    }

    @Override
    public void initViews(Bundle savedInstanceState) {
    }

    public void changePsw(View view){
        Intent intent = new Intent(this,ChangeActivity.class);
        startActivity(intent);
    }

    public void choiceLanguage(View view){
        Intent intent = new Intent(this,SetActivity.class);
        startActivity(intent);
    }

    public void toStats(View view){
        Intent intent = new Intent(this,StatsActivity.class);
        startActivity(intent);
    }

    public void helpCenter(View view){
        Intent intent = new Intent(this,HelpCenterActivity.class);
        startActivity(intent);
    }

    public void feedBack(View view){
        Intent intent = new Intent(this,FeedbackActivity.class);
        startActivity(intent);
    }

    public void contractUs(View view){
//        Intent intent = new Intent(this,)
        PhoneUtil.call(this,"18148140090");
    }

    public void aboutUs(View view){
        Intent intent = new Intent(this,ShareActivity.class);
        startActivity(intent);
    }

    public void logOut(View view){
        AlertDialog dialog = new AlertDialog.Builder(this)
                .setTitle(getString(R.string.set_hint))
                .setMessage(getString(R.string.set_sure_exit))
                .setPositiveButton(getString(R.string.set_sure), (dialogInterface, i) -> {
                    ActivityManager.getInstance().finishAllActivity();
                    startActivity(new Intent(SetActivity.this,LoginActivity.class));
                })
                .setNegativeButton(getString(R.string.set_cancel), (dialogInterface, i) -> dialogInterface.dismiss())
                .create();
        dialog.show();

    }
}

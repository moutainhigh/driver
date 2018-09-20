package com.easymi.component.base;

import android.content.Intent;
import android.content.IntentFilter;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v7.app.AlertDialog;
import android.view.View;

import com.easymi.component.R;
import com.easymi.component.app.ActManager;
import com.easymi.component.app.XApp;
import com.easymi.component.receiver.GpsReceiver;
import com.easymi.component.receiver.NetWorkChangeReceiver;
import com.easymi.component.rxmvp.RxManager;
import com.easymi.component.utils.SysUtil;
import com.easymi.component.widget.swipeback.ikew.SwipeBackActivityBase;
import com.easymi.component.widget.swipeback.ikew.SwipeBackActivityHelper;
import com.easymi.component.widget.swipeback.ikew.Utils;
import com.easymi.component.utils.NetUtil;
import com.easymi.component.utils.PhoneFunc;
import com.easymi.component.utils.ToastUtil;
import com.trello.rxlifecycle.components.support.RxAppCompatActivity;

/**
 * Created by hcc on 16/8/7 21:18
 * 100332338@qq.com
 * <p/>
 * Activity基类
 */
public abstract class RxBaseActivity extends RxAppCompatActivity implements
        GpsReceiver.OnGpsStatusChangeListener,
        NetWorkChangeReceiver.OnNetChange, SwipeBackActivityBase {

    protected RxManager mRxManager;
    private GpsReceiver gpsReceiver;

    private NetWorkChangeReceiver netChangeReceiver;

    private SwipeBackActivityHelper mHelper;

    protected long lastChangeTime = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

//        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR);

        mRxManager = new RxManager();
        ActManager.getInstance().addActivity(this);

        // TODO 2018/9/14 原始的侧滑返回问题严重，最大问题是不支持API28 ，暂时移除
        // TODO 见https://www.jianshu.com/p/26fac8d30058?from=groupmessage
//        if (isEnableSwipe()) {
//            mHelper = new SwipeBackActivityHelper(this);
//            mHelper.onActivityCreate();
//        }

        setContentView(getLayoutId());


        //初始化控件
        initViews(savedInstanceState);
        //初始化ToolBar
        initToolBar();

    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        if (null != mHelper) {
            mHelper.onPostCreate();
        }
    }

    @Override
    public <T extends View> T findViewById(int id) {
        T v = super.findViewById(id);
        if (v == null && mHelper != null)
            return mHelper.findViewById(id);
        return v;
    }

    @Override
    public com.easymi.component.widget.swipeback.ikew.SwipeBackLayout getSwipeBackLayout() {
        return mHelper.getSwipeBackLayout();
    }

    @Override
    public void setSwipeBackEnable(boolean enable) {
        getSwipeBackLayout().setEnableGesture(enable);
    }

    @Override
    public void scrollToFinishActivity() {
        Utils.convertActivityToTranslucent(this);
        getSwipeBackLayout().scrollToFinishActivity();
    }

    public abstract boolean isEnableSwipe();

    @Override
    protected void onStart() {
        super.onStart();

        gpsReceiver = new GpsReceiver();
        gpsReceiver.setListener(this);
        IntentFilter intentFilter = new IntentFilter(LocationManager.PROVIDERS_CHANGED_ACTION);
        registerReceiver(gpsReceiver, intentFilter);

        netChangeReceiver = new NetWorkChangeReceiver();
        netChangeReceiver.setEvent(this);
        IntentFilter netFilter = new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);
        registerReceiver(netChangeReceiver, netFilter);

    }

    @Override
    protected void onResume() {
        super.onResume();
        //回到前台时停止播放静音音频
        XApp.getInstance().stopPlaySlientMusic();
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (SysUtil.isRunningInBackground(this)) {//后台运行时 静音播放音频保活
            XApp.getInstance().playSlientMusic();
        }
    }

    @Override
    protected void onStop() {

        if (null != netDialog && netDialog.isShowing()) {
            netDialog.dismiss();
        }
        if (null != gpsDialog && gpsDialog.isShowing()) {
            gpsDialog.dismiss();
        }

        super.onStop();
        unregisterReceiver(gpsReceiver);
        unregisterReceiver(netChangeReceiver);
    }

    /**
     * 设置布局layout
     *
     * @return
     */
    public abstract int getLayoutId();

    /**
     * 初始化views
     *
     * @param savedInstanceState
     */
    public abstract void initViews(Bundle savedInstanceState);

    /**
     * 初始化toolbar
     */
    public void initToolBar() {

    }

    /**
     * 加载数据
     */
    public void loadData() {
    }

    /**
     * 显示进度条
     */
    public void showProgressBar() {
    }

    /**
     * 隐藏进度条
     */
    public void hideProgressBar() {
    }

    /**
     * 初始化recyclerView
     */
    public void initRecyclerView() {
    }

    /**
     * 初始化refreshLayout
     */
    public void initRefreshLayout() {
    }

    /**
     * 设置数据显示
     */
    public void finishTask() {
    }

    @Override
    protected void onDestroy() {
        mRxManager.clear();
        super.onDestroy();
    }

    private AlertDialog gpsDialog;

    /**
     * 提醒用户前往设置界面开启GPS
     */
    @Override
    public void showGpsState(boolean isOpen) {
        if (!isOpen) {
            if (null == gpsDialog) {
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setMessage(getResources().getString(R.string.please_open_gps));
                builder.setNegativeButton(getResources().getString(R.string.ok),
                        (dialog, which) -> {
                            if (!PhoneFunc.isGPSEnable(RxBaseActivity.this)) {
                                try {
                                    if ("ZTE".equalsIgnoreCase(Build.MANUFACTURER)) {
                                        ToastUtil.showMessage(RxBaseActivity.this, getResources().getString(R.string.please_open_gps));
                                    } else {
                                        startActivity(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS));
                                    }
                                } catch (Exception e) {
                                    ToastUtil.showMessage(RxBaseActivity.this, getResources().getString(R.string.please_open_gps));
                                }
                            } else {
                                dialog.dismiss();
                            }
                        });
                gpsDialog = builder.create();
            } else {
                if (gpsDialog.isShowing()) {
                    return;
                }
            }
            gpsDialog.show();
        } else {
            if (null != gpsDialog && gpsDialog.isShowing()) {
                gpsDialog.dismiss();
            }
        }

    }

    private AlertDialog netDialog;

    @Override
    public void onNetChange(int status) {
        if (status == NetUtil.NETWORK_NONE) {
            if (netDialog == null) {
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setMessage(getResources().getString(R.string.lost_net_work));
                builder.setPositiveButton(getResources().getString(R.string.ok), (dialogInterface, i) -> dialogInterface.dismiss());
                netDialog = builder.create();
            } else {
                if (netDialog.isShowing()) {
                    return;
                }
            }
            netDialog.show();
        } else {
            if (null != netDialog && netDialog.isShowing()) {
                netDialog.dismiss();
            }
        }

    }

    protected void choosePic(int x, int y) {

    }
}

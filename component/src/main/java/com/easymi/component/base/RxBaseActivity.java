package com.easymi.component.base;

import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ActivityInfo;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;

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
import com.easymi.component.widget.swipeback.other.SwipeBackLayout;
import com.easymi.component.utils.NetUtil;
import com.easymi.component.utils.PhoneFunc;
import com.easymi.component.utils.ToastUtil;
import com.luck.picture.lib.PictureSelector;
import com.luck.picture.lib.config.PictureConfig;
import com.luck.picture.lib.config.PictureMimeType;
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

        if (isEnableSwipe()) {
            mHelper = new SwipeBackActivityHelper(this);
            mHelper.onActivityCreate();
        }

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
        // 进入相册 以下是例子：用不到的api可以不写
        PictureSelector.create(RxBaseActivity.this)
                .openGallery(PictureMimeType.ofImage())//全部.PictureMimeType.ofAll()、图片.ofImage()、视频.ofVideo()
                .maxSelectNum(1)// 最大图片选择数量 int
                .minSelectNum(1)// 最小选择数量 int
                .imageSpanCount(4)// 每行显示个数 int
                .selectionMode(PictureConfig.SINGLE)// 多选 or 单选 PictureConfig.MULTIPLE or PictureConfig.SINGLE
                .isCamera(true)// 是否显示拍照按钮 true or false
                .isZoomAnim(true)// 图片列表点击 缩放效果 默认true
                .sizeMultiplier(0.5f)// glide 加载图片大小 0~1之间 如设置 .glideOverride()无效
                .setOutputCameraPath("/emdriver")// 自定义拍照保存路径,可不填
                .enableCrop(true)// 是否裁剪 true or false
                .compress(true)// 是否压缩 true or false
                .compressMode(PictureConfig.LUBAN_COMPRESS_MODE)//系统自带 or 鲁班压缩 PictureConfig.SYSTEM_COMPRESS_MODE or LUBAN_COMPRESS_MODE
                .withAspectRatio(x, y)// int 裁剪比例 如16:9 3:2 3:4 1:1 可自定义
                .hideBottomControls(false)// 是否显示uCrop工具栏，默认不显示 true or false
                .isGif(false)// 是否显示gif图片 true or false
                .freeStyleCropEnabled(true)// 裁剪框是否可拖拽 true or false
                .circleDimmedLayer(false)// 是否圆形裁剪 true or false
                .showCropFrame(true)// 是否显示裁剪矩形边框 圆形裁剪时建议设为false   true or false
                .showCropGrid(true)// 是否显示裁剪矩形网格 圆形裁剪时建议设为false    true or false
                .openClickSound(false)// 是否开启点击声音 true or false
                .previewEggs(false)// 预览图片时 是否增强左右滑动图片体验(图片滑动一半即可看到上一张是否选中) true or false
                .rotateEnabled(true) // 裁剪是否可旋转图片 true or false
                .scaleEnabled(true)// 裁剪是否可放大缩小图片 true or false
                .forResult(PictureConfig.CHOOSE_REQUEST);//结果回调onActivityResult code
    }
}

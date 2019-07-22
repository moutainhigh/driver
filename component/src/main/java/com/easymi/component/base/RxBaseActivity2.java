package com.easymi.component.base;

import android.app.Activity;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.view.View;

import com.easymi.component.R;
import com.easymi.component.app.ActManager;
import com.easymi.component.app.XApp;
import com.easymi.component.receiver.GpsReceiver;
import com.easymi.component.receiver.NetWorkChangeReceiver;
import com.easymi.component.rxmvp.RxManager;
import com.easymi.component.utils.AlexStatusBarUtils;
import com.easymi.component.utils.EmUtil;
import com.easymi.component.utils.NetUtil;
import com.easymi.component.utils.PermissionUtil;
import com.easymi.component.utils.PhoneFunc;
import com.easymi.component.utils.SysUtil;
import com.easymi.component.utils.ToastUtil;
import com.easymi.component.utils.UIStatusBarHelper;
import com.easymi.component.widget.swipeback.ikew.SwipeBackActivityBase;
import com.easymi.component.widget.swipeback.ikew.SwipeBackActivityHelper;
import com.easymi.component.widget.swipeback.ikew.Utils;
import com.luck.picture.lib.PictureSelector;
import com.luck.picture.lib.config.PictureConfig;
import com.luck.picture.lib.config.PictureMimeType;

/**
 * Created by hcc on 16/8/7 21:18
 * 100332338@qq.com
 * <p/>
 * Activity基类
 */
public abstract class RxBaseActivity2 extends Activity implements
        GpsReceiver.OnGpsStatusChangeListener,
        NetWorkChangeReceiver.OnNetChange, SwipeBackActivityBase {

    protected RxManager mRxManager;
    private GpsReceiver gpsReceiver;

    private NetWorkChangeReceiver netChangeReceiver;

    private SwipeBackActivityHelper mHelper;

    protected long lastChangeTime = 0;

    private PermissionUtil.PermissionCallBack permissionCallBack;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR);
        UIStatusBarHelper.setStatusBarLightMode(this);
        AlexStatusBarUtils.setStatusColor(this, Color.WHITE);

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

    /**
     * 通过id找控件
     *
     * @param id
     * @param <T>
     * @return
     */
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
        registerReceiver(gpsReceiver, intentFilter, EmUtil.getBroadCastPermission(),null);

        netChangeReceiver = new NetWorkChangeReceiver();
        netChangeReceiver.setEvent(this);
        IntentFilter netFilter = new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);
        registerReceiver(netChangeReceiver, netFilter, EmUtil.getBroadCastPermission(),null);

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
        ActManager.getInstance().removeActivity(this);
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
                            if (!PhoneFunc.isGPSEnable(RxBaseActivity2.this)) {
                                try {
                                    if ("ZTE".equalsIgnoreCase(Build.MANUFACTURER)) {
                                        ToastUtil.showMessage(RxBaseActivity2.this, getResources().getString(R.string.please_open_gps));
                                    } else {
                                        startActivity(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS));
                                    }
                                } catch (Exception e) {
                                    ToastUtil.showMessage(RxBaseActivity2.this, getResources().getString(R.string.please_open_gps));
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

    /**
     * 申请权限直接调用此方法(不需要做其他的操作) 也不需要在重写{@link #onRequestPermissionsResult(int, String[], int[])}).
     *
     * @param callBack    申请权限时的回调接口
     * @param requestCode 申请时分配的请求码
     * @param permissions 需要申请的权限数组
     */
    public void requestPermission(PermissionUtil.PermissionCallBack callBack, int requestCode, String... permissions) {
        permissionCallBack = callBack;
        if (PermissionUtil.hasSelfPermissions(this, permissions)) {
            permissionCallBack.onGranted(requestCode);  //已经获取到了需要的权限回调处理,API版本23以下默认获取到权限
        } else if (PermissionUtil.shouldShowRequestPermissionRationale(this, permissions)) {
            permissionCallBack.showRationale(permissions, requestCode);  //被用户拒绝过但没有勾选不在提示时回调处理
        } else {
            ActivityCompat.requestPermissions(this, permissions, requestCode);    //开始请求权限
        }
    }

    /**
     * 申请权限结果,子类不需要重写,不需要调用.
     *
     * @param requestCode  requestCode 权限请求码
     * @param permissions  permissions 申请的权限
     * @param grantResults grantResults 申请权限结果
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (permissionCallBack == null) {
            return;
        }
        if (PermissionUtil.verifyPermissions(grantResults)) {
            permissionCallBack.onGranted(requestCode);
        } else {
            permissionCallBack.onDenied(requestCode);
        }
    }

    /**
     * 选择图片组件
     *
     * @param x 比例 宽
     * @param y 比例 高
     */
    protected void chooseAPic(int x, int y) {
        // 进入相册 以下是例子：用不到的api可以不写
        PictureSelector.create(RxBaseActivity2.this)
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
//                .compressWH(x,y)//压缩宽高比
////                .glideOverride(100,100)
//                .compressMode(PictureConfig.LUBAN_COMPRESS_MODE)//系统自带 or 鲁班压缩 PictureConfig.SYSTEM_COMPRESS_MODE or LUBAN_COMPRESS_MODE
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


    protected void takeAPicture(int x, int y) {
        // 进入相机 以下是例子：用不到的api可以不写
        PictureSelector.create(RxBaseActivity2.this)
                .openCamera(PictureMimeType.ofImage())//全部.PictureMimeType.ofAll()、图片.ofImage()、视频.ofVideo()
                .maxSelectNum(1)// 最大图片选择数量 int
                .minSelectNum(1)// 最小选择数量 int
                .selectionMode(PictureConfig.SINGLE)// 多选 or 单选 PictureConfig.MULTIPLE or PictureConfig.SINGLE
                .isCamera(true)// 是否显示拍照按钮 true or false
                .isZoomAnim(true)// 图片列表点击 缩放效果 默认true
                .sizeMultiplier(0.5f)// glide 加载图片大小 0~1之间 如设置 .glideOverride()无效
                .setOutputCameraPath("/emdriver")// 自定义拍照保存路径,可不填
                .enableCrop(true)// 是否裁剪 true or false
                .compress(true)// 是否压缩 true or false
//                .compressWH(x,y)//压缩宽高比
                .glideOverride(100, 100)
//                .compressMode(PictureConfig.LUBAN_COMPRESS_MODE)//系统自带 or 鲁班压缩 PictureConfig.SYSTEM_COMPRESS_MODE or LUBAN_COMPRESS_MODE
//                .compressGrade(Luban.THIRD_GEAR)
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

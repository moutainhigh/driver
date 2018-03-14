package com.easymi.daijia.flowMvp.oldCalc;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.amap.api.navi.model.AMapNaviPath;
import com.amap.api.services.route.DriveRouteResult;
import com.easymi.common.push.FeeChangeObserver;
import com.easymi.common.push.HandlePush;
import com.easymi.component.Config;
import com.easymi.component.DJOrderStatus;
import com.easymi.component.app.XApp;
import com.easymi.component.base.RxBaseActivity;
import com.easymi.component.entity.DymOrder;
import com.easymi.component.rxmvp.RxManager;
import com.easymi.component.utils.DensityUtil;
import com.easymi.component.utils.ToastUtil;
import com.easymi.component.widget.CusBottomSheetDialog;
import com.easymi.component.widget.LoadingButton;
import com.easymi.daijia.R;
import com.easymi.daijia.entity.DJOrder;
import com.easymi.daijia.flowMvp.ActFraCommBridge;
import com.easymi.daijia.flowMvp.FlowActivity;
import com.easymi.daijia.flowMvp.FlowContract;
import com.easymi.daijia.flowMvp.FlowPresenter;
import com.easymi.daijia.fragment.SettleFragmentDialog;
import com.easymi.daijia.util.PhoneUtil;

/**
 * Created by liuzihao on 2018/1/30.
 */

public class OldRunningActivity extends RxBaseActivity implements FlowContract.View, FeeChangeObserver {

    private LinearLayout back;
    private TextView running_time;
    private TextView distance;
    private TextView total_fee;
    private LoadingButton meter_wait_btn;
    private LoadingButton meter_settle_btn;

    private boolean forceOre;

    private DJOrder djOrder;
    private long orderId;

    private FlowPresenter presenter;

    private ActFraCommBridge bridge;

    SettleFragmentDialog settleFragmentDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {//复写onCreate 因为设置虚拟按键必须在setContentView之前
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        PhoneUtil.setHideVirtualKey(getWindow());//隐藏虚拟按键
        super.onCreate(savedInstanceState);
    }

    @Override
    public int getLayoutId() {
        return R.layout.activity_old_running;
    }

    @Override
    protected void onStart() {
        super.onStart();
        HandlePush.getInstance().addObserver(this);
    }

    @Override
    protected void onStop() {
        super.onStop();
        HandlePush.getInstance().deleteObserver(this);
    }

    @Override
    public void initViews(Bundle savedInstanceState) {
        // 屏幕常亮
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        orderId = getIntent().getLongExtra("orderId", -1);

        forceOre = XApp.getMyPreferences().getBoolean(Config.SP_ALWAYS_OREN, false);
        if (forceOre) {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);//动态设置为横屏
        }

        presenter = new FlowPresenter(this, this);

        back = findViewById(R.id.back);
        running_time = findViewById(R.id.running_time);
        distance = findViewById(R.id.distance);
        total_fee = findViewById(R.id.total_fee);
        meter_wait_btn = findViewById(R.id.meter_wait_btn);
        meter_settle_btn = findViewById(R.id.meter_settle_btn);

        initBridge();

        showView();

        presenter.findOne(orderId, false);

        back.setOnClickListener(view -> onBackPressed());
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(OldRunningActivity.this, FlowActivity.class);
        intent.putExtra("orderId", orderId);
        intent.putExtra("fromOld", true);
        startActivity(intent);
        finish();
    }

    @Override
    public void feeChanged(long orderId, String orderType) {
        if (djOrder == null) {
            return;
        }
        if (orderId == djOrder.orderId && orderType.equals(Config.DAIJIA)) {
            showView();
        }
    }

    @Override
    public void initToolbar() {

    }

    @Override
    public void initPop() {

    }

    @Override
    public void showTopView() {

    }

    @Override
    public void showToPlace(String toPlace) {

    }

    @Override
    public void showLeftTime(String leftTime) {

    }

    @Override
    public void initBridge() {
        bridge = new ActFraCommBridge() {
            @Override
            public void doAccept(LoadingButton btn) {

            }

            @Override
            public void doRefuse() {

            }

            @Override
            public void doToStart(LoadingButton btn) {
            }

            @Override
            public void doArriveStart() {

            }

            @Override
            public void doStartWait(LoadingButton btn) {
                presenter.startWait(djOrder.orderId, btn);
            }

            @Override
            public void doStartWait() {
                presenter.startWait(djOrder.orderId);
            }

            @Override
            public void doStartDrive(LoadingButton btn) {

            }

            @Override
            public void changeEnd() {

            }

            @Override
            public void doFinish() {
                finish();
            }

            @Override
            public void doQuanlan() {
                showMapBounds();
            }

            @Override
            public void doRefresh() {

            }

            @Override
            public void doUploadOrder(double darkCost, int mileage) {

            }

            @Override
            public void showDrive() {

            }

            @Override
            public void showCheating() {

            }

            @Override
            public void doConfirmMoney(LoadingButton btn, DymOrder dymOrder) {
                presenter.arriveDes(btn, dymOrder);
            }

            @Override
            public void doPay(double money) {
                showPayType(money);
            }

            @Override
            public void showSettleDialog() {

            }
        };
    }

    @Override
    public void showBottomFragment(DJOrder djOrder) {

    }

    @Override
    public void showOrder(DJOrder djOrder) {
        if (null == djOrder) {
            finish();
            return;
        }
        this.djOrder = djOrder;
        meter_wait_btn.setOnClickListener(view -> presenter.startWait(orderId, meter_wait_btn));
        meter_settle_btn.setOnClickListener(view -> {
            settleFragmentDialog = new SettleFragmentDialog(OldRunningActivity.this, djOrder, bridge);
            settleFragmentDialog.show();
        });
        if (djOrder.orderStatus == DJOrderStatus.START_WAIT_ORDER) {
            Intent intent = new Intent(OldRunningActivity.this, OldWaitActivity.class);
            intent.putExtra("orderId", orderId);
            startActivity(intent);
            finish();
        } else if (djOrder.orderStatus == DJOrderStatus.ARRIVAL_DESTINATION_ORDER) {
            if (settleFragmentDialog != null && settleFragmentDialog.isShowing()) {
                settleFragmentDialog.setDjOrder(djOrder);
            } else {
                settleFragmentDialog = new SettleFragmentDialog(this, djOrder, bridge);
                settleFragmentDialog.show();
            }
        }
    }

    @Override
    public void initMap() {

    }

    @Override
    public void showMapBounds() {

    }

    @Override
    public void cancelSuc() {

    }

    @Override
    public void refuseSuc() {

    }

    @Override
    public void showPath(int[] ints, AMapNaviPath path) {

    }

    @Override
    public void showPath(DriveRouteResult result) {

    }

    @Override
    public void showPayType(double money) {

        if (null != settleFragmentDialog) {
            settleFragmentDialog.dismiss();
        }

        CusBottomSheetDialog bottomSheetDialog = new CusBottomSheetDialog(this);

        View view = LayoutInflater.from(this).inflate(R.layout.pay_type_dialog, null, false);
        CheckBox payBalance = view.findViewById(R.id.pay_balance);
        CheckBox payHelpPay = view.findViewById(R.id.pay_help_pay);
        RelativeLayout balanceCon = view.findViewById(R.id.balance_con);
        RelativeLayout helppayCon = view.findViewById(R.id.helppay_con);
        Button sure = view.findViewById(R.id.pay_button);
        ImageView close = view.findViewById(R.id.ic_close);

        sure.setText(getString(R.string.pay_money) + money + getString(R.string.yuan));

        balanceCon.setOnClickListener(view13 -> payBalance.setChecked(true));

        helppayCon.setOnClickListener(view14 -> payHelpPay.setChecked(true));

        payBalance.setOnCheckedChangeListener((compoundButton, b) -> {
            if (b) {
                payHelpPay.setChecked(false);
            }
        });

        payHelpPay.setOnCheckedChangeListener((compoundButton, b) -> {
            if (b) {
                payBalance.setChecked(false);
            }
        });

        sure.setOnClickListener(view12 -> {
            if (payBalance.isChecked() || payHelpPay.isChecked()) {
                if (payHelpPay.isChecked()) {
                    presenter.payOrder(orderId, "helppay");
                } else if (payBalance.isChecked()) {
                    presenter.payOrder(orderId, "balance");
                }
                bottomSheetDialog.dismiss();
            } else {
                ToastUtil.showMessage(OldRunningActivity.this, getString(R.string.please_pay_title));
            }
        });

        close.setOnClickListener(view1 -> bottomSheetDialog.dismiss());

        bottomSheetDialog.setContentView(view);
        bottomSheetDialog.setCancelable(false);
        bottomSheetDialog.setOnDismissListener(dialogInterface -> {
            finish();
        });
        bottomSheetDialog.show();
    }

    @Override
    public void paySuc() {
        ToastUtil.showMessage(this, getString(R.string.pay_suc));
        finish();
    }

    @Override
    public void showLeft(int dis, int time) {

    }

    @Override
    public void showReCal() {

    }

    @Override
    public void showToEndFragment() {

    }

    @Override
    public RxManager getManager() {
        return mRxManager;
    }

    private void showView() {
        DymOrder dymOrder = DymOrder.findByIDType(orderId, Config.DAIJIA);
        if (dymOrder == null) {
            return;
        }
        running_time.setText(String.valueOf(dymOrder.travelTime));
        distance.setText(String.valueOf(dymOrder.distance));
        total_fee.setText(String.valueOf(dymOrder.totalFee));
        meter_wait_btn.setText(getString(R.string.waited) + dymOrder.waitTime + getString(R.string.minutes));
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        Log.e("lifecycle", "onConfigurationChanged()");
        super.onConfigurationChanged(newConfig);
        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);
        int width = dm.widthPixels;
        int height = dm.heightPixels;
        if (width > height) {//横屏

        } else {//竖屏
            onBackPressed();
        }
    }
}

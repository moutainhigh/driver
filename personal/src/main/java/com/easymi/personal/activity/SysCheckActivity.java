package com.easymi.personal.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toolbar;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationListener;
import com.amap.api.location.AMapLocationQualityReport;
import com.easymi.component.app.XApp;
import com.easymi.component.base.RxBaseActivity;
import com.easymi.component.entity.NetWorkUtil;
import com.easymi.component.utils.StringUtils;
import com.easymi.component.widget.CusToolbar;
import com.easymi.component.widget.RotateImageView;
import com.easymi.personal.R;

/**
 * Created by liuzihao on 2018/4/19.
 */

public class SysCheckActivity extends RxBaseActivity implements AMapLocationListener {

    RotateImageView rotateImageView;
    TextView netText;
    TextView locText;
    TextView noticeText;

    RotateImageView netImg;
    RotateImageView locImg;
    RotateImageView noticeImg;
    Button reCheck;

    TextView errCountText;
    TextView totalHint;

    TextView checkingText;
    LinearLayout resultLayout;

    FrameLayout back;

    ImageView leftBack;
    RelativeLayout toolbar;

    private AMapLocationClient locClient;

    private int errCount = 0;

    @Override
    public boolean isEnableSwipe() {
        return true;
    }

    @Override
    public int getLayoutId() {
        return R.layout.activity_sys_check;
    }

    @Override
    public void initViews(Bundle savedInstanceState) {
        rotateImageView = findViewById(R.id.rotate_img);
        netText = findViewById(R.id.net_work_state);
        locText = findViewById(R.id.loc_state);
        noticeText = findViewById(R.id.notice_state);
        reCheck = findViewById(R.id.re_check);

        netImg = findViewById(R.id.net_img);
        locImg = findViewById(R.id.loc_img);
        noticeImg = findViewById(R.id.notice_img);

        errCountText = findViewById(R.id.err_count);
        totalHint = findViewById(R.id.total_hint);

        resultLayout = findViewById(R.id.result_frame);
        checkingText = findViewById(R.id.checking_text);

        back = findViewById(R.id.back);

        toolbar = findViewById(R.id.cus_toolbar);
        leftBack = findViewById(R.id.left_icon);

        leftBack.setOnClickListener(view -> finish());

        startScan();

        reCheck.setOnClickListener(view -> startScan());
    }

    private void startScan() {
        reCheck.setVisibility(View.GONE);
        back.setBackgroundColor(getResources().getColor(R.color.green));
        toolbar.setBackgroundColor(getResources().getColor(R.color.green));
        resultLayout.setVisibility(View.GONE);
        checkingText.setVisibility(View.VISIBLE);

        errCount = 0;

        rotateImageView.startRotate();

        netImg.setImageResource(R.mipmap.ic_check_loading);
        locImg.setImageResource(R.mipmap.ic_check_loading);
        noticeImg.setImageResource(R.mipmap.ic_check_loading);

        netImg.startRotate();
        locImg.startRotate();
        noticeImg.startRotate();

        netText.setText(getString(R.string.check_waiting));
        locText.setText(getString(R.string.check_waiting));
        noticeText.setText(getString(R.string.check_waiting));

        netText.setTextColor(getResources().getColor(R.color.text_default));
        locText.setTextColor(getResources().getColor(R.color.text_default));
        noticeText.setTextColor(getResources().getColor(R.color.text_default));

        netText.setText(getString(R.string.checking));
        new Thread(() -> {
            try {
                Thread.sleep(1000);
                runOnUiThread(() -> {
                    boolean netEnable = NetWorkUtil.checkEnable(SysCheckActivity.this);
                    netText.setText(netEnable ? getString(R.string.net_ok) : getString(R.string.net_ok));
                    netText.setTextColor(netEnable ? getResources().getColor(R.color.green) : getResources().getColor(R.color.red));

                    netImg.pauseRotate();
                    netImg.reset();
                    netImg.setImageResource(netEnable ? R.mipmap.ic_check_ok : R.mipmap.ic_check_err);

                    errCount = errCount + (netEnable ? 0 : 1);

                    locText.setText(getString(R.string.checking));

                    locClient = new AMapLocationClient(this);
                    locClient.setLocationListener(this);
                    AMapLocationClientOption mLocationOption = new AMapLocationClientOption()
                            .setLocationPurpose(AMapLocationClientOption.AMapLocationPurpose.Transport)
                            .setGpsFirst(true)
                            .setWifiScan(false)
                            .setLocationCacheEnable(false)
                            .setLocationMode(AMapLocationClientOption.AMapLocationMode.Hight_Accuracy)
                            .setNeedAddress(true)
                            .setMockEnable(false)
                            .setSensorEnable(true);
                    locClient.setLocationOption(mLocationOption);
                    locClient.startLocation();
                });
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }).start();

    }

    @Override
    public void onLocationChanged(AMapLocation amapLocation) {
        if (amapLocation != null) {
            if (amapLocation.getErrorCode() == AMapLocation.LOCATION_SUCCESS) {

                AMapLocationQualityReport report = amapLocation.getLocationQualityReport();
                StringBuilder advice = new StringBuilder();
                if (report.getGPSStatus() != AMapLocationQualityReport.GPS_STATUS_OK) {
                    advice.append(getString(R.string.please_gps_open));
                }
                if (!NetWorkUtil.isWifiConnected(this)) {
                    advice.append(getString(R.string.please_wifi));
                }

                locText.setText(StringUtils.isBlank(advice.toString()) ? getString(R.string.loc_ok) : advice);
                locText.setTextColor(StringUtils.isBlank(advice.toString()) ? getResources().getColor(R.color.green) : getResources().getColor(R.color.yellow));

                errCount = errCount + (StringUtils.isBlank(advice.toString()) ? 0 : 1);

                locImg.pauseRotate();
                locImg.reset();
                locImg.setImageResource(StringUtils.isBlank(advice.toString()) ? R.mipmap.ic_check_ok : R.mipmap.ic_check_err);

            } else {
                //定位失败时，可通过ErrCode（错误码）信息来确定失败的原因，errInfo是错误信息，详见错误码表。
                locText.setText(amapLocation.getErrorInfo());
                locText.setTextColor(getResources().getColor(R.color.red));

                errCount = errCount + 1;

                locImg.pauseRotate();
                locImg.reset();
                locImg.setImageResource(R.mipmap.ic_check_err);
            }
        }
        desClient();

        noticeText.setText(getString(R.string.checking));
        new Thread(() -> {
            try {
                Thread.sleep(1000);
                runOnUiThread(() -> {
                    boolean connected = XApp.getInstance().isMqttConnect();
                    noticeText.setText(connected ? getString(R.string.notice_ok) : getString(R.string.notice_err));
                    noticeText.setTextColor(connected ? getResources().getColor(R.color.green) : getResources().getColor(R.color.red));

                    noticeImg.pauseRotate();
                    noticeImg.reset();
                    noticeImg.setImageResource(connected ? R.mipmap.ic_check_ok : R.mipmap.ic_check_err);

                    rotateImageView.pauseRotate();
                    rotateImageView.reset();
                    reCheck.setVisibility(View.VISIBLE);

                    showResult();
                });
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }).start();
    }

    private void showResult() {
        errCountText.setText(errCount + "");

        resultLayout.setVisibility(View.VISIBLE);
        checkingText.setVisibility(View.GONE);

        if (errCount == 0) {
            back.setBackgroundColor(getResources().getColor(R.color.green));
            toolbar.setBackgroundColor(getResources().getColor(R.color.green));
            totalHint.setText(getResources().getString(R.string.check_no_error));
        } else {
            back.setBackgroundColor(getResources().getColor(R.color.red));
            toolbar.setBackgroundColor(getResources().getColor(R.color.red));
            totalHint.setText(getResources().getString(R.string.check_have_error));
        }

        reCheck.setVisibility(View.VISIBLE);
    }

    private void desClient() {
        if (locClient != null) {
            locClient.stopLocation();
            locClient.onDestroy();
            locClient = null;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        desClient();
    }
}

package com.easymi.personal.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
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
import com.easymi.component.utils.PhoneUtil;
import com.easymi.component.utils.StringUtils;
import com.easymi.component.widget.CusToolbar;
import com.easymi.personal.R;
import com.kongqw.radarscanviewlibrary.RadarScanView;

/**
 * Created by liuzihao on 2018/4/19.
 */

public class SysCheckActivity extends RxBaseActivity implements AMapLocationListener {

    RadarScanView scanView;
    TextView netText;
    TextView locText;
    TextView noticeText;
    Button reCheck;

    private AMapLocationClient locClient;

    @Override
    public void initToolBar() {
        super.initToolBar();
        CusToolbar toolbar = findViewById(R.id.cus_toolbar);
        toolbar.setLeftBack(view -> finish());
        toolbar.setTitle(R.string.sys_check);
    }

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
        scanView = findViewById(R.id.radarScanView);
        netText = findViewById(R.id.net_work_state);
        locText = findViewById(R.id.loc_state);
        noticeText = findViewById(R.id.notice_state);
        reCheck = findViewById(R.id.re_check);

        startScan();

        reCheck.setOnClickListener(view -> startScan());
    }

    private void startScan() {
        scanView.startScan();
        netText.setText("等待中..");
        locText.setText("等待中..");
        noticeText.setText("等待中..");

        netText.setTextColor(getResources().getColor(R.color.text_default));
        locText.setTextColor(getResources().getColor(R.color.text_default));
        noticeText.setTextColor(getResources().getColor(R.color.text_default));

        netText.setText("检测中..");
        new Thread(() -> {
            try {
                Thread.sleep(1000);
                runOnUiThread(() -> {
                    boolean netEnable = NetWorkUtil.checkEnable(SysCheckActivity.this);
                    netText.setText(netEnable ? "网络正常" : "网络异常");
                    netText.setTextColor(netEnable ? getResources().getColor(R.color.green) : getResources().getColor(R.color.red));

                    locText.setText("检测中..");

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
                    advice.append("请开启GPS ");
                } else {
                    if (report.getGPSSatellites() == 0) {
                        advice.append("请行驶到开阔地带 ");
                    }
                    if (!NetWorkUtil.isWifiConnected(this)) {
                        advice.append("请打开WIFI有助于精准定位 ");
                    }
                }

                locText.setText(StringUtils.isBlank(advice.toString()) ? "定位状态良好" : advice);
                locText.setTextColor(StringUtils.isBlank(advice.toString()) ? getResources().getColor(R.color.green) : getResources().getColor(R.color.yellow));

            } else {
                //定位失败时，可通过ErrCode（错误码）信息来确定失败的原因，errInfo是错误信息，详见错误码表。
                locText.setText(amapLocation.getErrorInfo());
                locText.setTextColor(getResources().getColor(R.color.red));
            }
        }
        desClient();

        noticeText.setText("检测中..");
        new Thread(() -> {
            try {
                Thread.sleep(1000);
                runOnUiThread(() -> {
                    boolean connected = XApp.getInstance().isMqttConnect();
                    noticeText.setText(connected ? "通知消息正常" : "通知消息异常，请退出重新登录");
                    noticeText.setTextColor(connected ? getResources().getColor(R.color.green) : getResources().getColor(R.color.red));

                    scanView.stopScan();
                    reCheck.setVisibility(View.VISIBLE);
                });
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }).start();

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

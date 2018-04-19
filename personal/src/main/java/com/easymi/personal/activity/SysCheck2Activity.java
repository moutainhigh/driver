package com.easymi.personal.activity;

import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationListener;
import com.amap.api.location.AMapLocationQualityReport;
import com.easymi.component.app.XApp;
import com.easymi.component.base.RxBaseActivity;
import com.easymi.component.entity.NetWorkUtil;
import com.easymi.component.utils.StringUtils;
import com.easymi.component.widget.RotateImageView;
import com.easymi.personal.R;
import com.easymi.personal.adapter.CheckAdapter;
import com.easymi.personal.widget.NotScrollViewPager;

/**
 * Created by liuzihao on 2018/4/19.
 */

public class SysCheck2Activity extends RxBaseActivity implements AMapLocationListener {

    RelativeLayout checkLayout;
    RelativeLayout resultLayout;

    RotateImageView rotateImg;

    NotScrollViewPager viewPager;

    TextView errCount;
    TextView totalHint;

    TextView netState;
    TextView locState;
    TextView noticeState;

    Button reCheck;

    private String netResult;
    private String locResult;
    private String noticeResult;

    private int netColor;
    private int locColor;
    private int noticeColor;

    private AMapLocationClient locClient;

    @Override
    public boolean isEnableSwipe() {
        return true;
    }

    @Override
    public int getLayoutId() {
        return R.layout.activity_check_2;
    }

    @Override
    public void initViews(Bundle savedInstanceState) {
        checkLayout = findViewById(R.id.check_layout);
        resultLayout = findViewById(R.id.result_layout);
        rotateImg = findViewById(R.id.rotate_img);
        viewPager = findViewById(R.id.view_pager);

        errCount = findViewById(R.id.err_count);
        totalHint = findViewById(R.id.total_hint);

        netState = findViewById(R.id.net_work_state);
        locState = findViewById(R.id.loc_state);
        noticeState = findViewById(R.id.notice_state);

        reCheck = findViewById(R.id.btn);

        CheckAdapter adapter = new CheckAdapter(this);
        viewPager.setAdapter(adapter);

        viewPager.setOffscreenPageLimit(3);    //设置缓存个数，至少为3
        viewPager.setPageMargin(14);       //设置每一页之间的间距

        startScan();

        reCheck.setOnClickListener(view -> startScan());
    }

    private void startScan() {

        rotateImg.startRotate();

        checkLayout.setVisibility(View.VISIBLE);
        resultLayout.setVisibility(View.GONE);

        viewPager.setCurrentItem(0,true);

        new Thread(() -> {
            try {
                Thread.sleep(2000);
                runOnUiThread(() -> {
                    boolean netEnable = NetWorkUtil.checkEnable(SysCheck2Activity.this);
                    netResult = netEnable ? getString(R.string.net_ok) : getString(R.string.net_err);
                    netColor = netEnable ? getResources().getColor(R.color.green) : getResources().getColor(R.color.red);

                    viewPager.setCurrentItem(1,true);

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
                } else {
                    if (report.getGPSSatellites() == 0) {
                        advice.append(getString(R.string.please_kaikuo));
                    }
                    if (!NetWorkUtil.isWifiConnected(this)) {
                        advice.append(getString(R.string.please_wifi));
                    }
                }

                locResult = StringUtils.isBlank(advice.toString()) ? getString(R.string.loc_ok) : advice.toString();
                locColor = StringUtils.isBlank(advice.toString()) ? getResources().getColor(R.color.green) : getResources().getColor(R.color.yellow);

            } else {
                //定位失败时，可通过ErrCode（错误码）信息来确定失败的原因，errInfo是错误信息，详见错误码表。
                locResult = amapLocation.getErrorInfo();
                locColor = getResources().getColor(R.color.red);
            }
        }
        desClient();

        viewPager.setCurrentItem(2,true);
        new Thread(() -> {
            try {
                Thread.sleep(2000);
                runOnUiThread(() -> {
                    boolean connected = XApp.getInstance().isMqttConnect();
                    noticeResult = connected ? getString(R.string.notice_ok) : getString(R.string.notice_err);
                    noticeColor = connected ? getResources().getColor(R.color.green) : getResources().getColor(R.color.red);

                    showCheckResult();
                });
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }).start();
    }

    private void showCheckResult() {
        rotateImg.destroyRotate();
        viewPager.setCurrentItem(0,true);

        checkLayout.setVisibility(View.GONE);
        resultLayout.setVisibility(View.VISIBLE);

        if (netColor == getResources().getColor(R.color.green)
                && locColor == getResources().getColor(R.color.green)
                && noticeColor == getResources().getColor(R.color.green)) {
            errCount.setText(0);
            errCount.setTextColor(getResources().getColor(R.color.green));
            totalHint.setText(getResources().getString(R.string.check_no_error));
            totalHint.setTextColor(getResources().getColor(R.color.green));

        } else if (netColor == getResources().getColor(R.color.red)
                || locColor == getResources().getColor(R.color.red)
                || noticeColor == getResources().getColor(R.color.red)) {

            int count = 0;
            if (netColor == getResources().getColor(R.color.red)) {
                count++;
            }
            if (locColor == getResources().getColor(R.color.red)) {
                count++;
            }
            if (noticeColor == getResources().getColor(R.color.red)) {
                count++;
            }
            errCount.setText(count);
            errCount.setTextColor(getResources().getColor(R.color.red));
            totalHint.setText(getResources().getString(R.string.check_have_error));
            totalHint.setTextColor(getResources().getColor(R.color.red));
        } else {
            errCount.setText("1");
            errCount.setTextColor(getResources().getColor(R.color.yellow));
            totalHint.setText(getResources().getString(R.string.check_no_error));
            totalHint.setTextColor(getResources().getColor(R.color.yellow));
        }

        netState.setText(netResult);
        netState.setTextColor(netColor);

        locState.setText(locResult);
        locState.setTextColor(locColor);

        noticeState.setText(noticeResult);
        noticeState.setTextColor(noticeColor);
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

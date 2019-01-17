package com.easymi.common.widget;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.amap.api.maps.AMap;
import com.amap.api.maps.model.Marker;
import com.easymi.common.R;
import com.easymi.component.entity.TaxiSetting;
import com.easymi.component.utils.PhoneUtil;

/**
 *
 * @author developerLzh
 * @date 2017/12/25 0025
 */

public class NearInfoWindowAdapter implements AMap.InfoWindowAdapter {

    private Context context;
    private TaxiSetting taxiSetting;

    public NearInfoWindowAdapter(Context context) {
        this.context = context;
        taxiSetting = TaxiSetting.findOne();
    }

    @Override
    public View getInfoWindow(Marker marker) {
        return null;
    }

    @Override
    public View getInfoContents(Marker marker) {
        String driverName = marker.getSnippet();
        String driverPhone = marker.getTitle();
        View view = LayoutInflater.from(context).inflate(R.layout.near_info_window, null, false);
        TextView tv = view.findViewById(R.id.driver_name);
        ImageView callPhone = view.findViewById(R.id.call_phone);
        tv.setText(driverName);
        if (null != taxiSetting) {
            if (taxiSetting.canCallDriver == 2) {
                callPhone.setVisibility(View.GONE);
            } else {
                callPhone.setVisibility(View.VISIBLE);
            }
        } else {
            callPhone.setVisibility(View.VISIBLE);
        }
        callPhone.setOnClickListener(view1 -> PhoneUtil.call((Activity) context, driverPhone));
        return view;
    }
}

package com.easymi.cityline.adapter;

import android.content.Context;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.amap.api.maps.AMap;
import com.amap.api.maps.model.Marker;
import com.easymi.cityline.R;
import com.easymi.component.entity.Setting;

/**
 * Created by developerLzh on 2017/12/25 0025.
 */

public class LeftWindowAdapter implements AMap.InfoWindowAdapter {

    private Context context;
    private Setting setting;

    public LeftWindowAdapter(Context context) {
        this.context = context;
        setting = Setting.findOne();
    }

    @Override
    public View getInfoWindow(Marker marker) {

        return null;
    }

    @Override
    public View getInfoContents(Marker marker) {
        String leftDis = marker.getSnippet();
        String leftTime = marker.getTitle();

        View view = LayoutInflater.from(context).inflate(R.layout.left_info_window, null, false);

        TextView left_dis = view.findViewById(R.id.left_dis);
        TextView left_time = view.findViewById(R.id.left_time);
        left_dis.setText(Html.fromHtml(leftDis));
        left_time.setText(Html.fromHtml(leftTime));

        return view;
    }
}

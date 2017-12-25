package com.easymi.common.widget;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.amap.api.maps.AMap;
import com.amap.api.maps.model.Marker;
import com.easymi.common.R;

/**
 * Created by developerLzh on 2017/12/25 0025.
 */

public class NearInfoWindowAdapter implements AMap.InfoWindowAdapter {

    private Context context;

    public NearInfoWindowAdapter(Context context) {
        this.context = context;
    }

    @Override
    public View getInfoWindow(Marker marker) {
        String title = marker.getSnippet();
        View view = LayoutInflater.from(context).inflate(R.layout.near_info_window, null);
        TextView tv = view.findViewById(R.id.driver_name);
        tv.setText(title);
        return view;
    }

    @Override
    public View getInfoContents(Marker marker) {
        return null;
    }
}

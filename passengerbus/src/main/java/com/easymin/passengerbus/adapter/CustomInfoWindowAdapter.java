package com.easymin.passengerbus.adapter;
import android.view.View;

import com.amap.api.maps.AMap;
import com.amap.api.maps.model.Marker;

/**
 * 自定义infowindow
 */
public class CustomInfoWindowAdapter implements AMap.InfoWindowAdapter{
    @Override
    public View getInfoWindow(Marker marker) {
        return null;
    }

    @Override
    public View getInfoContents(Marker marker) {
        return null;
    }
//    private Context context;
//
//    public CustomInfoWindowAdapter(Context context) {
//        this.context = context;
//    }
//
//    @Override
//    public View getInfoWindow(Marker marker) {
//        View view = LayoutInflater.from(context).inflate(R.layout.map_info_window, null);
//        setViewContent(marker,view);
//        return view;
//    }
//
//    //这个方法根据自己的实体信息来进行相应控件的赋值
//    private void setViewContent(Marker marker,View view) {
//        //实例：
//        BusStationsBean storeInfo = (BusStationsBean) marker.getObject();
//        TextView tvName = (TextView) view.findViewById(R.id.tv_address);
//        tvName.setText(storeInfo.address);
//    }
//
//
//    @Override
//    public View getInfoContents(Marker marker) {
//        return null;
//    }
}

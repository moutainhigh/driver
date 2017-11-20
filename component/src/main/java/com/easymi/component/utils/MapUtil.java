package com.easymi.component.utils;

import com.amap.api.maps.model.LatLng;
import com.amap.api.maps.model.LatLngBounds;

import java.util.List;

/**
 * Created by developerLzh on 2017/11/20 0020.
 */

public class MapUtil {
    public static LatLngBounds getBounds(List<LatLng> latLngs, LatLng center) {

        if (null != latLngs && latLngs.size() > 0) {

            LatLng param = latLngs.get(0);

            double maxLat = param.latitude;
            double minLat = param.latitude;
            double maxLng = param.longitude;
            double minLng = param.longitude;


            for (LatLng driver : latLngs) {
                if (driver.latitude > maxLat) {
                    maxLat = driver.latitude;
                }
                if (driver.latitude < minLat) {
                    minLat = driver.latitude;
                }

                if (driver.longitude > maxLng) {
                    maxLng = driver.longitude;
                }
                if (driver.longitude < minLng) {
                    minLng = driver.longitude;
                }
            }

            double maxDiffLat = Math.max(Math.abs(maxLat - center.latitude), (minLat - center.latitude));
            double maxDiffLng = Math.max(Math.abs(maxLng - center.longitude), (minLng - center.longitude));

            //左上
            LatLng leftTopLatLng = new LatLng(center.latitude + maxDiffLat, center.longitude - maxDiffLng);
            //右下
            LatLng rightBottomLatLng = new LatLng(center.latitude - maxDiffLat, center.longitude + maxDiffLng);

            return new LatLngBounds.Builder().include(leftTopLatLng).include(rightBottomLatLng).build();

        }

        return null;
    }
}

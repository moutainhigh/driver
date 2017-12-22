package com.easymi.common.trace;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.amap.api.maps.model.LatLng;
import com.easymi.component.loc.LocService;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by developerLzh on 2017/11/24 0024.
 */

public class TraceReceiver extends BroadcastReceiver {

    TraceInterface traceInterface;

    public TraceReceiver(TraceInterface traceInterface) {
        this.traceInterface = traceInterface;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        if (null == intent.getAction()) {
            return;
        }

        if (intent.getAction().equals(LocService.BROAD_TRACE_SUC)) {
//            Gson gson = new Gson();

//            List<LatLng> tracelatLngs;
//            Type typeTrace = new TypeToken<ArrayList<LatLng>>() {
//            }.getType();
//            tracelatLngs = gson.fromJson(intent.getStringExtra("traceLatLngs"), typeTrace);
//
//            List<LatLng> originallatLngs;
//            Type typeTrace2 = new TypeToken<ArrayList<LatLng>>() {
//            }.getType();
//            originallatLngs = gson.fromJson(intent.getStringExtra("originalLatlngs"), typeTrace2);

//            if (traceInterface != null) {
//                traceInterface.showTraceAfter(originallatLngs, tracelatLngs);
//            }

            LatLng latLng = intent.getParcelableExtra("traceLatLng");

            if (traceInterface != null) {
                traceInterface.showTraceAfter(latLng);
            }
        }
    }
}

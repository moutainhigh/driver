package com.easymi.common.trace;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.easymi.component.entity.EmLoc;
import com.easymi.component.loc.LocService;
import com.google.gson.Gson;

/**
 *
 * @author developerLzh
 * @date 2017/11/24 0024
 */

public class TraceReceiver extends BroadcastReceiver {

    TraceInterface traceInterface;

    /**
     * 构造器
     * @param traceInterface
     */
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

            EmLoc emLoc = new Gson().fromJson(intent.getStringExtra("traceLoc"),EmLoc.class);

            if (traceInterface != null) {
                traceInterface.showTraceAfter(emLoc);
            }
        }
    }
}

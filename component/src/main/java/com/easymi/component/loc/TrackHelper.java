package com.easymi.component.loc;

import android.app.Notification;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.Build;

import com.amap.api.track.AMapTrackClient;
import com.amap.api.track.ErrorCode;
import com.amap.api.track.OnTrackLifecycleListener;
import com.amap.api.track.TrackParam;
import com.amap.api.track.query.entity.HistoryTrack;
import com.amap.api.track.query.entity.LocationMode;
import com.amap.api.track.query.entity.Point;
import com.amap.api.track.query.entity.Track;
import com.amap.api.track.query.model.AddTerminalRequest;
import com.amap.api.track.query.model.AddTerminalResponse;
import com.amap.api.track.query.model.AddTrackRequest;
import com.amap.api.track.query.model.AddTrackResponse;
import com.amap.api.track.query.model.DistanceRequest;
import com.amap.api.track.query.model.DistanceResponse;
import com.amap.api.track.query.model.HistoryTrackResponse;
import com.amap.api.track.query.model.LatestPointRequest;
import com.amap.api.track.query.model.LatestPointResponse;
import com.amap.api.track.query.model.OnTrackListener;
import com.amap.api.track.query.model.ParamErrorResponse;
import com.amap.api.track.query.model.QueryTerminalRequest;
import com.amap.api.track.query.model.QueryTerminalResponse;
import com.amap.api.track.query.model.QueryTrackResponse;
import com.easymi.component.Config;
import com.easymi.component.R;
import com.easymi.component.app.XApp;
import com.easymi.component.entity.BaseOrder;
import com.easymi.component.utils.EmUtil;
import com.easymi.component.utils.Log;

import java.util.List;

/**
 * Copyright (C) , 2012-2018 , 四川小咖科技有限公司
 *
 * @author Lzh
 * @version 5.0.0.000
 * @date 2018/10/23
 * @since 5.0.0.000
 */
public class TrackHelper {
    private AMapTrackClient aMapTrackClient;

    private static long terminalId;
    private static long trackId;

    private static TrackHelper trackHelper;

    private static final String CHANNEL_ID_SERVICE_RUNNING = "CHANNEL_ID_SERVICE_RUNNING";

    private OnGetTrackIdListener onGetTrackId;
    private OnGetTrackDisListener onGetTrackDisListener;
    private OnGetTrackLastPointListener onGetTrackLastPointListener;

    public void setOnGetTrackId(OnGetTrackIdListener onGetTrackId) {
        this.onGetTrackId = onGetTrackId;
    }

    public static TrackHelper getInstance() {
        if (null == trackHelper) {
            trackHelper = new TrackHelper();
        }
        return trackHelper;
    }

    public void destroy() {
        if (null != aMapTrackClient) {
            stopTrack();
            aMapTrackClient = null;
        }
        trackHelper = null;
    }

    String terminalName;

    private TrackHelper() {
        aMapTrackClient = new AMapTrackClient(XApp.getInstance());
        aMapTrackClient.setInterval(Config.NORMAL_LOC_TIME / 1000, 30);//5秒定位一次，30秒上传一次
        aMapTrackClient.setCacheSize(20);
        aMapTrackClient.setLocationMode(LocationMode.HIGHT_ACCURACY);

        terminalName = String.valueOf("driver-" + EmUtil.getEmployId());

    }

    /**
     * 开启上报轨迹
     *
     * @param trId
     */
    public void startTrack(long trId) {
        trackId = trId;
        if (terminalId == 0) {
            chainStartTerminal();
        } else {
            startTrack();
        }
    }

    /**
     * 从查询终端开始到开启上报
     */
    private void chainStartTerminal() {
        aMapTrackClient.queryTerminal(new QueryTerminalRequest(Config.TRACK_SERVICE_ID, terminalName),
                getTrackListener(Config.TRACK_SERVICE_ID, terminalName));
    }

    /**
     * 添加一条路线
     */
    public void addTrack() {
        AddTrackRequest addTrackRequest = new AddTrackRequest(Config.TRACK_SERVICE_ID, terminalId);
        aMapTrackClient.addTrack(addTrackRequest, getTrackListener(Config.TRACK_SERVICE_ID, terminalName));
    }

    /**
     * 开启上报轨迹
     *
     * @param
     */
    private void startTrack() {

        if (trackId == 0) {
            addTrack();
            return;
        }

        //查询是否存在该track
        TrackParam param = new TrackParam(Config.TRACK_SERVICE_ID, terminalId);
        param.setTrackId(trackId);

        aMapTrackClient.startTrack(param, getTrackLifecycleListener());
    }

    /**
     * 查询距离信息
     *
     * @return
     */
    public void queryDis(int trId, OnGetTrackDisListener onGetTrackDisListener) {
        this.onGetTrackDisListener = onGetTrackDisListener;
        trackId = trId;

        long curr = System.currentTimeMillis();

        //查询是否存在该track
        aMapTrackClient.queryDistance(new DistanceRequest(Config.TRACK_SERVICE_ID, terminalId, curr - 24 * 60 * 60 * 1000, curr, trackId),
                getTrackListener(Config.TRACK_SERVICE_ID, terminalName));//结束时间不能大于当前时间，且距离开始时间不能超过24小时
    }

    /**
     * 查询上次的位置信息
     *
     * @param
     */
    public void queryLastLoc(int trId, OnGetTrackLastPointListener onGetTrackLastPointListener) {
        this.onGetTrackLastPointListener = onGetTrackLastPointListener;
        trackId = trId;

        //查询是否存在该track
        aMapTrackClient.queryLatestPoint(new LatestPointRequest(Config.TRACK_SERVICE_ID, terminalId, trackId),
                getTrackListener(Config.TRACK_SERVICE_ID, terminalName));
    }


    public void stopTrack() {
        aMapTrackClient.stopTrack(new TrackParam(Config.TRACK_SERVICE_ID, terminalId), getTrackLifecycleListener());
    }

    private OnTrackLifecycleListener getTrackLifecycleListener() {
        return new OnTrackLifecycleListener() {
            @Override
            public void onBindServiceCallback(int i, String s) {
                Log.e("TrackHelper", s);
            }

            @Override
            public void onStartGatherCallback(int status, String s) {
                if (status == ErrorCode.TrackListen.START_GATHER_SUCEE ||
                        status == ErrorCode.TrackListen.START_GATHER_ALREADY_STARTED) {
                    Log.e("TrackHelper", "定位采集开启成功！");
                } else {
                    Log.e("TrackHelper", "定位采集启动异常！" + s);
                }
            }

            @Override
            public void onStartTrackCallback(int status, String s) {
                if (status == ErrorCode.TrackListen.START_TRACK_SUCEE ||
                        status == ErrorCode.TrackListen.START_TRACK_SUCEE_NO_NETWORK ||
                        status == ErrorCode.TrackListen.START_TRACK_ALREADY_STARTED) {
                    // 服务启动成功，继续开启收集上报
                    aMapTrackClient.startGather(this);
                    Log.e("TrackHelper", "轨迹上报成功" );
                } else {
                    Log.e("TrackHelper", "轨迹上报服务服务启动异常！" + s);
                }
            }

            @Override
            public void onStopGatherCallback(int i, String s) {
                Log.e("TrackHelper", "onStopGatherCallback-->"+s);
            }

            @Override
            public void onStopTrackCallback(int i, String s) {
                Log.e("TrackHelper", "onStopTrackCallback-->"+s);
            }
        };
    }

    private OnTrackListener getTrackListener(long serviceId, String terminalName) {
        return new OnTrackListener() {
            @Override
            public void onQueryTerminalCallback(QueryTerminalResponse queryTerminalResponse) {
                if (queryTerminalResponse.isSuccess()) {
                    if (queryTerminalResponse.getTid() <= 0) { //不存在时添加

                        aMapTrackClient.addTerminal(new AddTerminalRequest(terminalName, serviceId),
                                getTrackListener(serviceId, terminalName));
                        Log.e("TrackHelper", "查询终端成功，终端不存在，开始添加终端");

                    } else { //存在时直接开启track
                        terminalId = queryTerminalResponse.getTid();
                        XApp.getPreferencesEditor().putLong(Config.TERMINAL_ID, terminalId).apply();
                        startTrack();
                        Log.e("TrackHelper", "查询终端成功，终端存在，terminalId-->" + terminalId);
                    }
                }
            }

            @Override
            public void onCreateTerminalCallback(AddTerminalResponse addTerminalResponse) {
                if (addTerminalResponse.isSuccess()) {
                    // 创建完成，开启猎鹰服务
                    terminalId = addTerminalResponse.getTid();
                    XApp.getPreferencesEditor().putLong(Config.TERMINAL_ID, terminalId).apply();
                    startTrack();
                    Log.e("TrackHelper", "添加终端成功，terminalId-->" + terminalId);
                } else {
                    // 请求失败
                    Log.e("TrackHelper", "请求失败");
                }
            }

            @Override
            public void onDistanceCallback(DistanceResponse distanceResponse) {
                if (distanceResponse.isSuccess()) {
                    double meters = distanceResponse.getDistance();
                    // 行驶里程查询成功，行驶了meters米
                    Log.e("TrackHelper", "行驶里程查询成功，行驶了-->" + meters);
                    if (null != onGetTrackDisListener) {
                        onGetTrackDisListener.getDis(meters);
                    }
                } else {
                    Log.e("TrackHelper", "行驶里程查询失败" );
                    // 行驶里程查询失败
                }
            }

            @Override
            public void onLatestPointCallback(LatestPointResponse latestPointResponse) {
                if (latestPointResponse.isSuccess()) {
                    Point point = latestPointResponse.getLatestPoint().getPoint();
                    // 查询实时位置成功，point为实时位置信息
                    Log.e("TrackHelper", "查询实时位置成功，lat:" + point.getLat()+"  lng:"+point.getLng());
                    if (null != onGetTrackLastPointListener) {
                        onGetTrackLastPointListener.getPoint(point);
                    }
                } else {
                    // 查询实时位置失败
                    Log.e("TrackHelper", "查询实时位置失败" );
                }
            }

            @Override
            public void onHistoryTrackCallback(HistoryTrackResponse historyTrackResponse) {
                if (historyTrackResponse.isSuccess()) {
                    HistoryTrack historyTrack = historyTrackResponse.getHistoryTrack();
                    // historyTrack中包含终端轨迹信息
                } else {
                    // 查询失败
                }
            }

            @Override
            public void onQueryTrackCallback(QueryTrackResponse queryTrackResponse) {
                if (queryTrackResponse.isSuccess()) {
                    List<Track> tracks = queryTrackResponse.getTracks();
                    // 查询成功，tracks包含所有轨迹及相关轨迹点信息
                } else {
                    // 查询失败
                }
            }

            @Override
            public void onAddTrackCallback(AddTrackResponse addTrackResponse) {
                if (addTrackResponse.isSuccess()) {
                    trackId = addTrackResponse.getTrid();
                    Log.e("TrackHelper", "添加track成功，trackId-->" +trackId);
                    if (null != onGetTrackId) {
                        onGetTrackId.getTrackId(trackId);
                    }
                    startTrack();
                } else {
                    Log.e("TrackHelper", "添加track失败，" + addTrackResponse.getErrorMsg());
                }
            }

            @Override
            public void onParamErrorCallback(ParamErrorResponse paramErrorResponse) {

            }
        };
    }

    /**
     * 在8.0以上手机，如果app切到后台，系统会限制定位相关接口调用频率
     * 可以在启动轨迹上报服务时提供一个通知，这样Service启动时会使用该通知成为前台Service，可以避免此限制
     */
    private Notification buildNotification(Context context) {
        boolean isLogin = XApp.getMyPreferences().getBoolean(Config.SP_ISLOGIN, false);
        Intent intent = new Intent();

        if (isLogin) {
            intent.setClassName(context, "com.easymi.common.mvp.work.WorkActivity");

        } else {
            intent.setClassName(context, "com.easymi.common.activity.SplashActivity");
        }
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0,
                intent, 0);

        Notification.Builder builder = null;
        Notification notification = null;
        if (Build.VERSION.SDK_INT >= 26) {
            //Android O上对Notification进行了修改，如果设置的targetSDKVersion>=26建议使用此种方式创建通知栏
            String channelId = context.getPackageName();
            builder = new Notification.Builder(context, channelId);
        } else {
            builder = new Notification.Builder(context);
        }

        builder.setSmallIcon(R.mipmap.role_driver);
        builder.setSound(null);
        builder.setLargeIcon(BitmapFactory.decodeResource(context.getResources(), R.mipmap.ic_launcher));
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            builder.setColor(context.getResources().getColor(R.color.colorPrimary));
        }
        builder.setContentTitle(context.getResources().getString(R.string.app_name));
        builder.setContentText(context.getResources().getString(R.string.app_name)
                + context.getResources().getString(R.string.houtai));
        builder.setWhen(System.currentTimeMillis());
        builder.setContentIntent(pendingIntent);
        builder.setOngoing(true);
        builder.setSound(null);

        notification = builder.build();

        return notification;
    }

}

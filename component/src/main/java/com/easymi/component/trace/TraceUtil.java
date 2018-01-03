package com.easymi.component.trace;

import com.amap.api.maps.AMapUtils;
import com.amap.api.maps.model.LatLng;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by developerLzh on 2018/1/3 0003.
 */

public class TraceUtil {

    private boolean isFirst = true;//是否首次进入
   
    private List<TraceLocation> w1TempList = new ArrayList<>();
    private List<TraceLocation> w2TempList = new ArrayList<>();
    private List<TraceLocation> mListPoint = new ArrayList<>();

    private TraceLocation weight1 = new TraceLocation();
    private TraceLocation weight2;
    
    private final long CAR_MAX_SPEED = 22;

    private int w1Count = 0;


    /**
     * @param aMapLocation
     * @return 是否获得有效点，需要存储和计算距离
     */
    private Boolean filterPos(TraceLocation aMapLocation) {

        try {
            // 获取的第一个定位点不进行过滤
            if (isFirst) {
                isFirst = false;
                weight1.setLat(aMapLocation.getLat());
                weight1.setLng(aMapLocation.getLng());
                weight1.setTime(aMapLocation.getTime());

                // 将得到的第一个点存储入w1的缓存集合
                final TraceLocation traceLocation = new TraceLocation();
                traceLocation.setLat(aMapLocation.getLat());
                traceLocation.setLng(aMapLocation.getLng());
                traceLocation.setTime(aMapLocation.getTime());
                w1TempList.add(traceLocation);
                w1Count++;

                return true;

            } else {
//                // 过滤静止时的偏点，在静止时速度小于1米就算做静止状态
                if (aMapLocation.getSpeed() < 1) {
                    return false;
                }

                if (weight2 == null) {

                    // 计算w1与当前定位点p1的时间差并得到最大偏移距离D
                    long offsetTimeMils = aMapLocation.getTime() - weight1.getTime();
                    long offsetTimes = (offsetTimeMils / 1000);
                    long MaxDistance = offsetTimes * CAR_MAX_SPEED;
                    float distance = AMapUtils.calculateLineDistance(
                            new LatLng(weight1.getLat(), weight1.getLng()),
                            new LatLng(aMapLocation.getLat(), aMapLocation.getLng()));

                    if (distance > MaxDistance) {
                        // 将设置w2位新的点，并存储入w2临时缓存
                        weight2 = new TraceLocation();
                        weight2.setLat(aMapLocation.getLat());
                        weight2.setLng(aMapLocation.getLng());
                        weight2.setTime(aMapLocation.getTime());

                        w2TempList.add(weight2);

                        return false;

                    } else {
                        // 将p1加入到做坐标集合w1TempList
                        TraceLocation traceLocation = new TraceLocation();
                        traceLocation.setLat(aMapLocation.getLat());
                        traceLocation.setLat(aMapLocation.getLng());
                        traceLocation.setTime(aMapLocation.getTime());
                        w1TempList.add(traceLocation);
                        w1Count++;

                        // 更新w1权值点
                        weight1.setLat(weight1.getLat() * 0.2 + aMapLocation.getLat() * 0.8);
                        weight1.setLng(weight1.getLng() * 0.2 + aMapLocation.getLng() * 0.8);
                        weight1.setTime(aMapLocation.getTime());
                        weight1.setSpeed(aMapLocation.getSpeed());

                        if (w1TempList.size() > 3) {
                            // 将w1TempList中的数据放入finalList，并将w1TempList清空
                            mListPoint.addAll(w1TempList);
                            w1TempList.clear();
                            return true;
                        } else {
                            return false;
                        }
                    }

                } else {

                    // 计算w2与当前定位点p1的时间差并得到最大偏移距离D
                    long offsetTimeMils = aMapLocation.getTime() - weight2.getTime();
                    long offsetTimes = (offsetTimeMils / 1000);
                    long MaxDistance = offsetTimes * 16;
                    float distance = AMapUtils.calculateLineDistance(
                            new LatLng(weight2.getLat(), weight2.getLng()),
                            new LatLng(aMapLocation.getLat(), aMapLocation.getLng()));

                    if (distance > MaxDistance) {
                        w2TempList.clear();
                        // 将设置w2位新的点，并存储入w2临时缓存
                        weight2 = new TraceLocation();
                        weight2.setLat(aMapLocation.getLat());
                        weight2.setLng(aMapLocation.getLng());
                        weight2.setTime(aMapLocation.getTime());

                        w2TempList.add(weight2);

                        return false;
                    } else {
                        // 将p1加入到做坐标集合w2TempList
                        TraceLocation traceLocation = new TraceLocation();
                        traceLocation.setLat(aMapLocation.getLat());
                        traceLocation.setLng(aMapLocation.getLng());
                        traceLocation.setTime(aMapLocation.getTime());
                        w2TempList.add(traceLocation);

                        // 更新w2权值点
                        weight2.setLat(weight2.getLat() * 0.2 + aMapLocation.getLat() * 0.8);
                        weight2.setLng(weight2.getLng() * 0.2 + aMapLocation.getLng() * 0.8);
                        weight2.setTime(aMapLocation.getTime());
                        weight2.setSpeed(aMapLocation.getSpeed());

                        if (w2TempList.size() > 4) {
                            // 判断w1所代表的定位点数是否>4,小于说明w1之前的点为从一开始就有偏移的点
                            if (w1Count > 4) {
                                mListPoint.addAll(w1TempList);
                            } else {
                                w1TempList.clear();
                            }

                            // 将w2TempList集合中数据放入finalList中
                            mListPoint.addAll(w2TempList);

                            // 1、清空w2TempList集合 2、更新w1的权值点为w2的值 3、将w2置为null
                            w2TempList.clear();
                            weight1 = weight2;
                            weight2 = null;
                            return true;

                        } else {
                            return false;
                        }
                    }
                }
            }
        } finally {
//            FileWriteUtil.getInstance().save("tutu_driver_filter.txt", filterString);
//            Log.d("hhh","finnaly");
        }


    }
}

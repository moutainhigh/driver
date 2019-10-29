package com.easymin.carpooling.entity;

import android.os.Parcel;
import android.os.Parcelable;

import com.easymi.component.result.EmResult;

import java.io.Serializable;
import java.util.List;

/**
 * @Copyright (C), 2012-2019, Sichuan Xiaoka Technology Co., Ltd.
 * @FileName: AllStation
 * @Author: hufeng
 * @Date: 2019/9/17 下午9:40
 * @Description:
 * @History:
 */
public class AllStation implements Serializable {

    public long scheduleId;
    /**
     * 拼车班次状态  1未开始
     */
    public int scheduleStatus;
    public int currentStationId;
    public List<MyStation> scheduleStationVoList;

}

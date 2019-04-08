package com.easymi.common.entity;

import com.chad.library.adapter.base.entity.MultiItemEntity;
import com.easymi.common.R;

/**
 * Copyright (C), 2012-2018, Sichuan Xiaoka Technology Co., Ltd.
 * FileName: CityLine
 * @Author: shine
 * Date: 2018/11/14 下午4:26
 * Description:
 * History:
 */
public class CityLine implements MultiItemEntity {

    /**
     * 班次ID
     */
    public Long id;
    /**
     * 系统key
     */
    public String appKey;
    /**
     * 线路ID
     */
    public Long lineId;
    /**
     * 司机ID
     */
    public Long driverId;
    /**
     * 司机姓名
     */
    public String driverName;
    /**
     * 司机电话
     */
    public String driverPhone;
    /**
     * 车辆ID
     */
    public Long carId;
    /**
     * 车牌号
     */
    public String vehicleNo;
    /**
     * 售票截止时间(分钟)
     */
    public Integer minute;
    /**
     * 日期(2018-12-31)
     */
    public String day;
    /**
     * 小时分钟(06:30)
     */
    public String hour;
    /**
     * 座位数量(余票数量)
     */
    public Integer seats;
    /**
     * 已售票数
     */
    public Integer saleSeat;
    /**
     * 起点站点地址
     */
    public String startAddress;
    /**
     * 终点站点地址
     */
    public String endAddress;

    /**
     *
     * 售票中 1,等待行程开始 5,司机接人 10,司机送人,已结束 20
     */
    public int status;

    public static final int ITEM_HEADER = 1;//头
    public static final int ITEM_POSTER = 2;//内容
    public static final int ITEM_DESC = 3;//底

    public int viewType;

    public CityLine() {

    }

    public CityLine(int type) {
        this.viewType = type;
    }

    @Override
    public int getItemType() {
        return viewType;
    }

    /**
     * 售票中 1,等待行程开始 5,司机接人 10,司机送人 15,已结束 20
     */
    public String getStatus(){
        switch (status){
            case 1:
            case 5:
                return "未开始";
            case 10:
                return "接人中";
            case 15:
                return "送人中";
            default:
                return "已结束";
        }
    }
}

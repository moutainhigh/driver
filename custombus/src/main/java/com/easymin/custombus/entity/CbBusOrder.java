package com.easymin.custombus.entity;

import java.io.Serializable;
import java.util.List;

/**
 * @Copyright (C), 2012-2019, Sichuan Xiaoka Technology Co., Ltd.
 * @FileName: CbBusOrder
 * @Author: hufeng
 * @Date: 2019/2/18 下午2:25
 * @Description: 定制班车班次实体
 * @History:
 */
public class CbBusOrder implements Serializable{

    /**
     * 班次ID
     */
    public long id;

    /**
     * 时间
     */
    public long time;

    /**
     * 班次状态 1 未开始行程 5开始行程执行中 3 到达终点站
     */
    public int status;

    /**
     * 当前处理站点Id
     */
    public long currentStationId;

    /**
     * 当前处理站点状态(1前往,2到达)
     */
    public int currentStationStatus;

    /**
     * 当前站点验票倒计时
     */
    public long arrivedTime;

    /**
     * 验票倒计时分钟数-暂定10分钟
     */
    public int reciprocalMinute;

    /**
     * 站点列表
     */
    public List<Station> driverStationVos;

    /**
     * 获取起点站
     * @return
     */
    public Station getStartSite(){
        Station start = driverStationVos.get(0);
        return start;
    }

    /**
     * 获取终点站
     * @return
     */
    public Station getEndSite(){
        Station end = driverStationVos.get(driverStationVos.size()-1);
        return end;
    }

}

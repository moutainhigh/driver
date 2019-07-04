package com.easymi.common.push;

import com.easymi.component.entity.PassengerLocation;

/**
 * Copyright (C), 2012-2018, Sichuan Xiaoka Technology Co., Ltd.
 * FileName: PassengerLocSubject
 * @Author: shine
 * Date: 2018/12/11 下午1:18
 * Description:
 * History:
 */
public interface PassengerLocSubject {

    /**
     * 添加观察者
     * @param obj
     */
    void addPLObserver(PassengerLocObserver obj);

    /**
     * 移除观察者
     * @param obj
     */
    void deletePLObserver(PassengerLocObserver obj);

    /**
     * 当主题方法改变时,这个方法被调用,通知所有的观察
     * @param passengerLocation
     */
    void notifyPLObserver(PassengerLocation passengerLocation);
}

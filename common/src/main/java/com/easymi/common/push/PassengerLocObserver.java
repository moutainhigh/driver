package com.easymi.common.push;

import com.easymi.common.entity.PassengerLocation;

/**
 * Copyright (C), 2012-2018, Sichuan Xiaoka Technology Co., Ltd.
 * FileName: PassengerLocObserver
 * Author: shine
 * Date: 2018/12/11 下午1:18
 * Description:
 * History:
 * @author hufeng
 */
public interface PassengerLocObserver {
    /**
     * 乘客位置观察者
     * @param passengerLocation
     */
    void plChange(PassengerLocation passengerLocation);
}

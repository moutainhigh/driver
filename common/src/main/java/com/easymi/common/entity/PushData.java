package com.easymi.common.entity;

import com.easymi.component.entity.PushEmploy;

/**
 * Copyright (C), 2012-2018, Sichuan Xiaoka Technology Co., Ltd.
 * FileName:
 * @Author: hufeng
 * Date: 2018/12/24 下午1:10
 * Description:
 * History:
 */

public class PushData {

    /**
     * 司机所开通的业务类型
     */
    public String serviceType;
    /**
     * 系统key
     */
    public String appKey;
    /**
     * 司机信息
     */
    public PushEmploy driver;
    /**
     * 数据集
     */
    public PushDataLoc location;

}

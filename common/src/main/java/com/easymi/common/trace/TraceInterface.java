package com.easymi.common.trace;

import com.easymi.component.entity.EmLoc;

/**
 * Copyright (C), 2012-2018, Sichuan Xiaoka Technology Co., Ltd.
 * FileName:
 * @Author: shine
 * Date: 2018/12/24 下午5:00
 * Description:
 * History:
 */

public interface TraceInterface {
    /**
     * 轨迹纠偏
     * @param traceLoc
     */
    void showTraceAfter(EmLoc traceLoc);
}

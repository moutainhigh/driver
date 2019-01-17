package com.easymi.common.trace;

import com.easymi.component.entity.EmLoc;

/**
 *
 * @author developerLzh
 * @date 2017/11/24 0024
 */

public interface TraceInterface {
    /**
     * 轨迹纠偏
     * @param traceLoc
     */
    void showTraceAfter(EmLoc traceLoc);
}

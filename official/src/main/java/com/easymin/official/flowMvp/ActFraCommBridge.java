package com.easymin.official.flowMvp;

import com.easymi.component.entity.DymOrder;
import com.easymi.component.widget.LoadingButton;

import java.util.ArrayList;

/**
 * @Copyright (C), 2012-2019, Sichuan Xiaoka Technology Co., Ltd.
 * @FileName: ActFraCommBridge
 * @Author: hufeng
 * @Date: 2019/3/26 上午9:02
 * @Description:
 * @History:
 */
public interface ActFraCommBridge {

    /**
     * 前往预约地
     */
    void doToStart();

    /**
     * 到达预约地
     */
    void doArriveStart();

    /**
     * 开始出发
     */
    void doStartDrive();

    /**
     * 到达目的地
     */
    void arriveDes(DymOrder dymOrder);

    /**
     * 确认订单
     */
    void doConfirm(ArrayList<String> images);

    /**
     * 结束界面
     */
    void doFinish();

    /**
     * 显示费用详情
     */
    void toFeeDetail();
}

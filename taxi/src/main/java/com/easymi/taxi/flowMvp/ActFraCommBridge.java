package com.easymi.taxi.flowMvp;

import com.easymi.component.entity.DymOrder;
import com.easymi.component.widget.LoadingButton;

/**
 * Copyright (C), 2012-2018, Sichuan Xiaoka Technology Co., Ltd.
 * FileName: ActFraCommBridge
 * @Author: shine
 * Date: 2018/12/24 下午1:10
 * Description:
 * History:
 */

public interface ActFraCommBridge {

    /**
     * 出租车接单
     * @param btn
     */
    void doAccept(LoadingButton btn);

    /**
     * 拒单
     */
    void doRefuse();

    /**
     * 前往预约地
     * @param btn
     */
    void doToStart(LoadingButton btn);

    /**
     * 到达预约地
     */
    void doArriveStart();

    /**
     * 开始等待 未使用
     * @param btn
     */
    void doStartWait(LoadingButton btn);

    /**
     * 开始等待
     */
    void doStartWait();

    /**
     * 开始出发
     * @param btn
     */
    void doStartDrive(LoadingButton btn);

    /**
     * 显示结算按钮
     */
    void showSettleDialog();

    /**
     * 修改终点 未使用
     */
    void changeEnd();

    /**
     * 完成订单
     */
    void doFinish();

    /**
     * 全览
     */
    void doQuanlan();

    /**
     * 刷新地图显示
     */
    void doRefresh();

    /**
     * 上传定位点
     */
    void doUploadOrder();

    /**
     * 前往目的地
     */
    void showDrive();

    /**
     * 手动计价器 未使用
     */
    void showCheating();

}

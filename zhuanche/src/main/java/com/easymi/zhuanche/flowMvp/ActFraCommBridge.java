package com.easymi.zhuanche.flowMvp;

import com.easymi.component.entity.DymOrder;
import com.easymi.component.widget.LoadingButton;

/**
 * Copyright (C), 2012-2018, Sichuan Xiaoka Technology Co., Ltd.
 * FileName:
 * @Author: shine
 * Date: 2018/12/24 下午1:10
 * Description:
 * History:
 */

public interface ActFraCommBridge {
    /**
     * 接单
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
    void doStartDrive();

    /**
     * 确认费用
     * @param btn
     * @param dymOrder
     */
    void doConfirmMoney(LoadingButton btn, DymOrder dymOrder);

    /**
     * 支付
     * @param money
     */
    void doPay(double money);

    /**
     * 显示
     */
    void showSettleDialog();

    /**
     * 修改目的地
     */
    void changeEnd();

    /**
     * 结束界面
     */
    void doFinish();

    /**
     * 全览
     */
    void doQuanlan();

    /**
     * 刷新地图状态
     */
    void doRefresh();

    /**
     * 到达目的地等接口推点
     */
    void doUploadOrder();

    /**
     * 开始出发
     */
    void showDrive();

    /**
     * 显示手动计价器
     */
    void showCheating();

    /**
     * 显示费用详情
     */
    void toFeeDetail();

}

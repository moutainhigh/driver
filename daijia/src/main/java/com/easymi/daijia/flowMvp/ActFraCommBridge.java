package com.easymi.daijia.flowMvp;

/**
 * Created by liuzihao on 2017/11/15.
 *
 *
 */

public interface ActFraCommBridge {
    void doAccept();
    void doRefuse();

    void doToStart();
    void doArriveStart();
    void doStartWait();
    void doStartDrive();
    void doConfirmMoney();
    void doPay();
    void showSettleDialog();

    void changeEnd();

}

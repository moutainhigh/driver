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
    void doOutBeforeWait();
    void doOutAfterWait();
    void doStartDrive();

    void changeEnd();

    void showSettleDialog();
}

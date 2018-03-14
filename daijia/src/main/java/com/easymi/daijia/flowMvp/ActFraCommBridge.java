package com.easymi.daijia.flowMvp;

import com.easymi.component.entity.DymOrder;
import com.easymi.component.widget.LoadingButton;

/**
 * Created by liuzihao on 2017/11/15.
 */

public interface ActFraCommBridge {
    void doAccept(LoadingButton btn);

    void doRefuse();

    void doToStart(LoadingButton btn);

    void doArriveStart();

    void doStartWait(LoadingButton btn);

    void doStartWait();

    void doStartDrive(LoadingButton btn);

    void doConfirmMoney(LoadingButton btn, DymOrder dymOrder);

    void doPay(double money);

    void showSettleDialog();

    void changeEnd();

    void doFinish();

    void doQuanlan();

    void doRefresh();

    void doUploadOrder(double darkCost,int mileage);

    void showDrive();

    void showCheating();

}

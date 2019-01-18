package com.easymi.taxi.flowMvp;

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
    void doAccept(LoadingButton btn);

    void doRefuse();

    void doToStart(LoadingButton btn);

    void doArriveStart();

    void doStartWait(LoadingButton btn);

    void doStartWait();

    void doStartDrive(LoadingButton btn);

    void showSettleDialog();

    void changeEnd();

    void doFinish();

    void doQuanlan();

    void doRefresh();

    void doUploadOrder();

    void showDrive();

    void showCheating();

}

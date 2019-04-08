package com.easymi.common.push;

/**
 * Copyright (C), 2012-2018, Sichuan Xiaoka Technology Co., Ltd.
 * FileName:
 * @Author: shine
 * Date: 2018/12/24 下午1:10
 * Description: 费用信息监听分发接口
 * History:
 */

public interface FeeChangeSubject {
    /**
     * 添加观察者
     * @param obj
     */
    void addObserver(FeeChangeObserver obj);

    /**
     * 移除观察者
     * @param obj
     */
    void deleteObserver(FeeChangeObserver obj);

    /**
     * 当主题方法改变时,这个方法被调用,通知所有的观察
     * @param orderId
     * @param orderType
     */
    void notifyObserver(long orderId,String orderType);
}

package com.easymi.component.loc;

import com.easymi.component.entity.EmLoc;

/**
 * Created by liuzihao on 2017/12/13.
 *
 * 位置变化的主题
 *
 */

public interface LocSubject {
    //添加观察者
    void addObserver(LocObserver obj);
    //移除观察者
    void deleteObserver(LocObserver obj);
    //当主题方法改变时,这个方法被调用,通知所有的观察者
    void notifyObserver(EmLoc loc);
}

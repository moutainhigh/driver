package com.easymi.common.entity;

public class ManualConfigBean {
    /**
     * 可排班天数
     */
    public int day;
    /**
     * 1-显示 2-不显示乘客端'
     */
    public int isShow;
    /**
     * 1-显示 2-不显示 报班按钮'
     */
    public int showView;
    /**
     * 验票方式 1-有验票码 2-无验票码'
     */
    public int inspectTicket;

    //班车操作按钮模式  1-滑动 2-点击
    public int operationMode;
}

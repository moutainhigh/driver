package com.easymi.cityline.entity;

import java.io.Serializable;

/**
 * Copyright (C), 2012-2018, Sichuan Xiaoka Technology Co., Ltd.
 * FileName:
 * @Author: hufeng
 * Date: 2018/12/24 下午1:10
 * Description:
 * History:
 */

public class Sequence implements Serializable{
    /**
     * 1 数字 2 文字 3 图片
     */
    public int type;

    /**
     * 数字编号
     */
    public int num;
    /**
     * 文字内容
     */
    public String text;

    /**
     * 头像
     */
    public String photo;

    /**
     * 票数
     */
    public int ticketNumber;

    /**
     * 状态   0 未接 1 已接 2 跳过接 3 未送 4 已送 5 跳过送
     */
    public int status;
}

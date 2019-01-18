package com.easymi.taxi.entity;

import com.google.gson.annotations.SerializedName;

/**
 * Copyright (C), 2012-2018, Sichuan Xiaoka Technology Co., Ltd.
 * FileName: FinishActivity
 *@Author: shine
 * Date: 2018/12/24 下午1:10
 * Description:
 * History:
 */

public class ZCType {
    public long id;
    public String name;

    @SerializedName("is_book")
    public int isBook;

    @SerializedName("min_book_time")
    public int minBookTime;

}

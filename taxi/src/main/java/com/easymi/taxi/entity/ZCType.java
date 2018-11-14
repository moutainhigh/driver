package com.easymi.taxi.entity;

import com.google.gson.annotations.SerializedName;

/**
 * Created by developerLzh on 2017/11/27 0027.
 */

public class ZCType {
    public long id;
    public String name;

    @SerializedName("is_book")
    public int isBook;

    @SerializedName("min_book_time")
    public int minBookTime;

}

package com.easymi.common.entity;

import java.io.Serializable;

public class SeatBean implements Serializable {
    public int paddingLeft;
    public int paddingRight;
    public int paddingBottom;
    public boolean isChoose;
    public int isChild;
    public boolean isDialogSelect;
    // 1 禁售 2可选 3被选
    public int status;
    public int sort;
    public double price;
    public double childPrice;
    public int child;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        SeatBean seatBean = (SeatBean) o;

        return sort == seatBean.sort;
    }

    @Override
    public int hashCode() {
        return sort;
    }
}

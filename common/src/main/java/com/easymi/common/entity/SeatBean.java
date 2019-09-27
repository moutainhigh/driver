package com.easymi.common.entity;

import com.easymi.component.utils.CommonUtil;

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
    public int type;

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

    public String getDescAndSeatInfo() {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(CommonUtil.getPassengerDesc(type, sort));
        if (isChild == 1) {
            stringBuilder.append("(儿童座)");
        }
        return stringBuilder.toString();
    }

    public String getDesc() {
        return CommonUtil.getPassengerDesc(type, sort);
    }

}

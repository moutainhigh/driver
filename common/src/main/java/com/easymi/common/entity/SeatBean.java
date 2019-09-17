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
    public int listSize;

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

    public String getDesc() {
        if (listSize == 5) {
            if (sort == 1) {
                return "前右; ";
            } else if (sort == 2) {
                return "后左; ";
            } else if (sort == 3) {
                return "后中; ";
            } else if (sort == 4) {
                return "后右; ";
            }
        } else if (listSize == 7) {
            if (sort == 1) {
                return "前右; ";
            } else if (sort == 2) {
                return "中左; ";
            } else if (sort == 3) {
                return "中右; ";
            } else if (sort == 4) {
                return "后左; ";
            } else if (sort == 5) {
                return "后中; ";
            } else if (sort == 6) {
                return "后右; ";
            }
        }
        return "";
    }

}

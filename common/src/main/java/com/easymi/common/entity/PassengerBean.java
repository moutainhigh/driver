package com.easymi.common.entity;

import java.io.Serializable;

public class PassengerBean implements Serializable {
    public long id;
    public int riderType;
    public String riderName;
    public String riderPhone;
    public String riderCard;
    public String guardianName;
    public String guardianCard;
    public String guardianPhone;
    public boolean isSelect;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        PassengerBean that = (PassengerBean) o;

        return id == that.id;
    }

    @Override
    public int hashCode() {
        return (int) (id ^ (id >>> 32));
    }
}

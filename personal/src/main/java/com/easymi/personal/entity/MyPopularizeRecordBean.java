package com.easymi.personal.entity;

import java.io.Serializable;

public class MyPopularizeRecordBean implements Serializable {
    public String realName;
    public String phone;
    public int status;
    public int type;
    public String remark;
    public double settlementTotal;
    public double nonSettlementTotal;
    public long created;
    public long auditTime;
}

package com.easymi.personal.entity;

/**
 * Copyright (C), 2012-2018, Sichuan Xiaoka Technology Co., Ltd.
 * FileName: BankInfo
 * @Author: shine
 * Date: 2018/11/21 下午1:13
 * Description:
 * History:
 */
public class BankInfo {

    /**
     *银行名
     */
    public String bankName;

    /**
     *银行卡号
     */
    public String bankCardNo;

    /**
     *余额
     */
    public Double balance;

    /**
     *预存款
     */
    public Double preparedMoney;

    /**
     *所属公司Id
     */
    public Long companyId;

    /**
     *appKey
     */
    public String appKey;

    /**
     *创建时间
     */
    public Long created;

    /**
     *修改时间
     */
    public Long updated;

    /**
     *收款人
     */
    public String payee;
}

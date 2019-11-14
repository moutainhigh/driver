package com.easymi.common.entity;

import com.easymi.component.result.EmResult;

/**
 * @Copyright (C), 2012-2019, Sichuan Xiaoka Technology Co., Ltd.
 * @FileName: FaceAuth
 * @Author: hufeng
 * @Date: 2019/11/14 上午11:44
 * @Description:
 * @History:
 */
public class FaceAuth extends EmResult {

    /**
     * id
     */
    public Long id;

    /**
     * 头像地址
     */
    public String  headPortrait;

    /**
     * 人员名称
     */
    public String name;

    /**
     * 类型
     */
    public Integer userType;

    /**
     * 联系方式
     */
    public String phone;

    /**
     * 认证地址
     */
    public String address;


    /**
     * 认证精度维度
     */
    public Double  longitude;

    /**
     * 认证纬度
     */
    public Double latitude;

    /**
     * 认证状态
     */
    public Integer state;

    /**
     * 创建时间
     */
    public Long created;
}

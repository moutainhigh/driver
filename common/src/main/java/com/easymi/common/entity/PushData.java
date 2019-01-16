package com.easymi.common.entity;

import com.easymi.component.entity.PushEmploy;

/**
 *
 * @author liuzihao
 * @date 2017/12/18
 * <p>
 * 上行数据最外层
 */

public class PushData {

    /**
     * 司机所开通的业务类型
     */
    public String serviceType;
    /**
     * 系统key
     */
    public String appKey;
    /**
     * 司机信息
     */
    public PushEmploy driver;
    /**
     * 数据集
     */
    public PushDataLoc location;

}

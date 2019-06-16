package com.easymi.common.entity;

import com.easymi.component.result.EmResult;

/**
 * @Copyright (C), 2012-2019, Sichuan Xiaoka Technology Co., Ltd.
 * @FileName: MqttTopic
 * @Author: hufeng
 * @Date: 2019/6/16 下午5:53
 * @Description:
 * @History:
 */
public class MqttTopic extends EmResult {
    public String data;

    @Override
    public String toString() {
        return "MqttTopic{" +
                "data='" + data + '\'' +
                '}';
    }
}

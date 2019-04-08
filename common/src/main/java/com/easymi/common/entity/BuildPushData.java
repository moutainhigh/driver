package com.easymi.common.entity;

import com.easymi.component.entity.EmLoc;

/**
 * Copyright (C), 2012-2018, Sichuan Xiaoka Technology Co., Ltd.
 * FileName:
 * @Author: hufeng
 * Date: 2018/12/24 下午1:10
 * Description:
 * History:
 */

public class BuildPushData {

    public BuildPushData(EmLoc emLoc) {
        this.emLoc = emLoc;
    }

    /**
     * 推送数据格式
     */
    public EmLoc emLoc;
}

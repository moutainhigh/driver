package com.easymi.common.entity;

import com.easymi.component.entity.EmLoc;

/**
 *
 * @author liuzihao
 * @date 2018/3/13
 *
 * 构造上行数据模型
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

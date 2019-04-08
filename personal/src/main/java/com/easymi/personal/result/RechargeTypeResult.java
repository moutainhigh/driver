package com.easymi.personal.result;

import com.easymi.component.result.EmResult;

/**
 * @Copyright (C), 2012-2019, Sichuan Xiaoka Technology Co., Ltd.
 * @FileName: RechargeTypeResult
 * @Author: hufeng
 * @Date: 2019/3/19 下午4:11
 * @Description:
 * @History:
 */
public class RechargeTypeResult extends EmResult{

    public RechargeType data;

    public class RechargeType {
        public boolean balance;
        public boolean weChatApp;
        public boolean aliPayApp;
    }
}

package com.easymi.personal.result;

import com.easymi.component.result.EmResult;
import com.easymi.component.entity.Employ;

/**
 * Copyright (C), 2012-2018, Sichuan Xiaoka Technology Co., Ltd.
 * FileName:
 * @Author: shine
 * Date: 2018/12/24 下午1:10
 * Description:
 * History:
 */

public class LoginResult extends EmResult{

    private Employ data;

    public Employ getEmployInfo() {
        return data;
    }

}

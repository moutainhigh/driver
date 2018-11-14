package com.easymi.common.result;

import com.easymi.component.entity.Employ;
import com.easymi.component.result.EmResult;

/**
 * Created by liuzihao on 2017/11/16.
 */

public class LoginResult extends EmResult{
//    private Employ employInfo;

    private Employ data;

    public Employ getEmployInfo() {
        return data;
    }

}

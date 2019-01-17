package com.easymi.common.result;

import com.easymi.component.entity.Employ;
import com.easymi.component.result.EmResult;

/**
 *
 * @author liuzihao
 * @date 2017/11/16
 */

public class LoginResult extends EmResult{

    private Employ data;

    public Employ getEmployInfo() {
        return data;
    }

}

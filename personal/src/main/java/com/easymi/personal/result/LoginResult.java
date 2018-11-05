package com.easymi.personal.result;

import com.easymi.component.result.EmResult;
import com.easymi.component.entity.Employ;

/**
 * Created by liuzihao on 2017/11/16.
 */

public class LoginResult extends EmResult{

    private Employ data;
//    private Employ employInfo;

    public Employ getEmployInfo() {
        return data;
    }

}

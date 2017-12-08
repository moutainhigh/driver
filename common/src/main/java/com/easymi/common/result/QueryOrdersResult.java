package com.easymi.common.result;

import com.easymi.common.entity.BaseOrder;
import com.easymi.component.result.EmResult;

import java.util.List;

/**
 * Created by developerLzh on 2017/11/17 0017.
 */

public class QueryOrdersResult extends EmResult {

    public List<BaseOrder> orders;
    public int total;

}

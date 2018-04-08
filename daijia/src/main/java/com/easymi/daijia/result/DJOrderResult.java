package com.easymi.daijia.result;

import com.easymi.component.entity.DymOrder;
import com.easymi.component.result.EmResult;
import com.easymi.daijia.entity.Address;
import com.easymi.daijia.entity.Coupon;
import com.easymi.daijia.entity.DJOrder;

import java.util.List;

/**
 * Created by developerLzh on 2017/11/17 0017.
 */

public class DJOrderResult extends EmResult {
    public DJOrder order;

    public List<Address> address;

    public DymOrder orderFee;

    public Coupon coupon;
}

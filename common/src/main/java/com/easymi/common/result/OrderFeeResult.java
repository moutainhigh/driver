package com.easymi.common.result;

import com.easymi.component.result.EmResult;

/**
 * Copyright (C) , 2012-2018 , 四川小咖科技有限公司
 *
 * @author Lzh
 * @version 5.0.0.000
 * @date 2018/5/31
 * @since 5.0.0.000
 */
public class OrderFeeResult extends EmResult {

    public Cost budgetFee;

    public class Cost{
        public double start_price;
        public int wait_time;
        public double wait_time_fee;
        public int driver_time;
        public double drive_time_cost;
        public double total_amount;
        public double min_cost;
        public double mileage_cost;
        public double mileges;
    }
}

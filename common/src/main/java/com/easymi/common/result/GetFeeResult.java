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
public class GetFeeResult extends EmResult {

    public String data;

    public Cost budgetFee;

    public class Cost{
        /**
         * 起步价
         */
        public double start_price;
        /**
         * 等待时间
         */
        public int wait_time;
        /**
         * 等待费用
         */
        public double wait_time_fee;
        /**
         * 行驶时间
         */
        public int driver_time;
        /**
         * 行驶费用
         */
        public double drive_time_cost;
        /**
         * 总费用
         */
        public double total_amount;
        /**
         * 最小费用
         */
        public double min_cost;
        /**
         * 里程费用
         */
        public double mileage_cost;
        /**
         * 里程数
         */
        public double mileges;
    }
}

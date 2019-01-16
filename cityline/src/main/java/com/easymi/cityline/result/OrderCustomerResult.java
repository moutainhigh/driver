package com.easymi.cityline.result;

import com.easymi.common.entity.OrderCustomer;

import java.util.List;

/**
 *
 * @author liuzihao
 * @date 2018/11/20
 */

public class OrderCustomerResult {
    /**
     * 订单列表
     */
    public List<OrderCustomer> lists;
    /**
     * 订单总数
     */
    public int total;
}

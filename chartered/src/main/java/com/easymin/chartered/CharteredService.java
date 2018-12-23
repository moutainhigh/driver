package com.easymin.chartered;

import com.easymi.component.result.EmResult;

import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;
import rx.Observable;

/**
 * Copyright (C), 2012-2018, Sichuan Xiaoka Technology Co., Ltd.
 * FileName: CharteredService
 * Author: shine
 * Date: 2018/12/21 下午5:16
 * Description:
 * History:
 */
public interface CharteredService {

    /**
     * 包车租车 --> 查询单个订单
     *
     * @return
     */
    @GET("api/v1/chartered/order/{id}")
    Observable<EmResult> findOne(@Path("id") Long id);
}

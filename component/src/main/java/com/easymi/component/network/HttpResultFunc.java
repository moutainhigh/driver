package com.easymi.component.network;

import android.content.Context;

import com.easymi.component.result.EmResult;

import rx.functions.Func1;

/**
 * Created by Administrator on 2016/9/26.
 */


/**
 * 用来统一处理Http的resultCode,并将HttpResult的Data部分剥离出来返回给subscriber
 *
 * @param <T> Subscriber真正需要的数据类型，也就是Data部分的数据类型
 */
public class HttpResultFunc<T> implements Func1<EmResult<T>, T> {
    private Context context;

    public HttpResultFunc(Context context) {
        this.context = context;
    }

    @Override
    public T call(EmResult<T> httpResult) {
        if (httpResult.getCode() != 0) {
            throw new ApiException(context, httpResult.getCode());
        }
        return httpResult.getData();
    }
}

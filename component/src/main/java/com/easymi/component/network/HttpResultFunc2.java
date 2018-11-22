package com.easymi.component.network;

import android.content.res.Configuration;

import com.easymi.component.app.XApp;
import com.easymi.component.result.EmResult2;

import java.util.Locale;

import rx.functions.Func1;

/**
 * Created by Administrator on 2016/9/26.
 */


/**
 * 用来统一处理Http的resultCode,并将HttpResult的Data部分剥离出来返回给subscriber
 *
 * @param <T> Subscriber真正需要的数据类型，也就是Data部分的数据类型
 */
public class HttpResultFunc2<T> implements Func1<EmResult2<T>, T> {

    public HttpResultFunc2() {
    }

    @Override
    public T call(EmResult2<T> t) {
        if (t.getCode() != 1) {
            String msg = t.getMessage();
            Configuration config = XApp.getInstance().getResources().getConfiguration();   //获取默认配置
            if (config.locale == Locale.TAIWAN || config.locale == Locale.TRADITIONAL_CHINESE) {
                for (ErrCodeTran errCode : ErrCodeTran.values()) {
                    if (t.getCode() == errCode.getCode()) {
                        msg = errCode.getShowMsg();
                        break;
                    }
                }
            } else {
                for (ErrCode errCode : ErrCode.values()) {
                    if (t.getCode() == errCode.getCode()) {
                        msg = errCode.getShowMsg();
                        break;
                    }
                }
            }
            throw new ApiException(t.getCode(), msg);
        } else {
            return t.getData();
        }
    }
}

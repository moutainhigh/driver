package com.easymi.component.network;

import android.content.res.Configuration;

import com.easymi.component.Config;
import com.easymi.component.app.XApp;
import com.easymi.component.result.EmResult;
import com.easymi.component.utils.StringUtils;

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
public class HttpResultFunc<T extends EmResult> implements Func1<T, Boolean> {
    private int errCode;

    public HttpResultFunc() {
        errCode = 1;
    }

    public HttpResultFunc(int errCode) {
        this.errCode = errCode;
    }

    @Override
    public Boolean call(T t) {
        if (t.getCode() != errCode) {
            String msg = t.getMessage();
            //获取默认配置
            Configuration config = XApp.getInstance().getResources().getConfiguration();
            if(config.locale == Locale.TAIWAN || config.locale == Locale.TRADITIONAL_CHINESE){
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
            return true;
        }
    }
}

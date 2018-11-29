package cn.projcet.hf.securitycenter.network;

import android.content.res.Configuration;

import java.util.Locale;

import cn.projcet.hf.securitycenter.CApp;
import cn.projcet.hf.securitycenter.result.EmResult2;
import rx.functions.Func1;

/**
 * Created by Administrator on 2016/9/26.
 */


/**
 * 用来统一处理Http的resultCode,并将HttpResult的Data部分剥离出来返回给subscriber
 *
 * @param <T> Subscriber真正需要的数据类型，也就是Data部分的数据类型
 */
public class HttpResultFunc3<T extends EmResult2> implements Func1<T, Boolean> {

    public HttpResultFunc3() {
    }

    @Override
    public Boolean call(T t) {
        if (t.getCode() != 1) {
            String msg = t.getMessage();
            Configuration config = CApp.getInstance().getResources().getConfiguration();   //获取默认配置
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

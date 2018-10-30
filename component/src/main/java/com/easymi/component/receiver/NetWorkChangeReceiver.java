package com.easymi.component.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;

import com.easymi.component.utils.NetUtil;
import com.easymi.component.utils.StringUtils;


/**
 * Created by liuzihao on 2018/1/11.
 *
 * 网络状态变化广播处理
 *
 */

public class NetWorkChangeReceiver extends BroadcastReceiver{

    private OnNetChange event;

    public void setEvent(OnNetChange event) {
        this.event = event;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        if(StringUtils.isNotBlank(action)){
            if (intent.getAction().equals(ConnectivityManager.CONNECTIVITY_ACTION)) {
                int netWorkState = NetUtil.getNetWorkState(context);
                // 接口回调传过去状态的类型
                if(null != event){
                    event.onNetChange(netWorkState);
                }
            }
        }
    }

    public interface OnNetChange{
        void onNetChange(int status);
    }
}

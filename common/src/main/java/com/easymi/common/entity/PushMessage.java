package com.easymi.common.entity;

/**
 * Created by Administrator on 2016/5/23.
 */
public class PushMessage {

    public String code;//（例：0X00）

    public String type;//（例：0X00）

    public String data;//（通常为id,例：1）

    public String time;//（延迟时间，单位为秒,例：0）

    public Extra extra;

}

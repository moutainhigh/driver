package com.easymin.driver.securitycenter.utils;

import android.text.TextUtils;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;

public class URLDecoderUtil {
    public static String decode(String content) throws UnsupportedEncodingException {
        if(!TextUtils.isEmpty(content)){
            content = content.replaceAll("%(?![0-9a-fA-F]{2})", "%25");//替换单独出现的%为25%
            content = content.replaceAll("\\+", "%2B");//替换+号
           return URLDecoder.decode(content, "utf-8");
        }
        return "";
    }
}

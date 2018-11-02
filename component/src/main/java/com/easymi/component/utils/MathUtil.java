package com.easymi.component.utils;

/**
 * Created by liuzihao on 2018/2/28.
 */

public class MathUtil {

    /**
     * 该double的小数位数是否超过指定值
     *
     * @param num double值
     * @param max 最大保留小数位数
     * @return
     */
    public static boolean isDoubleLegal(double num, int max) {
        String s = String.valueOf(num);
        if (s.contains(".")) {
            String[] ls = s.split("\\.");
            if (ls.length >= 2) {
                String xiaoshu = ls[1];
                if (xiaoshu.length() > max) {
                    return false;
                } else {
                    return true;
                }
            } else {
                return true;
            }

        } else {
            return true;
        }
    }
}

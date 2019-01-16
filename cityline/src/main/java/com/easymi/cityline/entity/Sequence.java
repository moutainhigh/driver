package com.easymi.cityline.entity;

import java.io.Serializable;

/**
 *
 * @author liuzihao
 * @date 2018/11/14
 */

public class Sequence implements Serializable{
    /**
     * 1 数字 2 文字 3 图片
     */
    public int type;

    /**
     * 数字编号
     */
    public int num;
    /**
     * 文字内容
     */
    public String text;
}

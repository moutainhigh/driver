package com.easymi.component.decoration;

/**
 * Created by Administrator on 2017/11/29 0029.
 * 标识item的分组信息.
 */

public interface StickyItem {

    /**
     * 获取分组tag,不同的tag被认定为不同的分组.
     */
    String getGroupTag();

    /**
     * 该组头部显示的内容.
     */
    String getGroupContent();

}

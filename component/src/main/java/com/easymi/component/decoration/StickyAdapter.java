package com.easymi.component.decoration;

/**
 * Created by Administrator on 2017/11/29 0029.
 * 粘性Adapter.
 */

public interface StickyAdapter {

    /**
     * 返回标题内容.
     *
     * @param position item实际的位置.
     * @return 标题内容
     */
    String getContent(int position);

    boolean isFirstInGroup(int position);

    boolean isLasInGroup(int position);

}

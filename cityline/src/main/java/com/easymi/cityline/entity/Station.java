package com.easymi.cityline.entity;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author liuzihao
 * @date 2018/11/21
 */

public class Station {
    /**
     * 站点id
     */
    public long id;
    /**
     * 站点名字
     */
    public String name;
    /**
     * 站点详细信息
     */
    public List<MapPositionModel> coordinate;
}

package com.easymi.cityline.entity;

import java.util.ArrayList;
import java.util.List;

/**
 * Copyright (C), 2012-2018, Sichuan Xiaoka Technology Co., Ltd.
 * FileName:
 * @Author: hufeng
 * Date: 2018/12/24 下午1:10
 * Description:
 * History:
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

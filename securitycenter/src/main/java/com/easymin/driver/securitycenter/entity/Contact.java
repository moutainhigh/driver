package com.easymin.driver.securitycenter.entity;

import java.io.Serializable;

/**
 * Copyright (C), 2012-2018, Sichuan Xiaoka Technology Co., Ltd.
 * FileName: Contact
 *@Author: shine
 * Date: 2018/12/5 下午1:15
 * Description:
 * History:
 */
public class Contact implements Serializable{

    /**
     * id : 46
     * passenger_id : 105
     * passenger_phone : 18180635910
     * emerg_phone : 18982016009
     * emerg_name : 安哥
     * emerg_check : 1
     * appKey : 1HAcient1kLqfeX7DVTV0dklUkpGEnUC
     * created : 1543986694
     * updated : 1543986694
     * is_deleted : 0
     * share_auto : 0
     * share_start : null
     * share_end : null
     */

    public long id;
    public long passenger_id;
    public String passenger_phone;
    public String emerg_phone;
    public String emerg_name;
    public int emerg_check;
    public String appKey;
    public long created;
    public long updated;
    public int is_deleted;
    public int share_auto;
    public String share_start;
    public String share_end;

}

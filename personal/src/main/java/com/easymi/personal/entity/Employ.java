package com.easymi.personal.entity;

/**
 * Created by liuzihao on 2017/11/16.
 */

public class Employ {

    public long id;
    public String user_name;
    public String password;
    public String name;
    public String real_name;
    public String sex;
    public String id_card;
    public String phone;
    public String home_address;
    public String emergency;
    public String emergency_phone;
    public String drive_license_type;
    public String introducer;
    public String remark;
    public int is_freezed;
    public String order_status;
    public long company_id;
//    public String drive_license_type;
//    public String drive_license_type;
//    public String drive_license_type;
//    public String drive_license_type;
//    public String drive_license_type;

    @Override
    public String toString() {
        return "Employ{" +
                "id=" + id +
                ", user_name='" + user_name + '\'' +
                ", password='" + password + '\'' +
                ", name='" + name + '\'' +
                ", real_name='" + real_name + '\'' +
                ", sex='" + sex + '\'' +
                ", id_card='" + id_card + '\'' +
                ", phone='" + phone + '\'' +
                ", home_address='" + home_address + '\'' +
                ", emergency='" + emergency + '\'' +
                ", emergency_phone='" + emergency_phone + '\'' +
                '}';
    }
}

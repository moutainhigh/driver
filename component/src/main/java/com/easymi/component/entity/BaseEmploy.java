package com.easymi.component.entity;

import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;

import com.easymi.component.Config;
import com.easymi.component.app.XApp;
import com.easymi.component.db.SqliteHelper;
import com.easymi.component.utils.LogUtil;
import com.google.gson.annotations.SerializedName;

/**
 * Created by liuzihao on 2017/12/18.
 */

public class BaseEmploy {
    public long id;

    public int status;

    public String real_name;//真实姓名
    public long company_id;
    public String phone;
    public String child_type;//服务子类型


    /**
     * 根据ID修改数据
     */
    public boolean updateBase() {
        SqliteHelper helper = SqliteHelper.getInstance();
        SQLiteDatabase db = helper.openSqliteDatabase();
        ContentValues values = new ContentValues();
        values.put("id", id);
        values.put("real_name", real_name);
        values.put("company_id", company_id);
        values.put("phone", phone);
        values.put("child_type", child_type);
        values.put("status", status);

        /*
         * values.put("age", age); values.put("jialing", jialing);
		 */
        boolean flag = db.update("t_driverinfo", values, " id = ? ",
                new String[]{String.valueOf(id)}) == 1;
        return flag;
    }

    public BaseEmploy employ2This() {
        return Employ.findByID(XApp.getMyPreferences().getLong(Config.SP_DRIVERID, -1));
//        if (employ == null) {
//            LogUtil.e("TAG", "查询为null");
//            return null;
//        } else {
//            id = employ.id;
//            status = employ.status;
//            real_name = employ.real_name;
//            company_id = employ.company_id;
//            phone = employ.phone;
//            child_type = employ.child_type;
//            return employ;
//        }
    }
}

package com.easymi.component.entity;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Parcel;
import android.os.Parcelable;

import com.easymi.component.db.SqliteHelper;

import java.util.LinkedList;
import java.util.List;

/**
 * Created by liuzihao on 2017/11/16.
 */

public class Employ extends BaseEmploy implements Parcelable {

    public String user_name;//工号
    public String password;
    public String name;//昵称

    public String sex;

    public String company_name;

    public String portrait_path;

    public double balance;
    public String service_type;//服务类型

    public String bank_name;
    public String bank_card_no;
    public String cash_person_name;

    public double score;
    public String company_phone;

    /**
     * 保存数据
     */
    public boolean save() {
        SqliteHelper helper = SqliteHelper.getInstance();
        SQLiteDatabase db = helper.openSqliteDatabase();
        ContentValues values = new ContentValues();
        values.put("id", id);
        values.put("user_name", user_name);
        values.put("password", password);
        values.put("name", name);
        values.put("real_name", real_name);
        values.put("sex", sex);
        values.put("company_id", company_id);
        values.put("company_name", company_name);
        values.put("phone", phone);

        values.put("balance", balance);
        values.put("service_type", service_type);
        values.put("child_type", child_type);

        values.put("bank_name", bank_name);
        values.put("bank_card_no", bank_card_no);
        values.put("cash_person_name", cash_person_name);
        values.put("status", status);

        values.put("portrait_path", portrait_path);
        values.put("score", score);
        values.put("company_phone", company_phone);

        /*
         * values.put("age", age); values.put("jialing", jialing);
		 */
        boolean flag = db.insert("t_driverinfo", null, values) != -1;
        return flag;
    }

    /**
     * 判断司机是否存在
     */
    public static boolean exists(Long driverID) {
        SqliteHelper helper = SqliteHelper.getInstance();
        SQLiteDatabase db = helper.openSqliteDatabase();
        Cursor cursor = db.rawQuery(
                "select count(*) from t_driverinfo where id = ? ",
                new String[]{String.valueOf(driverID)});
        boolean flag = false;
        try {
            if (cursor.moveToNext()) {
                flag = (cursor.getInt(0) == 1);
            }
        } finally {
            cursor.close();
        }
        return flag;
    }

    /**
     * 根据ID查询数据
     */
    public static Employ findByID(Long driverID) {
        SqliteHelper helper = SqliteHelper.getInstance();
        SQLiteDatabase db = helper.openSqliteDatabase();
        Cursor cursor = db.query("t_driverinfo", new String[]{"id",
                        "user_name", "password", "name", "real_name", "sex",
                        "company_id", "company_name", "phone", "balance", "service_type",
                        "child_type", "bank_name", "bank_card_no", "cash_person_name",
                        "portrait_path", "score", "status", "company_phone"},
                "id = ?", new String[]{String.valueOf(driverID)},
                null, null, null);
        Employ driverInfo = new Employ();
        try {
            if (cursor.moveToFirst()) {
                driverInfo = new Employ();
                driverInfo.id = cursor.getLong(cursor
                        .getColumnIndex("id"));
                driverInfo.user_name = cursor.getString(cursor
                        .getColumnIndex("user_name"));
                driverInfo.password = cursor.getString(cursor
                        .getColumnIndex("password"));
                driverInfo.name = cursor.getString(cursor
                        .getColumnIndex("name"));
                driverInfo.real_name = cursor.getString(cursor
                        .getColumnIndex("real_name"));
                driverInfo.sex = cursor.getString(cursor
                        .getColumnIndex("sex"));
                driverInfo.company_id = cursor.getLong(cursor
                        .getColumnIndex("company_id"));
                driverInfo.company_name = cursor.getString(cursor
                        .getColumnIndex("company_name"));
                driverInfo.phone = cursor.getString(cursor
                        .getColumnIndex("phone"));

                driverInfo.balance = cursor.getDouble(cursor
                        .getColumnIndex("balance"));
                driverInfo.service_type = cursor.getString(cursor
                        .getColumnIndex("service_type"));
                driverInfo.child_type = cursor.getString(cursor
                        .getColumnIndex("child_type"));
                driverInfo.bank_name = cursor.getString(cursor
                        .getColumnIndex("bank_name"));
                driverInfo.bank_card_no = cursor.getString(cursor
                        .getColumnIndex("bank_card_no"));
                driverInfo.cash_person_name = cursor.getString(cursor
                        .getColumnIndex("cash_person_name"));
                driverInfo.portrait_path = cursor.getString(cursor
                        .getColumnIndex("portrait_path"));
                driverInfo.score = cursor.getDouble(cursor
                        .getColumnIndex("score"));
                driverInfo.status = cursor.getString(cursor
                        .getColumnIndex("status"));
                driverInfo.company_phone = cursor.getString(cursor
                        .getColumnIndex("company_phone"));

				/*
                 * driverInfo.age =
				 * cursor.getString(cursor.getColumnIndex("age"));
				 * driverInfo.jialing =
				 * cursor.getString(cursor.getColumnIndex("jialing"));
				 */
            }
        } catch (Exception e) {
//			CrashReport.setUserSceneTag();
        } finally {
            cursor.close();
        }
        return driverInfo;
    }

    /**
     * main进程 查询所有司机
     *
     * @return
     */
    public static List<Employ> findAll() {

        SqliteHelper helper = SqliteHelper.getInstance();
        SQLiteDatabase db = helper.openSqliteDatabase();

        Cursor cursor = db.rawQuery("select * from t_driverinfo", new String[]{});

        List<Employ> list = new LinkedList<>();

        try {
            while (cursor.moveToNext()) {
                Employ employ = cursorToEmploy(cursor);
                list.add(employ);
            }
        } finally {
            cursor.close();
        }
        return list;
    }

    /**
     * 其他进程 查询所有司机
     *
     * @return
     */
    public static List<Employ> findAll(SqliteHelper helper) {

        SQLiteDatabase db = helper.openSqliteDatabase();

        Cursor cursor = db.rawQuery("select * from t_driverinfo", new String[]{});

        List<Employ> list = new LinkedList<>();

        try {
            while (cursor.moveToNext()) {
                Employ employ = cursorToEmploy(cursor);
                list.add(employ);
            }
        } finally {
            cursor.close();
        }
        return list;
    }

    public static Employ cursorToEmploy(Cursor cursor) {
        Employ driverInfo = new Employ();

        driverInfo.id = cursor.getLong(cursor
                .getColumnIndex("id"));
        driverInfo.user_name = cursor.getString(cursor
                .getColumnIndex("user_name"));
        driverInfo.password = cursor.getString(cursor
                .getColumnIndex("password"));
        driverInfo.name = cursor.getString(cursor
                .getColumnIndex("name"));
        driverInfo.real_name = cursor.getString(cursor
                .getColumnIndex("real_name"));
        driverInfo.sex = cursor.getString(cursor
                .getColumnIndex("sex"));
        driverInfo.company_id = cursor.getLong(cursor
                .getColumnIndex("company_id"));
        driverInfo.company_name = cursor.getString(cursor
                .getColumnIndex("company_name"));
        driverInfo.phone = cursor.getString(cursor
                .getColumnIndex("phone"));

        driverInfo.balance = cursor.getDouble(cursor
                .getColumnIndex("balance"));
        driverInfo.service_type = cursor.getString(cursor
                .getColumnIndex("service_type"));
        driverInfo.child_type = cursor.getString(cursor
                .getColumnIndex("child_type"));
        driverInfo.bank_name = cursor.getString(cursor
                .getColumnIndex("bank_name"));
        driverInfo.bank_card_no = cursor.getString(cursor
                .getColumnIndex("bank_card_no"));
        driverInfo.cash_person_name = cursor.getString(cursor
                .getColumnIndex("cash_person_name"));
        driverInfo.portrait_path = cursor.getString(cursor
                .getColumnIndex("portrait_path"));
        driverInfo.score = cursor.getDouble(cursor
                .getColumnIndex("score"));
        driverInfo.status = cursor.getString(cursor
                .getColumnIndex("status"));
        driverInfo.company_phone = cursor.getString(cursor
                .getColumnIndex("company_phone"));
        return driverInfo;
    }

    /**
     * 根据ID修改数据
     */
    public boolean updateAll() {
        SqliteHelper helper = SqliteHelper.getInstance();
        SQLiteDatabase db = helper.openSqliteDatabase();
        ContentValues values = new ContentValues();
        values.put("id", id);
        values.put("user_name", user_name);
        values.put("password", password);
        values.put("name", name);
        values.put("real_name", real_name);
        values.put("sex", sex);
        values.put("company_id", company_id);
        values.put("company_name", company_name);
        values.put("phone", phone);
        values.put("portrait_path", portrait_path);

        values.put("balance", balance);
        values.put("service_type", service_type);
        values.put("child_type", child_type);

        values.put("bank_name", bank_name);
        values.put("bank_card_no", bank_card_no);
        values.put("cash_person_name", cash_person_name);
        values.put("score", score);
        values.put("status", status);
        values.put("company_phone", company_phone);
        /*
         * values.put("age", age); values.put("jialing", jialing);
		 */
        boolean flag = db.update("t_driverinfo", values, " id = ? ",
                new String[]{String.valueOf(id)}) == 1;
        return flag;
    }

    public boolean saveOrUpdate() {
        if (exists(this.id)) {
            return this.updateAll();
        } else {
            return this.save();
        }
    }

    public Employ() {
    }

    protected Employ(Parcel in) {
        id = in.readLong();
        user_name = in.readString();
        password = in.readString();
        name = in.readString();
        real_name = in.readString();
        sex = in.readString();
        company_id = in.readLong();
        phone = in.readString();
        balance = in.readDouble();
        service_type = in.readString();
        bank_name = in.readString();
        bank_card_no = in.readString();
        cash_person_name = in.readString();
        portrait_path = in.readString();
        score = in.readDouble();
        status = in.readString();
        company_phone = in.readString();
    }

    public static final Creator<Employ> CREATOR = new Creator<Employ>() {
        @Override
        public Employ createFromParcel(Parcel in) {
            return new Employ(in);
        }

        @Override
        public Employ[] newArray(int size) {
            return new Employ[size];
        }
    };

    @Override
    public String toString() {
        return "Employ{" +
                "id=" + id +
                ", user_name='" + user_name + '\'' +
                ", password='" + password + '\'' +
                ", name='" + name + '\'' +
                ", real_name='" + real_name + '\'' +
                ", sex='" + sex + '\'' +
                ", phone='" + phone + '\'' +
                ", company_id=" + company_id +
                ", balance=" + balance +
                ", service_type='" + service_type + '\'' +
                ", bank_name='" + bank_name + '\'' +
                ", bank_card_no='" + bank_card_no + '\'' +
                ", cash_person_name='" + cash_person_name + '\'' +
                ", portrait_path='" + portrait_path + '\'' +
                ", score='" + score + '\'' +
                ", status='" + status + '\'' +
                ", company_phone='" + company_phone + '\'' +
                '}';
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(id);
        dest.writeString(user_name);
        dest.writeString(password);
        dest.writeString(name);
        dest.writeString(real_name);
        dest.writeString(sex);
        dest.writeLong(company_id);
        dest.writeString(phone);
        dest.writeDouble(balance);
        dest.writeString(service_type);
        dest.writeString(bank_name);
        dest.writeString(bank_card_no);
        dest.writeString(cash_person_name);
        dest.writeString(portrait_path);
        dest.writeDouble(score);
        dest.writeString(status);
        dest.writeString(company_phone);
    }
}

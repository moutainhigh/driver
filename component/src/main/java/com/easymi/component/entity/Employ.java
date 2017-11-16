package com.easymi.component.entity;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Parcel;
import android.os.Parcelable;

import com.easymi.component.db.SqliteHelper;

/**
 * Created by liuzihao on 2017/11/16.
 */

public class Employ implements Parcelable{

    public long id;
    public String user_name;
    public String password;
    public String name;
    public String real_name;
    public String sex;
    public long company_id;
    public String phone;


    public String id_card;
    public String home_address;
    public String emergency;
    public String emergency_phone;
    public String drive_license_type;
    public String introducer;
    public String remark;
    public int is_freezed;
    public String order_status;
    public double balance;
    public double pre_money;
    public int driver_type;
    public String service_type;
    public String errand_child_type;
    public long service_times;
    public String bank_name;
    public String bank_card_no;
    public String cash_person_name;

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
        values.put("phone", phone);
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
                        "company_id", "phone"},
                "id = ?", new String[]{String.valueOf(driverID)},
                null, null, null);
        Employ driverInfo = null;
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
                driverInfo.phone = cursor.getString(cursor
                        .getColumnIndex("phone"));

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
     * 根据ID修改数据
     */
    public boolean update() {
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
        values.put("phone", phone);
		/*
		 * values.put("age", age); values.put("jialing", jialing);
		 */
        boolean flag = db.update("t_driverinfo", values, " id = ? ",
                new String[]{String.valueOf(id)}) == 1;
        return flag;
    }

    public boolean saveOrUpdate() {
        if (exists(this.id)) {
            return this.update();
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
        id_card = in.readString();
        home_address = in.readString();
        emergency = in.readString();
        emergency_phone = in.readString();
        drive_license_type = in.readString();
        introducer = in.readString();
        remark = in.readString();
        is_freezed = in.readInt();
        order_status = in.readString();
        balance = in.readDouble();
        pre_money = in.readDouble();
        driver_type = in.readInt();
        service_type = in.readString();
        errand_child_type = in.readString();
        service_times = in.readLong();
        bank_name = in.readString();
        bank_card_no = in.readString();
        cash_person_name = in.readString();
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
                ", id_card='" + id_card + '\'' +
                ", phone='" + phone + '\'' +
                ", home_address='" + home_address + '\'' +
                ", emergency='" + emergency + '\'' +
                ", emergency_phone='" + emergency_phone + '\'' +
                ", drive_license_type='" + drive_license_type + '\'' +
                ", introducer='" + introducer + '\'' +
                ", remark='" + remark + '\'' +
                ", is_freezed=" + is_freezed +
                ", order_status='" + order_status + '\'' +
                ", company_id=" + company_id +
                ", balance=" + balance +
                ", pre_money=" + pre_money +
                ", driver_type=" + driver_type +
                ", service_type='" + service_type + '\'' +
                ", errand_child_type='" + errand_child_type + '\'' +
                ", service_times=" + service_times +
                ", bank_name='" + bank_name + '\'' +
                ", bank_card_no='" + bank_card_no + '\'' +
                ", cash_person_name='" + cash_person_name + '\'' +
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
        dest.writeString(id_card);
        dest.writeString(home_address);
        dest.writeString(emergency);
        dest.writeString(emergency_phone);
        dest.writeString(drive_license_type);
        dest.writeString(introducer);
        dest.writeString(remark);
        dest.writeInt(is_freezed);
        dest.writeString(order_status);
        dest.writeDouble(balance);
        dest.writeDouble(pre_money);
        dest.writeInt(driver_type);
        dest.writeString(service_type);
        dest.writeString(errand_child_type);
        dest.writeLong(service_times);
        dest.writeString(bank_name);
        dest.writeString(bank_card_no);
        dest.writeString(cash_person_name);
    }
}

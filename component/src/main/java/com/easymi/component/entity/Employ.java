package com.easymi.component.entity;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Parcel;
import android.os.Parcelable;

import com.easymi.component.db.SqliteHelper;
import com.google.gson.annotations.SerializedName;

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

    public String app_key;

    public Vehicle vehicle;//车辆信息

    public String device_no;

    //1正常，2已完善，正在审核，3.没完善资料，4驳回原因
    @SerializedName("audit_type")
    public int auditType;

    @SerializedName("reject")
    public String reject;

//add hufeng

    /**
     * id
     */
//    private Long id;

    /**
     * 工号
     */
    private String userName;

    /**
     * 密码
     */
//    private String password;

    /**
     * 昵称
     */
    private String nickName;

    /**
     * 真实姓名
     */
    private String realName;

    /**
     * 身份证号码
     */
    private String idCard;

    /**
     * 性别 1，男；2，女；3，未知
     */
//    private Integer sex;

    /**
     * 出生日期
     */
    private Integer birthDate;

    /**
     * 手机号
     */
//    private String phone;

    /**
     * 头像照片地址
     */
    private String portraitPath;

    /**
     * 服务类型
     */
    private String serviceType;

    /**
     * 紧急联系人（姓名+电话+关系）
     */
    private String emergency;

    /**
     * 紧急电话
     */
    private String emergencyPhone;

    /**
     * 驾驶证初次领取时间
     */
    private Long licensingTime;

    /**
     * 驾驶证有效期起
     */
    private Integer driveLicenceStart;

    /**
     * 驾驶证有效期限止
     */
    private Integer driveLicenceEnd;

    /**
     * 等级
     */
    private Long level;

    /**
     * 入职时间
     */
    private Integer dutyTime;

    /**
     * 司机状态（0离线 5在线 10空闲 15派单 20接单 25前往预约地 30到达预约地 35前往目的地 40中途等待  45冻结）
     */
//    private String status;

    /**
     * 推荐人
     */
    private String introducer;

    /**
     * 所属公司Id
     */
    private Long companyId;

    /**
     * 创建时间
     */
    private Long created;

    /**
     * 修改时间
     */
    private Long updated;

    /**
     * 身份证照片地址
     */
    private String idcardPath;

    /**
     * 家庭地址
     */
    private String homeAddress;

    /**
     * 驾驶证照片地址
     */
    private String driveLicensePath;

    /**
     * 驾照类型
     */
    private String driveLicenseType;

    /**
     * 行驶证照片地址
     */
    private String drivingLicensePath;

    /**
     * 备注
     */
    private String remark;

    /**
     * 设备信息
     */
    private String appVersion;

    /**
     * 类型
     */
    private Long driverType;

    /**
     * 设备编号
     */
    private String deviceNo;

    /**
     * 设备类型
     */
    private String deviceType;

    /**
     * 全身照地址
     */
    private String fullBodyPath;

    /**
     * 身高
     */
    private String height;

    /**
     * 籍贯
     */
    private String originPlace;

    /**
     * 机动车驾驶证号
     */
    private String motorNo;

    /**
     * 国籍
     */
    private String nationality;

    /**
     * 网络预约出租车驾驶员证编号
     */
    private String networkTaximanNo;

    /**
     * 驾驶员民族
     */
    private String nation;

    /**
     * 驾驶员婚姻状况(0未婚，1已婚，2保密)
     */
    private String maritalStatus;

    /**
     * 驾驶员外语能力
     */
    private String foreignLanguageLevel;

    /**
     * 驾驶员通信地址
     */
    private String email;

    /**
     * 驾驶员学历
     */
    private String education;

    /**
     * 网络预约出租车驾驶员领证日期
     */
    private Integer networkTaximanLicenseDate;

    /**
     * 驾驶员发证机构
     */
    private String licenseOrganization;

    /**
     * 交通违章次数
     */
    private Integer trafficViolationTimes;

    /**
     * 合同有效期起
     */
    private Integer contractStart;

    /**
     * 合同有效期止
     */
    private Integer contractEnd;

    /**
     * NULL       NOT      NULL巡游出租汽车驾驶员
     */
    private Integer isCruisingTaxiDrivers;

    /**
     * 司机服务运营商
     */
    private String driverServiceOperator;

    /**
     * 使用地图类型
     */
    private String mapType;

    /**
     * 身份证背面照片地址
     */
    private String idcardBackPath;

    /**
     * 手机型号
     */
    private String mobileModel;

    /**
     * 手机运营商
     */
    private String mobileOperators;

    /**
     * NULL       NOT      NULL专职驾驶员
     */
    private Integer isFulltimeDriver;

    /**
     * 户口登记机关名称
     */
    private String householdRegistrationName;


    protected Employ(Parcel in) {
        user_name = in.readString();
        password = in.readString();
        name = in.readString();
        sex = in.readString();
        company_name = in.readString();
        portrait_path = in.readString();
        balance = in.readDouble();
        service_type = in.readString();
        bank_name = in.readString();
        bank_card_no = in.readString();
        cash_person_name = in.readString();
        score = in.readDouble();
        company_phone = in.readString();
        app_key = in.readString();
        device_no = in.readString();
        auditType = in.readInt();
        reject = in.readString();
        userName = in.readString();
        nickName = in.readString();
        realName = in.readString();
        idCard = in.readString();
        if (in.readByte() == 0) {
            birthDate = null;
        } else {
            birthDate = in.readInt();
        }
        portraitPath = in.readString();
        serviceType = in.readString();
        emergency = in.readString();
        emergencyPhone = in.readString();
        if (in.readByte() == 0) {
            licensingTime = null;
        } else {
            licensingTime = in.readLong();
        }
        if (in.readByte() == 0) {
            driveLicenceStart = null;
        } else {
            driveLicenceStart = in.readInt();
        }
        if (in.readByte() == 0) {
            driveLicenceEnd = null;
        } else {
            driveLicenceEnd = in.readInt();
        }
        if (in.readByte() == 0) {
            level = null;
        } else {
            level = in.readLong();
        }
        if (in.readByte() == 0) {
            dutyTime = null;
        } else {
            dutyTime = in.readInt();
        }
        introducer = in.readString();
        if (in.readByte() == 0) {
            companyId = null;
        } else {
            companyId = in.readLong();
        }
        if (in.readByte() == 0) {
            created = null;
        } else {
            created = in.readLong();
        }
        if (in.readByte() == 0) {
            updated = null;
        } else {
            updated = in.readLong();
        }
        idcardPath = in.readString();
        homeAddress = in.readString();
        driveLicensePath = in.readString();
        driveLicenseType = in.readString();
        drivingLicensePath = in.readString();
        remark = in.readString();
        appVersion = in.readString();
        if (in.readByte() == 0) {
            driverType = null;
        } else {
            driverType = in.readLong();
        }
        deviceNo = in.readString();
        deviceType = in.readString();
        fullBodyPath = in.readString();
        height = in.readString();
        originPlace = in.readString();
        motorNo = in.readString();
        nationality = in.readString();
        networkTaximanNo = in.readString();
        nation = in.readString();
        maritalStatus = in.readString();
        foreignLanguageLevel = in.readString();
        email = in.readString();
        education = in.readString();
        if (in.readByte() == 0) {
            networkTaximanLicenseDate = null;
        } else {
            networkTaximanLicenseDate = in.readInt();
        }
        licenseOrganization = in.readString();
        if (in.readByte() == 0) {
            trafficViolationTimes = null;
        } else {
            trafficViolationTimes = in.readInt();
        }
        if (in.readByte() == 0) {
            contractStart = null;
        } else {
            contractStart = in.readInt();
        }
        if (in.readByte() == 0) {
            contractEnd = null;
        } else {
            contractEnd = in.readInt();
        }
        if (in.readByte() == 0) {
            isCruisingTaxiDrivers = null;
        } else {
            isCruisingTaxiDrivers = in.readInt();
        }
        driverServiceOperator = in.readString();
        mapType = in.readString();
        idcardBackPath = in.readString();
        mobileModel = in.readString();
        mobileOperators = in.readString();
        if (in.readByte() == 0) {
            isFulltimeDriver = null;
        } else {
            isFulltimeDriver = in.readInt();
        }
        householdRegistrationName = in.readString();
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
        values.put("auditType", auditType);

//hf

//       values.put("id", id);
        values.put("userName", userName);
//     values.put("password", password);
        values.put("nickName", nickName);
        values.put("realName", realName);
        values.put("idCard", idCard);
//     values.put("sex", sex);
        values.put("birthDate", birthDate);
        values.put("phone", phone);
        values.put("portraitPath", portraitPath);
        values.put("serviceType", serviceType);
        values.put("emergency", emergency);
        values.put("emergencyPhone", emergencyPhone);
        values.put("licensingTime", licensingTime);
        values.put("driveLicenceStart", driveLicenceStart);
        values.put("driveLicenceEnd", driveLicenceEnd);
        values.put("level", level);
        values.put("dutyTime", dutyTime);
//         values.put("status", status);
        values.put("introducer", introducer);
        values.put("companyId", companyId);
        values.put("created", created);
        values.put("updated", updated);
        values.put("idcardPath", idcardPath);
        values.put("homeAddress", homeAddress);
        values.put("driveLicensePath", driveLicensePath);
        values.put("driveLicenseType", driveLicenseType);
        values.put("drivingLicensePath", drivingLicensePath);
        values.put("remark", remark);
        values.put("appVersion", appVersion);
        values.put("driverType", driverType);
        values.put("deviceNo", deviceNo);
        values.put("deviceType", deviceType);
        values.put("fullBodyPath", fullBodyPath);
        values.put("height", height);
        values.put("originPlace", originPlace);
        values.put("motorNo", motorNo);
        values.put("nationality", nationality);
        values.put("networkTaximanNo", networkTaximanNo);
        values.put("nation", nation);
        values.put("maritalStatus", maritalStatus);
        values.put("foreignLanguageLevel", foreignLanguageLevel);
        values.put("email", email);
        values.put("education", education);
        values.put("networkTaximanLicenseDate", networkTaximanLicenseDate);
        values.put("licenseOrganization", licenseOrganization);
        values.put("trafficViolationTimes", trafficViolationTimes);
        values.put("contractStart", contractStart);
        values.put("contractEnd", contractEnd);
        values.put("isCruisingTaxiDrivers", isCruisingTaxiDrivers);
        values.put("driverServiceOperator", driverServiceOperator);
        values.put("mapType", mapType);
        values.put("idcardBackPath", idcardBackPath);
        values.put("mobileModel", mobileModel);
        values.put("mobileOperators", mobileOperators);
        values.put("isFulltimeDriver", isFulltimeDriver);
        values.put("householdRegistrationName", householdRegistrationName);

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
        Cursor cursor = db.rawQuery("select * from t_driverinfo where id = ?", new String[]{String.valueOf(driverID)});
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

                driverInfo.auditType = cursor.getInt(cursor
                        .getColumnIndex("auditType"));
                driverInfo.vehicle = Vehicle.findByEmployId(driverID);

                //add hufeng

//                 driverInfo.id = cursor.getLong(cursor.getColumnIndex("id"));
                driverInfo.userName = cursor.getString(cursor
                        .getColumnIndex("userName"));
//     driverInfo.bank_card_no = cursor.getString(cursor.getColumnIndex("password"));
                driverInfo.nickName = cursor.getString(cursor
                        .getColumnIndex("nickName"));
                driverInfo.realName = cursor.getString(cursor
                        .getColumnIndex("realName"));
                driverInfo.idCard = cursor.getString(cursor
                        .getColumnIndex("idCard"));
//     driverInfo.sex = cursor.getInt(cursor.getColumnIndex("sex"));
                driverInfo.birthDate = cursor.getInt(cursor
                        .getColumnIndex("birthDate"));
                driverInfo.phone = cursor.getString(cursor
                        .getColumnIndex("phone"));
                driverInfo.portraitPath = cursor.getString(cursor
                        .getColumnIndex("portraitPath"));
                driverInfo.serviceType = cursor.getString(cursor
                        .getColumnIndex("serviceType"));
                driverInfo.emergency = cursor.getString(cursor
                        .getColumnIndex("emergency"));
                driverInfo.emergencyPhone = cursor.getString(cursor
                        .getColumnIndex("emergencyPhone"));
                driverInfo.licensingTime = cursor.getLong(cursor
                        .getColumnIndex("licensingTime"));
                driverInfo.driveLicenceStart = cursor.getInt(cursor
                        .getColumnIndex("driveLicenceStart"));
                driverInfo.driveLicenceEnd = cursor.getInt(cursor
                        .getColumnIndex("driveLicenceEnd"));
                driverInfo.level = cursor.getLong(cursor
                        .getColumnIndex("level"));
                driverInfo.dutyTime = cursor.getInt(cursor
                        .getColumnIndex("dutyTime"));
//                 driverInfo.status = cursor.getString(cursor.getColumnIndex("status"));
                driverInfo.introducer = cursor.getString(cursor
                        .getColumnIndex("introducer"));
                driverInfo.companyId = cursor.getLong(cursor
                        .getColumnIndex("companyId"));
                driverInfo.created = cursor.getLong(cursor
                        .getColumnIndex("created"));
                driverInfo.updated = cursor.getLong(cursor
                        .getColumnIndex("updated"));
                driverInfo.idcardPath = cursor.getString(cursor
                        .getColumnIndex("idcardPath"));
                driverInfo.homeAddress = cursor.getString(cursor
                        .getColumnIndex("homeAddress"));
                driverInfo.driveLicensePath = cursor.getString(cursor
                        .getColumnIndex("driveLicensePath"));
                driverInfo.driveLicenseType = cursor.getString(cursor
                        .getColumnIndex("driveLicenseType"));
                driverInfo.drivingLicensePath = cursor.getString(cursor
                        .getColumnIndex("drivingLicensePath"));
                driverInfo.remark = cursor.getString(cursor
                        .getColumnIndex("remark"));
                driverInfo.appVersion = cursor.getString(cursor
                        .getColumnIndex("appVersion"));
                driverInfo.driverType = cursor.getLong(cursor
                        .getColumnIndex("driverType"));
                driverInfo.deviceNo = cursor.getString(cursor
                        .getColumnIndex("deviceNo"));
                driverInfo.deviceType = cursor.getString(cursor
                        .getColumnIndex("deviceType"));
                driverInfo.fullBodyPath = cursor.getString(cursor
                        .getColumnIndex("fullBodyPath"));
                driverInfo.height = cursor.getString(cursor
                        .getColumnIndex("height"));
                driverInfo.originPlace = cursor.getString(cursor
                        .getColumnIndex("originPlace"));
                driverInfo.motorNo = cursor.getString(cursor
                        .getColumnIndex("motorNo"));
                driverInfo.nationality = cursor.getString(cursor
                        .getColumnIndex("nationality"));
                driverInfo.networkTaximanNo = cursor.getString(cursor
                        .getColumnIndex("networkTaximanNo"));
                driverInfo.nation = cursor.getString(cursor
                        .getColumnIndex("nation"));
                driverInfo.maritalStatus = cursor.getString(cursor
                        .getColumnIndex("maritalStatus"));
                driverInfo.foreignLanguageLevel = cursor.getString(cursor
                        .getColumnIndex("foreignLanguageLevel"));
                driverInfo.email = cursor.getString(cursor
                        .getColumnIndex("email"));
                driverInfo.education = cursor.getString(cursor
                        .getColumnIndex("education"));
                driverInfo.networkTaximanLicenseDate = cursor.getInt(cursor
                        .getColumnIndex("networkTaximanLicenseDate"));
                driverInfo.licenseOrganization = cursor.getString(cursor
                        .getColumnIndex("licenseOrganization"));
                driverInfo.trafficViolationTimes = cursor.getInt(cursor
                        .getColumnIndex("trafficViolationTimes"));
                driverInfo.contractStart = cursor.getInt(cursor
                        .getColumnIndex("contractStart"));
                driverInfo.contractEnd = cursor.getInt(cursor
                        .getColumnIndex("contractEnd"));
                driverInfo.isCruisingTaxiDrivers = cursor.getInt(cursor
                        .getColumnIndex("isCruisingTaxiDrivers"));
                driverInfo.driverServiceOperator = cursor.getString(cursor
                        .getColumnIndex("driverServiceOperator"));
                driverInfo.mapType = cursor.getString(cursor
                        .getColumnIndex("mapType"));
                driverInfo.idcardBackPath = cursor.getString(cursor
                        .getColumnIndex("idcardBackPath"));
                driverInfo.mobileModel = cursor.getString(cursor
                        .getColumnIndex("mobileModel"));
                driverInfo.mobileOperators = cursor.getString(cursor
                        .getColumnIndex("mobileOperators"));
                driverInfo.isFulltimeDriver = cursor.getInt(cursor
                        .getColumnIndex("isFulltimeDriver"));
                driverInfo.householdRegistrationName = cursor.getString(cursor
                        .getColumnIndex("householdRegistrationName"));

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
        driverInfo.auditType = cursor.getInt(cursor
                .getColumnIndex("auditType"));



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
        values.put("auditType", auditType);


        /*
         * values.put("age", age); values.put("jialing", jialing);
         */
        boolean flag = db.update("t_driverinfo", values, " id = ? ",
                new String[]{String.valueOf(id)}) == 1;
        return flag;
    }

    public boolean saveOrUpdate() {
        if (null != vehicle) { //保存或更新车辆信息
            vehicle.saveOrUpdate(id);
        }
        if (exists(this.id)) {
            return this.updateAll();
        } else {
            return this.save();
        }
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(user_name);
        dest.writeString(password);
        dest.writeString(name);
        dest.writeString(sex);
        dest.writeString(company_name);
        dest.writeString(portrait_path);
        dest.writeDouble(balance);
        dest.writeString(service_type);
        dest.writeString(bank_name);
        dest.writeString(bank_card_no);
        dest.writeString(cash_person_name);
        dest.writeDouble(score);
        dest.writeString(company_phone);
        dest.writeString(app_key);
        dest.writeString(device_no);
        dest.writeInt(auditType);
        dest.writeString(reject);
        dest.writeString(userName);
        dest.writeString(nickName);
        dest.writeString(realName);
        dest.writeString(idCard);
        if (birthDate == null) {
            dest.writeByte((byte) 0);
        } else {
            dest.writeByte((byte) 1);
            dest.writeInt(birthDate);
        }
        dest.writeString(portraitPath);
        dest.writeString(serviceType);
        dest.writeString(emergency);
        dest.writeString(emergencyPhone);
        if (licensingTime == null) {
            dest.writeByte((byte) 0);
        } else {
            dest.writeByte((byte) 1);
            dest.writeLong(licensingTime);
        }
        if (driveLicenceStart == null) {
            dest.writeByte((byte) 0);
        } else {
            dest.writeByte((byte) 1);
            dest.writeInt(driveLicenceStart);
        }
        if (driveLicenceEnd == null) {
            dest.writeByte((byte) 0);
        } else {
            dest.writeByte((byte) 1);
            dest.writeInt(driveLicenceEnd);
        }
        if (level == null) {
            dest.writeByte((byte) 0);
        } else {
            dest.writeByte((byte) 1);
            dest.writeLong(level);
        }
        if (dutyTime == null) {
            dest.writeByte((byte) 0);
        } else {
            dest.writeByte((byte) 1);
            dest.writeInt(dutyTime);
        }
        dest.writeString(introducer);
        if (companyId == null) {
            dest.writeByte((byte) 0);
        } else {
            dest.writeByte((byte) 1);
            dest.writeLong(companyId);
        }
        if (created == null) {
            dest.writeByte((byte) 0);
        } else {
            dest.writeByte((byte) 1);
            dest.writeLong(created);
        }
        if (updated == null) {
            dest.writeByte((byte) 0);
        } else {
            dest.writeByte((byte) 1);
            dest.writeLong(updated);
        }
        dest.writeString(idcardPath);
        dest.writeString(homeAddress);
        dest.writeString(driveLicensePath);
        dest.writeString(driveLicenseType);
        dest.writeString(drivingLicensePath);
        dest.writeString(remark);
        dest.writeString(appVersion);
        if (driverType == null) {
            dest.writeByte((byte) 0);
        } else {
            dest.writeByte((byte) 1);
            dest.writeLong(driverType);
        }
        dest.writeString(deviceNo);
        dest.writeString(deviceType);
        dest.writeString(fullBodyPath);
        dest.writeString(height);
        dest.writeString(originPlace);
        dest.writeString(motorNo);
        dest.writeString(nationality);
        dest.writeString(networkTaximanNo);
        dest.writeString(nation);
        dest.writeString(maritalStatus);
        dest.writeString(foreignLanguageLevel);
        dest.writeString(email);
        dest.writeString(education);
        if (networkTaximanLicenseDate == null) {
            dest.writeByte((byte) 0);
        } else {
            dest.writeByte((byte) 1);
            dest.writeInt(networkTaximanLicenseDate);
        }
        dest.writeString(licenseOrganization);
        if (trafficViolationTimes == null) {
            dest.writeByte((byte) 0);
        } else {
            dest.writeByte((byte) 1);
            dest.writeInt(trafficViolationTimes);
        }
        if (contractStart == null) {
            dest.writeByte((byte) 0);
        } else {
            dest.writeByte((byte) 1);
            dest.writeInt(contractStart);
        }
        if (contractEnd == null) {
            dest.writeByte((byte) 0);
        } else {
            dest.writeByte((byte) 1);
            dest.writeInt(contractEnd);
        }
        if (isCruisingTaxiDrivers == null) {
            dest.writeByte((byte) 0);
        } else {
            dest.writeByte((byte) 1);
            dest.writeInt(isCruisingTaxiDrivers);
        }
        dest.writeString(driverServiceOperator);
        dest.writeString(mapType);
        dest.writeString(idcardBackPath);
        dest.writeString(mobileModel);
        dest.writeString(mobileOperators);
        if (isFulltimeDriver == null) {
            dest.writeByte((byte) 0);
        } else {
            dest.writeByte((byte) 1);
            dest.writeInt(isFulltimeDriver);
        }
        dest.writeString(householdRegistrationName);
    }


    public Employ() {

    }

//    protected Employ(Parcel in) {
//        id = in.readLong();
//        user_name = in.readString();
//        password = in.readString();
//        name = in.readString();
//        real_name = in.readString();
//        sex = in.readString();
//        company_id = in.readLong();
//        phone = in.readString();
//        balance = in.readDouble();
//        service_type = in.readString();
//        bank_name = in.readString();
//        bank_card_no = in.readString();
//        cash_person_name = in.readString();
//        portrait_path = in.readString();
//        score = in.readDouble();
//        status = in.readString();
//        company_phone = in.readString();
//        auditType = in.readInt();
//    }
//
//    public static final Creator<Employ> CREATOR = new Creator<Employ>() {
//        @Override
//        public Employ createFromParcel(Parcel in) {
//            return new Employ(in);
//        }
//
//        @Override
//        public Employ[] newArray(int size) {
//            return new Employ[size];
//        }
//    };
//
//    @Override
//    public String toString() {
//        return "Employ{" +
//                "id=" + id +
//                ", user_name='" + user_name + '\'' +
//                ", password='" + password + '\'' +
//                ", name='" + name + '\'' +
//                ", real_name='" + real_name + '\'' +
//                ", sex='" + sex + '\'' +
//                ", phone='" + phone + '\'' +
//                ", company_id=" + company_id +
//                ", balance=" + balance +
//                ", service_type='" + service_type + '\'' +
//                ", bank_name='" + bank_name + '\'' +
//                ", bank_card_no='" + bank_card_no + '\'' +
//                ", cash_person_name='" + cash_person_name + '\'' +
//                ", portrait_path='" + portrait_path + '\'' +
//                ", score='" + score + '\'' +
//                ", status='" + status + '\'' +
//                ", company_phone='" + company_phone + '\'' +
//                ", auditType='" + auditType + '\'' +
//                '}';
//    }
//
//    @Override
//    public int describeContents() {
//        return 0;
//    }
//
//    @Override
//    public void writeToParcel(Parcel dest, int flags) {
//        dest.writeLong(id);
//        dest.writeString(user_name);
//        dest.writeString(password);
//        dest.writeString(name);
//        dest.writeString(real_name);
//        dest.writeString(sex);
//        dest.writeLong(company_id);
//        dest.writeString(phone);
//        dest.writeDouble(balance);
//        dest.writeString(service_type);
//        dest.writeString(bank_name);
//        dest.writeString(bank_card_no);
//        dest.writeString(cash_person_name);
//        dest.writeString(portrait_path);
//        dest.writeDouble(score);
//        dest.writeString(status);
//        dest.writeString(company_phone);
//        dest.writeInt(auditType);
//    }
}

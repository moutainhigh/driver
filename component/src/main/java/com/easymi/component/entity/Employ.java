package com.easymi.component.entity;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Parcel;
import android.os.Parcelable;

import com.easymi.component.db.SqliteHelper;
import com.easymi.component.utils.EmUtil;
import com.easymi.component.utils.Log;
import com.google.gson.annotations.SerializedName;

import java.util.LinkedList;
import java.util.List;

/**
 * Copyright (C), 2012-2018, Sichuan Xiaoka Technology Co., Ltd.
 * FileName:
 *
 * @Author: shine
 * Date: 2018/12/24 下午1:10
 * Description:
 * History:
 */
public class Employ implements Parcelable {

    public long id;

    /**
     * 工号
     */
    public String userName;

    /**
     * 密码
     */
    public String password;

    /**
     * 昵称
     */
    public String nickName;

    /**
     * 真实姓名
     */
    public String realName;

    /**
     * 身份证号码
     */
    public String idCard;

    /**
     * 性别 1，男；2，女；3，未知
     */
    public int sex;

    /**
     * 出生日期
     */
    public long birthDate;

    /**
     * 手机号
     */
    public String phone;

    /**
     * 头像照片地址
     */
    public String portraitPath;

    /**
     * 服务类型
     */
    public String serviceType;

    /**
     * 紧急联系人（姓名+电话+关系）
     */
    public String emergency;

    /**
     * 紧急电话
     */
    public String emergencyPhone;

    /**
     * 驾驶证初次领取时间
     */
    public long licensingTime;

    /**
     * 驾驶证有效期起
     */
    public int driveLicenceStart;

    /**
     * 驾驶证有效期限止
     */
    public int driveLicenceEnd;

    /**
     * 等级
     */
    public long level;

    /**
     * 入职时间
     */
    public long dutyTime;

    /**
     * 司机状态（0离线 5在线 10空闲 15派单 20接单 25前往预约地 30到达预约地 35前往目的地 40中途等待  45冻结）
     * 司机状态 1离线 2空闲 5派单 10接单 15前往预约地 20到达预约地 25前往目的地 28中途等待 45冻结   修改后的
     * 司机状态 1离线 2登陆未听单 3上线听单中（空闲） 5派单 10接单 15前往预约地 20到达预约地 25前往目的地 28中途等待 45冻结   再次修改后的
     **/
    public int status;

    /**
     * 推荐人
     */
    public String introducer;

    /**
     * 所属公司Id
     */
    public long companyId;

    /**
     * 创建时间
     */
    public long created;

    /**
     * 修改时间
     */
    public long updated;

    /**
     * 身份证照片地址
     */
    public String idcardPath;

    /**
     * 家庭地址
     */
    public String homeAddress;

    /**
     * 驾驶证照片地址
     */
    public String driveLicensePath;

    /**
     * 驾照类型
     */
    public String driveLicenseType;

    /**
     * 行驶证照片地址
     */
    public String drivingLicensePath;

    /**
     * 备注
     */
    public String remark;

    /**
     * 设备信息
     */
    public String appVersion;

    /**
     * 类型
     */
    public long driverType;

    /**
     * 设备编号
     */
    public String deviceNo;

    /**
     * 设备类型
     */
    public String deviceType;

    /**
     * 全身照地址
     */
    public String fullBodyPath;

    /**
     * 身高
     */
    public String height;

    /**
     * 籍贯
     */
    public String originPlace;

    /**
     * 机动车驾驶证号
     */
    public String motorNo;

    /**
     * 国籍
     */
    public String nationality;

    /**
     * 网络预约出租车驾驶员证编号
     */
    public String networkTaximanNo;

    /**
     * 驾驶员民族
     */
    public String nation;

    /**
     * 驾驶员婚姻状况(0未婚，1已婚，2保密)
     */
    public String maritalStatus;

    /**
     * 驾驶员外语能力
     */
    public String foreignLanguageLevel;

    /**
     * 驾驶员通信地址
     */
    public String email;

    /**
     * 驾驶员学历
     */
    public String education;

    /**
     * 网络预约出租车驾驶员领证日期
     */
    public long networkTaximanLicenseDate;

    /**
     * 驾驶员发证机构
     */
    public String licenseOrganization;

    /**
     * 交通违章次数
     */
    public int trafficViolationTimes;

    /**
     * 合同有效期起
     */
    public long contractStart;

    /**
     * 合同有效期止
     */
    public long contractEnd;

    /**
     * NULL       NOT      NULL巡游出租汽车驾驶员
     */
    public int isCruisingTaxiDrivers;

    /**
     * 司机服务运营商
     */
    public String driverServiceOperator;

    /**
     * 使用地图类型
     */
    public String mapType;

    /**
     * 身份证背面照片地址
     */
    public String idcardBackPath;

    /**
     * 手机型号
     */
    public String mobileModel;

    /**
     * 手机运营商
     */
    public String mobileOperators;

    /**
     * NULL       NOT      NULL专职驾驶员
     */
    public int isFulltimeDriver;

    /**
     * 户口登记机关名称
     */
    public String householdRegistrationName;

    /**
     * 登录token
     */
    public String token;

    /**
     * 刷新token
     */
    public String refreshToken;

    /**
     * 余额
     */
    public double balance;

    /**
     * 司机平均星级
     */
    public double star;

    /**
     * specialModel 专车车辆型号
     */
    public long modelId;

    /**
     * specialModel 出租车车辆型号
     */
    public long taxiModelId;

    /**
     * 注册状态：1.未提交资料；2.审核中；3驳回；4通过
     */
    public int registerStatus;

    /**
     * 司机名片地址host
     */
    public String qrCodeUrl;

    /**
     * 客服电话
     */
    public String serviceTel;


    protected Employ(Parcel in) {
        id = in.readLong();
        userName = in.readString();
        password = in.readString();
        nickName = in.readString();
        realName = in.readString();
        idCard = in.readString();
        sex = in.readInt();
        birthDate = in.readLong();
        phone = in.readString();
        portraitPath = in.readString();
        serviceType = in.readString();
        emergency = in.readString();
        emergencyPhone = in.readString();
        licensingTime = in.readLong();
        driveLicenceStart = in.readInt();
        driveLicenceEnd = in.readInt();
        level = in.readLong();
        dutyTime = in.readLong();
        status = in.readInt();
        introducer = in.readString();
        companyId = in.readLong();
        created = in.readLong();
        updated = in.readLong();
        idcardPath = in.readString();
        homeAddress = in.readString();
        driveLicensePath = in.readString();
        driveLicenseType = in.readString();
        drivingLicensePath = in.readString();
        remark = in.readString();
        appVersion = in.readString();
        driverType = in.readLong();
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
        networkTaximanLicenseDate = in.readLong();
        licenseOrganization = in.readString();
        trafficViolationTimes = in.readInt();
        contractStart = in.readLong();
        contractEnd = in.readLong();
        isCruisingTaxiDrivers = in.readInt();
        driverServiceOperator = in.readString();
        mapType = in.readString();
        idcardBackPath = in.readString();
        mobileModel = in.readString();
        mobileOperators = in.readString();
        isFulltimeDriver = in.readInt();
        householdRegistrationName = in.readString();
        token = in.readString();
        refreshToken = in.readString();
        balance = in.readDouble();
        modelId = in.readLong();
        taxiModelId = in.readLong();
        registerStatus = in.readInt();
        qrCodeUrl = in.readString();
        serviceTel = in.readString();
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
        values.put("userName", userName);
        values.put("password", password);
        values.put("nickName", nickName);
        values.put("realName", realName);
        values.put("idCard", idCard);
        values.put("sex", sex);
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
        values.put("status", status);
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
        values.put("token", token);
        values.put("refreshToken", refreshToken);
        values.put("balance", balance);
        values.put("modelId", modelId);
        values.put("taxiModelId", taxiModelId);
        values.put("registerStatus", registerStatus);
        values.put("qrCodeUrl",qrCodeUrl);
        values.put("serviceTel",serviceTel);
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

                driverInfo.id = cursor.getLong(cursor.getColumnIndex("id"));
                driverInfo.userName = cursor.getString(cursor
                        .getColumnIndex("userName"));
                driverInfo.password = cursor.getString(cursor.getColumnIndex("password"));
                driverInfo.nickName = cursor.getString(cursor
                        .getColumnIndex("nickName"));
                driverInfo.realName = cursor.getString(cursor
                        .getColumnIndex("realName"));
                driverInfo.idCard = cursor.getString(cursor
                        .getColumnIndex("idCard"));
                driverInfo.sex = cursor.getInt(cursor.getColumnIndex("sex"));
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
                driverInfo.status = cursor.getInt(cursor.getColumnIndex("status"));
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
                driverInfo.token = cursor.getString(cursor
                        .getColumnIndex("token"));
                driverInfo.refreshToken = cursor.getString(cursor
                        .getColumnIndex("refreshToken"));
                driverInfo.balance = cursor.getDouble(cursor
                        .getColumnIndex("balance"));
                driverInfo.modelId = cursor.getLong(cursor
                        .getColumnIndex("modelId"));
                driverInfo.taxiModelId = cursor.getLong(cursor.getColumnIndex("taxiModelId"));

                driverInfo.registerStatus = cursor.getInt(cursor.getColumnIndex("registerStatus"));

                driverInfo.qrCodeUrl = cursor.getString(cursor.getColumnIndex("qrCodeUrl"));

                driverInfo.serviceTel = cursor.getString(cursor.getColumnIndex("serviceTel"));
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

        driverInfo.id = cursor.getLong(cursor.getColumnIndex("id"));
        driverInfo.userName = cursor.getString(cursor
                .getColumnIndex("userName"));
        driverInfo.password = cursor.getString(cursor.getColumnIndex("password"));
        driverInfo.nickName = cursor.getString(cursor
                .getColumnIndex("nickName"));
        driverInfo.realName = cursor.getString(cursor
                .getColumnIndex("realName"));
        driverInfo.idCard = cursor.getString(cursor
                .getColumnIndex("idCard"));
        driverInfo.sex = cursor.getInt(cursor.getColumnIndex("sex"));
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
        driverInfo.status = cursor.getInt(cursor.getColumnIndex("status"));
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
        driverInfo.token = cursor.getString(cursor
                .getColumnIndex("token"));
        driverInfo.refreshToken = cursor.getString(cursor
                .getColumnIndex("refreshToken"));
        driverInfo.balance = cursor.getDouble(cursor
                .getColumnIndex("balance"));
        driverInfo.modelId = cursor.getLong(cursor
                .getColumnIndex("modelId"));
        driverInfo.taxiModelId = cursor.getLong(cursor.getColumnIndex("taxiModelId"));

        driverInfo.qrCodeUrl = cursor.getString(cursor.getColumnIndex("qrCodeUrl"));

        driverInfo.serviceTel = cursor.getString(cursor.getColumnIndex("serviceTel"));

        driverInfo.registerStatus = cursor.getInt(cursor.getColumnIndex("registerStatus"));
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
        values.put("userName", userName);
        values.put("password", password);
        values.put("nickName", nickName);
        values.put("realName", realName);
        values.put("idCard", idCard);
        values.put("sex", sex);
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
        values.put("status", status);
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
        values.put("token", token);
        values.put("refreshToken", refreshToken);
        values.put("balance", balance);
        values.put("modelId", modelId);
        values.put("taxiModelId", taxiModelId);
        values.put("serviceTel",serviceTel);

        values.put("registerStatus", registerStatus);
        values.put("qrCodeUrl",qrCodeUrl);

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


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(id);
        dest.writeString(userName);
        dest.writeString(password);
        dest.writeString(nickName);
        dest.writeString(realName);
        dest.writeString(idCard);
        dest.writeInt(sex);
        dest.writeLong(birthDate);
        dest.writeString(phone);
        dest.writeString(portraitPath);
        dest.writeString(serviceType);
        dest.writeString(emergency);
        dest.writeString(emergencyPhone);
        dest.writeLong(licensingTime);
        dest.writeInt(driveLicenceStart);
        dest.writeInt(driveLicenceEnd);
        dest.writeLong(level);
        dest.writeLong(dutyTime);
        dest.writeInt(status);
        dest.writeString(introducer);
        dest.writeLong(companyId);
        dest.writeLong(created);
        dest.writeLong(updated);
        dest.writeString(idcardPath);
        dest.writeString(homeAddress);
        dest.writeString(driveLicensePath);
        dest.writeString(driveLicenseType);
        dest.writeString(drivingLicensePath);
        dest.writeString(remark);
        dest.writeString(appVersion);
        dest.writeLong(driverType);
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
        dest.writeLong(networkTaximanLicenseDate);
        dest.writeString(licenseOrganization);
        dest.writeInt(trafficViolationTimes);
        dest.writeLong(contractStart);
        dest.writeLong(contractEnd);
        dest.writeInt(isCruisingTaxiDrivers);
        dest.writeString(driverServiceOperator);
        dest.writeString(mapType);
        dest.writeString(idcardBackPath);
        dest.writeString(mobileModel);
        dest.writeString(mobileOperators);
        dest.writeInt(isFulltimeDriver);
        dest.writeString(householdRegistrationName);
        dest.writeString(token);
        dest.writeString(refreshToken);
        dest.writeDouble(balance);
        dest.writeDouble(star);
        dest.writeLong(modelId);
        dest.writeLong(taxiModelId);
        dest.writeInt(registerStatus);
        dest.writeString(qrCodeUrl);
        dest.writeString(serviceTel);
    }


}

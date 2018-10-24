package com.easymi.component.db;

import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;

import com.easymi.component.app.XApp;
import com.easymi.component.utils.EmUtil;
import com.easymi.component.utils.ToastUtil;

import java.util.LinkedList;
import java.util.List;

public class SqliteHelper extends SQLiteOpenHelper {

    public static final String DB_NAME = "data.db";

    private static final int VERSION = 61;

    private StringBuffer sqlBuf;

    private SQLiteDatabase sqlDatabase;

    private static SqliteHelper sqlHelpler;

    private Context context;

    private SqliteHelper(Context context) {
        super(context, DB_NAME, null, VERSION);    //创建一个数据库
        sqlBuf = new StringBuffer();
        this.context = context;
    }

    public static void init(Context context) {

        if (sqlHelpler == null) {
            sqlHelpler = new SqliteHelper(context);
        }
    }

    public static SqliteHelper getInstance() {
        if (sqlHelpler == null) {
            throw new NullPointerException(
                    "SqliteHelper init function not call");
        }
        return sqlHelpler;
    }

    public synchronized SQLiteDatabase openSqliteDatabase()
            throws SQLiteException {
        if (sqlDatabase != null) {
            if ((sqlDatabase.isDbLockedByCurrentThread())) {
                try {
                    Thread.sleep(0xa);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            return sqlDatabase;
        }
        sqlDatabase = sqlHelpler.getReadableDatabase();
        return sqlDatabase;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        createDriverInfoTable(db);
        createDymTable(db);
        createSettingTable(db);
        createZCSettingTable(db);
        createVehicleInfoTable(db);
        createSystemTable(db);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + "t_driverinfo");
        createDriverInfoTable(db);

        db.execSQL("DROP TABLE IF EXISTS " + "t_dyminfo");
        createDymTable(db);

        db.execSQL("DROP TABLE IF EXISTS " + "t_settinginfo");
        createSettingTable(db);

        db.execSQL("DROP TABLE IF EXISTS " + "t_zc_settinginfo");
        createZCSettingTable(db);

        db.execSQL("DROP TABLE IF EXISTS " + "t_Vehicle");
        createVehicleInfoTable(db);

        db.execSQL("DROP TABLE IF EXISTS " + "t_systemconfig");
        createSystemTable(db);

        EmUtil.employLogout(context);
    }

    private void createDriverInfoTable(SQLiteDatabase db) {
        sqlBuf.append("CREATE TABLE ").append("t_driverinfo").append(" (")
                .append("id").append(" INTEGER PRIMARY KEY, ")
                .append("user_name").append(" ").append("TEXT").append(",")
                .append("password").append(" ").append("TEXT").append(",")
                .append("name").append(" ").append("TEXT").append(",")
                .append("real_name").append(" ").append("TEXT").append(",")
                .append("phone").append(" ").append("TEXT").append(",")
                .append("sex").append(" ").append("TEXT").append(",")
                .append("company_name").append(" ").append("TEXT").append(",")
                .append("portrait_path").append(" ").append("TEXT").append(",")
                .append("score").append(" ").append("DOUBLE").append(",")

                .append("balance").append(" ").append("DOUBLE").append(",")
                .append("service_type").append(" ").append("TEXT").append(",")
                .append("child_type").append(" ").append("TEXT").append(",")
                .append("bank_name").append(" ").append("TEXT").append(",")
                .append("bank_card_no").append(" ").append("TEXT").append(",")
                .append("cash_person_name").append(" ").append("TEXT").append(",")
                .append("status").append(" ").append("TEXT").append(",")
                .append("company_phone").append(" ").append("TEXT").append(",")

                .append("company_id").append(" ").append("LONG")
                .append(");");
        execCreateTableSQL(db);
    }

    private void createDymTable(SQLiteDatabase db) {
        sqlBuf.append("CREATE TABLE ").append("t_dyminfo").append(" (")
                .append("id").append(" INTEGER PRIMARY KEY AUTOINCREMENT, ")
                .append("orderId").append(" ").append("LONG").append(",")
                .append("orderType").append(" ").append("TEXT").append(",")
                .append("startFee").append(" ").append("DOUBLE").append(",")
                .append("waitTime").append(" ").append("INTEGER").append(",")
                .append("waitTimeFee").append(" ").append("DOUBLE").append(",")
                .append("travelTime").append(" ").append("INTEGER").append(",")
                .append("passengerId").append(" ").append("LONG").append(",")
                .append("travelFee").append(" ").append("DOUBLE").append(",")
                .append("totalFee").append(" ").append("DOUBLE").append(",")
                .append("disFee").append(" ").append("DOUBLE").append(",")
                .append("distance").append(" ").append("DOUBLE").append(",")

                .append("paymentFee").append(" ").append("DOUBLE").append(",")
                .append("extraFee").append(" ").append("DOUBLE").append(",")
                .append("remark").append(" ").append("TEXT").append(",")
                .append("couponFee").append(" ").append("DOUBLE").append(",")
                .append("orderTotalFee").append(" ").append("DOUBLE").append(",")
                .append("orderShouldPay").append(" ").append("DOUBLE").append(",")
                .append("prepay").append(" ").append("DOUBLE").append(",")

                .append("addedFee").append(" ").append("DOUBLE").append(",")
                .append("addedKm").append(" ").append("INTEGER").append(",")

                .append("minestMoney").append(" ").append("DOUBLE").append(",")

                //添加专车收费
                .append("peakCost").append(" ").append("DOUBLE").append(",")
                .append("nightPrice").append(" ").append("DOUBLE").append(",")
                .append("lowSpeedCost").append(" ").append("DOUBLE").append(",")
                .append("lowSpeedTime").append(" ").append("INTEGER").append(",")
                .append("peakMile").append(" ").append("DOUBLE").append(",")
                .append("nightTime").append(" ").append("INTEGER").append(",")
                .append("nightMile").append(" ").append("DOUBLE").append(",")
                .append("nightTimePrice").append(" ").append("DOUBLE").append(",")

                //起终点trackId
                .append("toStartTrackId").append(" ").append("LONG").append(",")
                .append("toEndTrackId").append(" ").append("LONG").append(",")

                .append("orderStatus").append(" ").append("INTEGER")
                .append(");");
        execCreateTableSQL(db);
    }

    private void createSettingTable(SQLiteDatabase db) {
        sqlBuf.append("CREATE TABLE ").append("t_settinginfo").append(" (")
                .append("id").append(" INTEGER PRIMARY KEY AUTOINCREMENT, ")
                .append("isPaid").append(" ").append("INTEGER").append(",")
                .append("isExpenses").append(" ").append("INTEGER").append(",")
                .append("canCancelOrder").append(" ").append("INTEGER").append(",")
                .append("isAddPrice").append(" ").append("INTEGER").append(",")
                .append("isWorkCar").append(" ").append("INTEGER").append(",")
                .append("employChangePrice").append(" ").append("INTEGER").append(",")
                .append("doubleCheck").append(" ").append("INTEGER").append(",")
                .append("canCallDriver").append(" ").append("INTEGER").append(",")
                .append("payMoney1").append(" ").append("DOUBLE").append(",")
                .append("payMoney2").append(" ").append("DOUBLE").append(",")
                .append("payMoney3").append(" ").append("DOUBLE").append(",")

                .append("workCarChangeOrder").append(" ").append("INTEGER").append(",")
                .append("employChangeOrder").append(" ").append("employChangeOrder")
                .append(");");
        execCreateTableSQL(db);
    }

    private void createZCSettingTable(SQLiteDatabase db) {
        sqlBuf.append("CREATE TABLE ").append("t_zc_settinginfo").append(" (")
                .append("id").append(" INTEGER PRIMARY KEY AUTOINCREMENT, ")
                .append("isPaid").append(" ").append("INTEGER").append(",")
                .append("isExpenses").append(" ").append("INTEGER").append(",")
                .append("canCancelOrder").append(" ").append("INTEGER").append(",")
                .append("isAddPrice").append(" ").append("INTEGER").append(",")
                .append("employChangePrice").append(" ").append("INTEGER").append(",")
                .append("employChangeOrder").append(" ").append("employChangeOrder")
                .append(");");
        execCreateTableSQL(db);
    }

    private void createVehicleInfoTable(SQLiteDatabase db) {
        sqlBuf.append("CREATE TABLE ").append("t_Vehicle").append(" (")
                .append("employId").append(" INTEGER PRIMARY KEY, ")
                .append("vehicleId").append(" ").append("LONG").append(",")
                .append("companyId").append(" ").append("LONG").append(",")
                .append("vehicleBrand").append(" ").append("TEXT").append(",")
                .append("vehicleModel").append(" ").append("TEXT").append(",")
                .append("plateColor").append(" ").append("TEXT").append(",")
                .append("vehicleNo").append(" ").append("TEXT").append(",")
                .append("vehicleType").append(" ").append("TEXT").append(",")
                .append("commercialType").append(" ").append("INTEGER").append(",")

                .append("serviceType").append(" ").append("INTEGER")
                .append(");");
        execCreateTableSQL(db);
    }

    private void createSystemTable(SQLiteDatabase db) {
        sqlBuf.append("CREATE TABLE ").append("t_systemconfig").append(" (")
                .append("id").append(" INTEGER PRIMARY KEY AUTOINCREMENT, ")
                .append("tixianBase").append(" ").append("DOUBLE").append(",")
                .append("tixianMin").append(" ").append("DOUBLE").append(",")
                .append("tixianMax").append(" ").append("DOUBLE").append(",")
                .append("tixianMemo").append(" ").append("TEXT").append(",")
                .append("payMoney1").append(" ").append("DOUBLE").append(",")
                .append("payMoney2").append(" ").append("DOUBLE").append(",")
                .append("payMoney3").append(" ").append("DOUBLE").append(",")
                .append("payType").append(" ").append("TEXT").append(",")

                .append("canCallDriver").append(" ").append("INTEGER")
                .append(");");
        execCreateTableSQL(db);
    }

    private void execCreateTableSQL(SQLiteDatabase db) {
        db.execSQL(sqlBuf.toString());
        sqlBuf.setLength(0x0);
    }

    /**
     * 删除数据库
     */
    public boolean deleteDatabase(Context context) {
        return context.deleteDatabase(DB_NAME);
    }

    public List<String> findAllTableName(SQLiteDatabase db) {

        Cursor cursor = db.rawQuery("select name from sqlite_master where type='table' order by name", new String[]{});

        List<String> list = new LinkedList<>();

        try {
            while (cursor.moveToNext()) {
                String table = cursor.getString(0);
                list.add(table);
            }
        } finally {
            cursor.close();
        }
        return list;
    }

}

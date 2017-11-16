package com.easymi.component.db;

import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;

import com.easymi.component.app.XApp;

import java.util.LinkedList;
import java.util.List;

public class SqliteHelper extends SQLiteOpenHelper {

    public static final String BLANK = " ";

    public static final String COMMA = ",";

    public static final String DB_DIR = "databases";

    public static final String DB_NAME = "data.db";

    private static final int VERSION = 40;

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
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        int current = oldVersion;

        //一有数据库的更新就将sp里的东西归为默认值
        SharedPreferences.Editor editor = XApp.getPreferencesEditor();
        editor.putBoolean("exit_flag", true);
        editor.putBoolean("isLogin", false);
        editor.putBoolean("bindDevice", false);
        editor.putLong("driverID", 0);
        editor.apply();

        List<String> tableNames = findAllTableName(db);
        for (String s : tableNames) {
            if (s.equals("android_metadata") || s.equals("sqlite_sequence")) {

            } else {
                db.execSQL("DROP TABLE IF EXISTS " + s);
            }
        }
        createDriverInfoTable(db);
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
                .append("company_id").append(" ").append("LONG")
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
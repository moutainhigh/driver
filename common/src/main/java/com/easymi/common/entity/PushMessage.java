package com.easymi.common.entity;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.easymi.component.db.SqliteHelper;

import java.util.LinkedList;
import java.util.List;

public class PushMessage {

    public static void save(String data) {
        SqliteHelper helper = SqliteHelper.getInstance();
        SQLiteDatabase db = helper.openSqliteDatabase();
        ContentValues values = new ContentValues();
        values.put("data", data);
        db.insert("t_cp_temp_message", null, values);
    }

    public static void deleteAll() {
        SqliteHelper helper = SqliteHelper.getInstance();
        SQLiteDatabase db = helper.openSqliteDatabase();
        db.delete("t_cp_temp_message", null, null);
    }

    public static void delete(String data) {
        SqliteHelper helper = SqliteHelper.getInstance();
        SQLiteDatabase db = helper.openSqliteDatabase();
        db.delete("t_cp_temp_message", "data = ?", new String[]{data});
    }

    public static List<String> findAll() {
        SqliteHelper helper = SqliteHelper.getInstance();
        SQLiteDatabase db = helper.openSqliteDatabase();

        Cursor cursor = db.rawQuery("select * from t_cp_temp_message", new String[]{});

        List<String> list = new LinkedList<>();

        try {
            while (cursor.moveToNext()) {
                String data = cursor.getString(cursor.getColumnIndex("data"));
                list.add(data);
            }
        } finally {
            cursor.close();
        }
        return list;
    }
}
